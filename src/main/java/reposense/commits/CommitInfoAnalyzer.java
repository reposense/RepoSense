package reposense.commits;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.commits.model.ContributionPair;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Analyzes commit information found in the git log.
 */
public class CommitInfoAnalyzer {
    public static final DateFormat GIT_STRICT_ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static final String TAB_SPLITTER = "\t";
    private static final String MOVED_FILE_INDICATION = "=> ";
    private static final String BINARY_FILE_CONTRIBUTION = "-";
    private static final int STAT_ADDITION_INDEX = 0;
    private static final int STAT_DELETION_INDEX = 1;
    private static final int STAT_FILE_PATH_INDEX = 2;

    private static final Logger logger = LogsManager.getLogger(CommitInfoAnalyzer.class);
    private static final String MESSAGE_START_ANALYZING_COMMIT_INFO = "Analyzing commits info for %s (%s)...";

    private static final String LOG_SPLITTER = "\\|\\n\\|";
    private static final String REF_SPLITTER = ",\\s";
    private static final String NEW_LINE_SPLITTER = "\\n";
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
                .distinct()
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

        String[] refs = (elements.length > REF_NAME_INDEX)
                ? elements[REF_NAME_INDEX].split(REF_SPLITTER)
                : new String[]{""};
        String[] tags = Arrays.stream(refs).filter(ref -> ref.contains(TAG_PREFIX)).toArray(String[]::new);
        if (tags.length == 0) {
            tags = null; // set to null so it won't be converted to json
        } else {
            extractTagNames(tags);
        }

        if (statLine.isEmpty()) { // empty commit, no files changed
            return new CommitResult(author, hash, date, messageTitle, messageBody, tags);
        }

        String[] statInfos = statLine.split(NEW_LINE_SPLITTER);
        String[] fileTypeContributions = Arrays.copyOfRange(statInfos, 0, statInfos.length - 1);
        Map<FileType, ContributionPair> fileTypeAndContributionMap =
                getFileTypesAndContribution(fileTypeContributions, config);

        return new CommitResult(author, hash, date, messageTitle, messageBody, tags, fileTypeAndContributionMap);
    }

    /**
     * Returns the number of lines added and deleted for the specified file types in {@code config}.
     */
    private static Map<FileType, ContributionPair> getFileTypesAndContribution(String[] filePathContributions,
            RepoConfiguration config) {
        Map<FileType, ContributionPair> fileTypesAndContributionMap = new HashMap<>();
        for (String filePathContribution : filePathContributions) {
            String[] infos = filePathContribution.split(TAB_SPLITTER);

            if (isBinaryContribution(infos[STAT_ADDITION_INDEX], infos[STAT_DELETION_INDEX])) {
                continue; // skip binary file contributions
            }

            int addition = Integer.parseInt(infos[STAT_ADDITION_INDEX]);
            int deletion = Integer.parseInt(infos[STAT_DELETION_INDEX]);
            String filePath = extractFilePath(infos[STAT_FILE_PATH_INDEX]);
            FileType fileType = config.getFileType(filePath);

            if (!fileTypesAndContributionMap.containsKey(fileType)) {
                fileTypesAndContributionMap.put(fileType, new ContributionPair());
            }

            ContributionPair contributionPair = fileTypesAndContributionMap.get(fileType);
            contributionPair.addInsertions(addition);
            contributionPair.addDeletions(deletion);
        }
        return fileTypesAndContributionMap;
    }

    /**
     * Extracts the correct file path from the unprocessed git log {@code filePath}
     */
    private static String extractFilePath(String filePath) {
        if (!filePath.contains(MOVED_FILE_INDICATION)) {
            return filePath;
        }
        // moved file has the format: fileA => newPosition/fileA
        String filteredFilePath =
                filePath.substring(filePath.indexOf(MOVED_FILE_INDICATION) + MOVED_FILE_INDICATION.length());
        // Removes the trailing '}' character from the file name, as renamed file names have ending '}' character.
        filteredFilePath = filteredFilePath.replaceAll("}$", "");
        return filteredFilePath;
    }

    /**
     * Detects binary file contribution based on the git log {@code addition} and {@code deletion}.
     */
    private static boolean isBinaryContribution(String addition, String deletion) {
        // git log returns "-" for binary file additions and deletions
        return addition.equals(BINARY_FILE_CONTRIBUTION) && deletion.equals(BINARY_FILE_CONTRIBUTION);
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
}
