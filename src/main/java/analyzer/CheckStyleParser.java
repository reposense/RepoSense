package analyzer;

import data.FileInfo;
import data.IssueInfo;
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

    public static void addStyleIssue(FileInfo fileInfo, String rootRepo){
        String raw = CommandRunner.checkStyleRaw(rootRepo + '/' + fileInfo.getPath());
        //System.out.println(raw);
        System.out.println(fileInfo.getPath());

        NodeList nodeList = getNodeListFromRawOutput(raw);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String message = element.getAttribute("message");
            String serverity = element.getAttribute("severity");

            int lineNumber = Integer.parseInt(element.getAttribute("line"));

            fileInfo.getLineByNumber(lineNumber).setIssue(new IssueInfo(serverity,message));
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
