package main;

import analyzer.Analyzer;
import data.Author;
import data.FileInfo;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Analyzer b = new Analyzer("/Users/matanghao1/Developer/main");


        long time = System.nanoTime();
        ArrayList<FileInfo> list = b.analyzeAllFile();
        long time1 = System.nanoTime();
        System.out.println(time1-time);
        HashMap<Author,Integer> result = b.getAuthorIssueCount(list);
        for (Author author:result.keySet()){
            System.out.println(author.getName());
            System.out.println(result.get(author));
        }
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        InputSource is = new InputSource(new StringReader(result));
//        Document doc = builder.parse(is);
//        doc.getDocumentElement().normalize();
//        NodeList nodeList = doc.getElementsByTagName("*");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            if (!nodeList.item(i).getNodeName().equals("file")) continue;
//            System.out.println(((Element) nodeList.item(i)).getAttribute("name"));
//        }



    }
}
