package reposense.model;

import reposense.model.RepoConfiguration;
import reposense.parser.InvalidCsvException;
import reposense.parser.InvalidHeaderException;
import reposense.parser.ParseException;

import java.io.IOException;
import java.util.List;

public interface RunConfiguration {

    List<RepoConfiguration> getRepoConfigurations() throws ParseException, IOException, InvalidCsvException, InvalidHeaderException;

}
