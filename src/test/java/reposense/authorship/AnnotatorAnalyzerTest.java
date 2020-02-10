package reposense.authorship;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

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

public class AnnotatorAnalyzerTest {

    private static final Author HARRY_AUTHOR = new Author("harryggg");
    private static final Author FAKE_AUTHOR = new Author("fakeAuthor");
    private static final String TEST_REPO_GIT_LOCATION = "https://github.com/reposense/testrepo-Alpha.git";
    private static RepoConfiguration config;
    private static final Date SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 8);
    private static final Date UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 10);

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), relativePath);
        return FileInfoAnalyzer.analyzeFile(config, fileInfo);
    }

    /**
     * Asserts the correctness of authorship overriding with regards to @@author tags given
     * in the test file.
     */
    public void assertAnnotationAnalysisCorrectness(FileResult fileResult, Author originalAuthor,
            Author overridingAuthor) {
        for (LineInfo line : fileResult.getLines()) {
            Author lineAuthor = line.getAuthor();
            if (line.getContent().startsWith("fake")) {
                Assert.assertEquals(originalAuthor, lineAuthor);
            } else {
                Assert.assertEquals(overridingAuthor, lineAuthor);
            }
        }
    }


    @BeforeClass
    public static void beforeClass() throws Exception {
        config = new RepoConfiguration(new RepoLocation(TEST_REPO_GIT_LOCATION), "master");
        GitClone.clone(config);
        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
    }

    @Test
    public void ignoreAnnotationTest() {
        config.setAnnotationOverwrite(false);
        config.setAuthorList(Collections.singletonList(FAKE_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertAnnotationAnalysisCorrectness(fileResult, FAKE_AUTHOR, FAKE_AUTHOR);
    }

    @Test
    public void applyAnnotationTest() {
        config.setAnnotationOverwrite(true);
        config.setAuthorList(Arrays.asList(FAKE_AUTHOR, HARRY_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertAnnotationAnalysisCorrectness(fileResult, FAKE_AUTHOR, HARRY_AUTHOR);
    }

    @Test
    public void disownCodeTest() {
        config.setAnnotationOverwrite(true);
        config.setAuthorList(Collections.singletonList(FAKE_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertAnnotationAnalysisCorrectness(fileResult, FAKE_AUTHOR, Author.UNKNOWN_AUTHOR);
    }

}
