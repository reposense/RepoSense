package analyzer;


import dataObject.RepoConfiguration;
import dataObject.FileInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class FileAnalyzer {

    public static ArrayList<FileInfo> analyzeAllFiles(RepoConfiguration config) {
        ArrayList<String> relativePaths = new ArrayList<>();
        getAllPossibleFilePaths(config, new File(config.getRepoRoot()),relativePaths);
        ArrayList<FileInfo> result = processFiles(config,relativePaths);
        return result;
    }

    private static void getAllPossibleFilePaths(RepoConfiguration config, File directory, ArrayList<String> relativePaths){
        for (File file:directory.listFiles()) {

            String relativePath = file.getPath().replaceFirst(config.getRepoRoot(), "");
            if (shouldIgnore(relativePath, config.getIgnoreDirectoryList())) continue;
            if (file.isDirectory()) {
                getAllPossibleFilePaths(config, file, relativePaths);
            } else {
                if (!relativePath.endsWith(".java")) continue;
                relativePaths.add(relativePath);
            }
        }
    }

    private static ArrayList<FileInfo> processFiles(RepoConfiguration config, List<String> relativePaths){
        List<FileInfo> fileInfos = Collections.synchronizedList(new ArrayList<FileInfo>());
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (String relativePath: relativePaths) {
            Runnable worker = new FileAnalyzeThread(fileInfos,config,relativePath);
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        return new ArrayList<>(fileInfos);
    }


    private static boolean shouldIgnore(String name, List<String> ignoreList) {
        for (String element : ignoreList) {
            if (name.contains(element)) return true;
        }
        return false;
    }

}
