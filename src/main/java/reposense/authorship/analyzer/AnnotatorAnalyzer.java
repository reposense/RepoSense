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
    private static final String REGEX_AUTHOR_TAG_FORMAT = "@@author(\\s+.*)?";

    private static final String[][] COMMENT_FORMATS = {
            {"//", null},
            {"/\\*", "\\*/"},
            {"#", null},
            {"<!--", "-->"},
            {"%", null},
            {"\\[.*]:\\s*#\\s*\\(", "\\)"},
            {"<!---", "--->"}
    };

    private static final Pattern[] COMMENT_PATTERNS = {
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[0][0], COMMENT_FORMATS[0][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[1][0], COMMENT_FORMATS[1][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[2][0], COMMENT_FORMATS[2][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[3][0], COMMENT_FORMATS[3][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[4][0], COMMENT_FORMATS[4][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[5][0], COMMENT_FORMATS[5][1])),
            Pattern.compile(generateCommentRegex(COMMENT_FORMATS[6][0], COMMENT_FORMATS[6][1]))
    };

    /**
     * Overrides the authorship information in {@code fileInfo} based on annotations given on the file.
     *
     * @param fileInfo FileInfo to be further analyzed with author annotations.
     * @param authorConfig AuthorConfiguration for current analysis.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, AuthorConfiguration authorConfig) {
        Optional<Author> currentAnnotatedAuthor = Optional.empty();
        Path filePath = Paths.get(fileInfo.getPath());
        for (LineInfo lineInfo : fileInfo.getLines()) {
            String lineContent = lineInfo.getContent();
            if (lineContent.contains(AUTHOR_TAG) && isValidCommentLine(lineContent)) {
                Optional<Author> newAnnotatedAuthor = findAuthorInLine(lineContent, authorConfig);
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
     * @param line Line to be analyzed.
     * @param authorConfig AuthorConfiguration for the analysis of this repo.
     * @return Optional {@code Author} found in the line.
     */
    private static Optional<Author> findAuthorInLine(String line, AuthorConfiguration authorConfig) {
        Optional<String> optionalName = extractAuthorName(line);

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
    public static Optional<String> extractAuthorName(String line) {
        return Optional.of(line)
                // gets component after AUTHOR_TAG
                .map(l -> l.split(AUTHOR_TAG))
                .filter(array -> array.length >= 2)
                // separates by end-comment format to obtain the author's name at the zeroth index
                .map(array -> COMMENT_FORMATS[getCommentTypeIndex(line)][1] != null
                        ? array[1].trim().split(COMMENT_FORMATS[getCommentTypeIndex(line)][1])
                        : new String[]{ array[1].trim() })
                .filter(array -> array.length > 0)
                .map(array -> array[0].trim())
                // checks if the author name is not empty
                .filter(trimmedParameters -> !trimmedParameters.isEmpty());
    }

    /**
     * Generates regex for valid comment formats in which author tag is found, with {@code REGEX_AUTHOR_TAG_FORMAT}
     * flanked by {@code commentStart} and {@code commentEnd}.
     */
    private static String generateCommentRegex(String commentStart, String commentEnd) {
        if (commentEnd == null) {
            return "^[\\s]*" + commentStart + "[\\s]*" + REGEX_AUTHOR_TAG_FORMAT + "[\\s]*$";
        }
        return "^[\\s]*" + commentStart + "[\\s]*" + REGEX_AUTHOR_TAG_FORMAT + "[\\s]*(" + commentEnd + ")?[\\s]*$";
    }

    /**
     * Returns the index in {@code COMMENT_FORMATS} representing the type of comment the @@author tag line is.
     *
     * @param line The line to be checked
     * @return The index of the comment syntax type if the comment pattern matches, -1 if no match could be found
     */
    public static int getCommentTypeIndex(String line) {
        for (int i = 0; i < COMMENT_PATTERNS.length; i++) {
            Pattern commentPattern = COMMENT_PATTERNS[i];
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
    private static boolean isValidCommentLine(String line) {
        return getCommentTypeIndex(line) >= 0;
    }
}
