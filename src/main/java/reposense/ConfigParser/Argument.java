package reposense.ConfigParser;

import java.util.Date;
import java.util.Optional;

public final class Arguments {
    private static final String DEFAULT_FILE_ARG = ".";

    private Optional<Date> sinceDate;
    private Optional<Date> untilDate;
    private String outputFile = DEFAULT_FILE_ARG;

}
