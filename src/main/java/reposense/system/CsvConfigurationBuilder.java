package reposense.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import reposense.dataobject.Author;
import reposense.dataobject.RepoConfiguration;
import reposense.util.Constants;


public class CsvConfigurationBuilder {

    public static List<RepoConfiguration> buildConfigs(Path configFile, Date fromDate, Date toDate) {
        List<RepoConfiguration> configs = parseConfig(configFile);
        for (RepoConfiguration config : configs) {
            config.setToDate(toDate);
            config.setFromDate(fromDate);
        }
        return configs;
    }

    private static List<RepoConfiguration> parseConfig(Path csvFile) {
        String line;
        List<RepoConfiguration> configs = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(Files.newBufferedReader(csvFile))) {
            br.readLine(); //skip title line
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] elements = line.split(Constants.CSV_SPLITTER);
                String org = elements[0];
                String repoName = elements[1];
                String branch = elements[2];
                RepoConfiguration newConfig = new RepoConfiguration(org, repoName, branch);
                aggregateAuthorInfo(elements, newConfig);
                configs.add(newConfig);
            }
            return configs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void aggregateAuthorInfo(String[] elements, RepoConfiguration config) {
        for (int i = 3; i < elements.length; i += 3) {
            Author currentAuthor = new Author(elements[i]);
            config.getAuthorList().add(currentAuthor);
            //put the gitID itself as alias
            config.getAuthorAliasMap().put(elements[i], currentAuthor);
            //handle student's display name
            if (i + 1 == elements.length) {
                // put the gitID itself as display name if display name is not available
                config.getAuthorDisplayNameMap().put(currentAuthor, currentAuthor.getGitId());
                break;
            }
            if (elements[i + 1].length() == 0) {
                config.getAuthorDisplayNameMap().put(currentAuthor, currentAuthor.getGitId());
            } else {
                config.getAuthorDisplayNameMap().put(currentAuthor, elements[i + 1]);
            }
            //handle student's git aliases
            if (i + 2 == elements.length) {
                break;
            }
            if (elements[i + 2].length() != 0) {
                for (String alias : elements[i + 2].split(Constants.AUTHOR_ALIAS_SPLITTER)) {
                    config.getAuthorAliasMap().put(alias, currentAuthor);
                }
            }
        }
    }
}
