package reposense.util;

import java.io.File;

public class TestConstants {
    public static final String TEST_ORG = "reposense";
    public static final String TEST_REPO = "testrepo-Alpha";
    public static final String TEST_REPO_DISPLAY_NAME = TEST_ORG + "_" + TEST_REPO + "_" + "master";

    public static final String TEST_REPO_GIT_LOCATION =
            "https://github.com/" + TEST_ORG + "/" + TEST_REPO + ".git";

    public static final String LOCAL_TEST_REPO_ADDRESS = Constants.REPOS_ADDRESS
            + File.separator + TEST_ORG + "_" + TEST_REPO + "_" + "master" + File.separator + TEST_REPO;

    public static final String FIRST_COMMIT_HASH = "7d7584f";
    public static final String TEST_COMMIT_HASH = "2fb6b9b";

    public static final String MAIN_AUTHOR_NAME = "harryggg";
    public static final String FAKE_AUTHOR_NAME = "fakeAuthor";

    public static final String DISK_REPO_DISPLAY_NAME = "DISK_TEST_REPO";
}
