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

    private String help;
    private String config;
    private String repos;
    private String view;
    private String output;
    private String sinceDate;
    private String untilDate;
    private String formats;
    private String ignoreStandaloneConfig;
    private String extraString;

    public InputBuilder() {
        this.help = "";
        this.config = "";
        this.repos = "";
        this.view = "";
        this.output = "";
        this.sinceDate = "";
        this.untilDate = "";
        this.formats = "";
        this.ignoreStandaloneConfig = "";
        this.extraString = "";
    }

    public String build() {
        String input = "";
        return input.concat(help)
                .concat(config)
                .concat(repos)
                .concat(view)
                .concat(output)
                .concat(sinceDate)
                .concat(untilDate)
                .concat(formats)
                .concat(ignoreStandaloneConfig)
                .concat(extraString);
    }

    public InputBuilder addHelp() {
        help = ArgsParser.HELP_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder addConfig(Path path) {
        config = ArgsParser.CONFIG_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder addRepos(String... paths) {
        repos = ArgsParser.REPO_FLAGS[0] + WHITESPACE;
        for (String path : paths) {
            repos += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder addView(Path... paths) {
        view = ArgsParser.VIEW_FLAGS[0] + WHITESPACE;
        for (Path path : paths) {
            view += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder addOutput(Path path) {
        output = ArgsParser.OUTPUT_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder addSinceDate(String date) {
        sinceDate = ArgsParser.SINCE_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder addUntilDate(String date) {
        untilDate = ArgsParser.UNTIL_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder addFormats(String formats) {
        this.formats = ArgsParser.FORMAT_FLAGS[0] + WHITESPACE + formats + WHITESPACE;
        return this;
    }

    public InputBuilder addIgnoreStandaloneConfig() {
        ignoreStandaloneConfig = ArgsParser.IGNORE_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder add(String content) {
        extraString += content + WHITESPACE;
        return this;
    }

    public InputBuilder reset() {
        this.help = "";
        this.config = "";
        this.repos = "";
        this.view = "";
        this.output = "";
        this.sinceDate = "";
        this.untilDate = "";
        this.formats = "";
        this.ignoreStandaloneConfig = "";
        this.extraString = "";
        return this;
    }
}
