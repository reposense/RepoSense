package reposense.commits;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
import reposense.system.LogsManager;

/**
 * Analyzes commit information found in the git log.
 */
public class CommitInfoAnalyzer {
    private static final Logger logger = LogsManager.getLogger(CommitInfoAnalyzer.class);

    private static final DateFormat GIT_ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_SPLITTER = "\\|";

    private static final int COMMIT_HASH_INDEX = 0;
    private static final int AUTHOR_INDEX = 1;
    private static final int DATE_INDEX = 2;
    private static final int MESSAGE_INDEX = 3;

    private static final Pattern INSERTION_PATTERN = Pattern.compile("([0-9]+) insertion");
    private static final Pattern DELETION_PATTERN = Pattern.compile("([0-9]+) deletion");

    /**
     * Extracts the relevant data from {@code commitInfo} into a {@code CommitResult}.
     */
    public static CommitResult analyzeCommit(CommitInfo commitInfo, Map<String, Author> authorAliasMap) {
        String infoLine = commitInfo.getInfoLine();
        String statLine = commitInfo.getStatLine();

        String[] elements = infoLine.split(LOG_SPLITTER);
        String hash = elements[COMMIT_HASH_INDEX];
        Author author = authorAliasMap.getOrDefault(elements[AUTHOR_INDEX], new Author(Author.UNKNOWN_AUTHOR_GIT_ID));

        Date date = null;
        try {
            date = GIT_ISO_FORMAT.parse(elements[DATE_INDEX]);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, "Unable to parse the date from git log result for commit.", pe);
        }

        String message = elements[MESSAGE_INDEX];
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
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}
