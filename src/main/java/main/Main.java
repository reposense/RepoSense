package main;

import analyzer.Blamer;
import data.Line;
import system.CommandRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Blamer b = new Blamer("/Users/matanghao1/Developer/main");
        //b.blameSingleFile("src/com/tg/parser/Parser.java");
        HashMap<String, ArrayList<Line>> result = b.getAllFilesBlame();
        System.out.println(result.get("src/com/tg/parser/Parser.java").get(10).getAuthor().getName());
        //System.out.println("git/deadea".matches("git"));
    }
}
