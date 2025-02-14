package reposense.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import reposense.model.reportconfig.ReportAuthorDetails;
import reposense.model.reportconfig.ReportBranchData;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.model.reportconfig.ReportRepoConfiguration;
import reposense.parser.exceptions.InvalidLocationException;
import reposense.system.LogsManager;

/**
 * Represents RepoSense run configured by the one-stop configuration file.
 */
public class OneStopConfigRunConfiguration implements RunConfiguration {
    private static final Logger logger = LogsManager.getLogger(OneStopConfigRunConfiguration.class);

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
    public List<RepoConfiguration> getRepoConfigurations() throws InvalidLocationException {
        ReportConfiguration reportConfiguration = this.cliArguments.getReportConfiguration();
        List<RepoConfiguration> repoConfigs = new ArrayList<>();
        List<AuthorConfiguration> authorConfigs = new ArrayList<>();
        List<GroupConfiguration> groupConfigs = new ArrayList<>();

        for (ReportRepoConfiguration rrc : reportConfiguration.getReportRepoConfigurations()) {
            logger.info("Parsing " + rrc.getRepo() + "...");
            RepoLocation repoLocation = new RepoLocation(rrc.getRepo());
            groupConfigs.add(rrc.getGroupConfiguration(repoLocation));

            // iterate for each branch and for each author
            for (ReportBranchData rbd : rrc.getBranches()) {
                logger.info("Parsing " + rbd.getBranch() + "...");
                // create the repoconfiguration object
                // we will need to add the group configuration details to the repo configuration builder
                RepoConfiguration.Builder builder = new RepoConfiguration.Builder()
                        .location(repoLocation);

                // set the relevant branch details for the repo
                builder = builder.branch(rbd.getBranch())
                        .ignoreGlobList(rbd.getIgnoreGlobList())
                        .ignoredAuthorsList(rbd.getIgnoreAuthorList())
                        .fileSizeLimit(rbd.getFileSizeLimit())
                        .isStandaloneConfigIgnored(true); // remove this when we deprecated the standalone config


                AuthorConfiguration authorConfiguration = new AuthorConfiguration(repoLocation, rbd.getBranch());
                for (ReportAuthorDetails rad : rbd.getReportAuthorDetails()) {
                    logger.info("Parsing " + rad.getAuthorGitHostId() + "...");


                    // prepare the author details
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
}
