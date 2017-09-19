package report;

import analyzer.RepoAnalyzer;
import com.google.gson.Gson;
import dataObject.Configuration;
import dataObject.RepoInfo;
import git.GitCloner;
import util.Constants;
import util.FileUtil;

import java.io.*;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {

    public static void generateReport(Configuration config){

        GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
        RepoInfo repoinfo = new RepoInfo(config.getOrganization(), config.getRepoName());
        RepoAnalyzer.analyzeCommits(config, repoinfo);

        String reportName = generateReportDirectoryName(config.getOrganization(), config.getRepoName());

        copyBasicTemplate(reportName);

        Gson gson = new Gson();
        String result = gson.toJson(repoinfo);

        try {
            PrintWriter out = new PrintWriter(getReportPath(reportName));
            out.println(attachJsPrefix(result));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("report Generated!");

    }

    private static void copyBasicTemplate(String reportName){
        try {
            FileUtil.copyFolder(new File(Constants.TEMPLATE_ADDRESS), new File(Constants.REPORT_ADDRESS+"/"+reportName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateReportDirectoryName(String organization, String repoName){
        return organization + "_" + repoName + "_" + System.currentTimeMillis();
    }

    private static String attachJsPrefix(String original){
        return "var resultJson = "+original;
    }

    private static String getReportPath(String reportName){
        return Constants.REPORT_ADDRESS + "/" + reportName+ "/result.js";
    }

}
