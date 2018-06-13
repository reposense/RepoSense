package reposense.analyzer;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.dataobject.Author;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.CommitResult;
import reposense.util.Constants;

public class CommitInfoAnalyzer {

    private static final Pattern INSERTION_PATTERN = Pattern.compile("([0-9]+) insertion");
    private static final Pattern DELETION_PATTERN = Pattern.compile("([0-9]+) deletion");

    public static CommitResult analyzeCommit(CommitInfo commitInfo, Map<String, Author> authorAliasMap) {
        String infoLine = commitInfo.getInfoLine();
        String statLine = commitInfo.getStatLine();

        String[] elements = infoLine.split(Constants.LOG_SPLITTER);
        String hash = elements[0];
        Author author = authorAliasMap.get(elements[1]);
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
        return new CommitResult(author, hash, date, message, insertion, deletion);
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
