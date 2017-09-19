package git;

import dataObject.Author;
import dataObject.CommitInfo;
import system.CommandRunner;
import util.Constants;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class GitLogger {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("([0-9]+) insertions");
    private static final Pattern DELETION_PATTERN = Pattern.compile("([0-9]+) deletions");


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
        String[] rawLines= rawResult.split("\n");
        for (int i=0;i<rawLines.length;i++){
            result.add(parseRawLine(rawLines[i],rawLines[++i]));
        }
        return result;
    }

    private static CommitInfo parseRawLine(String infoLine, String statLine){
        String[] elements = infoLine.split(Constants.LOG_SPLITTER);
        String hash = elements[0];
        Author author = new Author(elements[1]);
        Date date = null;
        try {
            date = Constants.ISO_FORMAT.parse(elements[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String message = elements[3];
        int insertion = getInsertion(statLine);
        int deletion = getDeletion(statLine);
        return new CommitInfo(author,hash,date,message,insertion,deletion);
    }

    private static int getInsertion(String raw){
        return getNumberWithPattern(raw, INSERTION_PATTERN);
    }

    private static int getDeletion(String raw){
        return getNumberWithPattern(raw, DELETION_PATTERN);
    }

    private static int getNumberWithPattern(String raw, Pattern p){
        Matcher m = p.matcher(raw);
        if (m.find()){
            return (Integer.parseInt(m.group(1)));
        } else {
            return 0;
        }
    }
}
