package reposense.gitmanagement;

import java.io.IOException;

import org.kohsuke.github.GitHub;

/**
 * Store github credentials
 * Authenticate with github using the credentials
 */
public class GitCredentials {

    private static final String USERNAME = "reposensetest";
    private static final String PASSCODE = "reposense123";

    private static GitHub github;

    private GitCredentials() throws IOException {
        github = null;
        authenticateGithub();
    }

    /*
     * Authenticates github using the username and the passcode
     */
    private void authenticateGithub() throws IOException {
        github = GitHub.connectUsingPassword(USERNAME, PASSCODE);
    }
}
