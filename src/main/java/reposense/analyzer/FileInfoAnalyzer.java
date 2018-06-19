package reposense.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.FileResult;
import reposense.dataobject.LineInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;
import reposense.util.Constants;

public class FileInfoAnalyzer {
    private static final Logger logger = LogsManager.getLogger(FileInfoAnalyzer.class);

    private static final int AUTHOR_NAME_OFFSET = "author ".length();

    /**
     * Analyzes the {@code fileInfo}, then generates and returns the {@code FileResult}.
     * Returns null if the file contains the {@code Constants#REUSED_TAG}, or none of the {@code Author} specified in
     * {@code config} contributed to the file in {@code fileInfo}.
     */
    public static FileResult analyzeFile(RepoConfiguration config, FileInfo fileInfo) {
        String relativePath = fileInfo.getPath();
        if (isReused(config.getRepoRoot(), relativePath)) {
            return null;
        }

        analyzeFileContributions(config, fileInfo);

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
        for (LineInfo line: fileInfo.getLines()) {
            Author author = line.getAuthor();
            authorContributionMap.put(author, authorContributionMap.getOrDefault(author, 0) + 1);
        }
        return new FileResult(fileInfo.getPath(), fileInfo.getLines(), authorContributionMap);
    }

    /**
     * Analyzes the file specified in {@code fileInfo} and sets the {@code Author} for each line in {@code fileInfo}.
     */
    private static void analyzeFileContributions(RepoConfiguration config, FileInfo fileInfo) {
        Map<String, Author> authorAliasMap = config.getAuthorAliasMap();

        String blameResults = getGitBlameResult(config, fileInfo.getPath());
        String[] blameResultLines = blameResults.split("\n");
        int lineCount = 0;

        for (String line : blameResultLines) {
            String authorRawName = line.substring(AUTHOR_NAME_OFFSET);
            Author author = authorAliasMap.getOrDefault(authorRawName, new Author("-"));
            fileInfo.setLineAuthor(lineCount++, author);
        }
    }

    /**
     * Returns true if the first line in the file at {@code repoRoot}'s {@code relativePath} contains the
     * {@code Constants#REUSED_TAG}.
     */
    private static boolean isReused(String repoRoot, String relativePath) {
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.contains(Constants.REUSED_TAG)) {
                return true;
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        }
        return false;
    }

    /**
     * Returns the analysis result from running git blame on {@code filePath}.
     */
    private static String getGitBlameResult(RepoConfiguration config, String filePath) {
        return CommandRunner.blameRaw(config.getRepoRoot(), filePath, config.getSinceDate(), config.getUntilDate());
    }
}
