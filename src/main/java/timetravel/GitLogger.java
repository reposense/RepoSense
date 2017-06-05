package timetravel;

import data.Author;
import data.CommitInfo;
import system.CommandRunner;
import util.Constants;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class GitLogger {
    public static ArrayList<CommitInfo> getAllCommit(String repoRoot){
        String raw = CommandRunner.gitLog(repoRoot);
        ArrayList<CommitInfo> result = new ArrayList<CommitInfo>();
        for (String line:raw.split("\n")){
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
            result.add(new CommitInfo(author,hash,date,message));
        }
        return result;
    }
}
