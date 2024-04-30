package reposense.authorship.model;

/**
 * Stores the information of a candidate line used in {@code AuthorshipAnalyzer}.
 */
public class CandidateLine {
    private final int lineNumber;
    private final String lineContent;
    private final String filePath;
    private final String gitBlameCommitHash;
    private final double originalityScore;

    public CandidateLine(int lineNumber, String lineContent, String filePath, String gitBlameCommitHash,
            double originalityScore) {
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
        this.filePath = filePath;
        this.gitBlameCommitHash = gitBlameCommitHash;
        this.originalityScore = originalityScore;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getLineContent() {
        return lineContent;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getGitBlameCommitHash() {
        return gitBlameCommitHash;
    }

    public double getOriginalityScore() {
        return originalityScore;
    }
}
