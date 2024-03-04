package reposense.model;

import java.io.IOException;
import java.util.List;

import reposense.parser.exceptions.InvalidCsvException;
import reposense.parser.exceptions.InvalidHeaderException;
import reposense.parser.exceptions.ParseException;

/**
 * Interface to get configurations for current RepoSense run.
 */
public interface RunConfiguration {

    List<RepoConfiguration> getRepoConfigurations()
            throws ParseException, IOException, InvalidCsvException, InvalidHeaderException;

}
