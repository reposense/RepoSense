package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.RepoLocation;

public class AuthorConfigParserTest {
    private static final Path AUTHOR_CONFIG_EMPTY_LOCATION_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_emptyLocation_test.csv");
    private static final Path AUTHOR_CONFIG_EMPTY_CONFIG_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/require_trailing_whitespaces/authorconfig_emptyConfig_test.csv");
    private static final Path AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_noSpecialCharacter_test.csv");
    private static final Path AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_specialCharacter_test.csv");
    private static final Path AUTHOR_CONFIG_COMMAS_AND_DOUBLEQUOTES_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_commasAndDoubleQuotes_test.csv");
    private static final Path AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_multipleEmails_test.csv");
    private static final Path AUTHOR_CONFIG_INVALID_LOCATION = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_invalidLocation_test.csv");
    private static final Path AUTHOR_CONFIG_INVALID_HEADER_SIZE = loadResource(AuthorConfigParserTest.class,
            "AuthorConfigParserTest/authorconfig_invalidHeaderSize_test.csv");

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_MASTER_BRANCH = "master";

    private static final Author FIRST_AUTHOR = new Author("nbriannl");
    private static final Author SECOND_AUTHOR = new Author("zacharytang");
    private static final List<Author> AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS =
            Arrays.asList(FIRST_AUTHOR, SECOND_AUTHOR);

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);

    private static final Author FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("ProcessedCooked");
    private static final Author SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("codeeong");
    private static final Author THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("jordancjq");
    private static final String FIRST_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "Tay Fan Gao, Douya";
    private static final String SECOND_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "\"\"Tora, S/O,\" Doyua, T.\"";
    private static final String THIRD_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "jordancjq";
    private static final List<String> FIRST_COMMAS_AND_DOUBLEQUOTES_ALIAS =
            Collections.singletonList("Tay Fan Gao, Douya \"SOC, Y2S1\"");
    private static final List<String> SECOND_COMMAS_AND_DOUBLEQUOTES_ALIAS = Collections.emptyList();
    private static final List<String> THIRD_COMMAS_AND_DOUBLEQUOTES_ALIAS =
            Arrays.asList("Borex T\"ony Tong");
    private static final Map<Author, List<String>> AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP =
            Stream.of(new Object[][]{
                    {FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR, FIRST_COMMAS_AND_DOUBLEQUOTES_ALIAS},
                    {SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR, SECOND_COMMAS_AND_DOUBLEQUOTES_ALIAS},
                    {THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR, THIRD_COMMAS_AND_DOUBLEQUOTES_ALIAS}
            }).collect(Collectors.toMap(data -> (Author) data[0], data -> (List<String>) data[1]));
    private static final Map<Author, String> AUTHOR_DISPLAY_NAME_COMMAS_AND_DOUBLE_QUOTES_MAP =
            Stream.of(new Object[][]{
                    {FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR, FIRST_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME},
                    {SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR, SECOND_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME},
                    {THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR, THIRD_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME}
            }).collect(Collectors.toMap(data -> (Author) data[0], data -> (String) data[1]));

    private static final List<String> FIRST_AUTHOR_EMAIL_LIST =
            Arrays.asList("nbr@example.com", "nbriannl@test.net", "nbriannl@users.noreply.github.com");

    @Test
    public void authorConfig_noSpecialCharacter_success() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_emptyLocation_success() throws Exception {
        AuthorConfiguration expectedConfig = new AuthorConfiguration(new RepoLocation(""));

        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_LOCATION_FILE);
        List<AuthorConfiguration> authorConfigs = authorConfigCsvParser.parse();
        AuthorConfiguration authorConfig = authorConfigs.get(0);

        Assert.assertEquals(1, authorConfigs.size());
        Assert.assertEquals(expectedConfig.getLocation(), authorConfig.getLocation());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfig.getBranch());
        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, authorConfig.getAuthorList());
    }

    @Test (expected = InvalidCsvException.class)
    public void authorConfig_emptyConfig_throwsInvalidCsvException() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_CONFIG_FILE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void authorConfig_specialCharacter_success() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_multipleEmails_success() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Author actualAuthor = config.getAuthorList().get(0);
        Assert.assertEquals(FIRST_AUTHOR_EMAIL_LIST.size(), actualAuthor.getEmails().size());
        Assert.assertTrue(actualAuthor.getEmails().containsAll(FIRST_AUTHOR_EMAIL_LIST));
    }

    @Test
    public void authorConfig_invalidLocation_success() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_LOCATION);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(3, config.getAuthorList().size());
    }

    @Test (expected = InvalidCsvException.class)
    public void authorConfig_invalidHeaderSize_throwsInvalidCsvException() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_HEADER_SIZE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void parse_multipleColumnsWithCommasAndDoubleQuotes_success() throws Exception {
        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_COMMAS_AND_DOUBLEQUOTES_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());
        Assert.assertEquals(AUTHOR_DISPLAY_NAME_COMMAS_AND_DOUBLE_QUOTES_MAP, config.getAuthorDisplayNameMap());

        Assert.assertEquals(AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP.size(), config.getAuthorList().size());
        config.getAuthorList().forEach(author -> {
            Assert.assertEquals(AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP.get(author), author.getAuthorAliases());
        });
    }
}
