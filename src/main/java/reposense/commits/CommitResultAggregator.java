package reposense.commits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.commits.model.AuthorIntervalContribution;
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
        HashSet<Author> suspiciousAuthors = new HashSet<>();

        Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions =
                getAuthorIntervalContributions(config, commitResults, startDate, 1, suspiciousAuthors);

        Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions =
                getAuthorIntervalContributions(config, commitResults, startDate, 7, suspiciousAuthors);

        Map<Author, Float> authorContributionVariance =
                calcAuthorContributionVariance(authorDailyIntervalContributions);

        return new CommitContributionSummary(
                config.getAuthorDisplayNameMap(),
                authorDailyIntervalContributions,
                authorWeeklyIntervalContributions,
                authorContributionVariance);
    }

    private static Map<Author, Float> calcAuthorContributionVariance(
            Map<Author, List<AuthorIntervalContribution>> intervalContributionMaps) {
        Map<Author, Float> result = new HashMap<>();
        for (Author author : intervalContributionMaps.keySet()) {
            List<AuthorIntervalContribution> contributions = intervalContributionMaps.get(author);
            result.put(author, getContributionVariance(contributions));
        }
        return result;
    }

    private static float getContributionVariance(List<AuthorIntervalContribution> contributions) {
        if (contributions.size() == 0) {
            return 0;
        }
        //get mean
        float total = 0;
        for (AuthorIntervalContribution contribution : contributions) {
            total += contribution.getTotalContribution();
        }
        float mean = total / contributions.size();
        float variance = 0;
        for (AuthorIntervalContribution contribution : contributions) {
            variance += Math.pow((mean - contribution.getTotalContribution()), 2);
        }
        return variance / contributions.size();
    }

    private static Map<Author, List<AuthorIntervalContribution>> getAuthorIntervalContributions(
            RepoConfiguration config, List<CommitResult> commitInfos,
            Date startDate, int intervalLength, Set<Author> suspiciousAuthors) {
        //init
        Map<Author, List<AuthorIntervalContribution>> result = new HashMap<>();
        for (Author author : config.getAuthorDisplayNameMap().keySet()) {
            result.put(author, new ArrayList<>());
        }
        if (!commitInfos.isEmpty()) {
            Date currentDate = getStartOfDate(startDate);
            Date nextDate = getNextCutoffDate(currentDate, intervalLength);
            initIntervalContributionForNewDate(result, currentDate, nextDate);

            for (CommitResult commit : commitInfos) {
                while (nextDate.before(commit.getTime())) {
                    currentDate = new Date(nextDate.getTime());
                    nextDate = getNextCutoffDate(nextDate, intervalLength);
                    initIntervalContributionForNewDate(result, currentDate, nextDate);
                }
                List<AuthorIntervalContribution> tempList = result.get(commit.getAuthor());
                if (tempList != null) {
                    tempList.get(tempList.size() - 1).updateForCommit(commit);
                }
            }
        }
        return result;
    }

    private static void initIntervalContributionForNewDate(
            Map<Author, List<AuthorIntervalContribution>> map, Date sinceDate, Date untilDate) {
        for (List<AuthorIntervalContribution> dateToInterval : map.values()) {
            //dials back one minute so that github api can include the commit on the time itself
            dateToInterval.add(new AuthorIntervalContribution(0, 0, sinceDate, untilDate));
        }
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

    private static Date getNextCutoffDate(Date current, int intervalLength) {
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.DATE, intervalLength);
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
