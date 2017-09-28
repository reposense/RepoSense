package report;

import analyzer.RepoAnalyzer;
import com.google.gson.Gson;
import dataObject.RepoConfiguration;
import dataObject.RepoInfo;
import git.GitCloner;
import util.Constants;
import util.FileUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {

    public static void generateReposReport(List<RepoConfiguration> repoConfigs){
        String reportName = Long.toString(System.currentTimeMillis());
        copyStaticLib(reportName);
        List<RepoInfo> repos = analyzeRepos(repoConfigs);
        for (RepoInfo repo : repos) {
            generateIndividualRepoReport(repo, reportName);
        }
    }

    private static List<RepoInfo> analyzeRepos(List<RepoConfiguration> configs) {
        List<RepoInfo> result = new ArrayList<>();
        for (RepoConfiguration config : configs) {
            GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
            RepoInfo repoinfo = new RepoInfo(config.getOrganization(), config.getRepoName());
            RepoAnalyzer.analyzeCommits(config, repoinfo);
            result.add(repoinfo);
        }
        return result;
    }

    private static void generateIndividualRepoReport(RepoInfo repoinfo, String reportName){

        String repoReportName = generateIndividualReportDirectoryName(repoinfo.getOrganization(), repoinfo.getRepoName());
        String repoReportDirectory = Constants.REPORT_ADDRESS+"/"+reportName+"/"+repoReportName;
        new File(repoReportDirectory).mkdirs();
        copyTemplate(repoReportDirectory, Constants.STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS);

        Gson gson = new Gson();
        String result = gson.toJson(repoinfo);

        try {
            PrintWriter out = new PrintWriter(getReportPath(repoReportDirectory));
            out.println(attachJsPrefix(result));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("report for "+ repoReportName+" Generated!");

    }

    private static void copyStaticLib(String reportName){
        String staticLibDirectory = Constants.REPORT_ADDRESS+"/"+reportName+"/"+"static";
        new File(staticLibDirectory).mkdirs();
        copyTemplate(staticLibDirectory, Constants.STATIC_LIB_TEMPLATE_ADDRESS );
    }

    private static void copyTemplate(String dest, String src){
        try {
            FileUtil.copyFolder(new File(src), new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateIndividualReportDirectoryName(String organization, String repoName){
        return organization + "_" + repoName;
    }

    private static String attachJsPrefix(String original){
        return "var resultJson = "+original;
    }

    private static String getReportPath(String repoReportDirectory){
        return repoReportDirectory+ "/result.js";
    }

}
