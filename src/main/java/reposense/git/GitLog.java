package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains git log related functionalities.
 * Git log is responsible to obtain the commit logs and the authors' info.
 */
public class GitLog {

    public static String get(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i ";
        command += Util.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate());
        command += " --pretty=format:\"%H|%aN|%ad|%s\" --date=iso --shortstat";
        command += Util.convertToFilterAuthorArgs(author);
        command += Util.convertToGitFormatsArgs(config.getFormats());
        command += Util.convertToGitExcludeGlobArgs(author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

}
