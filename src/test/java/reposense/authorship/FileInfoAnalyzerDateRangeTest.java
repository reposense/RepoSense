package reposense.authorship;

import static reposense.parser.ArgsParser.DEFAULT_ORIGINALITY_THRESHOLD;
import static reposense.system.CommandRunner.runCommand;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

public class FileInfoAnalyzerDateRangeTest {
    private static final String FILE_NAME = "annotated.java";
    private static final Author ANNOTATED_AUTHOR = new Author("annotated");

    @TempDir
    private Path tempDir;

    private RepoConfiguration config;

    @BeforeEach
    public void setUp() throws Exception {
        runCommand(tempDir, "git init");
        commitLines("2026-10-30T09:59:59+08:00", "// @@author annotated", "old");
        commitLines("2026-10-30T10:00:00+08:00", "at since");
        commitLines("2026-10-30T10:30:00+08:00", "within range");
        commitLines("2026-10-30T11:00:00+08:00", "at until");
        commitLines("2026-10-30T11:00:01+08:00", "// @@author", "after range");

        config = new RepoConfiguration.Builder()
                .location(new RepoLocation(""))
                .repoFolderName(tempDir.toString())
                .zoneId(ZoneId.of("UTC+08:00"))
                .sinceDate(LocalDateTime.of(2026, 10, 30, 10, 0))
                .untilDate(LocalDateTime.of(2026, 10, 30, 11, 0))
                .fileTypeManager(Collections.singletonList(FileType.convertStringFormatToFileType("java")))
                .build();
        config.addAuthors(Arrays.asList(new Author("writer"), ANNOTATED_AUTHOR));
    }

    @Test
    public void analyzeTextFile_annotationsRespectDateRangeWithoutLastModifiedDates() throws Exception {
        FileResult result = assertDateFilteredAnnotations();
        result.getLines().forEach(line -> Assertions.assertNull(line.getLastModifiedDate()));
    }

    @Test
    public void analyzeTextFile_annotationsRespectDateRangeWithLastModifiedDates() throws Exception {
        config.setIsLastModifiedDateIncluded(true);
        FileResult result = assertDateFilteredAnnotations();
        Assertions.assertEquals(config.getSinceDate(), result.getLines().get(2).getLastModifiedDate());
        Assertions.assertEquals(config.getUntilDate(), result.getLines().get(4).getLastModifiedDate());
    }

    @Test
    public void analyzeTextFile_allAnnotatedLinesOutsideDateRange_returnsNull() throws Exception {
        config.setSinceDate(LocalDateTime.of(2026, 10, 30, 12, 0));
        config.setUntilDate(LocalDateTime.of(2026, 10, 30, 13, 0));
        FileInfo fileInfo = readFileInfo();
        Assertions.assertNull(analyze(fileInfo));
        fileInfo.getLines().forEach(line -> Assertions.assertEquals(Author.UNKNOWN_AUTHOR, line.getAuthor()));
    }

    private FileResult assertDateFilteredAnnotations() throws Exception {
        FileResult result = analyze(readFileInfo());
        Assertions.assertNotNull(result);
        List<Author> actualAuthors = result.getLines().stream()
                .map(LineInfo::getAuthor)
                .collect(Collectors.toList());
        Assertions.assertEquals(Arrays.asList(Author.UNKNOWN_AUTHOR, Author.UNKNOWN_AUTHOR,
                ANNOTATED_AUTHOR, ANNOTATED_AUTHOR, ANNOTATED_AUTHOR,
                Author.UNKNOWN_AUTHOR, Author.UNKNOWN_AUTHOR), actualAuthors);
        Assertions.assertEquals(3, result.getAuthorContributionMap().get(ANNOTATED_AUTHOR));
        return result;
    }

    private FileInfo readFileInfo() throws Exception {
        FileInfo fileInfo = new FileInfo(FILE_NAME);
        for (String content : Files.readAllLines(tempDir.resolve(FILE_NAME), StandardCharsets.UTF_8)) {
            fileInfo.addLine(new LineInfo(fileInfo.getNumOfLines() + 1, content));
        }
        return fileInfo;
    }

    private FileResult analyze(FileInfo fileInfo) {
        return new FileInfoAnalyzer().analyzeTextFile(config, fileInfo, false, DEFAULT_ORIGINALITY_THRESHOLD);
    }

    private void commitLines(String date, String... lines) throws Exception {
        Files.write(tempDir.resolve(FILE_NAME), Arrays.asList(lines), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        runCommand(tempDir, "git add " + FILE_NAME);
        runCommand(tempDir, "git -c user.name=writer -c user.email=writer@example.com -c commit.gpgsign=false"
                + " commit --date=" + date + " -m test");
    }
}
