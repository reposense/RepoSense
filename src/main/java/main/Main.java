package main;

import analyzer.RepoAnalyzer;
import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.FileInfo;
import dataObject.MethodInfo;
import git.GitCloner;
import org.xml.sax.SAXException;
import report.RepoInfoFileGenerator;
import system.CommandRunner;

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

        //RepoInfoFileGenerator.generateForNewestCommit("/Users/matanghao1/Developer/main/");
        //CommandRunner.cloneRepo("https://github.com/cs2103aug2015-w09-4j/main")
        GitCloner.downloadRepo("cs2103aug2015-w09-4j","main","develop");

    }

}
