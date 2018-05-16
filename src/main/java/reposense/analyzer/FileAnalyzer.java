package reposense.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;


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
            FileAnalyzeThread thread = new FileAnalyzeThread(fileInfos, config, relativePath);
            thread.run();
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

}
