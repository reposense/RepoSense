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
                List<Author> authors = getAuthors(elements);
                configs.add((new ConfigurationBuilder(org,repoName,branch)).authorList(authors).build());
            }
            return configs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Author> getAuthors(String[] elements){
        List<Author> authors = new ArrayList<>();
        for (int i=3;i<elements.length;i++){
            authors.add(new Author(elements[i]));
        }
        return authors;
    }
}
