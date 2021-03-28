package reposense.parser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.impl.action.VersionArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import reposense.RepoSense;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.FileType;
import reposense.model.LocationsCliArguments;
import reposense.model.ViewCliArguments;
import reposense.system.LogsManager;

/**
 * Verifies and parses a string-formatted date to a {@code CliArguments} object.
 */
public class ArgsParser {
    public static final String DEFAULT_REPORT_NAME = "reposense-report";

    public static final String[] HELP_FLAGS = new String[]{"--help", "-h"};
    public static final String[] CONFIG_FLAGS = new String[]{"--config", "-c"};
    public static final String[] REPO_FLAGS = new String[]{"--repo", "--repos", "-r"};
    public static final String[] VIEW_FLAGS = new String[]{"--view", "-v"};
    public static final String[] OUTPUT_FLAGS = new String[]{"--output", "-o"};
    public static final String[] ASSETS_FLAGS = new String[]{"--assets", "-a"};
    public static final String[] SINCE_FLAGS = new String[]{"--since", "-s"};
    public static final String[] UNTIL_FLAGS = new String[]{"--until", "-u"};
    public static final String[] PERIOD_FLAGS = new String[]{"--period", "-p"};
    public static final String[] SHALLOW_CLONING_FLAGS = new String[]{"--shallow-cloning", "-S"};
    public static final String[] FORMAT_FLAGS = new String[]{"--formats", "-f"};
    public static final String[] IGNORE_FLAGS = new String[]{"--ignore-standalone-config", "-i"};
    public static final String[] TIMEZONE_FLAGS = new String[]{"--timezone", "-t"};
    public static final String[] VERSION_FLAGS = new String[]{"--version", "-V"};
    public static final String[] LAST_MODIFIED_DATE_FLAGS = new String[]{"--last-modified-date", "-l"};

    private static final Logger logger = LogsManager.getLogger(ArgsParser.class);

    private static final String PROGRAM_USAGE = "java -jar RepoSense.jar";
    private static final String PROGRAM_DESCRIPTION =
            "RepoSense is a contribution analysis tool for Git repositories.";
    private static final String MESSAGE_HEADER_MUTEX = "mutual exclusive arguments";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\".";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" must not be later than today's date.";
    private static final String MESSAGE_HAVE_SINCE_DATE_UNTIL_DATE_AND_PERIOD =
            "\"Since Date\", \"Until Date\", and \"Period\" cannot be applied together.";
    private static final String MESSAGE_USING_DEFAULT_CONFIG_PATH =
            "Config path not provided, using the config folder as default.";
    private static final Path EMPTY_PATH = Paths.get("");
    private static final Path DEFAULT_CONFIG_PATH = Paths.get(System.getProperty("user.dir")
            + File.separator + "config" + File.separator);
    private static final Path DEFAULT_ASSETS_PATH = Paths.get(System.getProperty("user.dir")
            + File.separator + "assets" + File.separator);

    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor(PROGRAM_USAGE)
                .addHelp(false)
                .build()
                .description(PROGRAM_DESCRIPTION);

        MutuallyExclusiveGroup mutexParser = parser
                .addMutuallyExclusiveGroup(MESSAGE_HEADER_MUTEX)
                .required(false);

        MutuallyExclusiveGroup mutexParser2 = parser
                .addMutuallyExclusiveGroup(MESSAGE_HEADER_MUTEX)
                .required(false);

        // Boolean flags
        parser.addArgument(HELP_FLAGS)
                .help("Show help message.")
                .action(new HelpArgumentAction());

        parser.version("RepoSense " + RepoSense.getVersion());
        parser.addArgument(VERSION_FLAGS)
                .help("Show the version of RepoSense.")
                .action(new VersionArgumentAction());

