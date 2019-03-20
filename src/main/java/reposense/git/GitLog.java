package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains git log related functionalities.
 * Git log is responsible to obtain the commit logs and the authors' info.
 */
public class GitLog {

    public static String get(RepoConfiguration config, Author author) {
        return get(config, author, null, null);
    }

    public static String get(RepoConfiguration config, Author author, Date sinceDate, Date untilDate) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += GitUtil.convertToGitDateRangeArgs(sinceDate, untilDate);
        command += " --pretty=format:\"%H|%aN|%aE|%ad|%s\" --date=iso --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }
}
