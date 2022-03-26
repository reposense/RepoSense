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
 * unknown author.
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
     * Updates {@code authorConfig} if a new {@link Author} is found.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, AuthorConfiguration authorConfig) {
        Optional<Author> currentAnnotatedAuthor = Optional.empty();
        Path filePath = Paths.get(fileInfo.getPath());
        for (LineInfo lineInfo : fileInfo.getLines()) {
            String lineContent = lineInfo.getContent();
            if (lineContent.contains("@@author")) {
                int formatIndex = checkValidCommentLine(lineContent);
                if (formatIndex >= 0) {
                    Optional<Author> newAnnotatedAuthor = findAuthorInLine(lineContent, authorConfig,
                            currentAnnotatedAuthor, formatIndex);

                    if (!newAnnotatedAuthor.isPresent()) {
                        // end of an author tag should belong to the current author too.
                        lineInfo.setAuthor(currentAnnotatedAuthor.get());
                    } else if (newAnnotatedAuthor.get().isIgnoringFile(filePath)) {
                        newAnnotatedAuthor = Optional.empty();
                    }

                    //set a new author
                    currentAnnotatedAuthor = newAnnotatedAuthor;
                }
            }
            currentAnnotatedAuthor.ifPresent(lineInfo::setAuthor);
        }
    }

    /**
     * Extracts the author name from the given {@code line}, finds out if {@link Author} is already present
     * in {@code authorConfig}, and returns this {@link Author} stored in an {@link Optional}.
     * Uses {@code currentAnnotatedAuthor} to keep track of which {@link Author} to attribute a line to.
     *
     * @return {@link Optional} of {@link Author#UNKNOWN_AUTHOR} if there is an author config file and
     *              no matching {@link Author} is found,
     *         an empty {@link Optional} if an end author tag is used (i.e. "@@author"),
     *         {@link Optional} of the tagged author otherwise.
     */
    private static Optional<Author> findAuthorInLine(String line, AuthorConfiguration authorConfig,
            Optional<Author> currentAnnotatedAuthor, int formatIndex) {
        try {
            Map<String, Author> authorAliasMap = authorConfig.getAuthorDetailsToAuthorMap();
            String name = extractAuthorName(line, formatIndex);
            if (name == null) {
                if (!currentAnnotatedAuthor.isPresent()) {
                    // Attribute to unknown author if an empty author tag was provided, but not as an end author tag
                    return Optional.of(Author.UNKNOWN_AUTHOR);
                }
                return Optional.empty();
            }
            if (!authorAliasMap.containsKey(name) && !AuthorConfiguration.hasAuthorConfigFile()) {
                authorConfig.addAuthor(new Author(name));
            }
            return Optional.of(authorAliasMap.getOrDefault(name, Author.UNKNOWN_AUTHOR));
        } catch (ArrayIndexOutOfBoundsException e) {
            if (!currentAnnotatedAuthor.isPresent()) {
                return Optional.of(Author.UNKNOWN_AUTHOR);
            }
            return Optional.empty();
        }
    }

    /**
     * Extracts the {@link Author} name that follows the specific format from {@code line} at {@code formatIndex}.
     *
     * @return an empty string if no such author was found, the new author name otherwise.
     */
    public static String extractAuthorName(String line, int formatIndex) {
        String[] splitByAuthorTag = line.split(AUTHOR_TAG);
        if (splitByAuthorTag.length < 2) {
            return null;
        }

        String[] splitByCommentFormat = splitByAuthorTag[1].trim().split(COMMENT_FORMATS[formatIndex][1]);
        if (splitByCommentFormat.length == 0) {
            return null;
        }
        String authorTagParameters = splitByCommentFormat[0];
        String trimmedParameters = authorTagParameters.trim();
        Matcher matcher = PATTERN_AUTHOR_NAME_FORMAT.matcher(trimmedParameters);

        boolean foundMatch = matcher.find();
        return (foundMatch) ? trimmedParameters : null;
    }

    /**
     * Generates regex for valid comment formats in which author tag is found, with {@code REGEX_AUTHOR_TAG_FORMAT}
     * flanked by {@code commentStart} and {@code commentEnd}.
     */
    private static String generateCommentRegex(String commentStart, String commentEnd) {
        return "^[\\s]*" + commentStart + "[\\s]*" + REGEX_AUTHOR_TAG_FORMAT + "[\\s]*(" + commentEnd + ")?[\\s]*$";
    }

    /**
     * Checks if the {@code line} is a valid @@author tag comment line.
     *
     * @param line The line to be checked.
     * @return The index of the comment if the comment pattern matches, -1 if no match could be found.
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
