package reposense.parser;

import java.io.File;
import java.io.IOException;
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
    private static final Path AUTHOR_CONFIG_EMPTY_LOCATION_FILE = new File(AuthorConfigParserTest.class
            .getClassLoader().getResource("AuthorConfigParserTest/authorconfig_emptyLocation_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_EMPTY_CONFIG_FILE = new File(AuthorConfigParserTest.class.getClassLoader()
            .getResource("AuthorConfigParserTest/require_trailing_whitespaces/authorconfig_emptyConfig_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE = new File(AuthorConfigParserTest.class
            .getClassLoader().getResource("AuthorConfigParserTest/authorconfig_noSpecialCharacter_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE = new File(AuthorConfigParserTest.class
            .getClassLoader().getResource("AuthorConfigParserTest/authorconfig_specialCharacter_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_COMMAS_AND_DOUBLEQUOTES_FILE = new File(AuthorConfigParserTest.class
            .getClassLoader().getResource("AuthorConfigParserTest/authorconfig_commasAndDoubleQuotes_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE = new File(AuthorConfigParserTest.class
            .getClassLoader().getResource("AuthorConfigParserTest/authorconfig_multipleEmails_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_INVALID_LOCATION = new File(AuthorConfigParserTest.class.getClassLoader()
            .getResource("AuthorConfigParserTest/authorconfig_invalidLocation_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_INVALID_HEADER_SIZE = new File(AuthorConfigParserTest.class.getClassLoader()
            .getResource("AuthorConfigParserTest/authorconfig_invalidHeaderSize_test.csv").getFile()).toPath();

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
    public void authorConfig_noSpecialCharacter_success() throws IOException, InvalidLocationException {
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
    public void authorConfig_emptyLocation_success() throws ParseException, IOException {
        AuthorConfiguration expectedConfig = new AuthorConfiguration(new RepoLocation(""));

        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_LOCATION_FILE);
        List<AuthorConfiguration> authorConfigs = authorConfigCsvParser.parse();
        AuthorConfiguration authorConfig = authorConfigs.get(0);

        Assert.assertEquals(1, authorConfigs.size());
        Assert.assertEquals(expectedConfig.getLocation(), authorConfig.getLocation());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfig.getBranch());
        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, authorConfig.getAuthorList());
    }

    @Test (expected = IOException.class)
    public void authorConfig_emptyConfig_throwsIoException() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_CONFIG_FILE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void authorConfig_specialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_multipleEmails_success() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Author actualAuthor = config.getAuthorList().get(0);
        Assert.assertEquals(FIRST_AUTHOR_EMAIL_LIST.size(), actualAuthor.getEmails().size());
        Assert.assertTrue(actualAuthor.getEmails().containsAll(FIRST_AUTHOR_EMAIL_LIST));
    }

    @Test
    public void authorConfig_invalidLocation_success() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_LOCATION);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(3, config.getAuthorList().size());
    }

    @Test (expected = IOException.class)
    public void authorConfig_invalidHeaderSize_throwsIoException() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_HEADER_SIZE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void parse_multipleColumnsWithCommasAndDoubleQuotes_success() throws IOException, InvalidLocationException {
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
