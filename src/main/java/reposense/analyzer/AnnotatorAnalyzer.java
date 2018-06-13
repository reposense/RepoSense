package reposense.analyzer;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.LineInfo;
import reposense.dataobject.RepoConfiguration;


public class AnnotatorAnalyzer {

    private static final String AUTHOR_TAG = "@@author";
    private static final String REGEX_AUTHOR_NAME_FORMAT = "([a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9])";
    private static final Pattern PATTERN_AUTHOR_NAME_FORMAT = Pattern.compile(REGEX_AUTHOR_NAME_FORMAT);
    private static final int MATCHER_GROUP_AUTHOR_NAME = 1;

    /**
     * Overrides the authorship information in {@code fileInfo} based on given annotations.
     */
    public static void aggregateAnnotationAuthorInfo(FileInfo fileInfo, RepoConfiguration config) {
        Author currentAuthor = null;
        for (LineInfo lineInfo : fileInfo.getLines()) {
            if (lineInfo.getContent().contains(AUTHOR_TAG)) {
                Author newAuthor = findAuthorInLine(lineInfo.getContent(), config.getAuthorAliasMap());
                if (newAuthor == null) {
                    //end of an author tag should belong to this author too.
                    lineInfo.setAuthor(currentAuthor);
                }
                //set a new author
                currentAuthor = newAuthor;
            }
            if (currentAuthor != null) {
                lineInfo.setAuthor(currentAuthor);
            }
        }
    }

    private static Author findAuthorInLine(String line, TreeMap<String, Author> authorAliasMap) {
        try {
            String[] split = line.split(AUTHOR_TAG);
            String name = extractAuthorName(split[1]);
            if (name == null) {
                return null;
            }
            return authorAliasMap.get(name);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
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
