package reposense.authorship;

import static reposense.model.Author.UNKNOWN_AUTHOR;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AnnotatorAnalyzerTest extends GitTestTemplate {
    private static final Date SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 8);
    private static final Date UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 10);
    private static final String TIME_ZONE_ID_STRING = "Asia/Singapore";
    private static final Author[] EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_DISOWN_CODE_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR
    };


    @Before
    public void before() throws Exception {
        super.before();
        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
        config.setZoneId(TIME_ZONE_ID_STRING);
    }

    @Test
    public void analyzeAnnotation_authorNamePresentInConfig_overrideAuthorship() {
        config.setAuthorList(Arrays.asList(FAKE_AUTHOR, MAIN_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST));
    }

    @Test
    public void analyzeAnnotation_authorNameNotInConfig_disownCode() {
        config.setAuthorList(Collections.singletonList(FAKE_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_DISOWN_CODE_TEST));
    }

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), relativePath);
        return FileInfoAnalyzer.analyzeTextFile(config, fileInfo);
    }
}
