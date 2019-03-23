package reposense.git;

import static reposense.util.StringsUtil.addQuote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import reposense.model.Author;
import reposense.model.Format;
import reposense.util.StringsUtil;

/**
 * Contains Git related utilities.
 */
class GitUtil {
    static final DateFormat GIT_LOG_SINCE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+08:00");
    static final DateFormat GIT_LOG_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59+08:00");

    // ignore check against email
    private static final String AUTHOR_NAME_PATTERN = "^%s <.*>$";

    // ignore check against author name
    private static final String AUTHOR_EMAIL_PATTERN = "^.* <%s>$";

    // capture prefix and + operator
    private static final String EMAIL_PREFIX_REGEX_GROUP = "\\(.*+\\)\\?";

    // capture + operator and suffix
    private static final String EMAIL_SUFFIX_REGEX_GROUP = "\\(+.*\\)\\?";

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
                .map(email -> String.format(AUTHOR_EMAIL_PATTERN, convertToEmailRegex(email)) + OR_OPERATOR_PATTERN)
                .forEach(filterAuthorArgsBuilder::append);

        filterAuthorArgsBuilder.append(
                String.format(AUTHOR_NAME_PATTERN,
                        StringsUtil.replaceSpecialSymbols(author.getGitId(), "."))).append("\"");
        return filterAuthorArgsBuilder.toString();
    }

    /**
     * Converts email to regex, adding optional regex groups for better matching.
     */
    private static String convertToEmailRegex(String email) {
        String[] emails = email.split("@");
        String authorEmailRegex = EMAIL_PREFIX_REGEX_GROUP;
        authorEmailRegex += StringsUtil.replaceSpecialSymbols(emails[0], ".");
        authorEmailRegex += EMAIL_SUFFIX_REGEX_GROUP;
        authorEmailRegex += ".";
        authorEmailRegex += StringsUtil.replaceSpecialSymbols(emails[1], ".");
        return authorEmailRegex;
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
     */
    static String convertToGitExcludeGlobArgs(List<String> ignoreGlobList) {
        StringBuilder gitExcludeGlobArgsBuilder = new StringBuilder();
        final String cmdFormat = " " + addQuote(":(exclude)%s");
        ignoreGlobList.stream()
                .filter(item -> !item.isEmpty())
                .map(ignoreGlob -> String.format(cmdFormat, ignoreGlob))
                .forEach(gitExcludeGlobArgsBuilder::append);

        return gitExcludeGlobArgsBuilder.toString();
    }
}
