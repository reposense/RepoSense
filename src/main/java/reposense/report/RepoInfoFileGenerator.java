package reposense.report;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import reposense.analyzer.ContentAnalyzer;
import reposense.analyzer.RepoAnalyzer;
import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoContributionSummary;
import reposense.dataobject.RepoInfo;
import reposense.frontend.RepoSense;
import reposense.git.GitCloner;
import reposense.git.GitClonerException;
import reposense.util.Constants;
import reposense.util.FileUtil;


public class RepoInfoFileGenerator {

    public static void generateReposReport(List<RepoConfiguration> repoConfigs, String targetFileLocation) {
        String reportName = generateReportName();
        List<RepoInfo> repos = new ArrayList<>();

        for (RepoConfiguration config: repoConfigs) {
            try {
                GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
            } catch (GitClonerException e) {
                System.out.println("Exception met when cloning the repo, will skip this one");
                continue;
            }
            RepoInfo repoInfo = analyzeRepo(config);
            repos.add(repoInfo);
            generateIndividualRepoReport(repoInfo, reportName, targetFileLocation);
            FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
        }
        copyTemplate(reportName, targetFileLocation);

        Map<String, RepoContributionSummary> repoSummaries =
                ContributionSummaryGenerator.analyzeContribution(repos, repoConfigs);
        FileUtil.writeJsonFile(repoSummaries, getSummaryResultPath(reportName, targetFileLocation), "summaryJson");

    }

    private static RepoInfo analyzeRepo(RepoConfiguration config) {
        RepoInfo repoinfo = new RepoInfo(
                config.getOrganization(),
                config.getRepoName(),
                config.getBranch(),
                config.getAuthorDisplayNameMap()
        );
        repoinfo.setCommits(RepoAnalyzer.analyzeCommits(config, repoinfo));
        repoinfo.setFileinfos(RepoAnalyzer.analyzeAuthorship(config, repoinfo));
        repoinfo.setAuthorContributionMap(
                ContentAnalyzer.getAuthorMethodContributionCount(repoinfo.getFileinfos(), config.getAuthorList()));
        return repoinfo;
    }

    private static void generateIndividualRepoReport(RepoInfo repoinfo, String reportName, String targetFileLocation) {
        String repoReportName = repoinfo.getDirectoryName();
        String repoReportDirectory = targetFileLocation + "/" + reportName + "/" + repoReportName;
        new File(repoReportDirectory).mkdirs();
        String templateLocation = targetFileLocation + File.separator
                + reportName + File.separator
                + Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS;
        FileUtil.copyFiles(new File(templateLocation), new File(repoReportDirectory));
        List<FileInfo> fileInfos = repoinfo.getFileinfos();
        FileUtil.writeJsonFile(fileInfos, getIndividualResultPath(repoReportDirectory), "resultJson");
        System.out.println("report for " + repoReportName + " Generated!");
    }

    private static void copyTemplate(String reportName, String targetFileLocation) {
        String location = targetFileLocation + File.separator + reportName;
        InputStream is = RepoSense.class.getResourceAsStream(Constants.TEMPLATE_ZIP_ADDRESS);
        FileUtil.unzip(new ZipInputStream(is), location);
    }

    private static String getIndividualResultPath(String repoReportDirectory) {
        return repoReportDirectory + "/result.js";
    }

    private static String getSummaryResultPath(String reportName, String targetFileLocation) {
        return targetFileLocation + "/" + reportName + "/summary.js";
    }

    private static String generateReportName() {
        return Constants.REPORT_NAME_FORMAT.format(new Date());
    }

}
