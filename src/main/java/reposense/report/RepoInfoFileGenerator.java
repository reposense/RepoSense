package reposense.report;

import java.io.File;
import java.io.InputStream;
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

    public static void generateReposReport(List<RepoConfiguration> repoConfigs, String targetFileLocation) {
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
            FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
        }

        if (!suspiciousAuthors.isEmpty()) {
            System.out.println("PLEASE NOTE, BELOW IS THE LIST OF SUSPICIOUS AUTHORS:");
            for (Author author : suspiciousAuthors) {
                System.out.println(author);
            }
        }
        FileUtil.writeJsonFile(repoConfigs, getSummaryResultPath(reportName, targetFileLocation));

    }

    private static void generateIndividualRepoReport(RepoConfiguration repoConfig, List<FileInfo> fileInfos,
            RepoContributionSummary summary, String reportName, String targetFileLocation) {

        String repoReportName = repoConfig.getDisplayName();
        String repoReportDirectory = targetFileLocation + "/" + reportName + "/" + repoReportName;
        new File(repoReportDirectory).mkdirs();
        String templateLocation = targetFileLocation + File.separator
                + reportName + File.separator
                + Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS;
        FileUtil.copyFiles(new File(templateLocation), new File(repoReportDirectory));
        FileUtil.writeJsonFile(fileInfos, getIndividualAuthorshipPath(repoReportDirectory));
        FileUtil.writeJsonFile(summary, getIndividualCommitsPath(repoReportDirectory));
        System.out.println("Report for " + repoReportName + " Generated!");
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
