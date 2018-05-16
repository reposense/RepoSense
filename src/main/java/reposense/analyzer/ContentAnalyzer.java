package reposense.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.LineInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoInfo;


public class ContentAnalyzer {

    private static HashMap<Author, Integer> getAuthorIssueCount(ArrayList<FileInfo> files, List<Author> authors) {
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (FileInfo fileInfo : files) {
            for (LineInfo line : fileInfo.getLines()) {
                if (line.hasIssue()) {

                    Author author = line.getAuthor();
                    if (!authors.isEmpty() && !authors.contains(author)) {
                        continue;
                    }
                    if (!result.containsKey(author)) {
                        result.put(author, 0);
                    }
                    int issueCount = result.get(author);
                    issueCount += line.getIssues().size();
                    result.put(author, issueCount);
                }
            }
        }
        return result;
    }

    public static HashMap<Author, Integer> getAuthorMethodContributionCount(
            List<FileInfo> files, List<Author> authors) {
        HashMap<Author, Integer> result = new HashMap<Author, Integer>();
        for (Author author : authors) {
            result.put(author, 0);
        }
        for (FileInfo fileInfo : files) {
            for (LineInfo line : fileInfo.getLines()) {
                Author author = line.getAuthor();
                if (!authors.contains(author)) {
                    continue;
                }
                int lineCount = result.get(author);
                lineCount += 1;
                result.put(author, lineCount);
            }
        }
        return result;
    }
}
