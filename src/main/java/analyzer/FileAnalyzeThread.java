package analyzer;

import dataObject.FileInfo;
import dataObject.LineInfo;
import dataObject.RepoConfiguration;
import util.Constants;

import java.io.*;
import java.util.List;

/**
 * Created by matanghao1 on 6/12/17.
 */
public class FileAnalyzeThread implements Runnable {

    private List<FileInfo> fileInfos;
    private RepoConfiguration config;
    private String relativePath;

    public FileAnalyzeThread(List<FileInfo> fileInfos, RepoConfiguration config, String relativePath){
        this.fileInfos = fileInfos;
        this.config = config;
        this.relativePath = relativePath;
    }

    @Override
    public void run() {
        if (isReused(config.getRepoRoot(),relativePath)) return;
        //System.out.println("analyzing file info for "+relativePath);
        FileInfo fileInfo = generateFileInfo(config.getRepoRoot(),relativePath);
        //System.out.println("analyzing blame "+relativePath);
        BlameParser.aggregateBlameInfo(fileInfo,config);
        //System.out.println("finish blame...");
        if (config.isNeedCheckStyle()) {
            CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
        }
        if (config.isAnnotationOverwrite()) {
            AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo);
        }
        if (!config.getAuthorList().isEmpty() && fileInfo.isAllAuthorsIgnored(config.getAuthorList())){
            return;
        }
        //System.out.println("extracting AST "+relativePath);
        MethodAnalyzer.aggregateMethodInfo(fileInfo,config);
        //System.out.println("finish extracting AST...");
        fileInfos.add(fileInfo);

    }

    private static FileInfo generateFileInfo(String repoRoot, String relativePath){
        FileInfo result = new FileInfo(relativePath);
        File file = new File(repoRoot+'/'+relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                result.getLines().add(new LineInfo(lineNum,line));
                lineNum += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean isReused(String repoRoot, String relativePath){
        File file = new File(repoRoot+'/'+relativePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine==null || firstLine.contains(Constants.REUSED_TAG)) return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
