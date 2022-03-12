package reposense.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the map of supported remote repo domain names.
 */
public class SupportedDomainUrlMap {

    private static final String BASE_URL_KEY = "BASE_URL";
    private static final String REPO_URL_KEY = "REPO_URL";
    private static final String COMMIT_PATH_KEY = "COMMIT_PATH";
    private static final String BLAME_PATH_KEY = "BLAME_PATH";

    private static final Map<String, String> GITHUB_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://github.com/");
            put(REPO_URL_KEY, "https://github.com/ORGANIZATION/REPO_NAME/");
            put(COMMIT_PATH_KEY, "commit/COMMIT_HASH/");
            put(BLAME_PATH_KEY, "blame/BRANCH/FILE_PATH/");
        }
    };
    private static final Map<String, String> GITLAB_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://gitlab.com/");
            put(REPO_URL_KEY, "https://gitlab.com/ORGANIZATION/REPO_NAME/");
            put(COMMIT_PATH_KEY, "-/commit/COMMIT_HASH/");
            put(BLAME_PATH_KEY, "-/blame/BRANCH/FILE_PATH/");
        }
    };
    private static final Map<String, String> BITBUCKET_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://bitbucket.org/");
            put(REPO_URL_KEY, "https://bitbucket.org/ORGANIZATION/REPO_NAME/");
            put(COMMIT_PATH_KEY, "commits/COMMIT_HASH/");
            put(BLAME_PATH_KEY, "annotate/BRANCH/FILE_PATH/");
        }
    };

    private static final SupportedDomainUrlMap DEFAULT_DOMAIN_URL_MAP = new SupportedDomainUrlMap();

    private final Map<String, Map<String, String>> domainUrlMap;

    private SupportedDomainUrlMap() {
        domainUrlMap = new HashMap<>();
        domainUrlMap.put("github", GITHUB_MAP);
        domainUrlMap.put("gitlab", GITLAB_MAP);
        domainUrlMap.put("bitbucket", BITBUCKET_MAP);
    }

    public Map<String, Map<String, String>> getDomainUrlMap() {
        return domainUrlMap;
    }

    /**
     * Returns true if {@code domainName} is currently supported.
     */
    public boolean isSupportedDomain(String domainName) {
        return domainUrlMap.containsKey(domainName);
    }

    /**
     * Returns true if {@code domainName} is currently supported.
     */
    public static boolean isSupportedDomainName(String domainName) {
        return DEFAULT_DOMAIN_URL_MAP.isSupportedDomain(domainName);
    }

    /**
     * Returns the singleton copy of the supported domain url map.
     */
    public static Map<String, Map<String, String>> getDefaultDomainUrlMap() {
        return DEFAULT_DOMAIN_URL_MAP.getDomainUrlMap();
    }
}
