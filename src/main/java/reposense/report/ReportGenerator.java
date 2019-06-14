package reposense.report;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;

import reposense.RepoSense;
import reposense.authorship.AuthorshipReporter;
import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.CommitsReporter;
import reposense.commits.model.CommitContributionSummary;
import reposense.git.GitCheckout;
import reposense.git.GitShortlog;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.StandaloneConfig;
import reposense.parser.StandaloneConfigJsonParser;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Contains report generation related functionalities.
 */
public class ReportGenerator {
    private static final String REPOSENSE_CONFIG_FOLDER = "_reposense";
    private static final String REPOSENSE_CONFIG_FILE = "config.json";
    private static final Logger logger = LogsManager.getLogger(ReportGenerator.class);

    // zip file which contains all the report template files
    private static final String TEMPLATE_FILE = "/templateZip.zip";

    private static final String MESSAGE_INVALID_CONFIG_JSON = "%s Ignoring the config provided by %s (%s).";
    private static final String MESSAGE_ERROR_CREATING_DIRECTORY =
            "Error has occurred while creating repo directory for %s (%s), will skip this repo.";
    private static final String MESSAGE_ERROR_DURING_ANALYSIS =
            "Error has occurred during analysis of %s (%s), will skip this repo.";
    private static final String MESSAGE_NO_STANDALONE_CONFIG = "%s (%s) does not contain a standalone config file.";
    private static final String MESSAGE_IGNORING_STANDALONE_CONFIG = "Ignoring standalone config file in %s (%s).";
    private static final String MESSAGE_MALFORMED_STANDALONE_CONFIG = "%s/%s/%s is malformed for %s (%s).";
    private static final String MESSAGE_NO_AUTHORS_SPECIFIED =
            "%s (%s) has no authors specified, using all authors by default.";
    private static final String MESSAGE_START_ANALYSIS = "Analyzing %s (%s)...";
    private static final String MESSAGE_COMPLETE_ANALYSIS = "Analysis of %s (%s) completed!";
    private static final String MESSAGE_REPORT_GENERATED = "The report is generated at %s";
    private static final String MESSAGE_BRANCH_DOES_NOT_EXIST = "Branch %s does not exist in %s! Analysis terminated.";

    private static final String LOG_ERROR_CLONING = "Failed to clone from %s";
    private static final String LOG_BRANCH_DOES_NOT_EXIST = "Branch \"%s\" does not exist.";

    private static Date earliestSinceDate = null;
    private static Date latestUntilDate = null;

    private static Set<RepoConfiguration> failedRepoConfigsList = new HashSet<>();

    /**
     * Generates the authorship and commits JSON file for each repo in {@code configs} at {@code outputPath}, as
     * well as the summary JSON file of all the repos.
     *
     * @throws IOException if templateZip.zip does not exists in jar file.
     */
    public static void generateReposReport(List<RepoConfiguration> configs, String outputPath,
            String generationDate, Date cliSinceDate, Date cliUntilDate) throws IOException {
        InputStream is = RepoSense.class.getResourceAsStream(TEMPLATE_FILE);
        FileUtil.copyTemplate(is, outputPath);

        earliestSinceDate = null;
        latestUntilDate = null;
        
        cloneAndAnalyzeRepos(configs, outputPath);
        removeFailedRepoConfigs(configs);

        Date sinceDate = cliSinceDate == null ? earliestSinceDate : cliSinceDate;
        Date untilDate = cliUntilDate == null ? latestUntilDate : cliUntilDate;

        FileUtil.writeJsonFile(
                new SummaryReportJson(configs, generationDate, sinceDate, untilDate, RepoSense.getVersion(),
                        ErrorSummary.getInstance().getErrorList()),
                getSummaryResultPath(outputPath));
        logger.info(String.format(MESSAGE_REPORT_GENERATED, outputPath));
    }

