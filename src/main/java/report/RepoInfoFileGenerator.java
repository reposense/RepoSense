package report;

import analyzer.RepoAnalyzer;
import com.google.gson.Gson;
import dataObject.Configuration;
import dataObject.RepoInfo;
import git.GitCloner;
import util.Constants;
import util.FileUtil;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {

    public static void generateForNewestCommit(Configuration config){

        GitCloner.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
        RepoInfo repoinfo = new RepoInfo(config.getOrganization(), config.getRepoName());
        RepoAnalyzer.analyzeRecentNCommit(config, repoinfo);

        Gson gson = new Gson();
        String result = gson.toJson(repoinfo);

        try {
            PrintWriter out = new PrintWriter(getReportPath(config.getOrganization(), config.getRepoName()));
            out.println(attachJsPrefix(result));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("report Generated!");

    }

    private static String attachJsPrefix(String original){
        return "var resultJson = "+original;
    }

    private static String getReportPath(String organization, String repoName){
        return Constants.REPORT_ADDRESS + "/result.js";
    }

}
