package factory;

import dataObject.Configuration;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class ConfigurationFactory {
    public static Configuration getMinimalConfig(String organization, String repoName, String branch){
        return new Configuration(organization, repoName, branch);
    }

    public static Configuration getCheckStyleConfig(String organization, String repoName, String branch){
        Configuration config = new Configuration(organization, repoName, branch);
        config.setNeedCheckStyle(true);
        return config;
    }
}
