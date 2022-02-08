package reposense.commits;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.commits.model.AuthorDailyContribution;
import reposense.commits.model.CommitContributionSummary;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.parser.SinceDateArgumentType;
import reposense.report.ReportGenerator;

/**
 * Uses the commit analysis results to generate the summary information of a repository.
 */
public class CommitResultAggregator {
    private static final int DAYS_IN_MS = 24 * 60 * 60 * 1000;
    private static final ZonedDateTime ARBITRARY_FIRST_COMMIT_DATE_UTC =
            ZonedDateTime.of(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE, ZoneId.of("Z"));

    /**
     * Returns the {@code CommitContributionSummary} generated from aggregating the {@code commitResults}.
     */
    public static CommitContributionSummary aggregateCommitResults(
            RepoConfiguration config, List<CommitResult> commitResults) {
        LocalDateTime startDate;
        ZoneId zoneId = ZoneId.of(config.getZoneId());
        startDate = (config.getSinceDate().equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE))
                ? getStartOfDate(getStartDate(commitResults, zoneId), zoneId)
                : config.getSinceDate();
        ReportGenerator.setEarliestSinceDate(startDate);

        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap =
                getAuthorDailyContributionsMap(config.getAuthorDisplayNameMap().keySet(),
                        commitResults, ZoneId.of(config.getZoneId()));

        LocalDateTime lastDate = commitResults.size() == 0
                ? null
                : getStartOfDate(commitResults.get(commitResults.size() - 1).getTime(), zoneId);

        Map<Author, Float> authorContributionVariance =
                calcAuthorContributionVariance(authorDailyContributionsMap, startDate, lastDate, config.getZoneId());

        return new CommitContributionSummary(
                config.getAuthorDisplayNameMap(),
                authorDailyContributionsMap,
                authorContributionVariance);
    }

    /**
     * Calculates the contribution variance of all authors.
     */
    private static Map<Author, Float> calcAuthorContributionVariance(
            Map<Author, List<AuthorDailyContribution>> intervalContributionMaps, LocalDateTime startDate,
            LocalDateTime lastDate, String zoneId) {
        Map<Author, Float> result = new HashMap<>();
        for (Author author : intervalContributionMaps.keySet()) {
            List<AuthorDailyContribution> contributions = intervalContributionMaps.get(author);
            result.put(author, getContributionVariance(contributions, startDate, lastDate, zoneId));
        }
        return result;
    }

    private static float getContributionVariance(List<AuthorDailyContribution> contributions,
            LocalDateTime startDate, LocalDateTime lastDate, String zoneId) {
        if (contributions.size() == 0) {
            return 0;
        }
        //get mean
        float total = 0;
        long startDateInMs = ZonedDateTime.of(startDate, ZoneId.of(zoneId)).toInstant().toEpochMilli();
        long lastDateInMs = ZonedDateTime.of(lastDate, ZoneId.of(zoneId)).toInstant().toEpochMilli();
        long totalDays = (lastDateInMs - startDateInMs) / DAYS_IN_MS + 1;

        for (AuthorDailyContribution contribution : contributions) {
            total += contribution.getTotalContribution();
        }
        float mean = total / totalDays;

        float variance = 0;
        long currentDate = ZonedDateTime.of(startDate, ZoneId.of(zoneId)).toInstant().toEpochMilli();

        int contributionIndex = 0;
        for (int i = 0; i < totalDays; i += 1) {

            // Check whether the contributionIndex is valid and the date being looked at has any contributions.
            if (contributionIndex < contributions.size() && currentDate
                    == ZonedDateTime.of(contributions.get(contributionIndex).getDate(), ZoneId.of(zoneId))
                    .toInstant().toEpochMilli()) {
                variance += Math.pow((mean - contributions.get(contributionIndex).getTotalContribution()), 2);
                contributionIndex += 1;
            } else {
                variance += Math.pow(mean, 2);
            }
            currentDate += DAYS_IN_MS;
        }
        return variance / totalDays;
    }

    private static Map<Author, List<AuthorDailyContribution>> getAuthorDailyContributionsMap(
            Set<Author> authorSet, List<CommitResult> commitResults, ZoneId zoneId) {
        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap = new HashMap<>();
        authorSet.forEach(author -> authorDailyContributionsMap.put(author, new ArrayList<>()));

        LocalDateTime commitStartDate;
        for (CommitResult commitResult : commitResults) {
            commitStartDate = getStartOfDate(commitResult.getTime(), zoneId);
            Author commitAuthor = commitResult.getAuthor();

            List<AuthorDailyContribution> authorDailyContributions = authorDailyContributionsMap.get(commitAuthor);

            // Check whether there are no contribution dates present or if the current commit date is not yet in
            // the authorDailyContributions list.
            if (authorDailyContributions.isEmpty() || !getStartOfDate(authorDailyContributions
                    .get(authorDailyContributions.size() - 1).getDate(), zoneId)
                            .equals(commitStartDate)) {
                addDailyContributionForNewDate(authorDailyContributions, commitStartDate);
            }

            authorDailyContributions.get(authorDailyContributions.size() - 1).addCommitContribution(commitResult);
        }

        return authorDailyContributionsMap;
    }

    private static void addDailyContributionForNewDate(
            List<AuthorDailyContribution> authorDailyContributions, LocalDateTime date) {
        authorDailyContributions.add(new AuthorDailyContribution(date));
    }

    /**
     * Get the starting point of the {@code current} date.
     */
    private static LocalDateTime getStartOfDate(LocalDateTime current, ZoneId zoneId) {
        if (current.equals(ARBITRARY_FIRST_COMMIT_DATE_UTC.withZoneSameInstant(zoneId).toLocalDateTime())) {
            return current;
        }

        return current.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Get the earliest commit date from {@code commitInfos}.
     * @return First commit date if there is at least one {@code CommitResult}. Otherwise, return
     * the {@code ARBITRARY_FIRST_COMMIT_DATE} converted to the timezone given by {@code zoneId}.
     */
    private static LocalDateTime getStartDate(List<CommitResult> commitInfos, ZoneId zoneId) {
        return (commitInfos.isEmpty())
                ? ARBITRARY_FIRST_COMMIT_DATE_UTC.withZoneSameInstant(zoneId).toLocalDateTime()
                : commitInfos.get(0).getTime();
    }
}
