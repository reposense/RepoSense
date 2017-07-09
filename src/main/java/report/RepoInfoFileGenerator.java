package report;

import analyzer.RepoAnalyzer;
import com.google.gson.Gson;
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

    public static void generateForNewestCommit(String organization, String repoName, String branch){

        GitCloner.downloadRepo(organization, repoName, branch);
        String rootRepo = FileUtil.getRepoDirectory(organization, repoName);
        RepoInfo repoinfo = new RepoInfo(organization, repoName);
        RepoAnalyzer.analyzeRecentNCommit(rootRepo, repoinfo,1);

        Gson gson = new Gson();
        String result = gson.toJson(repoinfo);

        try {
            PrintWriter out = new PrintWriter(getReportPath(organization, repoName));
            out.println(result);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }



    private static String getReportPath(String organization, String repoName){
        return Constants.REPORT_ADDRESS + "/" + organization +"_" + repoName + "_" + System.currentTimeMillis() + ".json";
    }

}
