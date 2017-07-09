package analyzer;


import dataObject.Author;
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

    public static void analyzeAllFiles(String repoRoot, File directory,ArrayList<FileInfo> result){

        for (File file:directory.listFiles()){

            String relativePath = file.getPath().replaceFirst(repoRoot,"");
            if (shouldIgnore(relativePath)) continue;
            if (file.isDirectory()){
                analyzeAllFiles(repoRoot, file,result);
            }else{
                if (!relativePath.endsWith(".java")) continue;
                FileInfo fileInfo = generateFileInfo(repoRoot,relativePath);
                BlameParser.aggregateBlameInfo(fileInfo,repoRoot);
                CheckStyleParser.aggregateStyleIssue(fileInfo,repoRoot);
                MethodAnalyzer.aggregateMethodInfo(fileInfo,repoRoot);
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
