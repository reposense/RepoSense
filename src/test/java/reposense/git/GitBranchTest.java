package reposense.git;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.exception.GitCloneException;
import reposense.model.Format;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;


public class GitBranchTest extends GitTestTemplate {

    protected static final String TEST_REPO_UNCOMMON_DEFAULT_GIT_LOCATION =
            "https://github.com/reposense/testrepo-UncommonDefaultBranch.git";

    @Test
    public void getCurrentBranch_masterBranch_success() {
        String currentBranch = GitBranch.getCurrentBranch(config.getRepoRoot());
        Assert.assertEquals("master", currentBranch);
    }

    @Test
    public void getCurrentBranch_uncommonDefaultBranch_success() throws GitCloneException, InvalidLocationException {
        RepoConfiguration uncommonDefaultConfig = new RepoConfiguration(
                new RepoLocation(TEST_REPO_UNCOMMON_DEFAULT_GIT_LOCATION), RepoConfiguration.DEFAULT_BRANCH);
        uncommonDefaultConfig.setFormats(Format.DEFAULT_FORMATS);

        GitClone.clone(uncommonDefaultConfig);
        String currentBranch = GitBranch.getCurrentBranch(uncommonDefaultConfig.getRepoRoot());
        Assert.assertEquals("uncommon", currentBranch);
    }
}
