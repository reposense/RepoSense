package analyzer;


import dataObject.Author;
import dataObject.Configuration;
import dataObject.FileInfo;
import dataObject.Line;
import system.CommandRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class FileAnalyzer {

    private static final String[] ignoredList = new String[] {"org/",".git",".log", ".class",".classpath","bin/",".gitignore",".DS_Store",".project"};

    public static void analyzeAllFiles(Configuration config, File directory, ArrayList<FileInfo> result){

        for (File file:directory.listFiles()){

            String relativePath = file.getPath().replaceFirst(config.getRepoRoot(),"");
            if (shouldIgnore(relativePath)) continue;
            if (file.isDirectory()){
                analyzeAllFiles(config, file,result);
            }else{
                if (!relativePath.endsWith(".java")) continue;
                FileInfo fileInfo = generateFileInfo(config.getRepoRoot(),relativePath);
                BlameParser.aggregateBlameInfo(fileInfo,config.getRepoRoot());
                if (config.isNeedCheckStyle()) {
                    CheckStyleParser.aggregateStyleIssue(fileInfo, config.getRepoRoot());
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
                result.getLines().add(new Line(lineNum,line));
                lineNum += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static boolean shouldIgnore(String name) {
        for (String element : ignoredList) {
            if (name.contains(element)) return true;
        }
        return false;
    }

}
