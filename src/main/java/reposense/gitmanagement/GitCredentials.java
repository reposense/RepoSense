package reposense.gitmanagement;

import java.io.IOException;

import org.kohsuke.github.GitHub;

/**
 * Store github credentials
 * Authenticate with github using the credentials
 */
public class GitCredentials {

    private static final String USERNAME = "reposensetest";
    private static final String TOKEN = "dd9a3a3d4aeebc57f45ef5903d2f891e5b6a8aaa";

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
        github = GitHub.connect(USERNAME, TOKEN);
    }

    /**
     * Returns the Github object
     */
    public GitHub getGithub() {
        return github;
    }
}
