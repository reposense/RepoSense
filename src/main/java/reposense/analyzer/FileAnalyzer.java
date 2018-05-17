package reposense.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitBlamer;
import reposense.util.Constants;


public class FileAnalyzer {

    public static ArrayList<FileInfo> analyzeAllFiles(RepoConfiguration config) {
        ArrayList<String> relativePaths = new ArrayList<>();
        getAllPossibleFilePaths(config, Paths.get(config.getRepoRoot()), relativePaths);
        Collections.sort(relativePaths);
        return processFiles(config, relativePaths);
    }

    private static void getAllPossibleFilePaths(
            RepoConfiguration config, Path directory, ArrayList<String> relativePaths) {
        try (Stream<Path> pathStream = Files.list(directory)) {
            for (Path filePath : pathStream.collect(Collectors.toList())) {
                String relativePath = filePath.toString().substring(config.getRepoRoot().length());
                if (shouldIgnore(relativePath, config.getIgnoreDirectoryList())) {
                    return;
                }
                if (Files.isDirectory(filePath)) {
                    getAllPossibleFilePaths(config, filePath, relativePaths);
                }
                if (relativePath.endsWith(".java") || relativePath.endsWith(".adoc")) {
                    relativePaths.add(relativePath.replace('\\', '/'));
                }
            }
        } catch (IOException ioe) {
            System.out.println("Error occured while getting all possible files to analyze.");
        }
    }

    private static ArrayList<FileInfo> processFiles(RepoConfiguration config, List<String> relativePaths) {
        List<FileInfo> fileInfos = Collections.synchronizedList(new ArrayList<FileInfo>());
        for (String relativePath : relativePaths) {
            if (isReused(config.getRepoRoot(), relativePath)) {
                continue;
            }
            FileInfo fileInfo = FileInfoGenerator.generateFileInfo(config.getRepoRoot(), relativePath);
            GitBlamer.aggregateBlameInfo(fileInfo, config);
            if (config.isNeedCheckStyle()) {
                CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
            }
            if (config.isAnnotationOverwrite()) {
                AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config);
            }
            if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())) {
                continue;
            }
            fileInfo.constructAuthorContributionMap();
            fileInfos.add(fileInfo);
        }
        return new ArrayList<>(fileInfos);
    }


    private static boolean shouldIgnore(String name, List<String> ignoreList) {
        for (String element : ignoreList) {
            if (name.contains(element)) {
                return true;
            }
        }
        return false;
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
