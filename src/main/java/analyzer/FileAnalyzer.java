package analyzer;


import dataObject.Configuration;
import dataObject.FileInfo;
import dataObject.LineInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class FileAnalyzer {

    public static void analyzeAllFiles(Configuration config, File directory, ArrayList<FileInfo> result){

        for (File file:directory.listFiles()){

            String relativePath = file.getPath().replaceFirst(config.getRepoRoot(),"");
            if (shouldIgnore(relativePath, config.getIgnoreList())) continue;
            if (file.isDirectory()){
                analyzeAllFiles(config, file,result);
            }else{
                if (!relativePath.endsWith(".java")) continue;
                FileInfo fileInfo = generateFileInfo(config.getRepoRoot(),relativePath);
                BlameParser.aggregateBlameInfo(fileInfo,config.getRepoRoot());
                if (config.isNeedCheckStyle()) {
                    CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
                }
                if (config.isAnnotationOverwrite()) {
                    AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo);
                }
                MethodAnalyzer.aggregateMethodInfo(fileInfo,config.getRepoRoot());
                result.add(fileInfo);
            }
        }

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

    private static boolean shouldIgnore(String name, List<String> ignoreList) {
        for (String element : ignoreList) {
            if (name.contains(element)) return true;
        }
        return false;
    }

}