        parser.addArgument(IGNORE_FLAGS)
                .dest(IGNORE_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to ignore the standalone config file in the repo.");

        parser.addArgument(VIEW_FLAGS)
                .dest(VIEW_FLAGS[0])
                .nargs("?")
                .metavar("PATH")
                .type(new ReportFolderArgumentType())
                .setConst(EMPTY_PATH)
                .help("Starts a server to display the report in the provided directory. "
                        + "If used as a flag (with no argument), "
                        + "generates a report and automatically displays the report.");

        parser.addArgument(OUTPUT_FLAGS)
                .dest(OUTPUT_FLAGS[0])
                .metavar("PATH")
                .type(new OutputFolderArgumentType())
                .setDefault(Paths.get(ArgsParser.DEFAULT_REPORT_NAME))
                .help("The directory to output the report folder, reposense-report. "
                        + "If not provided, the report folder will be created in the current working directory.");

        parser.addArgument(ASSETS_FLAGS)
                .dest(ASSETS_FLAGS[0])
                .metavar("PATH")
                .type(new AssetsFolderArgumentType())
                .setDefault(DEFAULT_ASSETS_PATH)
                .help("The directory to place assets files to customize report generation. "
                        + "If not provided, the assets folder in the current working directory will be used.");

        parser.addArgument(SINCE_FLAGS)
                .dest(SINCE_FLAGS[0])
                .metavar("dd/MM/yyyy")
                .type(new SinceDateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to start filtering.");

        parser.addArgument(UNTIL_FLAGS)
                .dest(UNTIL_FLAGS[0])
                .metavar("dd/MM/yyyy")
                .type(new UntilDateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to stop filtering.");

        parser.addArgument(PERIOD_FLAGS)
                .dest(PERIOD_FLAGS[0])
                .metavar("PERIOD")
                .type(new PeriodArgumentType())
                .setDefault(Optional.empty())
                .help("The number of days of the filtering window.");

        parser.addArgument(FORMAT_FLAGS)
                .dest(FORMAT_FLAGS[0])
                .nargs("*")
                .metavar("FORMAT")
                .type(new AlphanumericArgumentType())
                .setDefault(Collections.emptyList())
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

        parser.addArgument(TIMEZONE_FLAGS)
                .dest(TIMEZONE_FLAGS[0])
                .metavar("ZONE_ID[±hh[mm]]")
                .type(new ZoneIdArgumentType())
                .setDefault(ZoneId.systemDefault())
                .help("The timezone to use for the generated report. "
                        + "One kind of valid timezones is relative to UTC. E.g. UTC, UTC+08, UTC-1030. \n"
                        + "If not provided, system default timezone will be used.");

        // Mutex flags - these will always be the last parameters in help message.
        mutexParser.addArgument(CONFIG_FLAGS)
                .dest(CONFIG_FLAGS[0])
                .type(new ConfigFolderArgumentType())
                .metavar("PATH")
                .setDefault(DEFAULT_CONFIG_PATH)
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the config folder.");

        mutexParser.addArgument(REPO_FLAGS)
                .nargs("+")
                .dest(REPO_FLAGS[0])
                .metavar("LOCATION")
                .help("The GitHub URL or disk locations to clone repository.");

        mutexParser2.addArgument(LAST_MODIFIED_DATE_FLAGS)
                .dest(LAST_MODIFIED_DATE_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to keep track of the last modified date of each line of code.");

        mutexParser2.addArgument(SHALLOW_CLONING_FLAGS)
                .dest(SHALLOW_CLONING_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to make RepoSense employ Git's shallow cloning functionality, which can significantly "
                        + "reduce the time taken to clone large repositories. This flag should not be used for "
                        + "smaller repositories, where the .git file is smaller than 500 MB.");

        return parser;
    }

    /**
     * Parses the given string arguments to a {@code CliArguments} object.
     *
     * @throws HelpScreenException if given args contain the --help flag. Help message will be printed out
     * by the {@code ArgumentParser} hence this is to signal to the caller that the program is safe to exit.
     * @throws ParseException if the given string arguments fails to parse to a {@code CliArguments} object.
     */
    public static CliArguments parse(String[] args) throws HelpScreenException, ParseException {
        try {
            ArgumentParser parser = getArgumentParser();
            Namespace results = parser.parseArgs(args);
            Date sinceDate;
            Date untilDate;

            Path configFolderPath = results.get(CONFIG_FLAGS[0]);
            Path reportFolderPath = results.get(VIEW_FLAGS[0]);
            Path outputFolderPath = results.get(OUTPUT_FLAGS[0]);
            ZoneId zoneId = results.get(TIMEZONE_FLAGS[0]);
            Path assetsFolderPath = results.get(ASSETS_FLAGS[0]);
            Optional<Date> cliSinceDate = results.get(SINCE_FLAGS[0]);
            Optional<Date> cliUntilDate = results.get(UNTIL_FLAGS[0]);
            boolean isSinceDateProvided = cliSinceDate.isPresent();
            boolean isUntilDateProvided = cliUntilDate.isPresent();
            Optional<Integer> cliPeriod = results.get(PERIOD_FLAGS[0]);
            boolean isPeriodProvided = cliPeriod.isPresent();
            if (isSinceDateProvided && isUntilDateProvided && isPeriodProvided) {
                throw new ParseException(MESSAGE_HAVE_SINCE_DATE_UNTIL_DATE_AND_PERIOD);
            }

            Date currentDate = getCurrentDate(zoneId);

            if (isSinceDateProvided) {
                sinceDate = getZonedSinceDate(cliSinceDate.get(), zoneId);
            } else {
                sinceDate = isPeriodProvided
                        ? getDateMinusNDays(cliUntilDate, zoneId, cliPeriod.get())
                        : getDateMinusAMonth(cliUntilDate, zoneId);
            }

            if (isUntilDateProvided) {
                untilDate = getZonedUntilDate(cliUntilDate.get(), zoneId);
            } else {
                untilDate = (isSinceDateProvided && isPeriodProvided)
                        ? getDatePlusNDays(cliSinceDate, zoneId, cliPeriod.get())
                        : currentDate;
            }

            untilDate = untilDate.compareTo(currentDate) < 0
                    ? untilDate
                    : currentDate;
            List<String> locations = results.get(REPO_FLAGS[0]);
            List<FileType> formats = FileType.convertFormatStringsToFileTypes(results.get(FORMAT_FLAGS[0]));
            boolean isStandaloneConfigIgnored = results.get(IGNORE_FLAGS[0]);
            boolean shouldIncludeLastModifiedDate = results.get(LAST_MODIFIED_DATE_FLAGS[0]);
            boolean shouldPerformShallowCloning = results.get(SHALLOW_CLONING_FLAGS[0]);

            LogsManager.setLogFolderLocation(outputFolderPath);

            verifySinceDateIsValid(sinceDate);
            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            if (reportFolderPath != null && !reportFolderPath.equals(EMPTY_PATH)
                    && configFolderPath.equals(DEFAULT_CONFIG_PATH) && locations == null) {
                return new ViewCliArguments(reportFolderPath);
            }

            boolean isAutomaticallyLaunching = reportFolderPath != null;

            if (isAutomaticallyLaunching && !reportFolderPath.equals(EMPTY_PATH)) {
                logger.info(String.format("Ignoring argument '%s' for --view.", reportFolderPath.toString()));
            }

            if (locations != null) {
                return new LocationsCliArguments(locations, outputFolderPath, assetsFolderPath, sinceDate, untilDate,
                        isSinceDateProvided, isUntilDateProvided, formats, shouldIncludeLastModifiedDate,
                        shouldPerformShallowCloning, isAutomaticallyLaunching, isStandaloneConfigIgnored, zoneId);
            }

            if (configFolderPath.equals(EMPTY_PATH)) {
                logger.info(MESSAGE_USING_DEFAULT_CONFIG_PATH);
            }
            return new ConfigCliArguments(configFolderPath, outputFolderPath, assetsFolderPath, sinceDate, untilDate,
                    isSinceDateProvided, isUntilDateProvided, formats, shouldIncludeLastModifiedDate,
                    shouldPerformShallowCloning, isAutomaticallyLaunching, isStandaloneConfigIgnored, zoneId);
        } catch (HelpScreenException hse) {
            throw hse;
        } catch (ArgumentParserException ape) {
            throw new ParseException(getArgumentParser().formatUsage() + ape.getMessage() + "\n");
        }
    }

    /**
     * Returns a {@code Date} that is set to midnight for the given {@code zoneId}.
     */
    private static Date getZonedSinceDate(Date sinceDate, ZoneId zoneId) {
        if (sinceDate.equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE)) {
            return sinceDate;
        }

        int zoneRawOffset = getZoneRawOffset(zoneId);
        int systemRawOffset = getZoneRawOffset(ZoneId.systemDefault());

        Calendar cal = new Calendar
                .Builder()
                .setInstant(sinceDate.getTime())
                .build();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, systemRawOffset - zoneRawOffset);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is set to 23:59:59 for the given {@code zoneId}.
     */
    private static Date getZonedUntilDate(Date untilDate, ZoneId zoneId) {
        int zoneRawOffset = getZoneRawOffset(zoneId);
        int systemRawOffset = getZoneRawOffset(ZoneId.systemDefault());

        Calendar cal = new Calendar
                .Builder()
                .setInstant(untilDate.getTime())
                .build();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, systemRawOffset - zoneRawOffset);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is one month before {@code cliUntilDate} (if present) or one month before report
     * generation date otherwise. The time zone is adjusted to the given {@code zoneId}.
     */
    private static Date getDateMinusAMonth(Optional<Date> cliUntilDate, ZoneId zoneId) {
        Calendar cal = Calendar.getInstance();
        cliUntilDate.ifPresent(cal::setTime);
        cal.setTime(getZonedSinceDate(cal.getTime(), zoneId));
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is {@code numOfDays} before {@code cliUntilDate} (if present) or one month before
     * report generation date otherwise. The time zone is adjusted to the given {@code zoneId}.
     */
    private static Date getDateMinusNDays(Optional<Date> cliUntilDate, ZoneId zoneId, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cliUntilDate.ifPresent(cal::setTime);
        cal.setTime(getZonedSinceDate(cal.getTime(), zoneId));
        cal.add(Calendar.DATE, -numOfDays + 1);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is {@code numOfDays} after {@code cliSinceDate} (if present). The time zone is
     * adjusted to the given {@code zoneId}.
     */
    private static Date getDatePlusNDays(Optional<Date> cliSinceDate, ZoneId zoneId, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cliSinceDate.ifPresent(cal::setTime);
        cal.setTime(getZonedUntilDate(cal.getTime(), zoneId));
        cal.add(Calendar.DATE, numOfDays - 1);
        return cal.getTime();
    }

    /**
     * Returns current date with time set to 23:59:59. The time zone is adjusted to the given {@code zoneId}.
     */
    private static Date getCurrentDate(ZoneId zoneId) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getZonedUntilDate(cal.getTime(), zoneId));
        return cal.getTime();
    }

    /**
     * Verifies that {@code sinceDate} is earlier than {@code untilDate}.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than {@code untilDate}.
     */
    private static void verifyDatesRangeIsCorrect(Date sinceDate, Date untilDate)
            throws ParseException {
        if (sinceDate.getTime() > untilDate.getTime()) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }
    }

    /**
     * Verifies that {@code sinceDate} is no later than the date of report generation.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than date of report generation.
     */
    private static void verifySinceDateIsValid(Date sinceDate) throws ParseException {
        Date dateToday = new Date();
        if (sinceDate.getTime() > dateToday.getTime()) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE);
        }
    }

    /**
     * Get the raw offset in milliseconds for the {@code zoneId} timezone compared to UTC.
     */
    private static int getZoneRawOffset(ZoneId zoneId) {
        Instant now = Instant.now();
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(now);
        return zoneOffset.getTotalSeconds() * 1000;
    }
}
