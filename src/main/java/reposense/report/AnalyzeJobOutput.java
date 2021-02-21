package reposense.report;

import java.nio.file.Path;
import java.util.List;

import reposense.model.RepoLocation;

/**
 * Represents the output of threaded repo analysis job.
 */
public class AnalyzeJobOutput {
    private RepoLocation location;
    private boolean cloneSuccessful;
    private List<Path> files;
    private List<AnalysisErrorInfo> analysisErrors;

    public AnalyzeJobOutput(RepoLocation location, boolean cloneSuccessful,
            List<Path> files, List<AnalysisErrorInfo> analysisErrors) {
        this.location = location;
        this.cloneSuccessful = cloneSuccessful;
        this.files = files;
        this.analysisErrors = analysisErrors;
    }

    public RepoLocation getLocation() {
        return location;
    }

    public boolean isCloneSuccessful() {
        return cloneSuccessful;
    }

    public List<Path> getFiles() {
        return files;
    }

    public List<AnalysisErrorInfo> getAnalyseErrors() {
        return analysisErrors;
    }
}
