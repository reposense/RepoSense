package analyzer;


import data.Line;
import system.CommandRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class Blamer {
    private String repoRoot;
    private final String[] ignoredList = new String[] {".git",".log", ".class",".classpath","bin/",".gitignore",".DS_Store",".project"};

    public Blamer(String repoRoot){

        this.repoRoot = addFinalSlash(repoRoot);
    }

    public HashMap<String, ArrayList<Line>> getAllFilesBlame(){
        HashMap<String, ArrayList<Line>> result = new HashMap<String, ArrayList<Line>>();
        getRecursiveBlame(new File(repoRoot),result);
        return result;
    }

    private void getRecursiveBlame(File directory,HashMap<String, ArrayList<Line>> result){
        for (File file:directory.listFiles()){
            if (isIgnoredType(file.getAbsolutePath())) continue;
            if (file.isDirectory()){
                getRecursiveBlame(file,result);
            }else{
                String path = file.getAbsolutePath().replace(repoRoot,"");
                result.put(path,blameSingleFile(path));
            }
        }

    }

    public ArrayList<Line> blameSingleFile(String fileDirectory){

        ArrayList<Line> result = new ArrayList<Line>();
        String raw = CommandRunner.blameRaw(repoRoot, fileDirectory);
        if (!raw.contains("\n")) return result;

        String[] rawLines = raw.split("\n");
        for (int i =0;i<rawLines.length;i++){
            String authorName = getAuthorNameFromSingleLine(rawLines[i]);
            result.add(new Line(i,authorName));
        }
        return result;
    }

    private String getAuthorNameFromSingleLine(String line) {
        return line.substring(line.indexOf(" ") + 1);
    }

    private boolean isIgnoredType(String name){
        for (String element: ignoredList){
            if (name.contains(element)) return true;
        }
        return false;
    }

    private String addFinalSlash(String fileName){
        if (fileName.endsWith("/")){
            return fileName;
        }else{
            return fileName + "/";
        }
    }
}
