package report;

import analyzer.RepoAnalyzer;
import dataObject.FileInfo;
import dataObject.RepoConfiguration;
import dataObject.RepoContributionSummary;
import dataObject.RepoInfo;
import frontend.RepoSense;
import git.GitCloner;
import git.GitClonerException;
import util.Constants;
import util.FileUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {

    public static void generateReposReport(List<RepoConfiguration> repoConfigs, String targetFileLocation){
        String reportName = generateReportName();
        List<RepoInfo> repos = analyzeRepos(repoConfigs);
        copyTemplate(reportName, targetFileLocation);
        for (RepoInfo repo : repos) {
            generateIndividualRepoReport(repo, reportName,targetFileLocation);
        }

        Map<String, RepoContributionSummary> repoSummaries = ContributionSummaryGenerator.analyzeContribution(repos, repoConfigs);
        FileUtil.writeJsonFile(repoSummaries, getSummaryResultPath(reportName,targetFileLocation), "summaryJson");

    }

    private static List<RepoInfo> analyzeRepos(List<RepoConfiguration> configs) {
        List<RepoInfo> result = new ArrayList<>();
        int count = 1;
        for (RepoConfiguration config : configs) {
            System.out.println("Analyzing Repository No."+(count++)+"( " + configs.size() + " repositories in total)");
            try {
                GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
            } catch (GitClonerException e){
                System.out.println("Exception met when cloning the repo, will skip this one");
                continue;
            }
            RepoInfo repoinfo = new RepoInfo(config.getOrganization(), config.getRepoName(),config.getBranch(),config.getAuthorDisplayNameMap());
            RepoAnalyzer.analyzeCommits(config, repoinfo);
            result.add(repoinfo);
        }
        FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);

        return result;
    }

    private static void generateIndividualRepoReport(RepoInfo repoinfo, String reportName, String targetFileLocation){
        String repoReportName = repoinfo.getDirectoryName();
        String repoReportDirectory = targetFileLocation+"/"+reportName+"/"+repoReportName;
        new File(repoReportDirectory).mkdirs();
        String templateLocation = targetFileLocation+File.separator+
                reportName+ File.separator +
                Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS;
        FileUtil.copyFiles(new File(templateLocation), new File(repoReportDirectory));
        ArrayList<FileInfo> fileInfos = repoinfo.getFileinfos();
        FileUtil.writeJsonFile(fileInfos,getIndividualResultPath(repoReportDirectory),"resultJson");
        System.out.println("report for "+ repoReportName+" Generated!");
    }

    private static void copyTemplate(String reportName, String targetFileLocation){
        String location = targetFileLocation + File.separator + reportName;
        InputStream is = RepoSense.class.getResourceAsStream(Constants.TEMPLATE_ZIP_ADDRESS);
        FileUtil.unzip(new ZipInputStream(is),location);
    }

    private static String getIndividualResultPath(String repoReportDirectory){
        return repoReportDirectory+ "/result.js";
    }

    private static String getSummaryResultPath(String reportName, String targetFileLocation){
        return targetFileLocation+"/"+reportName+"/summary.js";
    }

    private static String generateReportName(){
        return Constants.REPORT_NAME_FORMAT.format(new Date());
    }

}
