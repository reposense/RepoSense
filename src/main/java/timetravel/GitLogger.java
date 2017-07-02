package timetravel;

import dataObject.Author;
import dataObject.CommitInfo;
import system.CommandRunner;
import util.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class GitLogger {
    public static ArrayList<CommitInfo> getCommits(String repoRoot){
        String raw = CommandRunner.gitLog(repoRoot);
        return parseCommitInfo(raw);
    }

    public static ArrayList<CommitInfo> getCommits(String repoRoot, int last){
        String raw = CommandRunner.gitLog(repoRoot, last);
        return parseCommitInfo(raw);
    }

    private static ArrayList<CommitInfo> parseCommitInfo(String rawResult){
        ArrayList<CommitInfo> result = new ArrayList<CommitInfo>();
        for (String line:rawResult.split("\n")){
            result.add(parseRawLine(line));
        }
        return result;
    }

    private static CommitInfo parseRawLine(String line){
        String[] elements = line.split(Constants.LOG_SPLITTER);
        String hash = elements[0];
        Author author = new Author(elements[1]);
        Date date = null;
        try {
            date = Constants.ISO_FORMAT.parse(elements[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String message = elements[3];
        return new CommitInfo(author,hash,date,message);
    }
}
