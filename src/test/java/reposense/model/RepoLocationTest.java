package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {

    @Test
    public void repoLocationParser_parseEmptyString_success() throws Exception {
        RepoLocation repoLocation = new RepoLocation("");
    }

}
