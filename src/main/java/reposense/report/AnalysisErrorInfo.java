package reposense.report;

import reposense.model.RepoConfiguration;

public class AnalysisErrorInfo {
    RepoConfiguration failedConfig;
    String errorMessage;

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
