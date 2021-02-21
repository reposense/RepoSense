package reposense.report;

import reposense.model.RepoConfiguration;

/**
 * Stores the RepoConfiguration and error message associated with errors encountered when analyzing repo.
 */
public class AnalysisErrorInfo {
    private RepoConfiguration failedConfig;
    private String errorMessage;

    public AnalysisErrorInfo(RepoConfiguration failedConfig, String errorMessage) {
        this.failedConfig = failedConfig;
        this.errorMessage = errorMessage;
    }

    public RepoConfiguration getFailedConfig() {
        return failedConfig;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
