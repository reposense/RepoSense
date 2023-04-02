package reposense.wizard;

import java.nio.file.Path;

import reposense.parser.ArgsParser;

/**
 * A utility class to help with building command line input.
 * Example usage: <br>
 *     {@code String input = new InputBuilder().addSinceDate("27/01/2017").build();}
 */
public class InputBuilder {
    private static final String WHITESPACE = " ";

    private StringBuilder input;

    public InputBuilder() {
        init();
    }

    /**
     * Initialize variables to default values.
     * Used by {@link InputBuilder#InputBuilder() constructor}.
     */
    private void init() {
        input = new StringBuilder();
    }

    /**
     * Returns the {@code input} generated from this {@link InputBuilder}.
     */
    public String build() {
        return input.toString();
    }

    /**
     * Adds the repo flag with the {@code paths} as arguments to the input.
     * This method should only be called once in one build.
     *
     * @param paths The repo paths.
     */
    public InputBuilder addRepos(String paths) {
        input.append(ArgsParser.REPO_FLAGS[0] + WHITESPACE + paths + WHITESPACE);
        return this;
    }

    /**
     * Adds the view flag with the {@code path} as argument to the input.
     * This method should only be called once in one build.
     *
     * @param path The view folder path.
     */
    public InputBuilder addView(Path path) {
        input.append(ArgsParser.VIEW_FLAGS[0] + WHITESPACE + addQuotationMarksToPath(path) + WHITESPACE);
        return this;
    }

    /**
     * Adds the view flag only to the input.
     * This method should only be called once in one build.
     */
    public InputBuilder addView() {
        input.append(ArgsParser.VIEW_FLAGS[0] + WHITESPACE);
        return this;
    }

    /**
     * Adds the since flag with the {@code date} as argument to the input.
     * This method should only be called once in one build.
     *
     * @param date The since date.
     */
    public InputBuilder addSinceDate(String date) {
        input.append(ArgsParser.SINCE_FLAGS[0] + WHITESPACE + date + WHITESPACE);
        return this;
    }

    /**
     * Adds the until flag with the {@code date} as argument to the input.
     * This method should only be called once in one build.
     *
     * @param date The until date.
     */
    public InputBuilder addUntilDate(String date) {
        input.append(ArgsParser.UNTIL_FLAGS[0] + WHITESPACE + date + WHITESPACE);
        return this;
    }


    private static String addQuotationMarksToPath(String path) {
        return '"' + path + '"';
    }

    private static String addQuotationMarksToPath(Path path) {
        return addQuotationMarksToPath(path.toString());
    }

}
