package report;

import analyzer.RepoAnalyzer;
import com.google.gson.Gson;
import dataObject.RepoInfo;
import util.Constants;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfoFileGenerator {
    public static void generateForNewestCommit(String rootRepo){
        RepoInfo repoinfo = RepoAnalyzer.analyzeRecentNCommit(rootRepo,1);
        Gson gson = new Gson();
        String result = gson.toJson(repoinfo);

        try {
            PrintWriter out = new PrintWriter(getReportPath(rootRepo));
            out.println(result);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private static String getReportPath(String rootRepo){
        return Constants.REPORT_ADDRESS + "/" + getRepoName(rootRepo) + "_" + System.currentTimeMillis() + ".json";
    }

    private static String getRepoName(String rootRepo){
        String[] elements = rootRepo.split("/");
        return elements[elements.length-1];
    }

}
