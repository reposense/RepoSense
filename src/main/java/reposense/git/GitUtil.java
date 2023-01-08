package reposense.git;

import static reposense.util.StringsUtil.addQuotes;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.system.LogsManager;
import reposense.util.StringsUtil;

/**
 * Contains Git related utilities.
 */
class GitUtil {
    static final DateTimeFormatter GIT_LOG_SINCE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    static final DateTimeFormatter GIT_LOG_UNTIL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final Logger logger = LogsManager.getLogger(GitUtil.class);

    // ignore check against email
    private static final String AUTHOR_NAME_PATTERN = "^%s <.*>$";

    // ignore check against author name
    private static final String AUTHOR_EMAIL_PATTERN = "^.* <(.*\\+)?%s>$";

    private static final String OR_OPERATOR_PATTERN = "|";

    /**
     * Returns the {@code String} command to specify the date range of commits to analyze for `git` commands.
     * Date range is given by {@code sinceDate} and {@code untilDate}, with {@code zoneId} taken into account.
     */
    static String convertToGitDateRangeArgs(LocalDateTime sinceDate, LocalDateTime untilDate, ZoneId zoneId) {
        String gitDateRangeArgs = "";

        if (sinceDate != null) {
            gitDateRangeArgs += " --since=" + addQuotes(GIT_LOG_SINCE_DATE_FORMAT.format(
                    ZonedDateTime.of(sinceDate, zoneId)));
        }
        if (untilDate != null) {
            gitDateRangeArgs += " --until=" + addQuotes(GIT_LOG_UNTIL_DATE_FORMAT.format(
                    ZonedDateTime.of(untilDate, zoneId)));
        }

        return gitDateRangeArgs;
    }

    /**
     * Returns the {@code String} command to specify the {@code author} to analyze for `git log` command.
     * The regex type used is extended regex, which corresponds to the `--extended-regexp` flag in `git log`.
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
     * Returns the {@code String} command to specify the file {@code formats} to analyze for `git` commands.
     */
    public static String convertToGitFormatsArgs(List<FileType> formats) {
        StringBuilder gitFormatsArgsBuilder = new StringBuilder();
        final String cmdFormat = " -- " + addQuotes("*.%s");
        formats.stream()
                .map(format -> String.format(cmdFormat, format.toString()))
                .forEach(gitFormatsArgsBuilder::append);

        return gitFormatsArgsBuilder.toString();
    }

    /**
     * Returns the {@code String} command to specify the globs to exclude for `git log` command.
     * Also checks that every glob in {@code ignoreGlobList} only targets files within the given
     * repository's {@code root} directory.
     */
    public static String convertToGitExcludeGlobArgs(File root, List<String> ignoreGlobList) {
        StringBuilder gitExcludeGlobArgsBuilder = new StringBuilder();
        final String cmdFormat = " " + addQuotes(":(exclude)%s");
        ignoreGlobList.stream()
                .filter(item -> isValidIgnoreGlob(root, item))
                .map(ignoreGlob -> String.format(cmdFormat, ignoreGlob))
                .forEach(gitExcludeGlobArgsBuilder::append);

        return gitExcludeGlobArgsBuilder.toString();
    }

    /**
     * Returns true if the {@code ignoreGlob} is inside the current repository with reference to the {@code repoRoot}
     * directory.
     * Produces log messages when the invalid {@code ignoreGlob} is skipped.
     */
    private static boolean isValidIgnoreGlob(File repoRoot, String ignoreGlob) {
        String validPath = ignoreGlob;
        FileSystem fileSystem = FileSystems.getDefault();
        if (ignoreGlob.isEmpty()) {
            return false;
        } else if (ignoreGlob.startsWith("/") || ignoreGlob.startsWith("\\")) {
            // Ignore globs cannot start with a slash
            logger.log(Level.WARNING, ignoreGlob + " cannot start with / or \\.");
            return false;
        } else if (ignoreGlob.contains("/*") || ignoreGlob.contains("\\*")) {
            // contains directories
            validPath = ignoreGlob.substring(0, ignoreGlob.indexOf("/*"));
        } else if (ignoreGlob.contains("*")) {
            // no directories
            return true;
        }

        try {
            String fileGlobPath = "glob:" + repoRoot.getCanonicalPath().replaceAll("\\\\+", "\\/") + "/**";
            PathMatcher pathMatcher = fileSystem.getPathMatcher(fileGlobPath);
            validPath = new File(repoRoot, validPath).getCanonicalPath();
            if (pathMatcher.matches(Paths.get(validPath))) {
                return true;
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
            return false;
        }

        logger.log(Level.WARNING, ignoreGlob + " will be skipped as this glob points to the outside of "
                + "the repository.");
        return false;
    }
}
