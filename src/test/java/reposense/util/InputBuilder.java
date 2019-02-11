package reposense.util;

import java.nio.file.Path;

import reposense.parser.ArgsParser;

/**
 * A utility class to help with building command line input.
 * Example usage: <br>
 *     {@code String input = new InputBuilder().addSince("27/01/2017").build();}
 */
public class InputBuilder {
    private static final String WHITESPACE = " ";

    private String input;

    public InputBuilder() {
        this.input = "";
    }

    public String build() {
        return input;
    }

    public InputBuilder addHelp() {
        input += ArgsParser.HELP_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder addConfig(Path path) {
        input += ArgsParser.CONFIG_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder addRepos(String... paths) {
        input += ArgsParser.REPO_FLAGS[0] + WHITESPACE;
        for (String path : paths) {
            input += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder addView(Path... paths) {
        input += ArgsParser.VIEW_FLAGS[0] + WHITESPACE;
        for (Path path : paths) {
            input += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder addOutput(Path path) {
        input += ArgsParser.OUTPUT_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder addSince(String date) {
        input += ArgsParser.SINCE_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder addUntil(String date) {
        input += ArgsParser.UNTIL_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder addFormats(String formats) {
        input += ArgsParser.FORMAT_FLAGS[0] + WHITESPACE + formats + WHITESPACE;
        return this;
    }

    public InputBuilder addIgnore() {
        input += ArgsParser.IGNORE_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder add(String content) {
        input += content + WHITESPACE;
        return this;
    }

    public InputBuilder addWhiteSpace(int num) {
        for (int i = 0; i < num; i++) {
            input += WHITESPACE;
        }
        return this;
    }

    public InputBuilder reset() {
        input = "";
        return this;
    }
}
