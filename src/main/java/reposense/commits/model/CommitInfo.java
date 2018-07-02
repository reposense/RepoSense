package reposense.commits.model;

/**
 * Stores the raw information generated for each commit.
 */
public class CommitInfo {
    private final String infoLine;
    private final String statLine;

    public CommitInfo(String infoLine, String statLine) {
        this.infoLine = infoLine;
        this.statLine = statLine;
    }

    public String getInfoLine() {
        return infoLine;
    }

    public String getStatLine() {
        return statLine;
    }
}