    /**
     * Groups {@code RepoConfiguration} with the same {@code RepoLocation} together so that they are only cloned once.
     */
    private static Map<RepoLocation, List<RepoConfiguration>> groupConfigsByRepoLocation(
            List<RepoConfiguration> configs) {
        Map<RepoLocation, List<RepoConfiguration>> repoLocationMap = new HashMap<>();
        for (RepoConfiguration config : configs) {
            RepoLocation location = config.getLocation();

            if (!repoLocationMap.containsKey(location)) {
                repoLocationMap.put(location, new ArrayList<>());
            }
            repoLocationMap.get(location).add(config);
        }
        return repoLocationMap;
    }

    /**
     * Clone, analyze and generate the report for repositories in {@code repoLocationMap}.
     * Performs analysis and report generation of each repository in parallel with the cloning of the next repository.
     */
    private static void cloneAndAnalyzeRepos(List<RepoConfiguration> configs, String outputPath) {
        Map<RepoLocation, List<RepoConfiguration>> repoLocationMap = groupConfigsByRepoLocation(configs);
        RepoCloner repoCloner = new RepoCloner();
        RepoLocation clonedRepoLocation = null;

        List<RepoLocation> repoLocationList = new ArrayList<>(repoLocationMap.keySet());
        Set<RepoLocation> successfullyClonedRepos = new HashSet<>();

        // clones another repo in parallel while analyzing a cloned repo (if any)
        for (RepoLocation location : repoLocationList) {
            repoCloner.clone(repoLocationMap.get(location).get(0));

            if (clonedRepoLocation != null) {
                analyzeRepos(outputPath, repoLocationMap.get(clonedRepoLocation),
                        repoCloner.getCurrentRepoDefaultBranch());
                successfullyClonedRepos.add(clonedRepoLocation);
            }
            clonedRepoLocation = repoCloner.getClonedRepoLocation();
        }
        if (clonedRepoLocation != null) {
            analyzeRepos(outputPath, repoLocationMap.get(clonedRepoLocation),
                    repoCloner.getCurrentRepoDefaultBranch());
            successfullyClonedRepos.add(clonedRepoLocation);
        }

        // handles configs that failed to clone and clean up the cloned repo directory.
        handleCloningFailed(configs.stream()
                .filter(config -> !successfullyClonedRepos.contains(config.getLocation()))
                .collect(Collectors.toList()));
        repoCloner.cleanup();
    }

