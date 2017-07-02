package main;

import analyzer.RepoAnalyzer;
import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.FileInfo;
import dataObject.MethodInfo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {
        ArrayList<CommitInfo> map = RepoAnalyzer.analyzeRecentNCommit("/Users/matanghao1/Developer/main/",1);
//        for (CommitInfo commit: map){
//            ArrayList<FileInfo> files = commit.getFileinfos();
//            for (FileInfo file:files){
//                System.out.println(file.getPath());
//                for (MethodInfo method: file.getMethodInfos()){
//                    System.out.println(method.getMethodName());
//                    System.out.println(method.getOwner().getName());
//
//                }
//            }
//        }
        for (CommitInfo commit: map){
            for (Author author:commit.getAuthorContributionMap().keySet()){
                System.out.println(author.getName());
                System.out.println(commit.getAuthorContributionMap().get(author));
                System.out.println(commit.getAuthorIssueMap().get(author));
            }
        }



    }

}
