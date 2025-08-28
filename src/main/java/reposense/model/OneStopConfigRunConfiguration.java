package reposense.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import reposense.model.reportconfig.ReportAuthorDetails;
import reposense.model.reportconfig.ReportBranchData;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.model.reportconfig.ReportRepoConfiguration;
import reposense.parser.exceptions.InvalidDatesException;
import reposense.parser.exceptions.InvalidLocationException;
import reposense.system.LogsManager;

/**
 * Represents RepoSense run configured by the one-stop configuration file.
 */
public class OneStopConfigRunConfiguration implements RunConfiguration {
    private static final Logger logger = LogsManager.getLogger(OneStopConfigRunConfiguration.class);
    private static final String MESSAGE_CLI_CONFIG_DATE_CONFLICT =
            "You specified in CLI a date range of --since to --until, "
            + "but your report config specifies a date range that extends outside --SINCE or --UNTIL. "
            + "Either modify your CLI flags or your report config date range.";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" should not be later than \"Until Date\"";

    private final CliArguments cliArguments;

    public OneStopConfigRunConfiguration(CliArguments cliArguments) {
        this.cliArguments = cliArguments;
    }

    /**
     * Constructs a list of {@link RepoConfiguration}.
     *
     * @throws InvalidLocationException if the location specified in the config file is invalid.
     */
    @Override
    public List<RepoConfiguration> getRepoConfigurations() throws InvalidLocationException, InvalidDatesException {
        ReportConfiguration reportConfiguration = this.cliArguments.getReportConfiguration();
        List<RepoConfiguration> repoConfigs = new ArrayList<>();
        List<AuthorConfiguration> authorConfigs = new ArrayList<>();
        List<GroupConfiguration> groupConfigs = new ArrayList<>();

        for (ReportRepoConfiguration rrc : reportConfiguration.getReportRepoConfigurations()) {
            logger.info("Parsing " + rrc.getRepo() + "...");
            RepoLocation repoLocation = new RepoLocation(rrc.getRepo());
            groupConfigs.add(rrc.getGroupConfiguration(repoLocation));

            for (ReportBranchData rbd : rrc.getBranches()) {
                logger.info("Parsing " + rbd.getBranch() + "...");

                RepoConfiguration.Builder builder = new RepoConfiguration.Builder()
                        .location(repoLocation)
                        .branch(rbd.getBranch())
                        .ignoreGlobList(rbd.getIgnoreGlobList())
                        .ignoredAuthorsList(rbd.getIgnoreAuthorList())
                        .fileSizeLimit(rbd.getFileSizeLimit())
                        // Needs to be removed this when we deprecate the standalone config
                        .isStandaloneConfigIgnored(true);

                setSinceUntilDate(builder, rbd);

                AuthorConfiguration authorConfiguration = new AuthorConfiguration(repoLocation, rbd.getBranch());
                for (ReportAuthorDetails rad : rbd.getReportAuthorDetails()) {
                    logger.info("Parsing " + rad.getAuthorGitHostId() + "...");

                    Author author = new Author(rad.getAuthorGitHostId());
                    author.setEmails(rad.getAuthorEmails());
                    author.setDisplayName(rad.getAuthorDisplayName());
                    author.setAuthorAliases(rad.getAuthorGitAuthorNames());

                    authorConfiguration.addAuthor(author);
                }
                authorConfigs.add(authorConfiguration);
                repoConfigs.add(builder.build());
            }
        }

        logger.info("Merging author, group and repo configurations...");
        RepoConfiguration.merge(repoConfigs, authorConfigs);
        RepoConfiguration.setGroupConfigsToRepos(repoConfigs, groupConfigs);

        logger.info("Finished parsing OneStopConfigRunConfiguration!");
        return repoConfigs;
    }

    private void setSinceUntilDate(RepoConfiguration.Builder builder, ReportBranchData rbd)
            throws InvalidDatesException {
        LocalDateTime configSinceDate = rbd.getSinceDate();
        LocalDateTime configUntilDate = rbd.getUntilDate();
        LocalDateTime chosenSinceDate = getValidDate(configSinceDate, true);
        LocalDateTime chosenUntilDate = getValidDate(configUntilDate, false);

        assert chosenSinceDate != null && chosenUntilDate != null;

        if (chosenSinceDate.isAfter(chosenUntilDate)) {
            throw new InvalidDatesException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }

        builder.setSinceDateBasedOnConfig(true, chosenSinceDate);
        builder.setUntilDateBasedOnConfig(true, chosenUntilDate);
    }

    /**
     * Determines the effective date to use based on the report config and CLI arguments.
     * If the report config date is not provided, the corresponding CLI date is used.
     *
     * @param configDate the date specified in the report config (might be {@code null}).
     * @return the valid date to be used (either from config or CLI).
     * @throws InvalidDatesException if the config date falls outside the CLI date range.
     */
    private LocalDateTime getValidDate(LocalDateTime configDate, boolean isSinceDate)
            throws InvalidDatesException {
        boolean isCliSinceProvided = cliArguments.isSinceDateProvided();
        boolean isCliUntilProvided = cliArguments.isUntilDateProvided();
        LocalDateTime cliSinceDate = cliArguments.getSinceDate();
        LocalDateTime cliUntilDate = cliArguments.getUntilDate();

        if (configDate == null) {
            // if config's date is not specified, use cli value
            return isSinceDate ? cliArguments.getSinceDate() : cliArguments.getUntilDate();
        } else if (isCliSinceProvided && isCliUntilProvided) {
            // if both since and until date are provided in cli, then we need to check whether config date lays between
            // the time period
            if (configDate.isBefore(cliSinceDate) || configDate.isAfter(cliUntilDate)) {
                throw new InvalidDatesException(MESSAGE_CLI_CONFIG_DATE_CONFLICT);
            }
        }
        return configDate;
    }
}
