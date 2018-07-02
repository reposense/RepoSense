package reposense.builder;

import java.util.List;

import reposense.model.Author;
import reposense.model.RepoConfiguration;


public class ConfigurationBuilder {
    private RepoConfiguration config;

    public ConfigurationBuilder(String organization, String repoName, String branch) {
        config = new RepoConfiguration(organization, repoName, branch);
    }

    public ConfigurationBuilder needCheckStyle(boolean value) {
        config.setNeedCheckStyle(value);
        return this;
    }

    public ConfigurationBuilder commitNum(int value) {
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

    public RepoConfiguration build() {
        return config;
    }

}
