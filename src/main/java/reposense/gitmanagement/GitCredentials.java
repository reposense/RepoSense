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

    public GitCredentials() throws IOException {
        github = null;
        authenticateGithub();
    }

    /**
     * Authenticates github using the username and the passcode
     *
     * @throws IOException if there is any problem authenticating with github
     */
    private void authenticateGithub() throws IOException {
        github = GitHub.connectUsingPassword(USERNAME, PASSCODE);
    }

    /**
     * Returns the Github object
     */
    public GitHub getGithub() {
        return github;
    }
}
