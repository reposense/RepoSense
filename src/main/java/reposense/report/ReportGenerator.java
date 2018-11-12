package reposense.report;

import static reposense.git.GitShortlog.extractAuthorsFromLog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;

import reposense.RepoSense;
import reposense.authorship.AuthorshipReporter;
import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.CommitsReporter;
import reposense.commits.model.CommitContributionSummary;
import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.StandaloneConfig;
import reposense.parser.StandaloneConfigJsonParser;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class ReportGenerator {
    private static final String REPOSENSE_CONFIG_FOLDER = "_reposense";
    private static final String REPOSENSE_CONFIG_FILE = "config.json";
    private static final Logger logger = LogsManager.getLogger(ReportGenerator.class);

    // zip file which contains all the dashboard template files
    private static final String TEMPLATE_FILE = "/templateZip.zip";

    private static final String MESSAGE_INVALID_CONFIG_JSON = "%s Ignoring the config provided by this repository.";

    /**
     * Generates the authorship and commits JSON file for each repo in {@code configs} at {@code outputPath}, as
     * well as the summary JSON file of all the repos.
     *
     * @throws IOException if templateZip.zip does not exists in jar file.
     */
    public static void generateReposReport(List<RepoConfiguration> configs, String outputPath,
            String generationDate) throws IOException {
        InputStream is = RepoSense.class.getResourceAsStream(TEMPLATE_FILE);
        FileUtil.copyTemplate(is, outputPath);

        for (RepoConfiguration config : configs) {
            Path repoReportDirectory;
            try {
                GitDownloader.downloadRepo(config);
                repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
                FileUtil.createDirectory(repoReportDirectory);
            } catch (GitDownloaderException gde) {
                logger.log(Level.WARNING, "Exception met while trying to clone the repo, will skip this one", gde);
                repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
                FileUtil.createDirectory(repoReportDirectory);
                generateEmptyRepoReport(repoReportDirectory.toString());
                continue;
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error while creating repo directory, will skip this repo.", ioe);
                continue;
            }

            // preprocess the config and repo
            updateRepoConfig(config);
            updateAuthorList(config);

            CommitContributionSummary commitSummary = CommitsReporter.generateCommitSummary(config);
            AuthorshipSummary authorshipSummary = AuthorshipReporter.generateAuthorshipSummary(config);
            generateIndividualRepoReport(commitSummary, authorshipSummary, repoReportDirectory.toString());

            try {
                FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error deleting report directory.", ioe);
            }
        }
        FileUtil.writeJsonFile(new SummaryReportJson(configs, generationDate), getSummaryResultPath(outputPath));
        logger.info("The report is generated at " + outputPath);
    }

    /**
     * Updates {@code config} with configuration provided by repository if exists.
     */
    public static void updateRepoConfig(RepoConfiguration config) {
        Path configJsonPath =
                Paths.get(config.getRepoRoot(), REPOSENSE_CONFIG_FOLDER, REPOSENSE_CONFIG_FILE).toAbsolutePath();

        if (config.isStandaloneConfigIgnored() || !Files.exists(configJsonPath)) {
            return;
        }

        try {
            StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(configJsonPath);
            config.update(standaloneConfig);
        } catch (JsonSyntaxException jse) {
            logger.warning(String.format("%s/%s/%s is malformed.",
                    config.getDisplayName(), REPOSENSE_CONFIG_FOLDER, REPOSENSE_CONFIG_FILE));
        } catch (IllegalArgumentException iae) {
            logger.warning(String.format(MESSAGE_INVALID_CONFIG_JSON, iae.getMessage()));
        } catch (IOException ioe) {
            throw new AssertionError(
                    "This exception should not happen as we have performed the file existence check.");
        }
    }

    /**
     * Find and update {@code config} with all the author identities if author list is empty.
     */
    private static void updateAuthorList(RepoConfiguration config) {
        if (config.getAuthorList().isEmpty()) {
            logger.info(String.format(
                    "%s has no authors specified, using all authors by default.", config.getDisplayName()));
            List<Author> authorList = extractAuthorsFromLog(config);
            config.setAuthorList(authorList);
        }
    }

    private static void generateIndividualRepoReport(
            CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary, String repoReportDirectory) {
        CommitReportJson commitReportJson = new CommitReportJson(commitSummary, authorshipSummary);
        FileUtil.writeJsonFile(commitReportJson, getIndividualCommitsPath(repoReportDirectory));
        FileUtil.writeJsonFile(authorshipSummary.getFileResults(), getIndividualAuthorshipPath(repoReportDirectory));
    }

    private static void generateEmptyRepoReport(String repoReportDirectory) {
        CommitReportJson emptyCommitReportJson = new CommitReportJson();
        FileUtil.writeJsonFile(emptyCommitReportJson, getIndividualCommitsPath(repoReportDirectory));
        FileUtil.writeJsonFile(Collections.emptyList(), getIndividualAuthorshipPath(repoReportDirectory));
    }

    private static String getSummaryResultPath(String targetFileLocation) {
        return targetFileLocation + "/summary.json";
    }

    private static String getIndividualAuthorshipPath(String repoReportDirectory) {
        return repoReportDirectory + "/authorship.json";
    }

    private static String getIndividualCommitsPath(String repoReportDirectory) {
        return repoReportDirectory + "/commits.json";
    }
}