    /**
     * Analyzes all repos in {@code configs} and generates their report.
     */
    private static void analyzeRepos(String outputPath, List<RepoConfiguration> configs, String defaultBranch) {
        for (RepoConfiguration config : configs) {
            config.updateBranch(defaultBranch);

            Path repoReportDirectory;
            logger.info(String.format(MESSAGE_START_ANALYSIS, config.getLocation(), config.getBranch()));
            try {
                repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
                FileUtil.createDirectory(repoReportDirectory);
            } catch (IOException ioe) {
                logger.log(Level.WARNING,
                        String.format(MESSAGE_ERROR_CREATING_DIRECTORY, config.getLocation(), config.getBranch()), ioe);
                continue;
            } catch (RuntimeException rte) {
                logger.log(Level.SEVERE,
                        String.format(MESSAGE_ERROR_DURING_ANALYSIS, config.getLocation(), config.getBranch()), rte);
                continue;
            }

            try {
                GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, String.format(MESSAGE_BRANCH_DOES_NOT_EXIST,
                        config.getBranch(), config.getLocation()), e);
                handleBranchingFailed(config);
                continue;
            }
            analyzeRepo(config, repoReportDirectory.toString());
        }
    }

    /**
     * Analyzes repo specified by {@code config} and generates the report.
     */
    private static void analyzeRepo(RepoConfiguration config, String repoReportDirectory) {
        // preprocess the config and repo
        updateRepoConfig(config);
        updateAuthorList(config);

        CommitContributionSummary commitSummary = CommitsReporter.generateCommitSummary(config);
        AuthorshipSummary authorshipSummary = AuthorshipReporter.generateAuthorshipSummary(config);
        generateIndividualRepoReport(commitSummary, authorshipSummary, repoReportDirectory);
        logger.info(String.format(MESSAGE_COMPLETE_ANALYSIS, config.getLocation(), config.getBranch()));
    }

    /**
     * Updates {@code config} with configuration provided by repository if exists.
     */
    public static void updateRepoConfig(RepoConfiguration config) {
        Path configJsonPath =
                Paths.get(config.getRepoRoot(), REPOSENSE_CONFIG_FOLDER, REPOSENSE_CONFIG_FILE).toAbsolutePath();

        if (!Files.exists(configJsonPath)) {
            logger.info(String.format(MESSAGE_NO_STANDALONE_CONFIG, config.getLocation(), config.getBranch()));
            return;
        }

        if (config.isStandaloneConfigIgnored()) {
            logger.info(String.format(MESSAGE_IGNORING_STANDALONE_CONFIG, config.getLocation(), config.getBranch()));
            return;
        }

        try {
            StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(configJsonPath);
            config.update(standaloneConfig);
        } catch (JsonSyntaxException jse) {
            logger.warning(String.format(MESSAGE_MALFORMED_STANDALONE_CONFIG, config.getDisplayName(),
                    REPOSENSE_CONFIG_FOLDER, REPOSENSE_CONFIG_FILE, config.getLocation(), config.getBranch()));
        } catch (IllegalArgumentException iae) {
            logger.warning(String.format(MESSAGE_INVALID_CONFIG_JSON,
                    iae.getMessage(), config.getLocation(), config.getBranch()));
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
            logger.info(String.format(MESSAGE_NO_AUTHORS_SPECIFIED, config.getLocation(), config.getBranch()));
            List<Author> authorList = GitShortlog.getAuthors(config);
            config.setAuthorList(authorList);
        }
    }

    /**
     * Adds {@code failedRepoConfig} into the collated list of failed repos and logs into the list of errors in the
     *  summary.
     */
    private static void handleBranchingFailed(RepoConfiguration failedRepoConfig) {
        ErrorSummary.getInstance().addErrorMessage(failedRepoConfig.getDisplayName(),
                String.format(LOG_BRANCH_DOES_NOT_EXIST, failedRepoConfig.getBranch()));
        failedRepoConfigsList.add(failedRepoConfig);
    }

    /**
     * Adds all {@code failedRepoConfigs} into the list of failed repos and logs into the list of errors in the
     *  summary.
     */
    private static void handleCloningFailed(List<RepoConfiguration> failedRepoConfigs) {
        for (RepoConfiguration failedConfig : failedRepoConfigs) {
            ErrorSummary.getInstance().addErrorMessage(failedConfig.getDisplayName(),
                    String.format(LOG_ERROR_CLONING, failedConfig.getLocation()));
            failedRepoConfigsList.add(failedConfig);
        }
    }

    /**
     * Remove configs from {@code repoConfigs} that failed to clone or branch out previously.
     */
    private static void removeFailedRepoConfigs(List<RepoConfiguration> repoConfigs) {
        repoConfigs.removeAll(failedRepoConfigsList);
    }

    private static void generateIndividualRepoReport(
            CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary, String repoReportDirectory) {
        CommitReportJson commitReportJson = new CommitReportJson(commitSummary, authorshipSummary);
        FileUtil.writeJsonFile(commitReportJson, getIndividualCommitsPath(repoReportDirectory));
        FileUtil.writeJsonFile(authorshipSummary.getFileResults(), getIndividualAuthorshipPath(repoReportDirectory));
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

    public static void setEarliestSinceDate(Date newEarliestSinceDate) {
        if (earliestSinceDate == null || newEarliestSinceDate.before(earliestSinceDate)) {
            earliestSinceDate = newEarliestSinceDate;
        }
    }

    public static void setLatestUntilDate(Date newLatestUntilDate) {
        if (latestUntilDate == null || newLatestUntilDate.after(latestUntilDate)) {
            latestUntilDate = newLatestUntilDate;
        }
    }
}
