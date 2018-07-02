package reposense.parser;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import reposense.util.FileUtil;

public class GenericInputArgumentType implements ArgumentType<Optional<GenericInput>> {
    public static final List<String> REPOSENSE_REPORT_FOLDER_FILES =
            Arrays.asList("summary.json", "index.html", "summary.js");
    public static final List<String> REPOSENSE_CONFIG_FOLDER_FILES =
            Arrays.asList("repo-config.csv", "author-config.csv");

    private static final PathMatcher REPOSENSE_REPORT_FOLDER_FILES_MATCHER =
            FileSystems.getDefault().getPathMatcher(
                    "glob:{summary.json,index.html,summary.js}");

    private static final PathMatcher REPOSENSE_CONFIG_FOLDER_FILES_MATCHER =
            FileSystems.getDefault().getPathMatcher(
                    "glob:{repo-config.csv,author-config.csv}");

    private static final PathMatcher CSV_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.csv");

    private static Pattern GIT_URL_PATTERN =
            Pattern.compile("(?:git|ssh|https?|git@[-\\w.]+):(\\/\\/)?(.*?)(\\.git)(\\/?|\\#[-\\d\\w._]+?)$");

    private static final String PARSE_EXCEPTION_INVALID_TYPES =
            String.format("Unable to parse input. It has be to a %s.", GenericInput.VALID_TYPES_MESSAGE);
    private static final String PARSE_EXCEPTION_NO_REPOSENSE_FOLDER =
            String.format("Unable to find %s, %s or config csv file in current working directory.",
                    ArgsParser.REPOSENSE_CONFIG_FOLDER, ArgsParser.REPOSENSE_REPORT_FOLDER);

    private static boolean isCsvConfigFile(Path path) {
        return FileUtil.isFileExists(CSV_MATCHER, path);
    }

    private static boolean isReposenseConfigFolder(Path folderPath) {
        return FileUtil.isFolderAndContainsExpectedFiles(
                REPOSENSE_CONFIG_FOLDER_FILES_MATCHER, folderPath, REPOSENSE_CONFIG_FOLDER_FILES.size(), true);
    }

    private static boolean isReposenseReportFolder(Path folderPath) {
        return FileUtil.isFolderAndContainsExpectedFiles(
                REPOSENSE_REPORT_FOLDER_FILES_MATCHER, folderPath, REPOSENSE_REPORT_FOLDER_FILES.size(), true);
    }

    public static Optional<GenericInput> getDefault() throws ParseException {
        // Greedy search for ./docs folder
        Optional<GenericInput> reportFolder = parsePath(ArgsParser.REPOSENSE_REPORT_FOLDER);

        if (reportFolder.isPresent()) {
            return reportFolder;
        }

        Optional<GenericInput> configFolder = parsePath(ArgsParser.REPOSENSE_CONFIG_FOLDER);

        if (configFolder.isPresent()) {
            return configFolder;
        }

        throw new ParseException(PARSE_EXCEPTION_NO_REPOSENSE_FOLDER);
    }

    @Override
    public Optional<GenericInput> convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        Path path = Paths.get(value, ArgsParser.REPOSENSE_REPORT_FOLDER);
        Optional<GenericInput> reportFolder = parsePath(path.toString());

        if (reportFolder.isPresent()) {
            return reportFolder;
        }

        path = Paths.get(value, ArgsParser.REPOSENSE_CONFIG_FOLDER);
        Optional<GenericInput> configFolder = parsePath(path.toString());

        if (configFolder.isPresent()) {
            return configFolder;
        }

        Optional<GenericInput> csvFile = parsePath(value);

        if (csvFile.isPresent()) {
            return csvFile;
        }

        throw new ArgumentParserException(String.format(PARSE_EXCEPTION_INVALID_TYPES, value), parser);
    }

    private static Optional<GenericInput> parsePath(String strPath) {
        boolean isReposenseReportFolder = isReposenseReportFolder(Paths.get(strPath));
        boolean isReposenseConfigFolder = isReposenseConfigFolder(Paths.get(strPath));
        boolean isCsvConfigFile = isCsvConfigFile(Paths.get(strPath).toAbsolutePath());

        // Prioritise docs folder for incremental
        if (isReposenseReportFolder) {
            return Optional.of(new GenericInput(GenericInputType.REPOSENSE_REPORT_FOLDER, strPath));
        }

        if (isReposenseConfigFolder) {
            return Optional.of(new GenericInput(GenericInputType.REPOSENSE_CONFIG_FOLDER, strPath));
        }

        if (isCsvConfigFile) {
            return Optional.of(new GenericInput(GenericInputType.CSV_FILE, strPath));
        }

        return Optional.empty();
    }
}
