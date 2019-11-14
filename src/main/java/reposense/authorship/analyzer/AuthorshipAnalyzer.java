package reposense.authorship.analyzer;

import java.nio.file.Paths;
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

/**
 * Analyzes a line to find out if the author should be assigned partial or full credit.
 */
public class AuthorshipAnalyzer {
    private static final Logger logger = LogsManager.getLogger(AuthorshipAnalyzer.class);

    private static final double SIMILARITY_THRESHOLD = 0.8;

    private static final String DIFF_FILE_CHUNK_SEPARATOR = "\ndiff --git a/.*\n";
    private static final Pattern FILE_CHANGED_PATTERN =
            Pattern.compile("\n(-){3} a?/(?<preImageFilePath>.*)\n(\\+){3} b?/(?<postImageFilePath>.*)\n");
    private static final String PRE_IMAGE_FILE_PATH_GROUP_NAME = "preImageFilePath";
    private static final String POST_IMAGE_FILE_PATH_GROUP_NAME = "postImageFilePath";
    private static final String NONEXISTENT_FILE_SYMBOL = "/dev/null";
    private static final String LINE_CHUNKS_SEPARATOR = "\n@@ ";
    private static final int LINE_CHANGED_HEADER_INDEX = 0;
    private static final Pattern STARTING_LINE_NUMBER_PATTERN = Pattern.compile(
            "-(?<preImageStartLine>\\d+),?\\d* \\+\\d+,?\\d* @@");
    private static final String PREIMAGE_START_LINE_GROUP_NAME = "preImageStartLine";
    private static final String MATCH_GROUP_FAIL_MESSAGE_FORMAT = "Failed to match the %s group for:\n%s";
    private static final int AUTHOR_NAME_OFFSET = "author ".length();
    private static final int AUTHOR_EMAIL_OFFSET = "author-mail ".length();
    private static final int FULL_COMMIT_HASH_LENGTH = 40;
    private static final int COMMIT_TIME_OFFSET = "committer-time ".length();
    private static final String ADDED_LINE_SYMBOL = "+";
    private static final String DELETED_LINE_SYMBOL = "-";


    /**
     * Analyzes the authorship of {@code lineContent} in {@code filePath}.
     * Returns {@code true} if {@code currentAuthor} should be assigned full credit, false otherwise.
     */
    public static boolean analyzeAuthorship(
            RepoConfiguration config, String filePath, String lineContent, String commitHash, Author currentAuthor) {
        if (lineContent.length() == 0) {
            return true;
        }
        CandidateLine deletedLine = getDeletedLineWithHighestSimilarity(config, filePath, lineContent, commitHash);

        if (deletedLine == null) {
            return true;
        }
        GitBlameLineInfo gitBlameInfo = getGitBlameResults(config, deletedLine);

        if (gitBlameInfo.getAuthor().equals(Author.UNKNOWN_AUTHOR)
                || gitBlameInfo.getTimestampMilliseconds() < config.getSinceDate().getTime()
                || CommitHash.isInsideCommitList(gitBlameInfo.getCommitHash(), config.getIgnoreCommitList())
                || gitBlameInfo.getAuthor().getIgnoreGlobMatcher().matches(Paths.get(deletedLine.getFilePath()))) {
            return true;
        }
        if (!gitBlameInfo.getAuthor().equals(currentAuthor)) {
            return false;
        } else {
            return analyzeAuthorship(config, deletedLine.getFilePath(), deletedLine.getLineContent(),
                    gitBlameInfo.getCommitHash(), gitBlameInfo.getAuthor());
        }
    }

    /**
     * Returns the deleted line in {@code commitHash} that has the highest similarity with {@code lineContent}.
     */
    private static CandidateLine getDeletedLineWithHighestSimilarity(
            RepoConfiguration config, String filePath, String lineContent, String commitHash) {
        String gitLogResults = GitLog.getParentCommits(config.getRepoRoot(), commitHash);
        String[] parentCommits = gitLogResults.split(" ");

        CandidateLine highestSimilarityLine = null;

        for (String parentCommit : parentCommits) {
            String gitDiffResult = GitDiff.diffCommits(config.getRepoRoot(), parentCommit, commitHash);
            String[] fileDiffResultList = gitDiffResult.split(DIFF_FILE_CHUNK_SEPARATOR);

            for (String fileDiffResult : fileDiffResultList) {
                Matcher filePathMatcher = FILE_CHANGED_PATTERN.matcher(fileDiffResult);
                if (!filePathMatcher.find()) {
                    continue;
                }
                String preImageFilePath = filePathMatcher.group(PRE_IMAGE_FILE_PATH_GROUP_NAME);
                String postImageFilePath = filePathMatcher.group(POST_IMAGE_FILE_PATH_GROUP_NAME);
                if (preImageFilePath.equals(NONEXISTENT_FILE_SYMBOL) || !postImageFilePath.equals(filePath)) {
                    continue;
                }
                CandidateLine candidateLine = getDeletedLineWithHighestSimilarityInDiff(
                        fileDiffResult, lineContent, parentCommit, preImageFilePath);
                if (highestSimilarityLine == null
                        || candidateLine.getSimilarityScore() > highestSimilarityLine.getSimilarityScore()) {
                    highestSimilarityLine = candidateLine;
                }
            }
        }
        return highestSimilarityLine;
    }

