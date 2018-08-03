package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.RepoConfiguration;

public class CsvParserTest {
    private static final Path TEST_CONFIG_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("repoconfig_merge_test").getFile()).toPath();

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_BRANCH = "master";

    private static final Author FIRST_AUTHOR = new Author("nbriannl");
    private static final Author SECOND_AUTHOR = new Author("zacharytang");

    private static final List<String> FIRST_AUTHOR_ALIASES = Arrays.asList("Nbr");
    private static final List<String> SECOND_AUTHOR_ALIASES = Arrays.asList("Zac");

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("collated**", "**.java");

    @Test
    public void merge_twoRepoConfigs_success() throws ParseException, IOException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(TEST_REPO_BETA_LOCATION, TEST_REPO_BETA_BRANCH);
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.addAuthorAliases(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(SECOND_AUTHOR, SECOND_AUTHOR_ALIASES);
        expectedConfig.setAuthorDisplayName(SECOND_AUTHOR, "Zachary Tang");

        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        String input = String.format("-config %s", TEST_CONFIG_FOLDER);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(expectedConfig.getLocation(), actualConfigs.get(0).getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfigs.get(0).getAuthorList().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorDisplayNameMap().hashCode(),
                actualConfigs.get(0).getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(),
                actualConfigs.get(0).getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, actualConfigs.get(0).getIgnoreGlobList());
    }
}
