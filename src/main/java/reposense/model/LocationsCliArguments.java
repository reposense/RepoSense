package reposense.model;

import java.nio.file.Path;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Represents command line arguments user supplied when running the program with mandatory field -repos.
 */
public class LocationsCliArguments extends CliArguments {
    private List<String> locations;

    public LocationsCliArguments(List<String> locations, Path outputFilePath, Path assetsFilePath, Date sinceDate,
            Date untilDate, boolean isSinceDateProvided, boolean isUntilDateProvided, int numCloningThreads,
            int numAnalysisThreads, List<FileType> formats, boolean isLastModifiedDateIncluded,
            boolean isShallowCloningPerformed, boolean isPrettifyJsonPerformed, boolean isAutomaticallyLaunching,
            boolean isStandaloneConfigIgnored, ZoneId zoneId) {
        this.locations = locations;
        this.outputFilePath = outputFilePath;
        this.assetsFilePath = assetsFilePath;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.isLastModifiedDateIncluded = isLastModifiedDateIncluded;
        this.isShallowCloningPerformed = isShallowCloningPerformed;
        this.isPrettifyJsonPerformed = isPrettifyJsonPerformed;
        this.formats = formats;
        this.isAutomaticallyLaunching = isAutomaticallyLaunching;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.numCloningThreads = numCloningThreads;
        this.numAnalysisThreads = numAnalysisThreads;
        this.zoneId = zoneId;
    }

    public List<String> getLocations() {
        return locations;
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
                && this.locations.equals(otherLocationsCliArguments.locations);
    }
}
