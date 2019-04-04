package reposense.model;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -repos.
 */
public class LocationsCliArguments extends CliArguments {
    private List<String> locations;
    private boolean isStandaloneConfigIgnored;

    public LocationsCliArguments(List<String> locations, Path outputFilePath, Optional<Date> sinceDate,
            Optional<Date> untilDate, List<Format> formats, boolean isAutomaticallyLaunching,
            boolean isStandaloneConfigIgnored, ZoneId zoneId) {
        this.locations = locations;
        this.outputFilePath = outputFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.formats = formats;
        this.isAutomaticallyLaunching = isAutomaticallyLaunching;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.zoneId = zoneId;
    }

    public List<String> getLocations() {
        return locations;
    }

    public boolean isStandaloneConfigIgnored() {
        return isStandaloneConfigIgnored;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof LocationsCliArguments)) {
            return false;
        }

        LocationsCliArguments otherLocationsCliArguments = (LocationsCliArguments) other;

        return super.equals(other)
                && this.locations.equals(otherLocationsCliArguments.locations)
                && this.isStandaloneConfigIgnored == otherLocationsCliArguments.isStandaloneConfigIgnored;
    }
}
