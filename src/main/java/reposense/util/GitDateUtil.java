package reposense.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Contains git date related functionalities.
 */
public class GitDateUtil {
    public static final DateTimeFormatter GIT_STRICT_ISO_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

    /**
     * Returns a {@link ZonedDateTime} from a string {@code gitStrictIsoDate}.
     */
    public static ZonedDateTime parseGitStrictIsoDate(String gitStrictIsoDate) {
        return ZonedDateTime.parse(gitStrictIsoDate, GIT_STRICT_ISO_DATE_FORMAT);
    }

    /**
     * Returns a {@link LocalDateTime} from a string {@code gitStrictIsoDate}.
     */
    public static LocalDateTime parseGitStrictIsoDateWithZone(
            String gitStrictIsoDate, ZoneId zoneId) {
        return ZonedDateTime.parse(gitStrictIsoDate, GIT_STRICT_ISO_DATE_FORMAT)
                .withZoneSameInstant(zoneId).toLocalDateTime();
    }
}
