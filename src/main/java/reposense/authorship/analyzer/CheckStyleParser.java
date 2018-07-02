package reposense.authorship.analyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.IssueInfo;
import reposense.authorship.model.LineInfo;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

public class CheckStyleParser {
    private static final String MESSAGE = "message";
    private static final String SEVERITY = "severity";
    private static final String LINE = "line";

    private static final Logger logger = LogsManager.getLogger(CheckStyleParser.class);

    public static void aggregateStyleIssue(FileInfo fileInfo, String rootRepo) {
        String raw = CommandRunner.checkStyleRaw(rootRepo + '/' + fileInfo.getPath());
        logger.info("Checking style of:" + fileInfo.getPath());

        NodeList nodeList = getNodeListFromRawOutput(raw);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String message = element.getAttribute(MESSAGE);
            String severity = element.getAttribute(SEVERITY);

            int lineNumber = Integer.parseInt(element.getAttribute(LINE));

            LineInfo line = fileInfo.getLine(lineNumber);
            line.getIssues().add(new IssueInfo(severity, message));
        }
    }

    private static NodeList getNodeListFromRawOutput(String raw) {

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
