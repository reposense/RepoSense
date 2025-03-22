package reposense.authorship;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import reposense.authorship.analyzer.AnnotatorAnalyzer;
import reposense.authorship.analyzer.AuthorshipAnalyzer;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitBlame;
import reposense.git.GitLog;
import reposense.git.model.GitBlameLineInfo;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Analyzes the target and information given in the {@link FileInfo}.
 */
public class FileInfoAnalyzer {
    private static final Logger logger = LogsManager.getLogger(FileInfoAnalyzer.class);

    private static final String MESSAGE_FILE_MISSING = "Unable to analyze the file located at \"%s\" "
            + "as the file is missing from your system. Skipping this file.";

    private static final String MESSAGE_SHALLOW_CLONING_LAST_MODIFIED_DATE_CONFLICT = "Repo %s was cloned using "
            + "shallow cloning. As such, the \"last modified date\" values may be incorrect.";

    /**
     * Analyzes the lines of the file, given in the {@code fileInfo}, that has changed in the time period provided
     * by {@code config}.
     * Further analyzes the authorship of each line in the commit if {@code shouldAnalyzeAuthorship} is true, based on
     * {@code originalityThreshold}.
     * Returns null if the file is missing from the local system, or none of the
     * {@link Author} specified in {@code config} contributed to the file in {@code fileInfo}.
     */
    public FileResult analyzeTextFile(RepoConfiguration config, FileInfo fileInfo, boolean shouldAnalyzeAuthorship,
            double originalityThreshold) {
        String relativePath = fileInfo.getPath();

        if (Files.notExists(Paths.get(config.getRepoRoot(), relativePath))) {
            logger.severe(String.format(MESSAGE_FILE_MISSING, relativePath));
            return null;
        }

        if (FileUtil.isEmptyFile(config.getRepoRoot(), relativePath)) {
            return null;
        }

        aggregateBlameAuthorModifiedAndDateInfo(config, fileInfo, shouldAnalyzeAuthorship, originalityThreshold);
        fileInfo.setFileType(config.getFileType(fileInfo.getPath()));

        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config.getAuthorConfig(), shouldAnalyzeAuthorship);

