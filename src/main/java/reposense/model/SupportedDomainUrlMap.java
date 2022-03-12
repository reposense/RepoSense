package reposense.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the map of supported remote repo domain names.
 */
public class SupportedDomainUrlMap {

    private static final String BASE_URL_KEY = "BASE_URL";
    private static final String REPO_URL_KEY = "REPO_URL";
    private static final String BRANCH_KEY = "BRANCH";
    private static final String COMMIT_PATH_KEY = "COMMIT_PATH";
    private static final String BLAME_PATH_KEY = "BLAME_PATH";
    private static final String HISTORY_PATH_KEY = "HISTORY_PATH";

    private static final String ORGANIZATION_PLACEHOLDER = "ORGANIZATION";
    private static final String REPO_NAME_PLACEHOLDER = "REPO_NAME";
    private static final String BRANCH_PLACEHOLDER = "BRANCH";
    private static final String COMMIT_HASH_PLACEHOLDER = "COMMIT_HASH";
    private static final String FILE_PATH_PLACEHOLDER = "FILE_PATH";

    private static final Map<String, String> GITHUB_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://github.com/");
            put(REPO_URL_KEY, "https://github.com/" + ORGANIZATION_PLACEHOLDER + "/" + REPO_NAME_PLACEHOLDER + "/");
            put(BRANCH_KEY, "tree/" + BRANCH_PLACEHOLDER);
            put(COMMIT_PATH_KEY, "commit/" + COMMIT_HASH_PLACEHOLDER);
            put(BLAME_PATH_KEY, "blame/" + BRANCH_PLACEHOLDER + "/" + FILE_PATH_PLACEHOLDER);
            put(HISTORY_PATH_KEY, "commits/" + BRANCH_KEY + "/" + FILE_PATH_PLACEHOLDER);
        }
    };
    private static final Map<String, String> GITLAB_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://gitlab.com/");
            put(REPO_URL_KEY, "https://gitlab.com/" + ORGANIZATION_PLACEHOLDER + "/" + REPO_NAME_PLACEHOLDER + "/");
            put(BRANCH_KEY, "-/tree/" + BRANCH_PLACEHOLDER);
            put(COMMIT_PATH_KEY, "-/commit/" + COMMIT_HASH_PLACEHOLDER);
            put(BLAME_PATH_KEY, "-/blame/" + BRANCH_PLACEHOLDER + "/" + FILE_PATH_PLACEHOLDER);
            put(HISTORY_PATH_KEY, "-/commits/" + BRANCH_PLACEHOLDER + "/" + FILE_PATH_PLACEHOLDER);
        }
    };
    private static final Map<String, String> BITBUCKET_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "https://bitbucket.org/");
            put(REPO_URL_KEY, "https://bitbucket.org/" + ORGANIZATION_PLACEHOLDER + "/" + REPO_NAME_PLACEHOLDER + "/");
            put(BRANCH_KEY, "src/" + BRANCH_PLACEHOLDER);
            put(COMMIT_PATH_KEY, "commits/" + COMMIT_HASH_PLACEHOLDER);
            put(BLAME_PATH_KEY, "annotate/" + BRANCH_PLACEHOLDER + "/" + FILE_PATH_PLACEHOLDER);
            put(HISTORY_PATH_KEY, "history-node/HEAD/" + FILE_PATH_PLACEHOLDER + "?at=" + BRANCH_PLACEHOLDER);
        }
    };
    private static final Map<String, String> NOT_SUPPORTED_MAP = new HashMap<String, String>() {
        {
            put(BASE_URL_KEY, "UNSUPPORTED");
            put(REPO_URL_KEY, "UNSUPPORTED");
            put(BRANCH_KEY, "");
            put(COMMIT_PATH_KEY, "");
            put(BLAME_PATH_KEY, "");
            put(HISTORY_PATH_KEY, "");
        }
    };

    private static final SupportedDomainUrlMap DEFAULT_DOMAIN_URL_MAP = new SupportedDomainUrlMap();

    private final Map<String, Map<String, String>> domainUrlMap;

    private SupportedDomainUrlMap() {
        domainUrlMap = new HashMap<>();
        domainUrlMap.put("github", GITHUB_MAP);
        domainUrlMap.put("gitlab", GITLAB_MAP);
        domainUrlMap.put("bitbucket", BITBUCKET_MAP);
        domainUrlMap.put(RepoLocation.UNSUPPORTED_DOMAIN_NAME, NOT_SUPPORTED_MAP);
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
