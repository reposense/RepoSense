package analyzer;


import data.Author;
import data.CommitInfo;
import data.FileInfo;
import data.Line;
import timetravel.GitChecker;
import timetravel.GitLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class Analyzer {

    private final String[] ignoredList = new String[] {"org/",".git",".log", ".class",".classpath","bin/",".gitignore",".DS_Store",".project"};

    private String repoRoot;

    public Analyzer(String repoRoot){

        this.repoRoot = addFinalSlash(repoRoot);
    }

    public HashMap<CommitInfo, ArrayList<FileInfo>> analyzeRecentNCommit(int recent){
        ArrayList<CommitInfo> allCommits = GitLogger.getAllCommits(repoRoot);
        ArrayList<CommitInfo> processingCommits;
        if ( recent >= allCommits.size()){
            processingCommits = allCommits;
        }else{
            processingCommits = new ArrayList<CommitInfo>(allCommits.subList(allCommits.size()-recent, allCommits.size()));
        }
        HashMap<CommitInfo, ArrayList<FileInfo>> result = new HashMap<CommitInfo, ArrayList<FileInfo>>();
        for (CommitInfo commitInfo:processingCommits){
            GitChecker.checkOutToCommit(repoRoot,commitInfo);
            result.put(commitInfo,analyzeAllFile());
        }
        return result;

    }

    private static HashMap<Author, Integer> getAuthorIssueCount(ArrayList<FileInfo> files){
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files){
            for (Line line:fileInfo.getLines()){
                if (line.hasIssue()){

                    Author author = line.getAuthor();
                    if (!result.containsKey(author)){
                        result.put(author,0);
                    }
                    int issueCount = result.get(author);
                    issueCount += line.getIssues().size();
                    result.put(author,issueCount);
                }
            }
        }
        return result;
    }

    private ArrayList<FileInfo> analyzeAllFile(){
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
                if (!relativePath.endsWith(".java")) continue;
                FileInfo fileInfo = BlameParser.blameSingleFile(repoRoot,relativePath);
                CheckStyleParser.aggregateStyleIssue(fileInfo,repoRoot);
                MethodAnalyzer.aggregateMethodInfo(fileInfo,repoRoot);
                result.add(fileInfo);
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
