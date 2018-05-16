package reposense.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitBlamer;
import reposense.util.Constants;


public class FileAnalyzer {

    public static ArrayList<FileInfo> analyzeAllFiles(RepoConfiguration config) {
        ArrayList<String> relativePaths = new ArrayList<>();
        getAllPossibleFilePaths(config, new File(config.getRepoRoot()), relativePaths);
        ArrayList<FileInfo> result = processFiles(config, relativePaths);
        return result;
    }

    private static void getAllPossibleFilePaths(
            RepoConfiguration config, File directory, ArrayList<String> relativePaths) {
        for (File file : directory.listFiles()) {

            String relativePath = file.getPath().substring(config.getRepoRoot().length());
            if (shouldIgnore(relativePath, config.getIgnoreDirectoryList())) {
                continue;
            }
            if (file.isDirectory()) {
                getAllPossibleFilePaths(config, file, relativePaths);
            } else {
                if (!relativePath.endsWith(".java") && !relativePath.endsWith(".adoc")) {
                    continue;
                }
                relativePaths.add(relativePath);
            }
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
        File file = new File(repoRoot + '/' + relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.contains(Constants.REUSED_TAG)) {
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
