package main;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matanghao1 on 28/5/17.
 */
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {
//        RepoConfiguration config = ConfigurationFactory.getMinimalConfig("cs2103aug2015-w09-4j","main","develop");
//        config.setNeedCheckStyle(true);
//        RepoInfoFileGenerator.generateForNewestCommit(config);
        String raw = " 25 files changed, 72 deletions(-)";
        Pattern p = Pattern.compile("([0-9]+) insertions");
        Matcher m = p.matcher(raw);

        if (m.find()){
            System.out.println(Integer.parseInt(m.group(1)));

        }
    }

}
