package reposense.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.FileResult;
import reposense.dataobject.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.util.Constants;

public class FileInfoAnalyzer {
    private static final int AUTHOR_NAME_OFFSET = "author ".length();

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
            AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config);
        }

        if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())) {
            return null;
        }

        return FileResultGenerator.generateFileResult(fileInfo);
    }

    private static void analyzeFileContributions(RepoConfiguration config, FileInfo fileInfo) {
        TreeMap<String, Author> authorAliasMap = config.getAuthorAliasMap();

        String blameResults = CommandRunner.blameRaw(
                config.getRepoRoot(), fileInfo.getPath(), config.getSinceDate(), config.getUntilDate());
        String[] blameResultLines = blameResults.split("\n");
        int lineCount = 0;

        for (String line : blameResultLines) {
            String authorRawName = line.substring(AUTHOR_NAME_OFFSET);
            Author author = authorAliasMap.getOrDefault(authorRawName, new Author("-"));
            fileInfo.setLineAuthor(lineCount++, author);
        }
    }

    private static boolean isReused(String repoRoot, String relativePath) {
        Path path = Paths.get(repoRoot, relativePath);
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(path))) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.contains(Constants.REUSED_TAG)) {
                return true;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return false;
    }
}
