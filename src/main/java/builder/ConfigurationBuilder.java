package builder;

import dataObject.Author;
import dataObject.Configuration;

import java.util.List;

/**
 * Created by matanghao1 on 8/8/17.
 */

public class ConfigurationBuilder {
      private Configuration config;

      public ConfigurationBuilder(String organization, String repoName, String branch){
          config = new Configuration(organization, repoName, branch);
      }

      public ConfigurationBuilder needCheckStyle(boolean value) {
          config.setNeedCheckStyle(value);
          return this;
      }

      public  ConfigurationBuilder commitNum(int value) {
          config.setCommitNum(value);
          return this;
      }

      public ConfigurationBuilder annotationOverwrite(boolean value) {
          config.setAnnotationOverwrite(value);
          return this;
      }

      public ConfigurationBuilder ignoreDirectoryList(List<String> list) {
          config.setIgnoreDirectoryList(list);
          return this;
      }

    public ConfigurationBuilder authorList(List<Author> list) {
        config.setAuthorList(list);
        return this;
    }

      public Configuration build() {
          return config;
      }

}
