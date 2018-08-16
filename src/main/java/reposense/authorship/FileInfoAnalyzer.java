package reposense.authorship;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.authorship.analyzer.AnnotatorAnalyzer;
import reposense.authorship.analyzer.CheckStyleParser;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Analyzes the target and information given in the {@code FileInfo}.
 */
public class FileInfoAnalyzer {
    private static final Logger logger = LogsManager.getLogger(FileInfoAnalyzer.class);

    private static final String REUSED_TAG = "//@reused";
    private static final int AUTHOR_NAME_OFFSET = "author ".length();
    private static final int FULL_COMMIT_HASH_LENGTH = 40;

    /**
     * Analyzes the lines of the file, given in the {@code fileInfo}, that has changed in the time period provided
     * by {@code config}.
     * Returns null if the file contains the reused tag, or none of the {@code Author} specified in
     * {@code config} contributed to the file in {@code fileInfo}.
     */
    public static FileResult analyzeFile(RepoConfiguration config, FileInfo fileInfo) {
        String relativePath = fileInfo.getPath();
        if (isReused(config.getRepoRoot(), relativePath)) {
            return null;
        }

        aggregateBlameAuthorInfo(config, fileInfo);

        if (config.isNeedCheckStyle()) {
            CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
        }
        if (config.isAnnotationOverwrite()) {
            AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config.getAuthorAliasMap());
        }

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
        return new FileResult(fileInfo.getPath(), fileInfo.getLines(), authorContributionMap);
    }

    /**
     * Sets the {@code Author} for each line in {@code fileInfo} based on the git blame analysis on the file.
     */
    private static void aggregateBlameAuthorInfo(RepoConfiguration config, FileInfo fileInfo) {
        Map<String, Author> authorAliasMap = config.getAuthorAliasMap();

        String blameResults = getGitBlameResult(config, fileInfo.getPath());
        String[] blameResultLines = blameResults.split("\n");
        Path filePath = Paths.get(fileInfo.getPath());

        for (int lineCount = 0; lineCount < blameResultLines.length; lineCount += 2) {
            String commitHash = blameResultLines[lineCount].substring(0, FULL_COMMIT_HASH_LENGTH);
            String authorRawName = blameResultLines[lineCount + 1].substring(AUTHOR_NAME_OFFSET);
            Author author = authorAliasMap.getOrDefault(authorRawName, new Author(Author.UNKNOWN_AUTHOR_GIT_ID));

            if (!fileInfo.isFileLineTracked(lineCount / 2) || isAuthorIgnoringFile(author, filePath)
                    || config.getIgnoreCommitList().contains(commitHash)) {
                author = new Author(Author.UNKNOWN_AUTHOR_GIT_ID);
            }

            fileInfo.setLineAuthor(lineCount / 2, author);
        }
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath}.
     */
    private static String getGitBlameResult(RepoConfiguration config, String filePath) {
        return CommandRunner.blameRaw(config.getRepoRoot(), filePath);
    }

    /**
     * Returns true if the first line in the file at {@code repoRoot}'s {@code relativePath} contains the reused tag.
     */
    private static boolean isReused(String repoRoot, String relativePath) {
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.contains(REUSED_TAG)) {
                return true;
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        }
        return false;
    }

    /**
     * Returns true if the {@code author} is ignoring the {@code filePath} based on its ignore glob list.
     */
    private static boolean isAuthorIgnoringFile(Author author, Path filePath) {
        PathMatcher ignoreGlobMatcher = author.getIgnoreGlobMatcher();
        return ignoreGlobMatcher.matches(filePath);
    }
}
