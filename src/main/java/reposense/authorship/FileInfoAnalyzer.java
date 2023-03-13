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
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitBlame;
import reposense.git.GitLog;
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

    private static final int AUTHOR_NAME_OFFSET = "author ".length();
    private static final int AUTHOR_EMAIL_OFFSET = "author-mail ".length();
    private static final int AUTHOR_TIME_OFFSET = "author-time ".length();
    private static final int AUTHOR_TIMEZONE_OFFSET = "author-tz ".length();
    private static final int FULL_COMMIT_HASH_LENGTH = 40;

    private static final String MESSAGE_FILE_MISSING = "Unable to analyze the file located at \"%s\" "
            + "as the file is missing from your system. Skipping this file.";

    private static final String MESSAGE_SHALLOW_CLONING_LAST_MODIFIED_DATE_CONFLICT = "Repo %s was cloned using "
            + "shallow cloning. As such, the \"last modified date\" values may be incorrect.";

    /**
     * Analyzes the lines of the file, given in the {@code fileInfo}, that has changed in the time period provided
     * by {@code config}.
     * Returns null if the file is missing from the local system, or none of the
     * {@link Author} specified in {@code config} contributed to the file in {@code fileInfo}.
     */
    public FileResult analyzeTextFile(RepoConfiguration config, FileInfo fileInfo) {
        String relativePath = fileInfo.getPath();

        if (Files.notExists(Paths.get(config.getRepoRoot(), relativePath))) {
            logger.severe(String.format(MESSAGE_FILE_MISSING, relativePath));
            return null;
        }

        if (FileUtil.isEmptyFile(config.getRepoRoot(), relativePath)) {
            return null;
        }

        aggregateBlameAuthorModifiedAndDateInfo(config, fileInfo);
        fileInfo.setFileType(config.getFileType(fileInfo.getPath()));

        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config.getAuthorConfig());

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

        return FileResult.createTextFileResult(
            fileInfo.getPath(), fileInfo.getFileType(), fileInfo.getLines(), authorContributionMap,
            fileInfo.exceedsFileLimit());
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
     */
    private void aggregateBlameAuthorModifiedAndDateInfo(RepoConfiguration config, FileInfo fileInfo) {
        String blameResults;

        if (!config.isFindingPreviousAuthorsPerformed()) {
            blameResults = getGitBlameResult(config, fileInfo.getPath());
        } else {
            blameResults = getGitBlameWithPreviousAuthorsResult(config, fileInfo.getPath());
        }

        String[] blameResultLines = blameResults.split("\n");
        Path filePath = Paths.get(fileInfo.getPath());
        LocalDateTime sinceDate = config.getSinceDate();
        LocalDateTime untilDate = config.getUntilDate();

        for (int lineCount = 0; lineCount < blameResultLines.length; lineCount += 5) {
            String commitHash = blameResultLines[lineCount].substring(0, FULL_COMMIT_HASH_LENGTH);
            String authorName = blameResultLines[lineCount + 1].substring(AUTHOR_NAME_OFFSET);
            String authorEmail = blameResultLines[lineCount + 2]
                    .substring(AUTHOR_EMAIL_OFFSET).replaceAll("<|>", "");
            Long commitDateInMs = Long.parseLong(blameResultLines[lineCount + 3].substring(AUTHOR_TIME_OFFSET)) * 1000;
            LocalDateTime commitDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(commitDateInMs),
                    config.getZoneId());
            Author author = config.getAuthor(authorName, authorEmail);

            if (!fileInfo.isFileLineTracked(lineCount / 5) || author.isIgnoringFile(filePath)
                    || CommitHash.isInsideCommitList(commitHash, config.getIgnoreCommitList())
                    || commitDate.compareTo(sinceDate) < 0 || commitDate.compareTo(untilDate) > 0) {
                author = Author.UNKNOWN_AUTHOR;
            }

            if (config.isLastModifiedDateIncluded()) {
                if (config.isShallowCloningPerformed()) {
                    logger.warning(String.format(
                            MESSAGE_SHALLOW_CLONING_LAST_MODIFIED_DATE_CONFLICT, config.getRepoName()));
                }

                fileInfo.setLineLastModifiedDate(lineCount / 5, commitDate);
            }
            fileInfo.setLineAuthor(lineCount / 5, author);
        }
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath} with reference to the root directory
     * given in {@code config}.
     */
    private String getGitBlameResult(RepoConfiguration config, String filePath) {
        return GitBlame.blame(config.getRepoRoot(), filePath);
    }

    /**
     * Returns the analysis result from running git blame with finding previous authors enabled on {@code filePath}
     * with reference to the root directory given in {@code config}.
     */
    private String getGitBlameWithPreviousAuthorsResult(RepoConfiguration config, String filePath) {
        return GitBlame.blameWithPreviousAuthors(config.getRepoRoot(), filePath);
    }
}
