package reposense.template;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import reposense.authorship.FileInfoAnalyzer;
import reposense.authorship.FileInfoExtractor;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitCheckout;
import reposense.git.GitShow;
import reposense.git.exception.CommitNotFoundException;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.FileUtil;
import reposense.util.TestRepoCloner;

/**
 * Contains templates for git testing.
 */

public class GitTestTemplate {
    protected static final String TEST_REPO_GIT_LOCATION = "https://github.com/reposense/testrepo-Alpha.git";
    protected static final String IGNORE_REVS_FILE_NAME = ".git-blame-ignore-revs";
    protected static final String TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH = "1565-find-previous-authors";
    protected static final String FIRST_COMMIT_HASH = "7d7584f";
    protected static final String ROOT_COMMIT_HASH = "fd425072e12004b71d733a58d819d845509f8db3";
    protected static final String TEST_COMMIT_HASH = "2fb6b9b";
    protected static final String TEST_COMMIT_HASH_LONG = "2fb6b9b2dd9fa40bf0f9815da2cb0ae8731436c7";
    protected static final String TEST_COMMIT_HASH_PARENT = "c5a6dc774e22099cd9ddeb0faff1e75f9cf4f151";
    protected static final String MAIN_AUTHOR_NAME = "harryggg";
    protected static final String FAKE_AUTHOR_NAME = "fakeAuthor";
    protected static final String UNCONVENTIONAL_AUTHOR_NAME = "-unconventional_author-";
    protected static final String WHITESPACE_AUTHOR_NAME = "whitespace author";
    protected static final String IGNORED_AUTHOR_NAME = "FH-30";
    protected static final String EUGENE_AUTHOR_NAME = "eugenepeh";
    protected static final String YONG_AUTHOR_NAME = "Yong Hao TENG";
    protected static final String MINGYI_AUTHOR_NAME = "myteo";
    protected static final String JAMES_AUTHOR_NAME = "jamessspanggg";
    protected static final String JAMES_ALTERNATIVE_AUTHOR_NAME = "James Pang";
    protected static final String JINYAO_AUTHOR_NAME = "jylee-git";
    protected static final String LATEST_COMMIT_HASH = "abbd5888d5cd4e411c6a8e58e661b0eafdae1335";
    protected static final String LATEST_COMMIT_HASH_PARENT = "f768b9b1d9d1478f8ac8cf3b4c7f868479edc07a";
    protected static final String EMPTY_TREE_HASH = "4b825dc642cb6eb9a060e54bf8d69288fbee4904";
    protected static final String EUGENE_AUTHOR_README_FILE_COMMIT_07052018_STRING =
            "2d87a431fcbb8f73a731b6df0fcbee962c85c250";
    protected static final CommitHash EUGENE_AUTHOR_README_FILE_COMMIT_07052018 =
            new CommitHash(EUGENE_AUTHOR_README_FILE_COMMIT_07052018_STRING);
    protected static final String FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING =
            "768015345e70f06add2a8b7d1f901dc07bf70582";
    protected static final CommitHash FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018 =
            new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING);
    protected static final String MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING =
            "8d0ac2ee20f04dce8df0591caed460bffacb65a4";
    protected static final CommitHash MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018 =
            new CommitHash(MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING);
    protected static final String AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021_STRING =
            "1d29339e7d16eb5b2bc8fb542e08acedd3d4b0eb";
    protected static final CommitHash AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021 =
            new CommitHash(AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021_STRING);
    protected static final String FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING =
            "7d7584fc204922cc5ff3bd5ca073cad6bed2c46a";
    protected static final String FAKE_AUTHOR_BLAME_RANGED_COMMIT_TWO_06022018_STRING =
            "8d0ac2ee20f04dce8df0591caed460bffacb65a4";
    protected static final String FAKE_AUTHOR_BLAME_RANGED_COMMIT_THREE_07022018_STRING =
            "8e4ca1da5d413e9ab84a1e8d1474918afa97f7a1";
    protected static final String FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING =
            "768015345e70f06add2a8b7d1f901dc07bf70582";
    protected static final List<CommitHash> FAKE_AUTHOR_BLAME_RANGED_COMMIT_LIST_09022018 = Arrays.asList(
            new CommitHash(FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING),
            new CommitHash(FAKE_AUTHOR_BLAME_RANGED_COMMIT_TWO_06022018_STRING),
            new CommitHash(FAKE_AUTHOR_BLAME_RANGED_COMMIT_THREE_07022018_STRING),
            new CommitHash(FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING));
    protected static final List<CommitHash> AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_07082021 = Collections.singletonList(
            new CommitHash(AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021_STRING)
    );
    protected static final String NONEXISTENT_COMMIT_HASH = "nonExistentCommitHash";
    protected static final ZoneId TIME_ZONE_ID = ZoneId.of("Asia/Singapore");

    protected static final Author MAIN_AUTHOR = new Author(MAIN_AUTHOR_NAME);
    protected static final Author FAKE_AUTHOR = new Author(FAKE_AUTHOR_NAME);
    protected static final Author UNCONVENTIONAL_AUTHOR = new Author(UNCONVENTIONAL_AUTHOR_NAME);
    protected static final Author WHITESPACE_AUTHOR = new Author(WHITESPACE_AUTHOR_NAME);

    protected static ThreadLocal<RepoConfiguration> configs = ThreadLocal.withInitial(() -> {
        try {
            return newRepoConfiguration();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    });

    private static final Supplier<String> EXTRA_OUTPUT_FOLDER_NAME_SUPPLIER = () ->
            String.valueOf(Thread.currentThread().getId());

    @BeforeEach
    public void before() throws Exception {
        RepoConfiguration config = newRepoConfiguration();
        config.setAuthorList(Collections.singletonList(getAlphaAllAliasAuthor()));
        config.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        config.setZoneId(TIME_ZONE_ID);
        config.setIsLastModifiedDateIncluded(false);

        configs.set(config);
    }

    @BeforeAll
    public static void beforeClass() throws Exception {
        RepoConfiguration config = newRepoConfiguration();
        config.setZoneId(TIME_ZONE_ID);
        configs.set(config);

        TestRepoCloner.cloneAndBranch(config, EXTRA_OUTPUT_FOLDER_NAME_SUPPLIER.get());
    }

    @AfterEach
    public void after() {
        GitCheckout.checkout(configs.get().getRepoRoot(), "master");
    }

    private static RepoConfiguration newRepoConfiguration() throws Exception {
        return new RepoConfiguration(new RepoLocation(TEST_REPO_GIT_LOCATION), "master",
                EXTRA_OUTPUT_FOLDER_NAME_SUPPLIER.get());
    }

    /**
     * Generates the .git-blame-ignore-revs file containing {@link CommitHash}es
     * from {@code toIgnore} for the test repo.
     */
    public List<CommitHash> createTestIgnoreRevsFile(List<CommitHash> toIgnore) {
        String repoRoot = configs.get().getRepoRoot();
        List<CommitHash> expandedIgnoreCommitList = toIgnore.stream()
                .map(CommitHash::toString)
                .map(commitHash -> {
                    try {
                        return GitShow.getExpandedCommitHash(repoRoot, commitHash);
                    } catch (CommitNotFoundException e) {
                        return new CommitHash(commitHash);
                    }
                })
                .collect(Collectors.toList());

        String fileLocation = repoRoot + IGNORE_REVS_FILE_NAME;
        FileUtil.writeIgnoreRevsFile(fileLocation, expandedIgnoreCommitList);
        return expandedIgnoreCommitList;
    }

    public void removeTestIgnoreRevsFile() {
        String fileLocation = configs.get().getRepoRoot() + IGNORE_REVS_FILE_NAME;
        new File(fileLocation).delete();
    }

    /**
     * For each line in {@link FileResult}, assert that it is attributed to the expected author provided by
     * {@code expectedLineAuthors}.
     */
    public void assertFileAnalysisCorrectness(FileResult fileResult, List<Author> expectedLineAuthors) {
        List<LineInfo> lines = fileResult.getLines();
        assertEquals(expectedLineAuthors.size(), lines.size());

        Iterator<Author> lineAuthorsItr = expectedLineAuthors.iterator();
        Iterator<LineInfo> linesItr = lines.iterator();

        while (linesItr.hasNext() && lineAuthorsItr.hasNext()) {
            assertEquals(lineAuthorsItr.next(), linesItr.next().getAuthor());
        }
    }

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(configs.get(), relativePath);
        return FileInfoAnalyzer.analyzeTextFile(configs.get(), fileInfo);
    }

    /**
     * Returns a {@link Author} that has git id and aliases of all authors in testrepo-Alpha, so that no commits
     * will be filtered out in the `git log` command.
     */
    protected Author getAlphaAllAliasAuthor() {
        Author author = new Author(MAIN_AUTHOR_NAME);
        author.setAuthorAliases(Arrays.asList(FAKE_AUTHOR_NAME, EUGENE_AUTHOR_NAME, YONG_AUTHOR_NAME));
        return author;
    }
}
