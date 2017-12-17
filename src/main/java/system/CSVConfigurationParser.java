package system;

import builder.ConfigurationBuilder;
import dataObject.Author;
import dataObject.RepoConfiguration;
import util.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matanghao1 on 28/9/17.
 */
public class CSVConfigurationParser {

    public static List<RepoConfiguration> parseFromFile(File csvFile){
        String line;
        List<RepoConfiguration> configs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); //skip title line
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] elements = line.split(Constants.CSV_SPLITTER);
                String org = elements[0];
                String repoName = elements[1];
                String branch = elements[2];
                RepoConfiguration newConfig = new RepoConfiguration(org,repoName,branch);
                aggregateAuthorInfo(elements,newConfig);
                configs.add(newConfig);
            }
            return configs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void aggregateAuthorInfo(String[] elements, RepoConfiguration config){
        for (int i=3;i<elements.length;i++){
            Author currentAuthor = new Author(elements[i]);
            config.getAuthorList().add(currentAuthor);
            config.getAuthorAliasMap().put(elements[i],currentAuthor);
            i++;
            if (elements[i].length()!=0){
                for (String alias : elements[i].split(Constants.AUTHOR_ALIAS_SPLITTER)){
                    config.getAuthorAliasMap().put(alias,currentAuthor);
                }
            }
        }
    }
}
