package reposense.authorship.analyzer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
 * unknown author
 */
public class AnnotatorAnalyzer {
    private static final String AUTHOR_TAG = "@@author";
    // GitHub username format
    private static final String REGEX_AUTHOR_NAME_FORMAT = "^[a-zA-Z0-9](?:[a-zA-Z0-9]|-(?=[a-zA-Z0-9])){0,38}$";
    private static final Pattern PATTERN_AUTHOR_NAME_FORMAT = Pattern.compile(REGEX_AUTHOR_NAME_FORMAT);
    private static final String REGEX_AUTHOR_TAG_FORMAT = "@@author(\\s+[^\\s]+)?";

    private static final String[][] COMMENT_FORMATS = {
            {"//", "\\s"},
            {"/\\*", "\\*/"},
            {"#", "\\s"},
            {"<!--", "-->"},
            {"%", "\\s"},
    };

    private static final Pattern[] COMMENT_PATTERNS = {
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[0][0], COMMENT_FORMATS[0][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[1][0], COMMENT_FORMATS[1][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[2][0], COMMENT_FORMATS[2][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[3][0], COMMENT_FORMATS[3][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[4][0], COMMENT_FORMATS[4][1]))
    };

    /**
     * Overrides the authorship information in {@code fileInfo} based on annotations given on the file.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, AuthorConfiguration authorConfig) {
        Optional<Author> currentAnnotatedAuthor = Optional.empty();
        Path filePath = Paths.get(fileInfo.getPath());
        for (LineInfo lineInfo : fileInfo.getLines()) {
            String lineContent = lineInfo.getContent();
            currentAnnotatedAuthor.ifPresent(lineInfo::setAuthor);

            if (lineContent.contains(AUTHOR_TAG)) {
                int formatIndex = checkValidCommentLine(lineContent);
                if (formatIndex >= 0) {
                    Optional<Author> newAnnotatedAuthor = findAuthorInLine(lineContent, authorConfig, formatIndex);

                    boolean endOfAnnotatedSegment =
                            currentAnnotatedAuthor.isPresent() && !newAnnotatedAuthor.isPresent();
                    boolean isUnknownAuthor =
                            !newAnnotatedAuthor.isPresent() && !currentAnnotatedAuthor.isPresent();

                    if (endOfAnnotatedSegment) {
                        currentAnnotatedAuthor = Optional.empty();
                    } else if (isUnknownAuthor) {
                        currentAnnotatedAuthor = Optional.of(Author.UNKNOWN_AUTHOR);
                    } else {
                        currentAnnotatedAuthor = newAnnotatedAuthor.filter(author -> author.isIgnoringFile(filePath));
                    }
                }

                // Overrides the current line author if it has changed
                currentAnnotatedAuthor.ifPresent(lineInfo::setAuthor);
            }
        }
    }

    /**
     * Extracts the author name from the given {@code line}, finds the corresponding {@code Author}
     * in {@code authorAliasMap}, and returns this {@code Author} stored in an {@code Optional}.
     * @return {@code Optional.of(Author#UNKNOWN_AUTHOR)} if there is an author config file and
     *              no matching {@code Author} is found,
     *         {@code Optional.empty()} if an end author tag is used (i.e. "@@author"),
     *         {@code Optional.of(Author#tagged author)} otherwise.
     */
    private static Optional<Author> findAuthorInLine(String line, AuthorConfiguration authorConfig, int formatIndex) {
        Map<String, Author> authorAliasMap = authorConfig.getAuthorDetailsToAuthorMap();
        Optional<String> optionalName = extractAuthorName(line, formatIndex);

        optionalName.filter(name -> !authorAliasMap.containsKey(name) && !AuthorConfiguration.hasAuthorConfigFile())
                .ifPresent(name -> authorConfig.addAuthor(new Author(name)));
        return optionalName.map(name -> authorAliasMap.getOrDefault(name, Author.UNKNOWN_AUTHOR));
    }

    /**
     * Extracts the name that follows the specific format.
     *
     * @return an empty string if no such author was found, the new author name otherwise
     */
    public static Optional<String> extractAuthorName(String line, int formatIndex) {
        return Optional.of(line)
                // gets component after AUTHOR_TAG
                .map(l -> l.split(AUTHOR_TAG))
                .filter(array -> array.length >= 2)
                // separates by end-comment format to obtain the author's name at the zeroth index
                .map(array -> array[1].trim().split(COMMENT_FORMATS[formatIndex][1]))
                .filter(array -> array.length > 0)
                .map(array -> array[0].trim())
                // checks if the author name is valid
                .filter(trimmedParameters -> PATTERN_AUTHOR_NAME_FORMAT.matcher(trimmedParameters).find());
    }

    private static String generateCommentRegex(String commentStart, String commentEnd) {
        return "^[\\s]*" + commentStart + "[\\s]*" + REGEX_AUTHOR_TAG_FORMAT + "[\\s]*(" + commentEnd + ")?[\\s]*$";
    }

    /**
     * Checks if the line is a valid @@author tag comment line
     * @param line The line to be checked
     * @return The index of the comment if the comment pattern matches, -1 if no match could be found
     */
    public static int checkValidCommentLine(String line) {
        for (int i = 0; i < COMMENT_PATTERNS.length; i++) {
            Pattern commentPattern = COMMENT_PATTERNS[i];
            Matcher matcher = commentPattern.matcher(line);
            if (matcher.find()) {
                return i;
            }
        }
        return -1;
    }
}
