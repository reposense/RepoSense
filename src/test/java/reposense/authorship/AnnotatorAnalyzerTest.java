package reposense.authorship;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitClone;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class AnnotatorAnalyzerTest {

    private static final Date BLAME_TEST_SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 8);
    private static final Date BLAME_TEST_UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 10);
    protected static final Author HARRY_AUTHOR = new Author("harryggg");
    protected static final Author FAKE_AUTHOR= new Author("fakeAuthor");
    protected static final String TEST_REPO_GIT_LOCATION = "https://github.com/reposense/testrepo-Alpha.git";
    protected static RepoConfiguration config;

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), relativePath);
        return FileInfoAnalyzer.analyzeFile(config, fileInfo);
    }

    /**
     * Asserts the correctness of file analysis with regards to the contribution
     * made by author named in {@code FAKE_AUTHOR_NAME}.
     */
    public void assertFileAnalysisCorrectness(FileResult fileResult, Author originalAuthor,
            Author overridingAuthor) {
        for (LineInfo line : fileResult.getLines()) {
            Author lineAuthor = line.getAuthor();
            if (line.getContent().startsWith("fake")) {
                Assert.assertEquals(lineAuthor, originalAuthor);
            } else {
                Assert.assertEquals(lineAuthor, overridingAuthor);
            }
        }
    }


    @BeforeClass
    public static void before() throws Exception {
        config = new RepoConfiguration(new RepoLocation(TEST_REPO_GIT_LOCATION), "master");
        GitClone.clone(config);
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
    }

    @Test
    public void ignoreAnnotationTest() {
        config.setAnnotationOverwrite(false);
        config.setAuthorList(new ArrayList<>(Collections.singletonList(FAKE_AUTHOR)));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, FAKE_AUTHOR, null);
    }

    @Test
    public void applyAnnotationTest() {
        config.setAnnotationOverwrite(true);
        config.setAuthorList(new ArrayList<>(Arrays.asList(FAKE_AUTHOR, HARRY_AUTHOR)));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, FAKE_AUTHOR, HARRY_AUTHOR);
    }

    @Test
    public void disownCodeTest() {
        config.setAnnotationOverwrite(true);
        config.setAuthorList(new ArrayList<>(Collections.singletonList(FAKE_AUTHOR)));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, FAKE_AUTHOR, Author.UNKNOWN_AUTHOR);
    }

}