    /**
     * Returns the deleted line in {@code fileDiffResult} that has the highest similarity with {@code lineContent}.
     */
    private static CandidateLine getDeletedLineWithHighestSimilarityInDiff(
            String fileDiffResult, String lineContent, String commitHash, String filePath) {
        CandidateLine mostSimilarLine = null;

        String[] linesChangedChunk = fileDiffResult.split(LINE_CHUNKS_SEPARATOR);
        for (int sectionIndex = 1; sectionIndex < linesChangedChunk.length; sectionIndex++) {
            String linesChangedInSection = linesChangedChunk[sectionIndex];
            if (!linesChangedInSection.contains(ADDED_LINE_SYMBOL + lineContent)) {
                continue;
            }
            String[] linesChanged = linesChangedInSection.split("\n");
            int currentPreImageLineNumber = getPreImageStartLineNumber(linesChanged[LINE_CHANGED_HEADER_INDEX]);

            for (int lineIndex = 1; lineIndex < linesChanged.length; lineIndex++) {
                String lineChanged = linesChanged[lineIndex];
                if (lineChanged.startsWith(DELETED_LINE_SYMBOL)) {
                    String deletedLineContent = lineChanged.substring(1);
                    double similarityScore = similarityScore(lineContent, deletedLineContent);
                    if (similarityScore >= SIMILARITY_THRESHOLD
                            && (mostSimilarLine == null || similarityScore > mostSimilarLine.getSimilarityScore())) {
                        mostSimilarLine = new CandidateLine(
                                currentPreImageLineNumber, deletedLineContent, filePath, commitHash, similarityScore);
                    }
                }
                if (!lineChanged.startsWith(ADDED_LINE_SYMBOL)) {
                    currentPreImageLineNumber++;
                }
            }
        }
        return mostSimilarLine;
    }

    /**
     * Returns the pre-image starting line number within the file diff result, by matching the pattern inside
     * {@code linesChanged}.
     */
    private static int getPreImageStartLineNumber(String linesChanged) {
        Matcher chunkHeaderMatcher = STARTING_LINE_NUMBER_PATTERN.matcher(linesChanged);

        if (!chunkHeaderMatcher.find()) {
            logger.severe(String.format(MATCH_GROUP_FAIL_MESSAGE_FORMAT, "line changed", linesChanged));
            throw new AssertionError("Should not have error matching line number pattern inside chunk header!");
        }

        return Integer.parseInt(chunkHeaderMatcher.group(PREIMAGE_START_LINE_GROUP_NAME));
    }

    /**
     * Calculates the similarity score of {@code s} with {@code baseString}.
     */
    private static double similarityScore(String s, String baseString) {
        double levenshteinDistance = getLevenshteinDistance(s, baseString);
        return 1 - (levenshteinDistance / baseString.length());
    }

    /**
     * Calculates the Levenshtein Distance between {@code s} and {@code t} using Dynamic Programming.
     */
    private static int getLevenshteinDistance(String s, String t) {
        int[][] dp = new int[s.length() + 1][t.length() + 1];
        for (int i = 0; i <= s.length(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= t.length(); i++) {
            dp[0][i] = i;
        }
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= t.length(); j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                }
            }
        }
        return dp[s.length()][t.length()];
    }

    /**
     * Returns the git blame results for {@code line}.
     */
    private static GitBlameLineInfo getGitBlameResults(RepoConfiguration config, CandidateLine line) {
        String blameResults = GitBlame.blameLine(config.getRepoRoot(),  line.getGitBlameCommitHash(),
                line.getFilePath(), line.getLineNumber());
        String[] blameResultLines = blameResults.split("\n");

        String commitHash = blameResultLines[0].substring(0, FULL_COMMIT_HASH_LENGTH);
        String authorName = blameResultLines[1].substring(AUTHOR_NAME_OFFSET);
        String authorEmail = blameResultLines[2]
                .substring(AUTHOR_EMAIL_OFFSET).replaceAll("<|>", "");
        long timestamp = Long.parseLong(blameResultLines[3].substring(COMMIT_TIME_OFFSET)) * 1000;
        Author author = config.getAuthor(authorName, authorEmail);

        return new GitBlameLineInfo(commitHash, author, timestamp);
    }
}
