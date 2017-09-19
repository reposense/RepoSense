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
        commitInfo.setAuthorIssueMap(getAuthorIssueCount(commitInfo.getFileinfos(),config.getAuthorList()));
        commitInfo.setAuthorContributionMap(getAuthorMethodContributionCount(commitInfo.getFileinfos(),config.getAuthorList()));

    }

    private static HashMap<Author, Integer> getAuthorIssueCount(ArrayList<FileInfo> files, List<Author> authors){
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files){
            for (LineInfo line:fileInfo.getLines()){
                if (line.hasIssue()){

                    Author author = line.getAuthor();
                    if (!authors.isEmpty() && !authors.contains(author)) continue;
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

    private static HashMap<Author, Integer> getAuthorMethodContributionCount(ArrayList<FileInfo> files, List<Author> authors){
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files){
            for (LineInfo line:fileInfo.getLines()){

                Author author = line.getAuthor();
                if (!authors.isEmpty() && !authors.contains(author)) continue;

                if (!result.containsKey(author)){
                    result.put(author,0);
                }

                int lineCount = result.get(author);
                lineCount += 1;

                result.put(author,lineCount);
            }
        }
        return result;
    }
}
