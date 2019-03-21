package reposense.git;

import static reposense.util.StringsUtil.addQuote;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.Author;
import reposense.model.Format;
import reposense.system.LogsManager;
import reposense.util.StringsUtil;

/**
 * Contains Git related utilities.
 */
class GitUtil {
    static final DateFormat GIT_LOG_SINCE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+08:00");
    static final DateFormat GIT_LOG_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59+08:00");
    private static final Logger logger = LogsManager.getLogger(GitUtil.class);

    // ignore check against email
    private static final String AUTHOR_NAME_PATTERN = "^%s <.*>$";

    // ignore check against author name
    private static final String AUTHOR_EMAIL_PATTERN = "^.* <%s>$";

    private static final String OR_OPERATOR_PATTERN = "\\|";

    /**
     * Returns the {@code String} command to specify the date range of commits to analyze for `git` commands.
     */
    static String convertToGitDateRangeArgs(Date sinceDate, Date untilDate) {
        String gitDateRangeArgs = "";

        if (sinceDate != null) {
            gitDateRangeArgs += " --since=" + addQuote(GIT_LOG_SINCE_DATE_FORMAT.format(sinceDate));
        }
        if (untilDate != null) {
            gitDateRangeArgs += " --until=" + addQuote(GIT_LOG_UNTIL_DATE_FORMAT.format(untilDate));
        }

        return gitDateRangeArgs;
    }

    /**
     * Returns the {@code String} command to specify the authors to analyze for `git log` command.
     */
    static String convertToFilterAuthorArgs(Author author) {
        StringBuilder filterAuthorArgsBuilder = new StringBuilder(" --author=\"");

        // git author names and emails may contain regex meta-characters, so we need to escape those
        author.getAuthorAliases().stream()
                .map(authorAlias -> String.format(AUTHOR_NAME_PATTERN,
                        StringsUtil.replaceSpecialSymbols(authorAlias, ".")) + OR_OPERATOR_PATTERN)
                .forEach(filterAuthorArgsBuilder::append);
        author.getEmails().stream()
                .map(email -> String.format(AUTHOR_EMAIL_PATTERN,
                        StringsUtil.replaceSpecialSymbols(email, ".")) + OR_OPERATOR_PATTERN)
                .forEach(filterAuthorArgsBuilder::append);

        filterAuthorArgsBuilder.append(
                String.format(AUTHOR_NAME_PATTERN,
                        StringsUtil.replaceSpecialSymbols(author.getGitId(), "."))).append("\"");
        return filterAuthorArgsBuilder.toString();
    }

    /**
     * Returns the {@code String} command to specify the file formats to analyze for `git` commands.
     */
    static String convertToGitFormatsArgs(List<Format> formats) {
        StringBuilder gitFormatsArgsBuilder = new StringBuilder();
        final String cmdFormat = " -- " + addQuote("*.%s");
        formats.stream()
                .map(format -> String.format(cmdFormat, format.toString()))
                .forEach(gitFormatsArgsBuilder::append);

        return gitFormatsArgsBuilder.toString();
    }

    /**
     * Returns the {@code String} command to specify the globs to exclude for `git log` command.
     * Also checks that every file in {@code ignoreGlobList} is within the given repository {@code root}
     */
    public static String convertToGitExcludeGlobArgs(File root, List<String> ignoreGlobList) {
        StringBuilder gitExcludeGlobArgsBuilder = new StringBuilder();
        final String cmdFormat = " " + addQuote(":(exclude)%s");
        ignoreGlobList.stream()
                .filter(item -> !item.isEmpty() && isValidPath(root, item))
                .map(ignoreGlob -> String.format(cmdFormat, ignoreGlob))
                .forEach(gitExcludeGlobArgsBuilder::append);

        return gitExcludeGlobArgsBuilder.toString();
    }

    /**
     * Returns true if the {@code path} is inside the current repository
     * Produces log messages when the invalid file path is skipped
     */
    public static boolean isValidPath(File repoRoot, String path) {
        String validPath = path;
        FileSystem fileSystem = FileSystems.getDefault();
        if (path.contains("/*")) {
            // contains directories
            validPath = path.substring(0, path.indexOf("/*"));
        } else if (path.contains("*")) {
            // no directories
            return true;
        } else if (path.startsWith("/")) {
            // Ignore globs cannot start with a slash
            return false;
        }

        try {
            String fileGlobPath = "glob:" + repoRoot.getCanonicalPath().replaceAll("\\\\+", "\\/") + "/**";
            PathMatcher pathMatcher = fileSystem.getPathMatcher(fileGlobPath);
            validPath = new File(repoRoot, validPath).getCanonicalPath();
            if (pathMatcher.matches(Paths.get(validPath))) {
                return true;
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, path + " requires file system queries.");
        }

        logger.log(Level.WARNING, path + " will be skipped as this file is outside the repo.");
        return false;
    }
}
