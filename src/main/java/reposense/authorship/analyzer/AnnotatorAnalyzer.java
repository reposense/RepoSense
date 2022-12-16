package reposense.authorship.analyzer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.model.AuthorConfiguration;

/**
 * Analyzes the authorship of a {@code FileInfo} using the given annotations on the file.
 * Only the lines with the format (START OF LINE) COMMENT_SYMBOL @@author ONE_STRING_WITH_NO_SPACE (END OF LINE)
 * will be analyzed. Otherwise, the line will be ignored and treated as normal lines.
 * If the line is analyzed, and the string following the author tag is a valid git id, and there is no author config
 * file, then the code will be attributed to the author with that git id. Otherwise, the code will be attributed to
 * unknown author.
 */
public class AnnotatorAnalyzer {
    private static final String AUTHOR_TAG = "@@author";
    // GitHub username format
    private static final String REGEX_AUTHOR_NAME_FORMAT = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}$";
    private static final Pattern PATTERN_AUTHOR_NAME_FORMAT = Pattern.compile(REGEX_AUTHOR_NAME_FORMAT);
    private static final String REGEX_AUTHOR_TAG_FORMAT = "@@author(\\s+[^\\s]+)?";

    private static final String[] COMMON_COMMENT_FORMAT = {"<!--", "-->"};

    private static final String[][] COMMENT_FORMATS_GENERIC = {
            {"//", "\\s"},
            {"/\\*", "\\*/"},
            {"#", "\\s"},
            COMMON_COMMENT_FORMAT,
            {"%", "\\s"},
    };

    private static final String[][] COMMENT_FORMATS_MARKDOWN = {
            {"\\[.*]:\\s*#\\s*\\(", "\\)"},
            COMMON_COMMENT_FORMAT,
            {"<!---", "--->"}
    };

    static {
        for (int i = 0; i < COMMENT_FORMATS_GENERIC.length; i++) {
            COMMENT_PATTERNS_GENERIC[i] = Pattern.compile(generateCommentRegex(COMMENT_FORMATS_GENERIC[i][0],
                    COMMENT_FORMATS_GENERIC[i][1]));
        }
        for (int i = 0; i < COMMENT_FORMATS_MARKDOWN.length; i++) {
            COMMENT_PATTERNS_MARKDOWN[i] = Pattern.compile(generateCommentRegex(COMMENT_FORMATS_MARKDOWN[i][0],
                    COMMENT_FORMATS_MARKDOWN[i][1]));
        }
    }

    /**
     * Overrides the authorship information in {@code fileInfo} based on annotations given on the file.
     *
     * @param fileInfo     FileInfo to be further analyzed with author annotations.
     * @param authorConfig AuthorConfiguration for current analysis.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, AuthorConfiguration authorConfig) {
        Optional<Author> currentAnnotatedAuthor = Optional.empty();
        Path filePath = Paths.get(fileInfo.getPath());
        for (LineInfo lineInfo : fileInfo.getLines()) {
            String lineContent = lineInfo.getContent();
            if (lineContent.contains(AUTHOR_TAG) && isValidCommentLine(lineContent, isMarkdownFileType)) {
                Optional<Author> newAnnotatedAuthor = findAuthorInLine(lineContent, authorConfig, isMarkdownFileType);
                boolean isEndOfAnnotatedSegment = currentAnnotatedAuthor.isPresent() && !newAnnotatedAuthor.isPresent();
                boolean isUnknownAuthorSegment = !currentAnnotatedAuthor.isPresent() && !newAnnotatedAuthor.isPresent();

                if (isEndOfAnnotatedSegment) {
                    lineInfo.setAuthor(currentAnnotatedAuthor.get());
                    currentAnnotatedAuthor = Optional.empty();
                } else if (isUnknownAuthorSegment) {
                    currentAnnotatedAuthor = Optional.of(Author.UNKNOWN_AUTHOR);
                } else {
                    currentAnnotatedAuthor = newAnnotatedAuthor.filter(author -> !author.isIgnoringFile(filePath));
                }
            }
            currentAnnotatedAuthor.ifPresent(lineInfo::setAuthor);
        }
    }

    /**
     * Returns an optional {@code Author} corresponding to the @@author tag in {@code line}.
     * It looks for the corresponding {@code Author} object in the {@code authorAliasMap} inside
     * {@code authorConfig} and returns it. If an author config file is specified and the
     * author name found is not in it, then it returns {@code Author#UNKNOWN_AUTHOR} instead.
     *
     * @param line         Line to be analyzed.
     * @param authorConfig AuthorConfiguration for the analysis of this repo.
     * @return Optional {@code Author} found in the line.
     */
    private static Optional<Author> findAuthorInLine(String line, AuthorConfiguration authorConfig,
                                                     boolean isMarkdownFileType) {
        Optional<String> optionalName = extractAuthorName(line, isMarkdownFileType);

        optionalName.filter(name -> !authorConfig.containsName(name) && !AuthorConfiguration.hasAuthorConfigFile())
                .ifPresent(name -> authorConfig.addAuthor(new Author(name)));

        return optionalName.map(name -> authorConfig.getAuthor(name, name));
    }

    /**
     * Extracts the {@link Author} name that follows the specific format from {@code line} at {@code formatIndex}.
     *
     * @param line Line to extract the author's name from.
     * @return An optional string containing the author's name.
     */
    public static Optional<String> extractAuthorName(String line, boolean isMarkdownFileType) {
        return Optional.of(line)
                // gets component after AUTHOR_TAG
                .map(l -> l.split(AUTHOR_TAG))
                .filter(array -> array.length >= 2)
                // separates by end-comment format to obtain the author's name at the zeroth index
                .map(array -> array[1].trim().split(getClosingCommentTag(line, isMarkdownFileType)))
                .filter(array -> array.length > 0)
                .map(array -> array[0].trim())
                // checks if the author name is valid
                .filter(trimmedParameters -> PATTERN_AUTHOR_NAME_FORMAT.matcher(trimmedParameters).find());
    }

    /**
     * Returns the closing comment tag to split the {@code line} by.
     */
    private static String getClosingCommentTag(String line, boolean isMarkdownFileType) {
        return isMarkdownFileType ? COMMENT_FORMATS_MARKDOWN[getCommentTypeIndex(line, COMMENT_PATTERNS_MARKDOWN)][1]
                : COMMENT_FORMATS_GENERIC[getCommentTypeIndex(line, COMMENT_PATTERNS_GENERIC)][1];
    }

    /**
     * Generates regex for valid comment formats in which author tag is found, with {@code REGEX_AUTHOR_TAG_FORMAT}
     * flanked by {@code commentStart} and {@code commentEnd}.
     */
    private static String generateCommentRegex(String commentStart, String commentEnd) {
        return "^[\\s]*" + commentStart + "[\\s]*" + REGEX_AUTHOR_TAG_FORMAT + "[\\s]*(" + commentEnd + ")?[\\s]*$";
    }

    /**
     * Returns the index in {@code COMMENT_FORMATS} representing the type of comment the @@author tag line is.
     *
     * @param line The line to be checked
     * @return The index of the comment syntax type if the comment pattern matches, -1 if no match could be found
     */
    public static int getCommentTypeIndex(String line) {
        return getCommentTypeIndex(line, COMMENT_PATTERNS_GENERIC);
    }

    /**
     * Returns the index in the Markdown {@code COMMENT_FORMATS} representing the type of comment the @@author tag line
     * is.
     *
     * @param line The line to be checked
     * @param commentPatterns The comment patterns to check against
     * @return The index of the comment syntax type if the comment pattern matches, -1 if no match could be found
     */
    public static int getCommentTypeIndex(String line, Pattern[] commentPatterns) {
        requireNonNull(commentPatterns);
        for (int i = 0; i < commentPatterns.length; i++) {
            Pattern commentPattern = commentPatterns[i];
            Matcher matcher = commentPattern.matcher(line);
            if (matcher.find()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns true if line is one of the supported comment types.
     *
     * @param line Line to be checked.
     * @return True if line is a valid comment line.
     */
    private static boolean isValidCommentLine(String line, boolean isMarkdownFileType) {
        return isMarkdownFileType ? getCommentTypeIndex(line, COMMENT_PATTERNS_MARKDOWN) >= 0
                : getCommentTypeIndex(line, COMMENT_PATTERNS_GENERIC) >= 0;
    }
}
