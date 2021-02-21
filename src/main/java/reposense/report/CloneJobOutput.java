package reposense.report;

import reposense.model.RepoLocation;

public class CloneJobOutput {
    private RepoLocation location;
    private boolean cloneSuccessful;
    private String defaultBranch;

    public CloneJobOutput(RepoLocation location, String defaultBranch) {
        this.location = location;
        this.cloneSuccessful = true;
        this.defaultBranch = defaultBranch;
    }

    public CloneJobOutput(RepoLocation location) {
        this.location = location;
        this.cloneSuccessful = false;
    }

    public RepoLocation getLocation() {
        return location;
    }

    public boolean isCloneSuccessful() {
        return cloneSuccessful;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }
}
