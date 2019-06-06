package reposense.builder;

import java.util.List;

import reposense.model.Author;
import reposense.model.Group;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;

/**
 *
 */
public class ConfigurationBuilder {
    private RepoConfiguration config;

    public ConfigurationBuilder(String location, String branch) throws InvalidLocationException {
        config = new RepoConfiguration(new RepoLocation(location), branch);
    }

    /**
     *
     */
    public ConfigurationBuilder commitNum(int value) {
        config.setCommitNum(value);
        return this;
    }

    /**
     *
     */
    public ConfigurationBuilder annotationOverwrite(boolean value) {
        config.setAnnotationOverwrite(value);
        return this;
    }

    /**
     *
     */
    public ConfigurationBuilder ignoreGlobList(List<String> list) {
        config.setIgnoreGlobList(list);
        return this;
    }

    /**
     *
     */
    public ConfigurationBuilder authorList(List<Author> list) {
        config.setAuthorList(list);
        return this;
    }

    /**
     *
     */
    public ConfigurationBuilder groupList(List<Group> list) {
        config.setGroupList(list);
        return this;
    }

    public RepoConfiguration build() {
        return config;
    }

}
