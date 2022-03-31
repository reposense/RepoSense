package reposense;

import org.junit.jupiter.api.BeforeAll;
import reposense.git.GitClone;

public class LocalRepoSystemTest {

    @BeforeAll
    public static void setupLocalRepos() {
        GitClone.clone();
        // TODO clone 2 repos in 2 separate test locations
    }

    // write one dual local repo with same final directory test
    // write one for relative path test
}
