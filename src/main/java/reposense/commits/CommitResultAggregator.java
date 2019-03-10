package reposense.commits;

import java.util.ArrayList;
import java.util.Calendar;
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

/**
 * Uses the commit analysis results to generate the summary information of a repository.
 */
public class CommitResultAggregator {

    /**
     * Returns the {@code CommitContributionSummary} generated from aggregating the {@code commitResults}.
     */
    public static CommitContributionSummary aggregateCommitResults(
            RepoConfiguration config, List<CommitResult> commitResults) {
        Date startDate = config.getSinceDate() == null ? getStartDate(commitResults) : config.getSinceDate();

        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap =
                getAuthorDailyContributionsMap(config.getAuthorDisplayNameMap().keySet(), commitResults, startDate);

        Map<Author, Float> authorContributionVariance =
                calcAuthorContributionVariance(authorDailyContributionsMap);

        return new CommitContributionSummary(
                config.getAuthorDisplayNameMap(),
                authorDailyContributionsMap,
                authorContributionVariance);
    }

    private static Map<Author, Float> calcAuthorContributionVariance(
            Map<Author, List<AuthorDailyContribution>> intervalContributionMaps) {
        Map<Author, Float> result = new HashMap<>();
        for (Author author : intervalContributionMaps.keySet()) {
            List<AuthorDailyContribution> contributions = intervalContributionMaps.get(author);
            result.put(author, getContributionVariance(contributions));
        }
        return result;
    }

    private static float getContributionVariance(List<AuthorDailyContribution> contributions) {
        if (contributions.size() == 0) {
            return 0;
        }
        //get mean
        float total = 0;
        for (AuthorDailyContribution contribution : contributions) {
            total += contribution.getTotalContribution();
        }
        float mean = total / contributions.size();
        float variance = 0;
        for (AuthorDailyContribution contribution : contributions) {
            variance += Math.pow((mean - contribution.getTotalContribution()), 2);
        }
        return variance / contributions.size();
    }

    private static Map<Author, List<AuthorDailyContribution>> getAuthorDailyContributionsMap(
            Set<Author> authorSet, List<CommitResult> commitResults, Date startDate) {
        Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap = new HashMap<>();
        authorSet.forEach(author -> authorDailyContributionsMap.put(author, new ArrayList<>()));

        Date previousDate = null;
        for (CommitResult commitResult : commitResults) {
            Date commitDateByMidnight = getCommitDateAtMidnight(commitResult.getTime());

            if (!commitDateByMidnight.equals(previousDate)) {
                previousDate = new Date(commitDateByMidnight.getTime());
                addDailyContributionForNewDate(authorDailyContributionsMap, commitDateByMidnight);
            }

            List<AuthorDailyContribution> authorContributions =
                    authorDailyContributionsMap.get(commitResult.getAuthor());
            authorContributions.get(authorContributions.size() - 1).addCommitContribution(commitResult);
        }

        return authorDailyContributionsMap;
    }

    private static void addDailyContributionForNewDate(
            Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap, Date date) {
        authorDailyContributionsMap.values().forEach(authorDailyContributions ->
                authorDailyContributions.add(new AuthorDailyContribution(date)));
    }

    private static Date getStartOfDate(Date current) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(current);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static Date getCommitDateAtMidnight(Date commitDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(commitDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    private static Date getStartDate(List<CommitResult> commitInfos) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2050);
        Date min = cal.getTime();
        if (!commitInfos.isEmpty()) {
            min = commitInfos.get(0).getTime();
        }
        return min;
    }
}
