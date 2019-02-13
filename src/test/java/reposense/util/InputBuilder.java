package reposense.util;

import java.nio.file.Path;

import reposense.parser.ArgsParser;

/**
 * A utility class to help with building command line input.
 * Example usage: <br>
 *     {@code String input = new InputBuilder().setSinceDate("27/01/2017").build();}
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

    public InputBuilder setHelp() {
        help = ArgsParser.HELP_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder setConfig(Path path) {
        config = ArgsParser.CONFIG_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder setRepos(String... paths) {
        repos = ArgsParser.REPO_FLAGS[0] + WHITESPACE;
        for (String path : paths) {
            repos += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder setView(Path... paths) {
        view = ArgsParser.VIEW_FLAGS[0] + WHITESPACE;
        for (Path path : paths) {
            view += path + WHITESPACE;
        }
        return this;
    }

    public InputBuilder setOutput(Path path) {
        output = ArgsParser.OUTPUT_FLAGS[0] + WHITESPACE + path + WHITESPACE;
        return this;
    }

    public InputBuilder setSinceDate(String date) {
        sinceDate = ArgsParser.SINCE_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder setUntilDate(String date) {
        untilDate = ArgsParser.UNTIL_FLAGS[0] + WHITESPACE + date + WHITESPACE;
        return this;
    }

    public InputBuilder setFormats(String formats) {
        this.formats = ArgsParser.FORMAT_FLAGS[0] + WHITESPACE + formats + WHITESPACE;
        return this;
    }

    public InputBuilder setIgnoreStandaloneConfig() {
        ignoreStandaloneConfig = ArgsParser.IGNORE_FLAGS[0] + WHITESPACE;
        return this;
    }

    public InputBuilder addExtraString(String content) {
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
