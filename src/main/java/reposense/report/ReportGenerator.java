package reposense.report;

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
import reposense.git.GitClone;
import reposense.git.GitCloneException;
import reposense.git.GitShortlog;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.StandaloneConfig;
import reposense.parser.StandaloneConfigJsonParser;
import reposense.system.CommandRunnerProcess;
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

        cloneAndAnalyzeRepos(configs, outputPath);

        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
        FileUtil.writeJsonFile(new SummaryReportJson(configs, generationDate), getSummaryResultPath(outputPath));
        logger.info("The report is generated at " + outputPath);
    }

    private static void cloneAndAnalyzeRepos(List<RepoConfiguration> configs, String outputPath) throws IOException {
        boolean isPreviousRepoCloned = false;
        Path previousRepoReportDirectory = null;
        RepoLocation previousRepoLocation = null;

        for (int i = 0; i < configs.size(); i++) {
            RepoConfiguration config = configs.get(i);
            CommandRunnerProcess crp = null;
            boolean isCurrentRepoCloned = false;
            boolean isCurrentRepoSameAsPreviousRepo =
                    previousRepoLocation != null && previousRepoLocation.equals(config.getLocation());
            if (isCurrentRepoSameAsPreviousRepo) {
                isCurrentRepoCloned = true;
            } else {
                try {
                    crp = GitClone.startParallelClone(config);
                    isCurrentRepoCloned = true;
                } catch (GitCloneException gde) {
                    logger.log(Level.WARNING,
                            "Exception met while trying to clone the repo, will skip this repo.", gde);
                    handleGitCloneException(outputPath, config);
                }
            }
            if (isPreviousRepoCloned && previousRepoReportDirectory != null) {
                RepoConfiguration previousConfig = configs.get(i - 1);
                boolean isSameLocationAsCurrentConfig = config.getLocation().equals(previousConfig.getLocation());
                analyzeRepo(previousConfig, previousRepoReportDirectory, !isSameLocationAsCurrentConfig);
            }
            if (isCurrentRepoCloned && !isCurrentRepoSameAsPreviousRepo) {
                isCurrentRepoCloned = false;
                try {
                    GitClone.joinParallelClone(config, crp);
                    previousRepoReportDirectory = Paths.get(outputPath, config.getDisplayName());
                    FileUtil.createDirectory(previousRepoReportDirectory);
                    isCurrentRepoCloned = true;
                    previousRepoLocation = config.getLocation();
                } catch (GitCloneException gde) {
                    logger.log(Level.WARNING,
                            "Exception met while trying to clone the repo, will skip this repo.", gde);
                    handleGitCloneException(outputPath, config);
                } catch (IOException ioe) {
                    logger.log(Level.WARNING,
                            "Error has occurred while creating repo directory, will skip this repo.", ioe);
                } catch (RuntimeException rte) {
                    logger.log(Level.SEVERE, "Error has occurred during analysis, will skip this repo.", rte);
                }
            }
            isPreviousRepoCloned = isCurrentRepoCloned;
        }
        if (isPreviousRepoCloned && previousRepoReportDirectory != null) {
            analyzeRepo(configs.get(configs.size() - 1), previousRepoReportDirectory, true);
        }
    }

    private static void analyzeRepo(RepoConfiguration config, Path repoReportDirectory, boolean shouldDeleteDirectory) {
        // preprocess the config and repo
        updateRepoConfig(config);
        updateAuthorList(config);

        CommitContributionSummary commitSummary = CommitsReporter.generateCommitSummary(config);
        AuthorshipSummary authorshipSummary = AuthorshipReporter.generateAuthorshipSummary(config);
        generateIndividualRepoReport(commitSummary, authorshipSummary, repoReportDirectory.toString());

        if (shouldDeleteDirectory) {
            try {
                FileUtil.deleteDirectory(config.getRepoRoot());
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error deleting report directory.", ioe);
            }
        }
    }

    private static void handleGitCloneException(String outputPath, RepoConfiguration config) throws IOException {
        Path repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
        FileUtil.createDirectory(repoReportDirectory);
        generateEmptyRepoReport(repoReportDirectory.toString());
    }

    /**
     * Updates {@code config} with configuration provided by repository if exists.
     */
    public static void updateRepoConfig(RepoConfiguration config) {
        Path configJsonPath =
                Paths.get(config.getRepoRoot(), REPOSENSE_CONFIG_FOLDER, REPOSENSE_CONFIG_FILE).toAbsolutePath();

        if (!Files.exists(configJsonPath)) {
            logger.info(String.format("%s does not contain a standalone config file.", config.getLocation()));
            return;
        }

        if (config.isStandaloneConfigIgnored()) {
            logger.info(String.format("Ignoring standalone config file in %s.", config.getLocation()));
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
            List<Author> authorList = GitShortlog.getAuthors(config);
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
