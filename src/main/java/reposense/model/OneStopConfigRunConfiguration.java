package reposense.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.bytebuddy.asm.Advice;
import reposense.model.reportconfig.ReportAuthorDetails;
import reposense.model.reportconfig.ReportBranchData;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.model.reportconfig.ReportRepoConfiguration;
import reposense.parser.LocalDateTimeParser;
import reposense.parser.exceptions.InvalidDatesException;
import reposense.parser.exceptions.InvalidLocationException;
import reposense.system.LogsManager;

/**
 * Represents RepoSense run configured by the one-stop configuration file.
 */
public class OneStopConfigRunConfiguration implements RunConfiguration {
    private static final Logger logger = LogsManager.getLogger(OneStopConfigRunConfiguration.class);
    private static final String MESSAGE_CLI_CONFIG_DATE_CONFLICT =
            "you specified in CLI a date range of --SINCE to --UNTIL, "
            + "but your CSV config specifies a date range that extends outside --SINCE or --UNTIL. "
            + "Either modify your CLI flags or your CSV date range.";
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

                LocalDateTime configSinceDate = rbd.getSinceDate();
                LocalDateTime configUntilDate = rbd.getUntilDate();
                LocalDateTime chosenSinceDate = getValidDate(configSinceDate, true);
                LocalDateTime chosenUntilDate = getValidDate(configUntilDate, false);

                builder.setSinceDateBasedOnConfig(true, chosenSinceDate);
                builder.setUntilDateBasedOnConfig(true, chosenUntilDate);

                AuthorConfiguration authorConfiguration = new AuthorConfiguration(repoLocation, rbd.getBranch());
                for (ReportAuthorDetails rad : rbd.getReportAuthorDetails()) {
                    logger.info("Parsing " + rad.getAuthorGitHostId() + "...");

                    Author author = new Author(rad.getAuthorGitHostId());
                    author.setEmails(rad.getAuthorEmails());
                    author.setDisplayName(rad.getAuthorDisplayName());
                    author.setAuthorAliases(List.of(rad.getAuthorGitAuthorName()));

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

    private LocalDateTime getValidDate(LocalDateTime configDate, boolean isSinceDate)
            throws InvalidDatesException {
        boolean isCliSinceProvided = cliArguments.isSinceDateProvided();
        boolean isCliUntilProvided = cliArguments.isUntilDateProvided();
        LocalDateTime cliSinceDate = cliArguments.getSinceDate();
        LocalDateTime cliBeforeDate = cliArguments.getSinceDate();

        if (configDate == null) {
            // if config since date is not specified, use cli value
            System.out.println("configdate is null, use cli ones");
            return isSinceDate ? cliArguments.getSinceDate() : cliArguments.getUntilDate();
        } else if (isCliSinceProvided && isCliUntilProvided) {
            // if both since and until date are provided in cli, then we need to check whether config date lays between
            // the time period
            if (!configDate.isAfter(cliSinceDate) || !configDate.isBefore(cliBeforeDate)) {
                throw new InvalidDatesException(MESSAGE_CLI_CONFIG_DATE_CONFLICT);
            }
        }
        return configDate;
    }
}