        if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())) {
            return null;
        }

        return generateTextFileResult(fileInfo);
    }

    /**
     * Analyzes the binary file, given in the {@code fileInfo}, that has changed in the time period provided
     * by {@code config}.
     * Returns null if the file is missing from the local system, or none of the
     * {@link Author} specified in {@code config} contributed to the file in {@code fileInfo}.
     */
    public FileResult analyzeBinaryFile(RepoConfiguration config, FileInfo fileInfo) {
        String relativePath = fileInfo.getPath();

        if (Files.notExists(Paths.get(config.getRepoRoot(), relativePath))) {
            logger.severe(String.format(MESSAGE_FILE_MISSING, relativePath));
            return null;
        }

        fileInfo.setFileType(config.getFileType(fileInfo.getPath()));

        return generateBinaryFileResult(config, fileInfo);
    }

    /**
     * Generates and returns a {@link FileResult} with the authorship results from {@code fileInfo} consolidated.
     */
    private FileResult generateTextFileResult(FileInfo fileInfo) {
        HashMap<Author, Integer> authorContributionMap = new HashMap<>();
        for (LineInfo line : fileInfo.getLines()) {
            Author author = line.getAuthor();
            authorContributionMap.put(author, authorContributionMap.getOrDefault(author, 0) + 1);
        }

        return FileResult.createTextFileResult(fileInfo.getPath(), fileInfo.getFileType(), fileInfo.getLines(),
                authorContributionMap, fileInfo.exceedsFileLimit());
    }

    /**
     * Generates and returns a {@link FileResult} with the authorship results from binary {@code fileInfo} consolidated.
     * Authorship results are indicated in the {@code authorContributionMap} as contributions with zero line counts.
     * Returns {@code null} if none of the {@link Author} specified in {@code config} contributed to the file in
     * {@code fileInfo}.
     */
    private FileResult generateBinaryFileResult(RepoConfiguration config, FileInfo fileInfo) {
        List<String[]> authorsString = GitLog.getFileAuthors(config, fileInfo.getPath());
        if (authorsString.size() == 0) {
            return null;
        }

        Set<Author> authors = new HashSet<>();
        HashMap<Author, Integer> authorContributionMap = new HashMap<>();

        for (String[] lineDetails : authorsString) {
            String authorName = lineDetails[0];
            String authorEmail = lineDetails[1];
            authors.add(config.getAuthor(authorName, authorEmail));
        }

        for (Author author : authors) {
            authorContributionMap.put(author, 0);
        }

        return FileResult.createBinaryFileResult(fileInfo.getPath(), fileInfo.getFileType(), authorContributionMap);
    }

    /**
     * Sets the {@link Author} and {@link LocalDateTime} for each line in {@code fileInfo} based on the git blame
     * analysis of the file.
     * The {@code config} is used to obtain the root directory for running git blame as well as other parameters used
     * in determining which author to assign to each line and whether to set the last modified date for a
     * {@code lineInfo}.
     * Further analyzes the authorship of each line in the commit if {@code shouldAnalyzeAuthorship} is true, based on
     * {@code originalityThreshold}.
     */
    private void aggregateBlameAuthorModifiedAndDateInfo(RepoConfiguration config, FileInfo fileInfo,
            boolean shouldAnalyzeAuthorship, double originalityThreshold) {
        List<GitBlameLineInfo> gitBlameLineInfos = getGitBlameFileResult(config, fileInfo.getPath(),
                config.isFindingPreviousAuthorsPerformed());

        Path filePath = Paths.get(fileInfo.getPath());
        LocalDateTime sinceDate = config.getSinceDate();
        LocalDateTime untilDate = config.getUntilDate();

        for (int lineCount = 0; lineCount < gitBlameLineInfos.size(); lineCount++) {
            GitBlameLineInfo blameLineInfo = gitBlameLineInfos.get(lineCount);
            String commitHash = blameLineInfo.getCommitHash();
            LocalDateTime commitDate = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(blameLineInfo.getTimestampInSeconds()), config.getZoneId());
            Author author = config.getAuthor(blameLineInfo.getAuthorName(), blameLineInfo.getAuthorEmail());

            if (!fileInfo.isFileLineTracked(lineCount) || author.isIgnoringFile(filePath)
                    || CommitHash.isInsideCommitList(commitHash, config.getIgnoreCommitList())
                    || commitDate.isBefore(sinceDate) || commitDate.isAfter(untilDate)) {
                author = Author.UNKNOWN_AUTHOR;
            }

            if (config.isLastModifiedDateIncluded()) {
                if (config.isShallowCloningPerformed()) {
                    logger.warning(String.format(
                            MESSAGE_SHALLOW_CLONING_LAST_MODIFIED_DATE_CONFLICT, config.getRepoName()));
                }

                fileInfo.setLineLastModifiedDate(lineCount, commitDate);
            }
            fileInfo.setLineAuthor(lineCount, author);

            if (shouldAnalyzeAuthorship && !author.equals(Author.UNKNOWN_AUTHOR)) {
                String lineContent = fileInfo.getLine(lineCount + 1).getContent();
                boolean isFullCredit = AuthorshipAnalyzer.analyzeAuthorship(config, fileInfo.getPath(), lineContent,
                        commitHash, author, originalityThreshold);
                fileInfo.setIsFullCredit(lineCount, isFullCredit);
            }
        }
    }

    /**
     * Returns the analysis result from running git blame file on {@code filePath} with reference to the root directory
     * given in {@code config} and {@code withPreviousAuthors}.
     */
    private List<GitBlameLineInfo> getGitBlameFileResult(RepoConfiguration config, String filePath,
            boolean withPreviousAuthors) {
        return GitBlame.blameFile(config.getRepoRoot(), filePath, withPreviousAuthors);
    }
}
