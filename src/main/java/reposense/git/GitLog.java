package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuotesForFilePath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private static final String DEFAULT_EMAIL_IF_MISSING = "";

    /**
     * Returns the git commit log info of {@code author}, in the repository specified in {@code config}.
     */
    public static String get(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i --extended-regexp ";
        command += GitUtil.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate(), config.getZoneId());
        command += " --pretty=format:" + PRETTY_FORMAT_STRING + " --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFileTypeManager().getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

    /**
     * Returns the git commit log info of {@code author}, with the files changed, in the repository specified in
     * {@code config}.
     */
    public static String getWithFiles(RepoConfiguration config, Author author) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --no-merges -i --extended-regexp ";
        command += GitUtil.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate(), config.getZoneId());
        command += " --pretty=format:" + PRETTY_FORMAT_STRING + " --numstat --shortstat";
        command += GitUtil.convertToFilterAuthorArgs(author);
        command += GitUtil.convertToGitFormatsArgs(config.getFileTypeManager().getFormats());
        command += GitUtil.convertToGitExcludeGlobArgs(rootPath.toFile(), author.getIgnoreGlobList());

        return runCommand(rootPath, command);
    }

    /**
     * Returns the authors who modified the file at {@code filePath}, in the repository specified in {@code config}.
     * The output is a list of length-2 arrays containing the author's name and email.
     */
    public static List<String[]> getFileAuthors(RepoConfiguration config, String filePath) {
        Path rootPath = Paths.get(config.getRepoRoot());

        String command = "git log --pretty=format:\"%an\t%ae\" ";
        command += GitUtil.convertToGitDateRangeArgs(config.getSinceDate(), config.getUntilDate(), config.getZoneId());
        command += " " + addQuotesForFilePath(filePath);

        String result = runCommand(rootPath, command);
        return Arrays.stream(result.split("\n"))
                .map(authorAndEmailLine -> authorAndEmailLine.split("\t"))
                .map(authorAndEmailArray -> authorAndEmailArray.length == 1
                        ? new String[] {authorAndEmailArray[0], DEFAULT_EMAIL_IF_MISSING}
                        : authorAndEmailArray)
                .collect(Collectors.toList());
    }
}
