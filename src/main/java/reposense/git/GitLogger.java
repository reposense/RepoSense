package reposense.git;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.dataobject.Author;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.util.Constants;


public class GitLogger {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("([0-9]+) insertion");
    private static final Pattern DELETION_PATTERN = Pattern.compile("([0-9]+) deletion");

    public static List<CommitInfo> getCommits(RepoConfiguration config) {
        String raw = CommandRunner.gitLog(config.getRepoRoot(), config.getSinceDate(), config.getUntilDate());
        ArrayList<CommitInfo> relevantCommits = parseCommitInfo(raw, config);
        return relevantCommits;
    }

    private static ArrayList<CommitInfo> parseCommitInfo(String rawResult, RepoConfiguration config) {
        ArrayList<CommitInfo> result = new ArrayList<CommitInfo>();
        String[] rawLines = rawResult.split("\n");
        if (rawLines.length < 2) {
            //no log (maybe because no contribution for that file type)
            return result;
        }
        for (int i = 0; i < rawLines.length; i++) {
            CommitInfo commit = parseRawLine(rawLines[i], rawLines[++i], config);
            i++; //to skip the empty line
            if (commit == null) {
                continue;
            }
            result.add(commit);
        }
        Collections.reverse(result);
        return result;
    }

    private static CommitInfo parseRawLine(String infoLine, String statLine, RepoConfiguration config) {
        String[] elements = infoLine.split(Constants.LOG_SPLITTER);
        String hash = elements[0];
        Author author = config.getAuthorAliasMap().get(elements[1]);
        //if the commit is done by someone not being analyzed, skip it.
        if (author == null) {
            return null;
        }
        Date date = null;
        try {
            date = Constants.GIT_ISO_FORMAT.parse(elements[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String message = elements[3];
        int insertion = getInsertion(statLine);
        int deletion = getDeletion(statLine);
        return new CommitInfo(author, hash, date, message, insertion, deletion);
    }

    private static int getInsertion(String raw) {
        return getNumberWithPattern(raw, INSERTION_PATTERN);
    }

    private static int getDeletion(String raw) {
        return getNumberWithPattern(raw, DELETION_PATTERN);
    }

    private static int getNumberWithPattern(String raw, Pattern p) {
        Matcher m = p.matcher(raw);
        if (m.find()) {
            return (Integer.parseInt(m.group(1)));
        } else {
            return 0;
        }
    }
}
