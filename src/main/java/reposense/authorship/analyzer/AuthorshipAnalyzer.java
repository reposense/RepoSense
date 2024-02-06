package reposense.authorship.analyzer;

import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.authorship.model.CandidateLine;
import reposense.authorship.model.GitBlameLineInfo;
import reposense.git.GitBlame;
import reposense.git.GitDiff;
import reposense.git.GitLog;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.StringsUtil;

/**
 * Analyzes a line to find out if the author should be assigned partial or full credit.
 */
public class AuthorshipAnalyzer {
    private static final Logger logger = LogsManager.getLogger(AuthorshipAnalyzer.class);

    private static final double ORIGINALITY_THRESHOLD = 0.51;

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git a/.*\n";
    private static final Pattern FILE_CHANGED_PATTERN =
            Pattern.compile("\n(-){3} a?/(?<preImageFilePath>.*)\n(\\+){3} b?/(?<postImageFilePath>.*)\n");
    private static final String PRE_IMAGE_FILE_PATH_GROUP_NAME = "preImageFilePath";
    private static final String POST_IMAGE_FILE_PATH_GROUP_NAME = "postImageFilePath";
    private static final String FILE_ADDED_SYMBOL = "dev/null";
    private static final String HUNK_SEPARATOR = "\n@@ ";
    private static final int LINES_CHANGED_HEADER_INDEX = 0;
    private static final Pattern STARTING_LINE_NUMBER_PATTERN =
            Pattern.compile("-(?<preImageStartLine>\\d+),?\\d* \\+\\d+,?\\d* @@");
    private static final String PREIMAGE_START_LINE_GROUP_NAME = "preImageStartLine";
    private static final String MATCH_GROUP_FAIL_MESSAGE_FORMAT = "Failed to match the %s group for:\n%s";
    private static final int AUTHOR_NAME_OFFSET = "author ".length();
    private static final int AUTHOR_EMAIL_OFFSET = "author-mail ".length();
    private static final int FULL_COMMIT_HASH_LENGTH = 40;
    private static final int COMMIT_TIME_OFFSET = "committer-time ".length();
    private static final String ADDED_LINE_SYMBOL = "+";
    private static final String DELETED_LINE_SYMBOL = "-";

    private static final ConcurrentHashMap<String, String[]> GIT_LOG_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ArrayList<ArrayList<String>>> GIT_DIFF_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Analyzes the authorship of {@code lineContent} in {@code filePath}.
     * Returns {@code true} if {@code currentAuthor} should be assigned full credit, {@code false} otherwise.
     */
    public static boolean analyzeAuthorship(RepoConfiguration config, String filePath, String lineContent,
            String commitHash, Author currentAuthor) {
        // Empty lines are ignored and given full credit
        if (lineContent.isEmpty()) {
            return true;
        }

        CandidateLine deletedLine = getDeletedLineWithLowestOriginality(config, filePath, lineContent, commitHash);

        // Give full credit if there are no deleted lines found or deleted line is more than originality threshold
        if (deletedLine == null || deletedLine.getOriginalityScore() > ORIGINALITY_THRESHOLD) {
            return true;
        }

        GitBlameLineInfo deletedLineInfo = getGitBlameLineInfo(config, deletedLine);
        long sinceDateInMilliseconds = ZonedDateTime.of(config.getSinceDate(), config.getZoneId()).toEpochSecond();

        // Give full credit if author is unknown, is before since date, is in ignored list, or is an ignored file
        if (deletedLineInfo.getAuthor().equals(Author.UNKNOWN_AUTHOR)
                || deletedLineInfo.getTimestampMilliseconds() < sinceDateInMilliseconds
                || CommitHash.isInsideCommitList(deletedLineInfo.getCommitHash(), config.getIgnoreCommitList())
                || deletedLineInfo.getAuthor().isIgnoringFile(Paths.get(deletedLine.getFilePath()))) {
            return true;
        }

        // Give partial credit if currentAuthor is not the author of the previous version
        if (!currentAuthor.equals(deletedLineInfo.getAuthor())) {
            return false;
        }

        // Check the previous version as currentAuthor is the same as author of the previous version
        return analyzeAuthorship(config, deletedLine.getFilePath(), deletedLine.getLineContent(),
                deletedLineInfo.getCommitHash(), deletedLineInfo.getAuthor());
    }

