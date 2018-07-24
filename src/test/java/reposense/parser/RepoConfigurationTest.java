package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.StandaloneConfig;
import reposense.report.ReportGenerator;

public class RepoConfigurationTest {

    private static final String GIT_TEST_LOCATION = "https://github.com/reposense/testrepo-Delta.git";

    @Test
    public void configJson_overridesRepoConfig_success()
            throws InvalidLocationException, FileNotFoundException, GitDownloaderException {
        RepoConfiguration expectedConfig = new RepoConfiguration(GIT_TEST_LOCATION, "master");

        List<Author> authors = new ArrayList<Author>();
        authors.add(new Author("lithiumlkid"));
        authors.add(new Author("codeeong"));
        authors.add(new Author("jordancjq"));
        authors.add(new Author("lohtianwei"));

        expectedConfig.setAuthorList(authors);

        expectedConfig.setAuthorDisplayName(new Author("lithiumlkid"), "Ahm");
        expectedConfig.setAuthorDisplayName(new Author("codeeong"), "Cod");
        expectedConfig.setAuthorDisplayName(new Author("jordancjq"), "Jor");
        expectedConfig.setAuthorDisplayName(new Author("lohtianwei"), "Loh");

        expectedConfig.setAuthorAliases(new Author("lithiumlkid"), "lithiumlkid");
        expectedConfig.setAuthorAliases(new Author("codeeong"), "codeeong");
        expectedConfig.setAuthorAliases(new Author("jordancjq"), "jordancjq");
        expectedConfig.setAuthorAliases(new Author("lohtianwei"), "lohtianwei");

        RepoConfiguration actualConfig = new RepoConfiguration(GIT_TEST_LOCATION, "master");
        GitDownloader.downloadRepo(actualConfig);

        Path configJsonPath = Paths.get(actualConfig.getRepoRoot(),
                ReportGenerator.REPOSENSE_CONFIG_FOLDER, ReportGenerator.REPOSENSE_CONFIG_FILE).toAbsolutePath();
        StandaloneConfig standaloneConfig =  new StandaloneConfigJsonParser().parse(configJsonPath);
        actualConfig.updateRepoConfig(standaloneConfig);

        Assert.assertEquals(expectedConfig.getLocation(), actualConfig.getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfig.getAuthorList().hashCode());
        Assert.assertEquals(
                expectedConfig.getAuthorDisplayNameMap().hashCode(), actualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(), actualConfig.getAuthorAliasMap().hashCode());
    }
}
