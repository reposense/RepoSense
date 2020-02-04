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

/**
 * Analyzes the authorship of a {@code FileInfo} using the given annotations on the file.
 */
public class AnnotatorAnalyzer {
    private static final String AUTHOR_TAG = "@@author";
    private static final String REGEX_AUTHOR_NAME_FORMAT = "([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])";
    private static final Pattern PATTERN_AUTHOR_NAME_FORMAT = Pattern.compile(REGEX_AUTHOR_NAME_FORMAT);
    private static final int MATCHER_GROUP_AUTHOR_NAME = 1;

    /**
     * Overrides the authorship information in {@code fileInfo} based on annotations given on the file.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, Map<String, Author> authorAliasMap) {
        Optional<Author> currentAnnotatedAuthor = Optional.empty();
        Path filePath = Paths.get(fileInfo.getPath());
        for (LineInfo lineInfo : fileInfo.getLines()) {
            if (lineInfo.getContent().contains(AUTHOR_TAG)) {
                Optional<Author> newAnnotatedAuthor = findAuthorInLine(lineInfo.getContent(), authorAliasMap);

                if (!newAnnotatedAuthor.isPresent()) {
                    //end of an author tag should belong to the current author too.
                    //in case an end author tag was used without out a corresponding starting tag, attribute the
                    // line to UNKNOWN_AUTHOR
                    lineInfo.setAuthor(currentAnnotatedAuthor.orElse(Author.UNKNOWN_AUTHOR));
                } else if (newAnnotatedAuthor.get().getIgnoreGlobMatcher().matches(filePath)) {
                    newAnnotatedAuthor = Optional.empty();
                }

                //set a new author
                currentAnnotatedAuthor = newAnnotatedAuthor;
            }
            currentAnnotatedAuthor.ifPresent(lineInfo::setAuthor);
        }
    }

    /**
     * Extracts the author name from the given {@code line}, finds the corresponding {@code Author}
     * in {@code authorAliasMap},and returns the result.
     * @return {@code Author#UNKNOWN_AUTHOR} if no matching {@code Author} is found.
     */
    private static Optional<Author> findAuthorInLine(String line, Map<String, Author> authorAliasMap) {
        try {
            String[] split = line.split(AUTHOR_TAG);
            String name = extractAuthorName(split[1]);
            if (name == null) {
                return Optional.empty();
            }
            return Optional.of(authorAliasMap.getOrDefault(name, Author.UNKNOWN_AUTHOR));
        } catch (ArrayIndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the name that follows the specific format.
     *
     * @return an empty string if no such author was found, the new author name otherwise
     */
    private static String extractAuthorName(String authorTagParameters) {
        String trimmedParameters = authorTagParameters.trim();
        Matcher matcher = PATTERN_AUTHOR_NAME_FORMAT.matcher(trimmedParameters);

        boolean foundMatch = matcher.find();
        if (!foundMatch) {
            return null;
        }

        return matcher.group(MATCHER_GROUP_AUTHOR_NAME);
    }
}
