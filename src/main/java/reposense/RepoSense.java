package reposense;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.model.CommitContributionSummary;
import reposense.model.Author;
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

    private static void handleReposenseSourceFolder(CliArguments cliArguments, Path reposenseSourceFolderPath)
            throws IOException {
        Path csvFile = Paths.get(reposenseSourceFolderPath.toString(), "config.csv");
        handleCsvFile(cliArguments, csvFile);
    }

    private static void handleCsvFile(CliArguments cliArguments, Path csvFilePath) throws IOException {
        List<RepoConfiguration> configs = CsvParser.parse(csvFilePath);
        RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
        ReportGenerator.generateReposReport(configs, cliArguments.getOutputPath().toAbsolutePath().toString());
        FileUtil.zip(cliArguments.getOutputPath().toAbsolutePath(), ".json");
    }

    private static void handleReposenseReportFolder(CliArguments cliArguments, Path reposenseReportFolderPath)
            throws IOException {
        logger.warning("Warning -since, -until and -output will be ignored.");
        List<RepoConfiguration> configs = FileUtil.getRepoConfigsFromJson(reposenseReportFolderPath);

        for (RepoConfiguration config : configs) {
            List<AuthorshipSummary> authorshipSummaryList =
                    FileUtil.getAuthorshipFromJson(reposenseReportFolderPath, config.getDisplayName());
            CommitContributionSummary commitContributionSummary =
                    FileUtil.getCommitContributionSummaryFromJson(reposenseReportFolderPath, config.getDisplayName());
            Map<Author, Float> authorContributionVariance =
                    FileUtil.getAuthorContributionVarianceFromJson(reposenseReportFolderPath, config.getDisplayName());
        }
    }

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            switch (cliArguments.getGenericInputType()) {
            case CSV_FILE:
                System.out.println("Doing csv");
                // Run with RepoSense.jar config.csv
                handleCsvFile(cliArguments, cliArguments.getGenericInputValue());
                break;
            case REPOSENSE_CONFIG_FOLDER:
                System.out.println("Doing _reposense");
                handleReposenseSourceFolder(cliArguments, cliArguments.getGenericInputValue());
                break;
            case REPOSENSE_REPORT_FOLDER:
                // Run with RepoSense.jar path/to/docs
                System.out.println("Doing docs");
                handleReposenseReportFolder(cliArguments, cliArguments.getGenericInputValue());
                break;
            default:
                // Will not hit this line
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
