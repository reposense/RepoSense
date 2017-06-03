package main;

import analyzer.Analyzer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Analyzer b = new Analyzer("/Users/matanghao1/Developer/main");

        //b.blameSingleFile("src/com/tg/parser/Parser.java");
        //HashMap<String, ArrayList<Line>> result = b.getAllFilesBlame();
        //System.out.println(result.get("src/com/tg/parser/Parser.java").get(10).getAuthor().getName());
        //System.out.println("git/deadea".matches("git"));
        long time = System.nanoTime();
        //String result = CommandRunner.checkStyleRaw("/Users/matanghao1/Developer/main/");
//        System.out.println(b.getAllFilesBlame().entrySet().size());
        long time1 = System.nanoTime();
        System.out.println(time1-time);
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