    /**
     * Returns the deleted line in {@code commitHash} that has the lowest originality with {@code lineContent}.
     */
    private static CandidateLine getDeletedLineWithLowestOriginality(RepoConfiguration config, String filePath,
            String lineContent, String commitHash) {
        CandidateLine lowestOriginalityLine = null;

        String gitLogCacheKey = config.getRepoRoot() + commitHash;
        String[] parentCommits;
        if (GIT_LOG_CACHE.containsKey(gitLogCacheKey)) {
            parentCommits = GIT_LOG_CACHE.get(gitLogCacheKey);
        } else {
            String gitLogResults = GitLog.getParentCommits(config.getRepoRoot(), commitHash);
            parentCommits = gitLogResults.split(" ");
            GIT_LOG_CACHE.put(gitLogCacheKey, parentCommits);
        }

        for (String parentCommit : parentCommits) {
            String gitDiffCacheKey = config.getRepoRoot() + parentCommit + commitHash;
            ArrayList<String> fileDiffResultList;
            ArrayList<String> preImageFilePathList;
            ArrayList<String> postImageFilePathList;

            if (GIT_DIFF_CACHE.containsKey(gitDiffCacheKey)) {
                ArrayList<ArrayList<String>> cacheResults = GIT_DIFF_CACHE.get(gitDiffCacheKey);
                fileDiffResultList = cacheResults.get(0);
                preImageFilePathList = cacheResults.get(1);
                postImageFilePathList = cacheResults.get(2);
            } else {
                fileDiffResultList = new ArrayList<>();
                preImageFilePathList = new ArrayList<>();
                postImageFilePathList = new ArrayList<>();

                // Generate diff between commit and parent commit
                String gitDiffResult = GitDiff.diffCommits(config.getRepoRoot(), parentCommit, commitHash);
                String[] fileDiffResults = gitDiffResult.split(DIFF_FILE_CHUNK_SEPARATOR);

                for (String fileDiffResult : fileDiffResults) {
                    Matcher filePathMatcher = FILE_CHANGED_PATTERN.matcher(fileDiffResult);
                    if (!filePathMatcher.find()) {
                        continue;
                    }

                    // If file was added in the commit
                    String preImageFilePath = filePathMatcher.group(PRE_IMAGE_FILE_PATH_GROUP_NAME);
                    if (preImageFilePath.equals(FILE_ADDED_SYMBOL)) {
                        continue;
                    }

                    String postImageFilePath = filePathMatcher.group(POST_IMAGE_FILE_PATH_GROUP_NAME);

                    fileDiffResultList.add(fileDiffResult);
                    preImageFilePathList.add(preImageFilePath);
                    postImageFilePathList.add(postImageFilePath);
                }

                ArrayList<ArrayList<String>> cacheResults = new ArrayList<>();
                cacheResults.add(fileDiffResultList);
                cacheResults.add(preImageFilePathList);
                cacheResults.add(postImageFilePathList);

                GIT_DIFF_CACHE.put(gitDiffCacheKey, cacheResults);
            }

            for (int i = 0; i < fileDiffResultList.size(); i++) {
                // If file name does not match
                if (!postImageFilePathList.get(i).equals(filePath)) {
                    continue;
                }

                CandidateLine candidateLine = getDeletedLineWithLowestOriginalityInDiff(
                        fileDiffResultList.get(i), lineContent, parentCommit, preImageFilePathList.get(i));
                if (candidateLine == null) {
                    continue;
                }

                if (lowestOriginalityLine == null
                        || candidateLine.getOriginalityScore() < lowestOriginalityLine.getOriginalityScore()) {
                    lowestOriginalityLine = candidateLine;
                }
            }
        }

        return lowestOriginalityLine;
    }

