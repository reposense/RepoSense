package reposense.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.parser.InvalidLocationException;

/**
 * Represents a repository location.
 */
public class Location {
    private String location;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public Location(String location) throws InvalidLocationException {
        verifyLocation(location);
        this.location = location;
    }

    /**
     * Verifies {@code location} can be presented as a {@code URL} or {@code Path}.
     * @throws InvalidLocationException if otherwise.
     */
    private void verifyLocation(String location) throws InvalidLocationException {
        boolean isValidPathLocation = false;
        boolean isValidGitUrl = false;

        try {
            Path pathLocation = Paths.get(location);
            isValidPathLocation = Files.exists(pathLocation);
        } catch (InvalidPathException ipe) {
            // Ignore exception
        }

        try {
            new URL(location);
            isValidGitUrl = location.endsWith(RepoConfiguration.GIT_LINK_SUFFIX);
        } catch (MalformedURLException mue) {
            // Ignore exception
        }

        if (!isValidPathLocation && !isValidGitUrl) {
            throw new InvalidLocationException(location + " is an invalid location.");
        }
    }

    @Override
    public String toString() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof Location)) {
            return false;
        }

        Location otherLocation = (Location) other;
        return this.location.equals(otherLocation.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
