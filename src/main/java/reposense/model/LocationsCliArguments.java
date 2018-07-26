package reposense.model;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class LocationsCliArguments extends CliArguments {
    public LocationsCliArguments(List<String> locations,
            Path outputFilePath, Optional<Date> sinceDate, Optional<Date> untilDate, List<String> formats) {
        super(outputFilePath, sinceDate, untilDate, formats);
        this.locations = locations;
    }
}
