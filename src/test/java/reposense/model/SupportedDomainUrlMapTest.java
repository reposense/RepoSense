package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SupportedDomainUrlMapTest {

    private static final String SUPPORTED_DOMAIN_GITHUB = "github";
    private static final String SUPPORTED_DOMAIN_GITLAB = "gitlab";
    private static final String SUPPORTED_DOMAIN_BITBUCKET = "bitbucket";
    private static final String UNSUPPORTED_DOMAIN_1 = "opensource.ncsa.illinois.edu";
    private static final String UNSUPPORTED_DOMAIN_2 = "example.org";

    @Test
    public void getDomainsAccessed_noRemote_onlyUnsupportedEntry() {
        SupportedDomainUrlMap supportedDomainUrlMap = new SupportedDomainUrlMap();

        Map<String, Map<String, String>> expectedMap = new HashMap<>();
        expectedMap.put(RepoLocation.UNSUPPORTED_DOMAIN_NAME, SupportedDomainUrlMap.NOT_SUPPORTED_MAP);
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());
    }

    @Test
    public void getDomainsAccessed_remoteRepositories_onlyRelevantDomainsAdded() {
        SupportedDomainUrlMap supportedDomainUrlMap = new SupportedDomainUrlMap();

        Map<String, Map<String, String>> expectedMap = new HashMap<>();
        expectedMap.put(RepoLocation.UNSUPPORTED_DOMAIN_NAME, SupportedDomainUrlMap.NOT_SUPPORTED_MAP);

        supportedDomainUrlMap.isSupportedDomain(UNSUPPORTED_DOMAIN_1);
        // no change expected
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());

        supportedDomainUrlMap.isSupportedDomain(SUPPORTED_DOMAIN_GITHUB);
        expectedMap.put(SUPPORTED_DOMAIN_GITHUB, SupportedDomainUrlMap.GITHUB_MAP);
        // github mapping now added
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());

        supportedDomainUrlMap.isSupportedDomain(SUPPORTED_DOMAIN_GITLAB);
        expectedMap.put(SUPPORTED_DOMAIN_GITLAB, SupportedDomainUrlMap.GITLAB_MAP);
        // gitlab mapping now added
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());

        supportedDomainUrlMap.isSupportedDomain(UNSUPPORTED_DOMAIN_2);
        // no change expected
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());

        supportedDomainUrlMap.isSupportedDomain(SUPPORTED_DOMAIN_BITBUCKET);
        expectedMap.put(SUPPORTED_DOMAIN_BITBUCKET, SupportedDomainUrlMap.BITBUCKET_MAP);
        // bitbucket mapping now added
        Assert.assertEquals(expectedMap, supportedDomainUrlMap.getRequiredDomainUrlMap());
    }
}
