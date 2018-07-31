package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -repos.
 */
public class LocationsCliArguments extends CliArguments {
    private List<String> locations;

    public LocationsCliArguments(List<String> locations,
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<String> formats) {
        this.locations = locations;
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
    }

    public List<String> getLocations() {
        return locations;
    }
}
