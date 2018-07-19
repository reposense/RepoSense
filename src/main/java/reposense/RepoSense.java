package reposense;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.CliArguments;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.parser.ParseException;
import reposense.report.ReportGenerator;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class RepoSense {
    public static final List<String> DEFAULT_FILE_FORMATS = Arrays.asList("java", "adoc", "js", "md", "css",
            "html", "cs", "json", "xml", "py", "fxml", "tag", "jsp", "gradle");

    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    private static void handleCsvFile(CliArguments cliArguments, Path csvFilePath) throws IOException {
        List<RepoConfiguration> configs = CsvParser.parse(csvFilePath);
        RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
        ReportGenerator.generateReposReport(configs, cliArguments.getOutputPath().toAbsolutePath().toString());
        FileUtil.zip(cliArguments.getOutputPath().toAbsolutePath(), ".json");
    }

    private static void handleReposenseSourceFolder(CliArguments cliArguments, Path reposenseSourceFolderPath)
            throws IOException {
        Path repoConfigCsvFile = Paths.get(reposenseSourceFolderPath.toString(), "repo-config.csv");
        Path authorConfigCsvFile = Paths.get(reposenseSourceFolderPath.toString(), "author-config.csv");

        handleCsvFile(cliArguments, repoConfigCsvFile);
    }

    private static void handleReposenseReportFolder() {
        logger.warning("Warning -since, -until and -output will be ignored.");

        // Stub
    }

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            switch (cliArguments.getGenericInputType()) {
            case CSV_FILE:
                // Run with RepoSense.jar [path/to/*.csv]
                handleCsvFile(cliArguments, cliArguments.getGenericInputValue());
                break;
            case REPOSENSE_CONFIG_FOLDER:
                //Run with RepoSense.jar [path/to/_reposense]
                handleReposenseSourceFolder(cliArguments, cliArguments.getGenericInputValue());
                break;
            case REPOSENSE_REPORT_FOLDER:
                // Run with RepoSense.jar path/to/docs
                handleReposenseReportFolder();
                break;
            default:
                throw new AssertionError("Should not hit this line");
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        } catch (ParseException pe) {
            if (!pe.getMessage().isEmpty()) {
                logger.log(Level.WARNING, pe.getMessage(), pe);
            }
        }
    }
}
