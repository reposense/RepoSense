package report;

import analyzer.RepoAnalyzer;
import dataObject.RepoConfiguration;
import dataObject.RepoContributionSummary;
import dataObject.RepoInfo;
import git.GitCloner;
import util.Constants;
import util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {

    public static void generateReposReport(List<RepoConfiguration> repoConfigs){
        String reportName = generateReportName();
        List<RepoInfo> repos = analyzeRepos(repoConfigs);
        copyStaticLib(reportName);

        for (RepoInfo repo : repos) {
            generateIndividualRepoReport(repo, reportName);
        }
        Map<String, RepoContributionSummary> repoSummaries = ContributionSummaryGenerator.analyzeContribution(repos);
        FileUtil.writeJSONFile(repoSummaries, getSummaryResultPath(reportName), "summaryJson");
        FileUtil.copyFile(new File(Constants.STATIC_SUMMARY_REPORT_FILE_ADDRESS),new File(getSummaryPagePath(reportName)));
        FileUtil.copyFile(new File(Constants.STATIC_SUMMARY_REPORT_DETAIL_FILE_ADDRESS),new File(getDetailPagePath(reportName)));

    }

    private static List<RepoInfo> analyzeRepos(List<RepoConfiguration> configs) {
        List<RepoInfo> result = new ArrayList<>();
        for (RepoConfiguration config : configs) {
            GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
            RepoInfo repoinfo = new RepoInfo(config.getOrganization(), config.getRepoName(),config.getBranch());
            RepoAnalyzer.analyzeCommits(config, repoinfo);
            result.add(repoinfo);
        }
        return result;
    }

    private static void generateIndividualRepoReport(RepoInfo repoinfo, String reportName){

        String repoReportName = repoinfo.getDirectoryName();
        String repoReportDirectory = Constants.REPORT_ADDRESS+"/"+reportName+"/"+repoReportName;
        new File(repoReportDirectory).mkdirs();
        copyTemplate(repoReportDirectory, Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS);

        FileUtil.writeJSONFile(repoinfo,getIndividualResultPath(repoReportDirectory),"resultJson");

        System.out.println("report for "+ repoReportName+" Generated!");

    }

    private static void copyStaticLib(String reportName){
        String staticLibDirectory = Constants.REPORT_ADDRESS+"/"+reportName+"/"+"static";
        new File(staticLibDirectory).mkdirs();
        copyTemplate(staticLibDirectory, Constants.STATIC_LIB_TEMPLATE_ADDRESS );
    }


    private static void copyTemplate(String dest, String src){
        try {
            FileUtil.copyFiles(new File(src), new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getSummaryPagePath(String repoReportDirectory){
        return Constants.REPORT_ADDRESS+"/"+repoReportDirectory+ "/index.html";
    }

    private static String getDetailPagePath(String repoReportDirectory){
        return Constants.REPORT_ADDRESS+"/"+repoReportDirectory+ "/detail.html";
    }

    private static String getIndividualResultPath(String repoReportDirectory){
        return repoReportDirectory+ "/result.js";
    }

    private static String getSummaryResultPath(String reportName){
        return Constants.REPORT_ADDRESS+"/"+reportName+"/summary.js";
    }

    private static String generateReportName(){
        return Constants.REPORT_NAME_FORMAT.format(new Date());
    }

}
