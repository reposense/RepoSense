package reposense.report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.authorship.AuthorshipReporter;
import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.CommitsReporter;
import reposense.commits.model.CommitContributionSummary;
import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.Constants;
import reposense.util.FileUtil;

public class ReportGenerator {
    private static final Logger logger = LogsManager.getLogger(ReportGenerator.class);

    /**
     * Generates the authorship and commits JSON file for each repo in {@code configs} at {@code outputPath}, as
     * well as the summary JSON file of all the repos.
     */
    public static void generateReposReport(List<RepoConfiguration> configs, String outputPath) {
        try {
            FileUtil.copyTemplate(outputPath);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error while copying template files to output directory.", ioe);
            return;
        }
        Path templateLocation = Paths.get(outputPath, Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS);

        for (RepoConfiguration config : configs) {
            Path repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
            try {
                GitDownloader.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
                FileUtil.copyDirectoryFiles(templateLocation, repoReportDirectory);
            } catch (GitDownloaderException gde) {
                logger.log(Level.WARNING, "Exception met while trying to clone the repo, will skip this one", gde);
                continue;
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error while copying template files, will skip this repo.", ioe);
                continue;
            }

            HashMap<String, CommitContributionSummary> commitSummary = CommitsReporter.generateCommitSummary(config);
            HashMap<String, AuthorshipSummary> authorshipSummary = AuthorshipReporter.generateAuthorshipSummary(config);
            generateIndividualRepoReport(commitSummary, authorshipSummary, repoReportDirectory.toString());

            try {
                FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error deleting report directory.", ioe);
            }
        }
        FileUtil.writeJsonFile(configs, getSummaryResultPath(outputPath));
        FileUtil.writeJsonFile(Constants.getDocTypes(), getDocTypesPath(outputPath));
    }

    private static void generateIndividualRepoReport(
            HashMap<String, CommitContributionSummary> commitSummaries,
            HashMap<String, AuthorshipSummary> authorshipSummaries, String repoReportDirectory) {
        for (HashMap.Entry<String, CommitContributionSummary> commitSummary : commitSummaries.entrySet()) {
            String docType = commitSummary.getKey();
            generateIndividualCommitReportForEachDocType(docType, commitSummary.getValue(),
                    authorshipSummaries.get(docType), repoReportDirectory);
        }
    }

    private static void generateIndividualCommitReportForEachDocType(
            String docType, CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary,
            String repoReportDirectory
    ) {
        CommitReportJson commitReportJson = new CommitReportJson(commitSummary, authorshipSummary);
        FileUtil.writeJsonFile(commitReportJson, getIndividualCommitsPathForEachDocType(repoReportDirectory, docType));
        FileUtil.writeJsonFile(authorshipSummary.getFileResults(),
                getIndividualAuthorshipPathForEachDocType(repoReportDirectory, docType));
    }

    private static String getSummaryResultPath(String targetFileLocation) {
        return targetFileLocation + "/summary.json";
    }

    private static String getIndividualAuthorshipPathForEachDocType(String repoReportDirectory, String docType) {
        return repoReportDirectory + "/authorship_" + docType + ".json";
    }

    private static String getDocTypesPath(String outputpath) {
        return outputpath + "/doctype.json";
    }

    private static String getIndividualCommitsPathForEachDocType(String repoReportDirectory, String docType) {
        return repoReportDirectory + "/commits_" + docType + ".json";
    }
}
