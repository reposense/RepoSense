package reposense.report;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipInputStream;

import reposense.analyzer.ContentAnalyzer;
import reposense.analyzer.RepoAnalyzer;
import reposense.dataobject.Author;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoContributionSummary;
import reposense.frontend.RepoSense;
import reposense.git.GitCloner;
import reposense.git.GitClonerException;
import reposense.util.Constants;
import reposense.util.FileUtil;


public class RepoInfoFileGenerator {

    /**
     * Generates the repo report in a new folder inside {@code targetFileLocation}, using the configs in
     * {@code repoConfigs}, and returns the name of the folder generated.
     */
    public static String generateReposReport(List<RepoConfiguration> repoConfigs, String targetFileLocation) {
        String reportName = generateReportName();
        HashSet<Author> suspiciousAuthors = new HashSet<>();

        copyTemplate(reportName, targetFileLocation);
        for (RepoConfiguration config: repoConfigs) {
            try {
                GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
            } catch (GitClonerException e) {
                System.out.println("Exception met when cloning the repo, will skip this one");
                continue;
            }
            List<FileInfo> fileInfos = RepoAnalyzer.analyzeAuthorship(config);
            List<CommitInfo> commitInfos = RepoAnalyzer.analyzeCommits(config);
            HashMap<Author, Integer> authorContributionMap =
                    ContentAnalyzer.getAuthorMethodContributionCount(fileInfos, config.getAuthorList());
            RepoContributionSummary summary = ContributionSummaryGenerator.analyzeContribution(
                    config, commitInfos, authorContributionMap, suspiciousAuthors);
            generateIndividualRepoReport(config, fileInfos, summary, reportName, targetFileLocation);

            try {
                FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
            } catch (IOException ioe) {
                System.out.println("Error deleting report directory.");
            }
        }

        if (!suspiciousAuthors.isEmpty()) {
            System.out.println("PLEASE NOTE, BELOW IS THE LIST OF SUSPICIOUS AUTHORS:");
            for (Author author : suspiciousAuthors) {
                System.out.println(author);
            }
        }
        FileUtil.writeJsonFile(repoConfigs, getSummaryResultPath(reportName, targetFileLocation));

        return reportName;
    }

    private static void generateIndividualRepoReport(RepoConfiguration repoConfig, List<FileInfo> fileInfos,
            RepoContributionSummary summary, String reportName, String targetFileLocation) {
        String repoReportName = repoConfig.getDisplayName();
        Path repoReportDirectory = Paths.get(targetFileLocation, reportName, repoReportName);
        Path templateLocation = Paths.get(targetFileLocation, reportName,
                Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS);

        try {
            Files.copy(templateLocation, repoReportDirectory);
            FileUtil.writeJsonFile(fileInfos, getIndividualAuthorshipPath(repoReportDirectory.toString()));
            FileUtil.writeJsonFile(summary, getIndividualCommitsPath(repoReportDirectory.toString()));
            System.out.println("report for " + repoReportName + " Generated!");
        } catch (IOException ioe) {
            System.out.println("Error in copying template file for report.");
        }
    }

    private static void copyTemplate(String reportName, String targetFileLocation) {
        String location = targetFileLocation + File.separator + reportName;
        InputStream is = RepoSense.class.getResourceAsStream(Constants.TEMPLATE_ZIP_ADDRESS);
        FileUtil.unzip(new ZipInputStream(is), location);
    }

    private static String getIndividualAuthorshipPath(String repoReportDirectory) {
        return repoReportDirectory + "/authorship.json";
    }

    private static String getIndividualCommitsPath(String repoReportDirectory) {
        return repoReportDirectory + "/commits.json";
    }

    private static String getSummaryResultPath(String reportName, String targetFileLocation) {
        return targetFileLocation + "/" + reportName + "/summary.json";
    }

    private static String generateReportName() {
        return Constants.REPORT_NAME_FORMAT.format(new Date());
    }

}
