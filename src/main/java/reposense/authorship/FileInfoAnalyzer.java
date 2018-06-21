package reposense.authorship;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        int lineCount = 0;

        for (String line : blameResultLines) {
            String authorRawName = line.substring(AUTHOR_NAME_OFFSET);
            Author author = authorAliasMap.getOrDefault(authorRawName, new Author(Author.UNKNOWN_AUTHOR_GIT_ID));
            fileInfo.setLineAuthor(lineCount++, author);
        }
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath}.
     */
    private static String getGitBlameResult(RepoConfiguration config, String filePath) {
        return CommandRunner.blameRaw(config.getRepoRoot(), filePath, config.getSinceDate(), config.getUntilDate());
    }

    /**
     * Returns true if the first line in the file at {@code repoRoot}'s {@code relativePath} contains the reused tag.
     */
    private static boolean isReused(String repoRoot, String relativePath) {
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.contains(REUSED_TAG)) {
                return true;
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        }
        return false;
    }
}
