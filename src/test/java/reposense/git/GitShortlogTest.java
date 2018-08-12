package reposense.git;

import static reposense.git.GitShortlog.extractAuthorsFromLog;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;

public class GitShortlogTest extends GitTestTemplate {

    @Test
    public void extractAuthorsFromLog_validRepo_success() throws InvalidLocationException {
        List<Author> expectedAuthorList = new ArrayList<Author>();
        expectedAuthorList.add(new Author("eugenepeh"));
        expectedAuthorList.add(new Author("fakeAuthor"));
        expectedAuthorList.add(new Author("harryggg"));

        List<Author> actualAuthorList = extractAuthorsFromLog(config);

        Assert.assertEquals(3, actualAuthorList.size());
        Assert.assertEquals(expectedAuthorList, actualAuthorList);
    }
}