    /**
     * Returns the deleted line in {@code fileDiffResult} that has the lowest originality with {@code lineContent}.
     */
    private static CandidateLine getDeletedLineWithLowestOriginalityInDiff(String fileDiffResult, String lineContent,
            String commitHash, String filePath) {
        CandidateLine lowestOriginalityLine = null;

        String[] hunks = fileDiffResult.split(HUNK_SEPARATOR);

        // skip the diff header, index starts from 1
        for (int index = 1; index < hunks.length; index++) {
            String hunk = hunks[index];

            // skip hunk if lines added in the hunk does not include lineContent
            if (!hunk.contains(ADDED_LINE_SYMBOL + lineContent)) {
                continue;
            }

            String[] linesChanged = hunk.split("\n");
            int currentPreImageLineNumber = getPreImageStartingLineNumber(linesChanged[LINES_CHANGED_HEADER_INDEX]);

            // skip the lines changed header, index starts from 1
            for (int lineIndex = 1; lineIndex < linesChanged.length; lineIndex++) {
                String lineChanged = linesChanged[lineIndex];

                if (lineChanged.startsWith(DELETED_LINE_SYMBOL)) {
                    String deletedLineContent = lineChanged.substring(DELETED_LINE_SYMBOL.length());
                    double lowestOriginalityScore = lowestOriginalityLine == null
                            ? Integer.MAX_VALUE
                            : lowestOriginalityLine.getOriginalityScore();
                    double originalityScore = computeOriginalityScore(lineContent, deletedLineContent,
                            lowestOriginalityScore);

                    if (lowestOriginalityLine == null
                            || originalityScore < lowestOriginalityLine.getOriginalityScore()) {
                        lowestOriginalityLine = new CandidateLine(
                                currentPreImageLineNumber, deletedLineContent, filePath, commitHash,
                                originalityScore);
                    }
                }

                if (!lineChanged.startsWith(ADDED_LINE_SYMBOL)) {
                    currentPreImageLineNumber++;
                }
            }
        }

        return lowestOriginalityLine;
    }

    /**
     * Returns the pre-image starting line number by matching the pattern inside {@code linesChangedHeader}.
     *
     * @throws AssertionError if lines changed header matcher failed to find anything.
     */
    private static int getPreImageStartingLineNumber(String linesChangedHeader) {
        Matcher linesChangedHeaderMatcher = STARTING_LINE_NUMBER_PATTERN.matcher(linesChangedHeader);

        if (!linesChangedHeaderMatcher.find()) {
            logger.severe(
                    String.format(MATCH_GROUP_FAIL_MESSAGE_FORMAT, PREIMAGE_START_LINE_GROUP_NAME, linesChangedHeader));
            throw new AssertionError(
                    "Should not have error matching line number pattern inside lines changed header!");
        }

        return Integer.parseInt(linesChangedHeaderMatcher.group(PREIMAGE_START_LINE_GROUP_NAME));
    }

    /**
     * Calculates the originality score of {@code s} with {@code baseString}.
     */
    private static double computeOriginalityScore(String s, String baseString, double limit) {
        double levenshteinDistance = StringsUtil.getLevenshteinDistance(s, baseString, limit * baseString.length());
        return levenshteinDistance / baseString.length();
    }

    /**
     * Returns the git blame line info for {@code line}.
     */
    private static GitBlameLineInfo getGitBlameLineInfo(RepoConfiguration config, CandidateLine line) {
        String blameResults = GitBlame.blameLine(
                config.getRepoRoot(), line.getGitBlameCommitHash(), line.getFilePath(), line.getLineNumber());
        String[] blameResultLines = blameResults.split("\n");

        String commitHash = blameResultLines[0].substring(0, FULL_COMMIT_HASH_LENGTH);
        String authorName = blameResultLines[1].substring(AUTHOR_NAME_OFFSET);
        String authorEmail = blameResultLines[2].substring(AUTHOR_EMAIL_OFFSET).replaceAll("[<>]", "");
        long timestampMilliseconds = Long.parseLong(blameResultLines[5].substring(COMMIT_TIME_OFFSET));
        Author author = config.getAuthor(authorName, authorEmail);

        return new GitBlameLineInfo(commitHash, author, timestampMilliseconds);
    }
}
