package analyzer;

import dataObject.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by matanghao1 on 3/7/17.
 */
public class CommitAnalyzer {

    public static void aggregateFileInfos(Configuration config, CommitInfo commitInfo){
        ArrayList<FileInfo> result = FileAnalyzer.analyzeAllFiles(config);
        commitInfo.setFileinfos(result);
        commitInfo.setAuthorIssueMap(getAuthorIssueCount(commitInfo.getFileinfos(),config.getIgnoreAuthorList()));
        commitInfo.setAuthorContributionMap(getAuthorMethodContributionCount(commitInfo.getFileinfos(),config.getIgnoreAuthorList()));

    }

    private static HashMap<Author, Integer> getAuthorIssueCount(ArrayList<FileInfo> files, List<Author> ignoredAuthors){
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files){
            for (LineInfo line:fileInfo.getLines()){
                if (line.hasIssue()){

                    Author author = line.getAuthor();
                    if (ignoredAuthors.contains(author)) continue;
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

    private static HashMap<Author, Integer> getAuthorMethodContributionCount(ArrayList<FileInfo> files, List<Author> ignoredAuthors){
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files){
            for (MethodInfo method:fileInfo.getMethodInfos()){

                Author author = method.getOwner();
                if (ignoredAuthors.contains(author)) continue;

                if (!result.containsKey(author)){
                    result.put(author,0);
                }

                int lineCount = result.get(author);
                lineCount += method.getTotalLines();

                result.put(author,lineCount);
            }
        }
        return result;
    }
}
