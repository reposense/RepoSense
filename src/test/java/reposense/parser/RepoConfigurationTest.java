package reposense.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.report.ReportGenerator;

public class RepoConfigurationTest {

    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";
    private static final Author FIRST_AUTHOR = new Author("lithiumlkid");
    private static final Author SECOND_AUTHOR = new Author("codeeong");
    private static final Author THIRD_AUTHOR = new Author("jordancjq");
    private static final Author FOURTH_AUTHOR = new Author("lohtianwei");

    private static final List<String> FIRST_AUTHOR_ALIASES = Arrays.asList("Ahmad Syafiq");
    private static final List<String> SECOND_AUTHOR_ALIASES = Arrays.asList("Codee");
    private static final List<String> THIRD_AUTHOR_ALIASES = Arrays.asList("Jordan Chong");
    private static final List<String> FOURTH_AUTHOR_ALIASES = Arrays.asList("Tianwei");

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("*.pyc");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("*.aa1", "**.aa2");
    private static final List<String> SECOND_AUTHOR_GLOB_LIST = Collections.emptyList();

    @Test
    public void configJson_overridesRepoConfig_success() throws InvalidLocationException, GitDownloaderException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(SECOND_AUTHOR_GLOB_LIST);
        THIRD_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        FOURTH_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        List<Author> expectedAuthors = new ArrayList<Author>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);
        expectedAuthors.add(THIRD_AUTHOR);
        expectedAuthors.add(FOURTH_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.addAuthorAliases(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(SECOND_AUTHOR, SECOND_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(THIRD_AUTHOR, THIRD_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(FOURTH_AUTHOR, FOURTH_AUTHOR_ALIASES);
        expectedConfig.addAuthorDisplayName(FIRST_AUTHOR, "Ahm");
        expectedConfig.addAuthorDisplayName(SECOND_AUTHOR, "Cod");
        expectedConfig.addAuthorDisplayName(THIRD_AUTHOR, "Jor");
        expectedConfig.addAuthorDisplayName(FOURTH_AUTHOR, "Loh");
        expectedConfig.addAuthorAliases(FIRST_AUTHOR, Arrays.asList("Ahmad Syafiq"));
        expectedConfig.addAuthorAliases(SECOND_AUTHOR, Arrays.asList("Codee"));
        expectedConfig.addAuthorAliases(THIRD_AUTHOR, Arrays.asList("Jordan Chong"));
        expectedConfig.addAuthorAliases(FOURTH_AUTHOR, Arrays.asList("Tianwei"));

        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration actualConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        Assert.assertEquals(expectedConfig.getLocation(), actualConfig.getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfig.getAuthorList().hashCode());
        Assert.assertEquals(
                expectedConfig.getAuthorDisplayNameMap().hashCode(), actualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(), actualConfig.getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, actualConfig.getIgnoreGlobList());
    }
}
