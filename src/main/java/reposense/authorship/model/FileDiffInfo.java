package reposense.authorship.model;

/**
 * Stores the information of processed file diff used in {@code AuthorshipAnalyzer}.
 */
public class FileDiffInfo {
    private final String fileDiffResult;
    private final String preImageFilePath;
    private final String postImageFilePath;

    public FileDiffInfo(String fileDiffResult, String preImageFilePath, String postImageFilePath) {
        this.fileDiffResult = fileDiffResult;
        this.preImageFilePath = preImageFilePath;
        this.postImageFilePath = postImageFilePath;
    }

    public String getFileDiffResult() {
        return fileDiffResult;
    }

    public String getPreImageFilePath() {
        return preImageFilePath;
    }

    public String getPostImageFilePath() {
        return postImageFilePath;
    }
}
