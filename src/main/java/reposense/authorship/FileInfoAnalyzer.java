package reposense.authorship;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Logger;

import reposense.authorship.analyzer.AnnotatorAnalyzer;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitBlame;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Analyzes the target and information given in the {@code FileInfo}.
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

    /**
     * Analyzes the lines of the file, given in the {@code fileInfo}, that has changed in the time period provided
     * by {@code config}.
     * Returns null if the file is missing from the local system, or none of the
     * {@code Author} specified in {@code config} contributed to the file in {@code fileInfo}.
     */
    public static FileResult analyzeFile(RepoConfiguration config, FileInfo fileInfo) {
        String relativePath = fileInfo.getPath();

        if (Files.notExists(Paths.get(config.getRepoRoot(), relativePath))) {
            logger.severe(String.format(MESSAGE_FILE_MISSING, relativePath));
            return null;
        }

        if (FileUtil.isEmptyFile(config.getRepoRoot(), relativePath)) {
            return null;
        }

        aggregateBlameAuthorInfo(config, fileInfo);
        fileInfo.setFileType(config.getFileType(fileInfo.getPath()));

        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config.getAuthorEmailsAndAliasesMap());

        if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())) {
            return null;
        }

        return generateFileResult(fileInfo);
    }

    /**
     * Generates and returns a {@code FileResult} with the authorship results from {@code fileInfo} consolidated.
     */
    private static FileResult generateFileResult(FileInfo fileInfo) {
        HashMap<Author, Integer> authorContributionMap = new HashMap<>();
        for (LineInfo line : fileInfo.getLines()) {
            Author author = line.getAuthor();
            authorContributionMap.put(author, authorContributionMap.getOrDefault(author, 0) + 1);
        }
        return new FileResult(fileInfo.getPath(), fileInfo.getFileType(), fileInfo.getLines(), authorContributionMap);
    }

    /**
     * Sets the {@code Author} for each line in {@code fileInfo} based on the git blame analysis on the file.
     */
    private static void aggregateBlameAuthorInfo(RepoConfiguration config, FileInfo fileInfo) {
        String blameResults = getGitBlameResult(config, fileInfo.getPath());
        String[] blameResultLines = blameResults.split("\n");
        Path filePath = Paths.get(fileInfo.getPath());
        Long sinceDateInMs = config.getSinceDate().getTime();
        Long untilDateInMs = config.getUntilDate().getTime();
        int systemRawOffset = TimeZone.getTimeZone(ZoneId.systemDefault()).getRawOffset();

        for (int lineCount = 0; lineCount < blameResultLines.length; lineCount += 5) {
            String commitHash = blameResultLines[lineCount].substring(0, FULL_COMMIT_HASH_LENGTH);
            String authorName = blameResultLines[lineCount + 1].substring(AUTHOR_NAME_OFFSET);
            String authorEmail = blameResultLines[lineCount + 2]
                    .substring(AUTHOR_EMAIL_OFFSET).replaceAll("<|>", "");
            Long commitDateInMs = Long.parseLong(blameResultLines[lineCount + 3].substring(AUTHOR_TIME_OFFSET)) * 1000;
            String authorTimeZone = blameResultLines[lineCount + 4].substring(AUTHOR_TIMEZONE_OFFSET);
            Author author = config.getAuthor(authorName, authorEmail);

            int authorRawOffset = TimeZone.getTimeZone(ZoneOffset.of(authorTimeZone)).getRawOffset();
            if (systemRawOffset != authorRawOffset) {
                // adjust commit date according to difference in timezone
                commitDateInMs += authorRawOffset - systemRawOffset;
            }

            if (!fileInfo.isFileLineTracked(lineCount / 5) || author.isIgnoringFile(filePath)
                    || CommitHash.isInsideCommitList(commitHash, config.getIgnoreCommitList())
                    || commitDateInMs < sinceDateInMs || commitDateInMs > untilDateInMs) {
                author = Author.UNKNOWN_AUTHOR;
            }

            fileInfo.setLineAuthor(lineCount / 5, author);
        }
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath}.
     */
    private static String getGitBlameResult(RepoConfiguration config, String filePath) {
        return GitBlame.blame(config.getRepoRoot(), filePath);
    }
}
