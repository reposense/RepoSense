package reposense.commits;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Analyzes commit information found in the git log.
 */
public class CommitInfoAnalyzer {
    public static final DateFormat GIT_STRICT_ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final Logger logger = LogsManager.getLogger(CommitInfoAnalyzer.class);
    private static final String MESSAGE_START_ANALYZING_COMMIT_INFO = "Analyzing commits info for %s (%s)...";

    private static final String LOG_SPLITTER = "\\|\\n\\|";
    private static final String REF_SPLITTER = ",\\s";
    private static final String TAG_PREFIX = "tag:";

    private static final int COMMIT_HASH_INDEX = 0;
    private static final int AUTHOR_INDEX = 1;
    private static final int EMAIL_INDEX = 2;
    private static final int DATE_INDEX = 3;
    private static final int MESSAGE_TITLE_INDEX = 4;
    private static final int MESSAGE_BODY_INDEX = 5;
    private static final int REF_NAME_INDEX = 6;

    private static final Pattern INSERTION_PATTERN = Pattern.compile("([0-9]+) insertion");
    private static final Pattern DELETION_PATTERN = Pattern.compile("([0-9]+) deletion");

    private static final Pattern MESSAGEBODY_LEADING_PATTERN = Pattern.compile("^ {4}", Pattern.MULTILINE);

    /**
     * Analyzes each {@code CommitInfo} in {@code commitInfos} and returns a list of {@code CommitResult} that is not
     * specified to be ignored or the author is inside {@code config}.
     */
    public static List<CommitResult> analyzeCommits(List<CommitInfo> commitInfos, RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_ANALYZING_COMMIT_INFO, config.getLocation(), config.getBranch()));

        return commitInfos.stream()
                .map(commitInfo -> analyzeCommit(commitInfo, config))
                .filter(commitResult -> !commitResult.getAuthor().equals(Author.UNKNOWN_AUTHOR)
                        && !CommitHash.isInsideCommitList(commitResult.getHash(), config.getIgnoreCommitList()))
                .sorted(Comparator.comparing(CommitResult::getTime))
                .collect(Collectors.toList());
    }

    /**
     * Extracts the relevant data from {@code commitInfo} into a {@code CommitResult}.
     */
    public static CommitResult analyzeCommit(CommitInfo commitInfo, RepoConfiguration config) {
        String infoLine = commitInfo.getInfoLine();
        String statLine = commitInfo.getStatLine();

        String[] elements = infoLine.split(LOG_SPLITTER, 7);
        String hash = elements[COMMIT_HASH_INDEX];
        Author author = config.getAuthor(elements[AUTHOR_INDEX], elements[EMAIL_INDEX]);

        Date date = null;
        try {
            date = GIT_STRICT_ISO_DATE_FORMAT.parse(elements[DATE_INDEX]);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, "Unable to parse the date from git log result for commit.", pe);
        }

        String messageTitle = (elements.length > MESSAGE_TITLE_INDEX) ? elements[MESSAGE_TITLE_INDEX] : "";
        String messageBody = (elements.length > MESSAGE_BODY_INDEX)
                ? getCommitMessageBody(elements[MESSAGE_BODY_INDEX]) : "";

        String[] refs = elements[REF_NAME_INDEX].split(REF_SPLITTER);
        String[] tags = Arrays.stream(refs).filter(ref -> ref.contains(TAG_PREFIX)).toArray(String[]::new);
        if (tags.length == 0) {
            tags = null; // set to null so it won't be converted to json
        } else {
            extractTagNames(tags);
        }

        int insertion = getInsertion(statLine);
        int deletion = getDeletion(statLine);
        return new CommitResult(author, hash, date, messageTitle, messageBody, tags, insertion, deletion);
    }

    /**
     * Extracts the tag names in {@code tags}.
     */
    private static void extractTagNames(String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].substring(tags[i].lastIndexOf(TAG_PREFIX) + TAG_PREFIX.length()).trim();
        }
    }

    private static String getCommitMessageBody(String raw) {
        Matcher matcher = MESSAGEBODY_LEADING_PATTERN.matcher(raw);
        return matcher.replaceAll("");
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
