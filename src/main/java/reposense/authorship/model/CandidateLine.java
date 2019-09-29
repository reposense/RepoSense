package reposense.authorship.model;

/**
 * Stores the information of a candidate line used in {@code LineCreditAnalyzer}.
 */
public class CandidateLine {
    private int lineNumber;
    private String lineContent;
    private String filePath;
    private String gitBlameCommitHash;
    private double similarityScore;

    public CandidateLine(
            int lineNumber, String lineContent, String filePath, String gitBlameCommitHash, double similarityScore) {
        this.lineNumber = lineNumber;
        this.lineContent = lineContent;
        this.filePath = filePath;
        this.gitBlameCommitHash = gitBlameCommitHash;
        this.similarityScore = similarityScore;
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

    public double getSimilarityScore() {
        return similarityScore;
    }
}
