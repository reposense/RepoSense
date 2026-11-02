package reposense.authorship;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.authorship.analyzer.AnnotatorAnalyzer;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.RepoLocation;

public class AnnotatorAnalyzerOutOfDateTest {
    @Test
    public void aggregateAnnotationAuthorInfo_outOfDateTags_updateSegmentWithoutOverridingMarker() throws Exception {
        Author annotatedAuthor = new Author("annotated");
        Author writer = new Author("writer");
        AuthorConfiguration config = new AuthorConfiguration(new RepoLocation(""));
        config.addAuthor(annotatedAuthor);
        FileInfo fileInfo = new FileInfo("annotated.java");
        addLine(fileInfo, "// @@author annotated", Author.OUT_OF_DATE);
        addLine(fileInfo, "eligible unknown author", Author.UNKNOWN_AUTHOR);
        addLine(fileInfo, "excluded content", Author.OUT_OF_DATE);
        addLine(fileInfo, "// @@author", Author.OUT_OF_DATE);
        addLine(fileInfo, "after segment", writer);

        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config, true);

        List<Author> actualAuthors = fileInfo.getLines().stream()
                .map(LineInfo::getAuthor)
                .collect(Collectors.toList());
        Assertions.assertEquals(Arrays.asList(Author.OUT_OF_DATE, annotatedAuthor, Author.OUT_OF_DATE,
                Author.OUT_OF_DATE, writer), actualAuthors);
    }

    @Test
    public void aggregateAnnotationAuthorInfo_outOfDateDisownTag_stillDisownsEligibleLines() throws Exception {
        Author writer = new Author("writer");
        AuthorConfiguration config = new AuthorConfiguration(new RepoLocation(""));
        FileInfo fileInfo = new FileInfo("annotated.java");
        addLine(fileInfo, "// @@author", Author.OUT_OF_DATE);
        addLine(fileInfo, "disowned content", writer);

        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config, false);

        Assertions.assertEquals(Author.OUT_OF_DATE, fileInfo.getLines().get(0).getAuthor());
        Assertions.assertEquals(Author.UNKNOWN_AUTHOR, fileInfo.getLines().get(1).getAuthor());
    }

    private void addLine(FileInfo fileInfo, String content, Author author) {
        LineInfo line = new LineInfo(fileInfo.getNumOfLines() + 1, content);
        line.setAuthor(author);
        fileInfo.addLine(line);
    }
}
