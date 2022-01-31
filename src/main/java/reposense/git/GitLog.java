package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuote;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains git log related functionalities.
 * Git log is responsible to obtain the commit logs and the authors' info.
 */
public class GitLog {
    public static final String COMMIT_INFO_DELIMITER = "(?m)^>>>COMMIT INFO<<<\\n";

    private static final String PRETTY_FORMAT_STRING =
            "\">>>COMMIT INFO<<<%n%H|%n|%aN|%n|%aE|%n|%cI|%n|%s|%n|%w(0,4,4)%b%w(0,0,0)|%n|%D|\"";

    /**
     * Returns the git commit log info of {@code Author}, in the repository specified in {@code config}.
     */
    public static String get(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += GitUtil.convertToGitDateRangeArgs(
                ZonedDateTime.of(config.getSinceDate(), ZoneId.of(config.getZoneId())),
                ZonedDateTime.of(config.getUntilDate(), ZoneId.of(config.getZoneId())));
        command += " --pretty=format:" + PRETTY_FORMAT_STRING + " --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFileTypeManager().getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

    /**
     * Returns the git commit log info of {@code Author}, with the files changed, in the repository specified in
     * {@code config}.
     */
    public static String getWithFiles(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += GitUtil.convertToGitDateRangeArgs(
                ZonedDateTime.of(config.getSinceDate(), ZoneId.of(config.getZoneId())),
                ZonedDateTime.of(config.getUntilDate(), ZoneId.of(config.getZoneId())));
        command += " --pretty=format:" + PRETTY_FORMAT_STRING + " --numstat --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFileTypeManager().getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

    /**
     * Returns the authors who modified the binary file at {@code filePath}, in the repository specified in
     * {@code config}.
     */
    public static String getBinaryFileAuthors(RepoConfiguration config, String filePath) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --pretty=format:\"%an\t%ae\" ";
        command += GitUtil.convertToGitDateRangeArgs(
                ZonedDateTime.of(config.getSinceDate(), ZoneId.of(config.getZoneId())),
                ZonedDateTime.of(config.getUntilDate(), ZoneId.of(config.getZoneId())));
        command += " " + addQuote(filePath);

        return runCommand(rootPath, command);
    }
}
