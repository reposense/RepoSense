package reposense.model;

import java.io.IOException;
import java.util.List;

import reposense.parser.InvalidCsvException;
import reposense.parser.InvalidHeaderException;
import reposense.parser.ParseException;

public interface RunConfiguration {

    List<RepoConfiguration> getRepoConfigurations()
            throws ParseException, IOException, InvalidCsvException, InvalidHeaderException;

}
