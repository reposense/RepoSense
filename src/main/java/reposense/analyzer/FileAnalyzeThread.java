package reposense.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitBlamer;
import reposense.util.Constants;


public class FileAnalyzeThread implements Runnable {

    private List<FileInfo> fileInfos;
    private RepoConfiguration config;
    private String relativePath;

    public FileAnalyzeThread(List<FileInfo> fileInfos, RepoConfiguration config, String relativePath) {
        this.fileInfos = fileInfos;
        this.config = config;
        this.relativePath = relativePath;
    }

    @Override
    public void run() {
        if (isReused(config.getRepoRoot(), relativePath)) {
            return;
        }
        //System.out.println("analyzing file info for "+relativePath);
        FileInfo fileInfo = FileInfoGenerator.generateFileInfo(config.getRepoRoot(), relativePath);
        //System.out.println("analyzing blame "+relativePath);
        GitBlamer.aggregateBlameInfo(fileInfo, config);
        //System.out.println("finish blame...");
        if (config.isNeedCheckStyle()) {
            CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
        }
        if (config.isAnnotationOverwrite()) {
            AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config);
        }
        if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())) {
            return;
        }
        //MethodAnalyzer.aggregateMethodInfo(fileInfo,config);
        fileInfo.constructAuthorContributionMap();
        fileInfos.add(fileInfo);
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
