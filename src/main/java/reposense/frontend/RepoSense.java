package reposense.frontend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.report.AuthorshipReporter;
import reposense.report.CommitsReporter;
import reposense.system.LogsManager;
import reposense.util.Constants;
import reposense.util.FileUtil;

public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFilePath());
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
            generateReposReport(configs, cliArguments.getOutputFilePath().toAbsolutePath().toString());

        } catch (IOException ioe) {
            logger.warning(ioe.getMessage());
        } catch (ParseException pe) {
            logger.warning(pe.getMessage());
        }
    }

    public static String generateReposReport(List<RepoConfiguration> configs, String targetPath) {
        String reportName = generateReportName();
        FileUtil.copyTemplate(targetPath + File.separator + reportName);
        Path templateLocation = Paths.get(targetPath, reportName,
                Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS);

        for (RepoConfiguration config: configs) {
            Path repoReportDirectory = Paths.get(targetPath, reportName, config.getDisplayName());
            try {
                FileUtil.copyDirectoryFiles(templateLocation, repoReportDirectory);
                CommitsReporter.generateCommitReport(config, repoReportDirectory.toString());
                logger.info("Commit report generated!");

                AuthorshipReporter.generateAuthorshipReport(config, repoReportDirectory.toString());
                logger.info("Authorship report generated!");
                FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Error deleting report directory.", ioe);
            }
        }
        FileUtil.writeJsonFile(configs, getSummaryResultPath(reportName, targetPath));
        return reportName;
    }

    private static String getSummaryResultPath(String reportName, String targetFileLocation) {
        return targetFileLocation + "/" + reportName + "/summary.json";
    }


    private static String generateReportName() {
        return Constants.REPORT_NAME_FORMAT.format(new Date());
    }
}
