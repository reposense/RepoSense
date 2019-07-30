package reposense.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import reposense.parser.SinceDateArgumentType;
import reposense.parser.StandaloneConfigJsonParser;
import reposense.report.exception.NoAuthorsWithCommitsFoundException;
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
    private static final String MESSAGE_NO_AUTHORS_WITH_COMMITS_FOUND =
            "No authors found with commits for %s (%s).";
    private static final String MESSAGE_START_ANALYSIS = "Analyzing %s (%s)...";
    private static final String MESSAGE_COMPLETE_ANALYSIS = "Analysis of %s (%s) completed!";
    private static final String MESSAGE_REPORT_GENERATED = "The report is generated at %s";
    private static final String MESSAGE_BRANCH_DOES_NOT_EXIST = "Branch %s does not exist in %s! Analysis terminated.";

    private static final String LOG_ERROR_CLONING = "Failed to clone from %s";
    private static final String LOG_BRANCH_DOES_NOT_EXIST = "Branch \"%s\" does not exist.";

    private static Date earliestSinceDate = null;

    /**
     * Generates the authorship and commits JSON file for each repo in {@code configs} at {@code outputPath}, as
     * well as the summary JSON file of all the repos.
     *
     * @return the list of file paths that were generated.
     * @throws IOException if templateZip.zip does not exists in jar file.
     */
    public static List<Path> generateReposReport(List<RepoConfiguration> configs, String outputPath,
            String generationDate, Date cliSinceDate, Date untilDate) throws IOException {
        InputStream is = RepoSense.class.getResourceAsStream(TEMPLATE_FILE);
        FileUtil.copyTemplate(is, outputPath);

        earliestSinceDate = null;

        cloneAndAnalyzeRepos(configs, outputPath);

        Date reportSinceDate = (cliSinceDate.equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE))
                ? earliestSinceDate : cliSinceDate;

        FileUtil.writeJsonFile(
                new SummaryJson(configs, generationDate, reportSinceDate, untilDate, RepoSense.getVersion(),
                        ErrorSummary.getInstance().getErrorList()),
                getSummaryResultPath(outputPath));
        logger.info(String.format(MESSAGE_REPORT_GENERATED, outputPath));

        List<Path> reportFoldersAndFiles = new ArrayList<>();
        for (RepoConfiguration config : configs) {
            reportFoldersAndFiles.add(
                    Paths.get(outputPath + File.separator + config.getDisplayName()).toAbsolutePath());
        }
        reportFoldersAndFiles.add(Paths.get(outputPath, SummaryJson.SUMMARY_JSON_FILE_NAME));
        return reportFoldersAndFiles;
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

        RepoLocation repoLocation = repoLocationList.get(0);
        repoCloner.clone(repoLocationMap.get(repoLocation).get(0));

        for (int index = 1; index <= repoLocationList.size(); index++) {
            clonedRepoLocation = repoCloner.getClonedRepoLocation();

            if (clonedRepoLocation == null) {
                handleCloningFailed(configs, repoLocation);
            } else {
                // Clone the rest while analyzing the previously cloned repos in parallel.
                if (index < repoLocationList.size()) {
                    repoLocation = repoLocationList.get(index);
                    repoCloner.clone(repoLocationMap.get(repoLocation).get(0));
                }
                analyzeRepos(outputPath, configs, repoLocationMap.get(clonedRepoLocation),
                        repoCloner.getCurrentRepoDefaultBranch());
            }
        }
        repoCloner.cleanup();
    }

    /**
     * Analyzes all repos in {@code configsToAnalyze} and generates their report.
     * Also removes {@code configsToAnalyze} that failed to analyze from {@code configs}.
     */
    private static void analyzeRepos(String outputPath, List<RepoConfiguration> configs,
            List<RepoConfiguration> configsToAnalyze, String defaultBranch) {
        Iterator<RepoConfiguration> itr = configsToAnalyze.iterator();
        while (itr.hasNext()) {
            RepoConfiguration configToAnalyze = itr.next();
            configToAnalyze.updateBranch(defaultBranch);

            logger.info(
                    String.format(MESSAGE_START_ANALYSIS, configToAnalyze.getLocation(), configToAnalyze.getBranch()));
            try {
                GitCheckout.checkout(configToAnalyze.getRepoRoot(), configToAnalyze.getBranch());
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, String.format(MESSAGE_BRANCH_DOES_NOT_EXIST,
                        configToAnalyze.getBranch(), configToAnalyze.getLocation()), e);
                ErrorSummary.getInstance().addErrorMessage(configToAnalyze.getDisplayName(),
                        String.format(LOG_BRANCH_DOES_NOT_EXIST, configToAnalyze.getBranch()));
                configs.removeIf(config -> config.equals(configToAnalyze));
                continue;
            }

            Path repoReportDirectory;
            try {
                repoReportDirectory = Paths.get(outputPath, configToAnalyze.getOutputFolderName());
                FileUtil.createDirectory(repoReportDirectory);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CREATING_DIRECTORY,
                        configToAnalyze.getLocation(), configToAnalyze.getBranch()), ioe);
                continue;
            } catch (RuntimeException rte) {
                logger.log(Level.SEVERE, String.format(MESSAGE_ERROR_DURING_ANALYSIS,
                        configToAnalyze.getLocation(), configToAnalyze.getBranch()), rte);
                continue;
            }

            try {
                analyzeRepo(configToAnalyze, repoReportDirectory.toString());
            } catch (NoAuthorsWithCommitsFoundException e) {
                logger.log(Level.SEVERE, String.format(MESSAGE_NO_AUTHORS_WITH_COMMITS_FOUND,
                        configToAnalyze.getLocation(), configToAnalyze.getBranch()));
                generateEmptyRepoReport(repoReportDirectory.toString(), Author.NAME_NO_AUTHOR_WITH_COMMITS_FOUND);
            }
        }
    }

    /**
     * Analyzes repo specified by {@code config} and generates the report.
     */
    private static void analyzeRepo(
            RepoConfiguration config, String repoReportDirectory) throws NoAuthorsWithCommitsFoundException {
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
    private static void updateAuthorList(RepoConfiguration config) throws NoAuthorsWithCommitsFoundException {
        if (config.getAuthorList().isEmpty()) {
            logger.info(String.format(MESSAGE_NO_AUTHORS_SPECIFIED, config.getLocation(), config.getBranch()));
            List<Author> authorList = GitShortlog.getAuthors(config);

            if (authorList.isEmpty()) {
                throw new NoAuthorsWithCommitsFoundException();
            }

            config.setAuthorList(authorList);
        }
    }

    /**
     * Adds {@code configs} that were not successfully cloned from {@code failedRepoLocation}
     * into the list of errors in the summary report and removes them from the list of {@code configs}.
     */
    private static void handleCloningFailed(List<RepoConfiguration> configs, RepoLocation failedRepoLocation) {
        Iterator<RepoConfiguration> itr = configs.iterator();
        while (itr.hasNext()) {
            RepoConfiguration config = itr.next();
            if (config.getLocation().equals(failedRepoLocation)) {
                ErrorSummary.getInstance().addErrorMessage(config.getDisplayName(),
                        String.format(LOG_ERROR_CLONING, config.getLocation()));
                itr.remove();
            }
        }
    }

    /**
     * Generates a report at the {@code repoReportDirectory}.
     */
    public static void generateEmptyRepoReport(String repoReportDirectory, String displayName) {
        CommitReportJson emptyCommitReportJson = new CommitReportJson(displayName);
        FileUtil.writeJsonFile(emptyCommitReportJson, getIndividualCommitsPath(repoReportDirectory));
        FileUtil.writeJsonFile(Collections.emptyList(), getIndividualAuthorshipPath(repoReportDirectory));
    }

    private static void generateIndividualRepoReport(
            String repoReportDirectory, CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        CommitReportJson commitReportJson = new CommitReportJson(commitSummary, authorshipSummary);
        FileUtil.writeJsonFile(commitReportJson, getIndividualCommitsPath(repoReportDirectory));
        FileUtil.writeJsonFile(authorshipSummary.getFileResults(), getIndividualAuthorshipPath(repoReportDirectory));
    }

    private static String getSummaryResultPath(String targetFileLocation) {
        return targetFileLocation + "/" + SummaryJson.SUMMARY_JSON_FILE_NAME;
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
}
