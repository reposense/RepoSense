package reposense.authorship.analyzer;

import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.authorship.model.CandidateLine;
import reposense.authorship.model.FileDiffInfo;
import reposense.git.GitBlame;
import reposense.git.GitDiff;
import reposense.git.GitLog;
import reposense.git.model.GitBlameLineInfo;
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
    private static final Pattern DIFF_FILE_CHUNK_PATTERN = Pattern.compile("\ndiff --git a/.*\n");
    private static final Pattern FILE_CHANGED_PATTERN =
            Pattern.compile("\n(-){3} a?/(?<preImageFilePath>.*)\n(\\+){3} b?/(?<postImageFilePath>.*)\n");
    private static final String PRE_IMAGE_FILE_PATH_GROUP_NAME = "preImageFilePath";
    private static final String POST_IMAGE_FILE_PATH_GROUP_NAME = "postImageFilePath";
    private static final String FILE_ADDED_SYMBOL = "dev/null";
    private static final Pattern HUNK_PATTERN = Pattern.compile("\n@@ ");
    private static final int LINES_CHANGED_HEADER_INDEX = 0;
    private static final Pattern STARTING_LINE_NUMBER_PATTERN =
            Pattern.compile("-(?<preImageStartLine>\\d+),?\\d* \\+\\d+,?\\d* @@");
    private static final String PREIMAGE_START_LINE_GROUP_NAME = "preImageStartLine";
    private static final String MATCH_GROUP_FAIL_MESSAGE_FORMAT = "Failed to match the %s group for:\n%s";
    private static final String ADDED_LINE_SYMBOL = "+";
    private static final String DELETED_LINE_SYMBOL = "-";

    private static final ConcurrentHashMap<String, String[]> GIT_LOG_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ArrayList<FileDiffInfo>> GIT_DIFF_CACHE = new ConcurrentHashMap<>();

    /**
     * Analyzes the authorship of {@code lineContent} in {@code filePath} based on {@code originalityThreshold}.
     * Returns {@code true} if {@code currentAuthor} should be assigned full credit, {@code false} otherwise.
     */
    public static boolean analyzeAuthorship(RepoConfiguration config, String filePath, String lineContent,
            String commitHash, Author currentAuthor, double originalityThreshold) {
        // Empty lines are ignored and given full credit
        if (lineContent.isEmpty()) {
            return true;
        }

        Optional<CandidateLine> deletedLineOptional = getDeletedLineWithLowestOriginality(config, filePath, lineContent,
                commitHash);

        // Give full credit if there are no deleted lines found
        if (!deletedLineOptional.isPresent()) {
            return true;
        }

        CandidateLine deletedLine = deletedLineOptional.get();

        // Give full credit if deleted line's originality score exceeds the originality threshold
        if (deletedLine.getOriginalityScore() > originalityThreshold) {
            return true;
        }

        GitBlameLineInfo deletedLineInfo = GitBlame.blameLine(config.getRepoRoot(), deletedLine.getGitBlameCommitHash(),
                deletedLine.getFilePath(), deletedLine.getLineNumber());
        Author previousAuthor = config.getAuthor(deletedLineInfo.getAuthorName(), deletedLineInfo.getAuthorEmail());
        long sinceDateInMilliseconds = ZonedDateTime.of(config.getSinceDate(), config.getZoneId()).toEpochSecond();

        // Give full credit if author is unknown, is before since date, is in ignored list, or is an ignored file
        if (previousAuthor.equals(Author.UNKNOWN_AUTHOR)
                || deletedLineInfo.getTimestampMilliseconds() < sinceDateInMilliseconds
                || CommitHash.isInsideCommitList(deletedLineInfo.getCommitHash(), config.getIgnoreCommitList())
                || previousAuthor.isIgnoringFile(Paths.get(deletedLine.getFilePath()))) {
            return true;
        }

        // Give partial credit if currentAuthor is not the author of the previous version
        if (!currentAuthor.equals(previousAuthor)) {
            return false;
        }

        // Check the previous version as currentAuthor is the same as author of the previous version
        return analyzeAuthorship(config, deletedLine.getFilePath(), deletedLine.getLineContent(),
                deletedLineInfo.getCommitHash(), previousAuthor, originalityThreshold);
    }

    /**
     * Returns the deleted line in {@code commitHash} that has the lowest originality with {@code lineContent}.
     */
    private static Optional<CandidateLine> getDeletedLineWithLowestOriginality(RepoConfiguration config,
            String filePath, String lineContent, String commitHash) {
        CandidateLine lowestOriginalityLine = null;

        String gitLogCacheKey = config.getRepoRoot() + commitHash;
        String[] parentCommits;
        if (GIT_LOG_CACHE.containsKey(gitLogCacheKey)) {
            parentCommits = GIT_LOG_CACHE.get(gitLogCacheKey);
        } else {
            String gitLogResults = GitLog.getParentCommits(config.getRepoRoot(), commitHash);
            parentCommits = StringsUtil.SPACE.split(gitLogResults);
            GIT_LOG_CACHE.put(gitLogCacheKey, parentCommits);
        }

        for (String parentCommit : parentCommits) {
            String gitDiffCacheKey = config.getRepoRoot() + parentCommit + commitHash;
            ArrayList<FileDiffInfo> fileDiffInfoList;

            if (GIT_DIFF_CACHE.containsKey(gitDiffCacheKey)) {
                fileDiffInfoList = GIT_DIFF_CACHE.get(gitDiffCacheKey);
            } else {
                fileDiffInfoList = getFileDiffInfoList(config, parentCommit, commitHash);
                GIT_DIFF_CACHE.put(gitDiffCacheKey, fileDiffInfoList);
            }

            for (FileDiffInfo fileDiffInfo : fileDiffInfoList) {
                // If file name does not match
                if (!fileDiffInfo.getPostImageFilePath().equals(filePath)) {
                    continue;
                }

                CandidateLine candidateLine = getDeletedLineWithLowestOriginalityInDiff(
                        fileDiffInfo.getFileDiffResult(), lineContent, parentCommit,
                        fileDiffInfo.getPreImageFilePath());
                if (candidateLine == null) {
                    continue;
                }

                if (lowestOriginalityLine == null
                        || candidateLine.getOriginalityScore() < lowestOriginalityLine.getOriginalityScore()) {
                    lowestOriginalityLine = candidateLine;
                }
            }
        }

        return Optional.ofNullable(lowestOriginalityLine);
    }

    private static ArrayList<FileDiffInfo> getFileDiffInfoList(RepoConfiguration config, String parentCommit,
            String commitHash) {
        ArrayList<FileDiffInfo> fileDiffInfoList = new ArrayList<>();

        // Generate diff between commit and parent commit
        String gitDiffResult = GitDiff.diffCommits(config.getRepoRoot(), parentCommit, commitHash);
        String[] fileDiffResults = DIFF_FILE_CHUNK_PATTERN.split(gitDiffResult);

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

            fileDiffInfoList.add(new FileDiffInfo(fileDiffResult, preImageFilePath, postImageFilePath));
        }

        return fileDiffInfoList;
    }

    /**
     * Returns the deleted line in {@code fileDiffResult} that has the lowest originality with {@code lineContent}.
     */
    private static CandidateLine getDeletedLineWithLowestOriginalityInDiff(String fileDiffResult, String lineContent,
            String commitHash, String filePath) {
        CandidateLine lowestOriginalityLine = null;

        String[] hunks = HUNK_PATTERN.split(fileDiffResult);

        // skip the diff header, index starts from 1
        for (int index = 1; index < hunks.length; index++) {
            String hunk = hunks[index];

            // skip hunk if lines added in the hunk does not include lineContent
            if (!hunk.contains(ADDED_LINE_SYMBOL + lineContent)) {
                continue;
            }

            String[] linesChanged = StringsUtil.NEWLINE.split(hunk);
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
}
