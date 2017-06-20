package main;

import analyzer.Analyzer;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import data.Author;
import data.CommitInfo;
import data.FileInfo;
import data.MethodInfo;
import org.xml.sax.SAXException;
import system.CommandRunner;
import timetravel.GitLogger;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.github.javaparser.ast.CompilationUnit;
/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {
        Analyzer b = new Analyzer("/Users/matanghao1/Developer/main");
        HashMap<CommitInfo, ArrayList<FileInfo>> map = b.analyzeRecentNCommit(1);
        for (CommitInfo commit: map.keySet()){
            ArrayList<FileInfo> files = map.get(commit);
            for (FileInfo file:files){
                System.out.println(file.getPath());
                for (MethodInfo method: file.getMethodInfos()){
                    System.out.println(method.getMethodName());
                    System.out.println(method.getOwner().getName());

                }
            }
        }





    }

}
