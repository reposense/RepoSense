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
import reposense.report.ReportGenerator;
import reposense.util.TimeUtil;

/**
 * Uses the commit analysis results to generate the summary information of a repository.
 */
public class CommitResultAggregator {
    private static final int DAYS_IN_MS = 24 * 60 * 60 * 1000;

    /**
     * Returns the {@link CommitContributionSummary} generated from aggregating the {@code commitResults}.
     * Uses {@code config} to obtain details like author name, since date and timezone.
     */
    public static CommitContributionSummary aggregateCommitResults(
            RepoConfiguration config, List<CommitResult> commitResults) {
        LocalDateTime startDate;
        ZoneId zoneId = ZoneId.of(config.getZoneId());
        startDate = (TimeUtil.isEqualToArbitraryFirstDateConverted(config.getSinceDate(), zoneId))
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
     * Calculates the contribution variance of all authors across contributions made within a date range.
     * This date range is between {@code startDate} and {@code lastDate}, which are determined based on {@code zoneId}.
     * The authors and their respective contributions are stored in {@code intervalContributionMaps}.
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

    /**
     * Calculates the contribution variance for each author across the author's {@code contributions} made
     * between {@code startDate} and {@code lastDate}.
     * The {@code startDate} and {@code lastDate} are determined based on {@code zoneId}.
     */
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

    /**
     * Returns a mapping of each {@link Author} to their respective commit contributions.
     * For each author, commit contributions are consolidated into {@link AuthorDailyContribution}s based on the date
     * of each {@link CommitResult}.
     *
     * @param authorSet The set of authors.
     * @param commitResults The consolidated list of {@link CommitResult}s.
     * @param zoneId The timezone for all {@link CommitResult}s' dates.
     * @return a {@link Map} of each author to a list of {@link AuthorDailyContribution} across all dates in which
     * the author made commits.
     */
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
     * Gets the starting point of the {@code current} date.
     *
     * @return the {@code current} date if it is equal to the {@code ARBITRARY_FIRST_COMMIT_DATE} adjusted to the
     * timezone given by {@code zoneId}. Otherwise, return a {@link LocalDateTime} adjusted to have a time of 00:00:00.
     */
    private static LocalDateTime getStartOfDate(LocalDateTime current, ZoneId zoneId) {
        if (TimeUtil.isEqualToArbitraryFirstDateConverted(current, zoneId)) {
            return current;
        }

        return current.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * Gets the earliest commit date from {@code commitInfos}.
     *
     * @return First commit date if there is at least one {@link CommitResult}. Otherwise, return
     * the {@code ARBITRARY_FIRST_COMMIT_DATE} converted to the timezone given by {@code zoneId}.
     */
    private static LocalDateTime getStartDate(List<CommitResult> commitInfos, ZoneId zoneId) {
        return (commitInfos.isEmpty())
                ? TimeUtil.getArbitraryFirstCommitDateConverted(zoneId)
                : commitInfos.get(0).getTime();
    }
}
