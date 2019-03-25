package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains git log related functionalities.
 */
public class GitLog {

    /**
     * Responsible for obtaining the commit logs and the authors' info.
     */
    public static String get(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += GitUtil.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate());
        command += " --pretty=format:\"%H|%aN|%aE|%ad|%s\" --date=iso --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

    /**
     * Responsible for obtaining the commit logs with files changed and authors' info
     */
    public static String getWithFiles(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += GitUtil.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate());
        command += " --pretty=format:\"%H|%aN|%aE|%ad|%s\" --date=iso --stat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }
}
