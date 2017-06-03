package analyzer;


import data.FileInfo;
import data.Line;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class Analyzer {

    private final String[] ignoredList = new String[] {".git",".log", ".class",".classpath","bin/",".gitignore",".DS_Store",".project"};

    private String repoRoot;

    public Analyzer(String repoRoot){

        this.repoRoot = addFinalSlash(repoRoot);
    }

    public ArrayList<FileInfo> analyzeAllFile(){
        ArrayList<FileInfo> result = new ArrayList<FileInfo>();
        recursiveAnalyze(new File(repoRoot),result);
        return result;
    }

    private void recursiveAnalyze(File directory,ArrayList<FileInfo> result){
        for (File file:directory.listFiles()){
            String relativePath = file.getAbsolutePath().replaceFirst(repoRoot,"");
            if (shouldIgnore(relativePath)) continue;
            if (file.isDirectory()){
                recursiveAnalyze(file,result);
            }else{
                result.add(BlameParser.blameSingleFile(repoRoot,relativePath));
            }
        }

    }


    private String addFinalSlash(String fileName){
        if (fileName.endsWith("/")){
            return fileName;
        }else{
            return fileName + "/";
        }
    }

    private boolean shouldIgnore(String name){
        for (String element: ignoredList){
            if (name.contains(element)) return true;
        }
        return false;
    }

}
