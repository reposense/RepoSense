package reposense.authorship;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.GitCheckout;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class FileResultAggregatorTest extends GitTestTemplate {

    private static final LocalDateTime CLEAR_FILE_LINES_SINCE_DATE =
            TestUtil.getSinceDate(2017, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime CLEAR_FILE_LINES_UNTIL_DATE =
            TestUtil.getUntilDate(2022, Month.MARCH.getValue(), 8);

    private RepoConfiguration config;
    private FileInfoExtractor fileInfoExtractor = new FileInfoExtractor();
    private FileInfoAnalyzer fileInfoAnalyzer = new FileInfoAnalyzer();

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void aggregateFileResult_clearFileLines_success() {
        config.setSinceDate(CLEAR_FILE_LINES_SINCE_DATE);
        config.setUntilDate(CLEAR_FILE_LINES_UNTIL_DATE);
        config.setBranch("1647-FileAnalyzerTest-analyzeTextFile_fileExceedingFileSizeLimit_success");
        config.setAuthorList(new ArrayList<>());
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());

        // Logic identical to AuthorshipReporter.java
        List<FileInfo> textFileInfos = fileInfoExtractor.extractTextFileInfos(config);

        List<FileResult> fileResults = textFileInfos.stream()
                .filter(f -> !f.getPath().equals("annotationTest.java"))
                .map(fileInfo -> fileInfoAnalyzer.analyzeTextFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //

        FileResultAggregator fileResultAggregator = new FileResultAggregator();
        fileResultAggregator.aggregateFileResult(fileResults, config.getAuthorList(), config.getAllFileTypes());
        Assertions.assertEquals(fileResults.stream()
                .filter(f -> f.getPath().contains("largeFile.json"))
                .findFirst()
                .map(f -> f.getLines().size()), Optional.of(0));
    }
}
