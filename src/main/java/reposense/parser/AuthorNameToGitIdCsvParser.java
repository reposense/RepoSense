package reposense.parser;

import org.apache.commons.csv.CSVRecord;
import reposense.model.AuthorNameToGitId;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

public class AuthorNameToGitIdCsvParser extends CsvParser<AuthorNameToGitId>{
    private static final String AUTHOR_NAME_HEADER[] = {"Author Name"};
    private static final String GIT_ID_HEADER[] = {"Git Id"};

    public AuthorNameToGitIdCsvParser(Path csvFilePath) throws FileNotFoundException {
        super(csvFilePath);
    }

    @Override
    protected String[][] mandatoryHeaders() {
        return new String[][]{
                AUTHOR_NAME_HEADER, GIT_ID_HEADER
        };
    }

    @Override
    protected String[][] optionalHeaders() {
        return new String[0][];
    }


    @Override
    protected void processLine(List<AuthorNameToGitId> results, final CSVRecord record) {
        String authorName = get(record, AUTHOR_NAME_HEADER);
        String gitId = get(record, GIT_ID_HEADER);
        AuthorNameToGitId authorNameToGitId = new AuthorNameToGitId(authorName, gitId);
        results.add(authorNameToGitId);
    }
}
