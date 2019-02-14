package reposense.util;

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
        this.input = new StringBuilder();
    }

    public String build() {
        return input.toString();
    }

    /**
     * Add a help flag to the input.
     * This method should only be called once in one build.
     */
    public InputBuilder addHelp() {
        input.append(ArgsParser.HELP_FLAGS[0] + WHITESPACE);
        return this;
    }

    /**
     * Add a config flag with the config folder path to the input.
     * This method should only be called once in one build.
     *
     * @param path The config folder path.
     */
    public InputBuilder addConfig(Path path) {
        input.append(ArgsParser.CONFIG_FLAGS[0] + WHITESPACE + path + WHITESPACE);
        return this;
    }

    /**
     * Add a repo flag with the several repo paths to the input.
     * This method should only be called once in one build.
     *
     * @param paths The repo paths.
     */
    public InputBuilder addRepos(String... paths) {
        input.append(ArgsParser.REPO_FLAGS[0] + WHITESPACE);
        for (String path : paths) {
            input.append(path + WHITESPACE);
        }
        return this;
    }

    /**
     * Add a view flag with the view folder path to the input.
     * This method should only be called once in one build.
     *
     * @param path The view folder path.
     */
    public InputBuilder addView(Path path) {
        input.append(ArgsParser.VIEW_FLAGS[0] + WHITESPACE + path + WHITESPACE);
        return this;
    }

    /**
     * Add a view flag only to the input.
     * This method should only be called once in one build.
     */
    public InputBuilder addView() {
        input.append(ArgsParser.VIEW_FLAGS[0] + WHITESPACE);
        return this;
    }

    /**
     * Add a output flag with the output folder path to the input.
     * This method should only be called once in one build.
     *
     * @param path The output folder path.
     */
    public InputBuilder addOutput(Path path) {
        input.append(ArgsParser.OUTPUT_FLAGS[0] + WHITESPACE + path + WHITESPACE);
        return this;
    }

    /**
     * Add a since flag with the since date to the input.
     * This method should only be called once in one build.
     *
     * @param date The since date.
     */
    public InputBuilder addSinceDate(String date) {
        input.append(ArgsParser.SINCE_FLAGS[0] + WHITESPACE + date + WHITESPACE);
        return this;
    }

    /**
     * Add a until flag with the until date to the input.
     * This method should only be called once in one build.
     *
     * @param date The until date.
     */
    public InputBuilder addUntilDate(String date) {
        input.append(ArgsParser.UNTIL_FLAGS[0] + WHITESPACE + date + WHITESPACE);
        return this;
    }

    /**
     * Add a format flag with the formats to the input.
     * This method should only be called once in one build.
     *
     * @param formats The formats that need to be evaluated.
     */
    public InputBuilder addFormats(String formats) {
        input.append(ArgsParser.FORMAT_FLAGS[0] + WHITESPACE + formats + WHITESPACE);
        return this;
    }

    /**
     * Add a ignoreStandaloneConfig flag to the input.
     * This method should only be called once in one build.
     */
    public InputBuilder addIgnoreStandaloneConfig() {
        input.append(ArgsParser.IGNORE_FLAGS[0] + WHITESPACE);
        return this;
    }

    /**
     * Add extra content to the input.
     */
    public InputBuilder add(String content) {
        input.append(content + WHITESPACE);
        return this;
    }

    /**
     * Add {@code num} white spaces to the input.
     *
     * @param num The number of white spaces to add.
     */
    public InputBuilder addWhiteSpace(int num) {
        for (int i = 0; i < num; i++) {
            input.append(WHITESPACE);
        }
        return this;
    }

    /**
     * Reset the {@code InputBuilder}.
     */
    public InputBuilder reset() {
        input = new StringBuilder();
        return this;
    }
}
