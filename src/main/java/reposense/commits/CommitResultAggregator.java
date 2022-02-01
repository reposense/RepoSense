package reposense.commits;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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

    /**
     * Returns the {@code CommitContributionSummary} generated from aggregating the {@code commitResults}.
     */
    public static CommitContributionSummary aggregateCommitResults(
            RepoConfiguration config, List<CommitResult> commitResults) {
        LocalDateTime startDate;
        startDate = (config.getSinceDate().equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE))
                ? getStartOfDate(getStartDate(commitResults))
                : config.getSinceDate();
        ReportGenerator.setEarliestSinceDate(startDate);

        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap =
                getAuthorDailyContributionsMap(config.getAuthorDisplayNameMap().keySet(), commitResults);

        LocalDateTime lastDate = commitResults.size() == 0
                ? null
                : getStartOfDate(commitResults.get(commitResults.size() - 1).getTime());

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
        long totalDays = (ZonedDateTime.of(lastDate, ZoneId.of(zoneId)).toInstant().toEpochMilli()
                - ZonedDateTime.of(startDate, ZoneId.of(zoneId)).toInstant().toEpochMilli()) / DAYS_IN_MS + 1;

        for (AuthorDailyContribution contribution : contributions) {
            total += contribution.getTotalContribution();
        }
        float mean = total / totalDays;

        float variance = 0;
        long currentDate = ZonedDateTime.of(startDate, ZoneId.of(zoneId)).toInstant().toEpochMilli();

        int contributionIndex = 0;
        for (int i = 0; i < totalDays; i += 1) {
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
            Set<Author> authorSet, List<CommitResult> commitResults) {
        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap = new HashMap<>();
        authorSet.forEach(author -> authorDailyContributionsMap.put(author, new ArrayList<>()));

        LocalDateTime commitStartDate;
        for (CommitResult commitResult : commitResults) {
            commitStartDate = getStartOfDate(commitResult.getTime());
            Author commitAuthor = commitResult.getAuthor();

            List<AuthorDailyContribution> authorDailyContributions = authorDailyContributionsMap.get(commitAuthor);

            if (authorDailyContributions.isEmpty()
                    || !getStartOfDate(authorDailyContributions.get(authorDailyContributions.size() - 1).getDate())
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
    private static LocalDateTime getStartOfDate(LocalDateTime current) {
        if (current.equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE)) {
            return current;
        }

        return current.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    private static LocalDateTime getStartDate(List<CommitResult> commitInfos) {
        return (commitInfos.isEmpty())
                ? LocalDateTime.ofInstant(new Date(Long.MIN_VALUE).toInstant(), ZoneId.of("Z"))
                : commitInfos.get(0).getTime();
    }
}
