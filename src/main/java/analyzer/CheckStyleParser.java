package analyzer;

import data.FileInfo;
import data.IssueInfo;
import data.Line;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import system.CommandRunner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Created by matanghao1 on 3/6/17.
 */
public class CheckStyleParser {
    final private static String MESSAGE = "message";
    final private static String SEVERITY = "severity";
    final private static String LINE = "line";



    public static void aggregateStyleIssue(FileInfo fileInfo, String rootRepo){
        String raw = CommandRunner.checkStyleRaw(rootRepo + '/' + fileInfo.getPath());
        //System.out.println(raw);
        System.out.println(fileInfo.getPath());

        NodeList nodeList = getNodeListFromRawOutput(raw);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String message = element.getAttribute(MESSAGE);
            String severity = element.getAttribute(SEVERITY);

            int lineNumber = Integer.parseInt(element.getAttribute(LINE));

            Line line = fileInfo.getLineByNumber(lineNumber);
            line.getIssues().add(new IssueInfo(severity,message));
        }
    }

    private static NodeList getNodeListFromRawOutput(String raw){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected Exception while Initializing XML Parser");
        }
        InputSource is = new InputSource(new StringReader(raw));
        Document doc = null;
        try {
            doc = builder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("error");
        return nodeList;
    }
}
