package reposense.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.dataobject.Author;
import reposense.dataobject.AuthorIntervalContribution;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoContributionSummary;


public class ContributionSummaryGenerator {

    /**
     * Analyzes contribution of authors in a single repository.
     * @param suspiciousAuthors authors with bugs not caught in the analyzer.
     */
    public static RepoContributionSummary analyzeContribution(
            RepoConfiguration config,
            List<CommitInfo> commitInfos,
            HashMap<Author, Integer> authorContributionMap,
            HashSet<Author> suspiciousAuthors) {
        Date startDate = config.getSinceDate() == null ? getStartDate(commitInfos) : config.getSinceDate();
        RepoContributionSummary summary = new RepoContributionSummary();
        summary.setAuthorWeeklyIntervalContributions(
                getAuthorIntervalContributions(config, commitInfos, startDate, 7, suspiciousAuthors));
        summary.setAuthorDailyIntervalContributions(
                getAuthorIntervalContributions(config, commitInfos, startDate, 1, suspiciousAuthors));
        summary.setAuthorFinalContributionMap(authorContributionMap);
        summary.setAuthorContributionVariance(
                calcAuthorContributionVariance(summary.getAuthorDailyIntervalContributions()));
        summary.setAuthorDisplayNameMap(config.getAuthorDisplayNameMap());
        return summary;
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
            RepoConfiguration config, List<CommitInfo> commitInfos,
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

            for (CommitInfo commit : commitInfos) {
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
            Map<Author, List<AuthorIntervalContribution>> map, Date fromDate, Date toDate) {
        for (List<AuthorIntervalContribution> dateToInterval : map.values()) {
            //dials back one minute so that github api can include the commit on the time itself
            dateToInterval.add(new AuthorIntervalContribution(0, 0, fromDate, toDate));
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

    private static Date getStartDate(List<CommitInfo> commitInfos) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2050);
        Date min = cal.getTime();
        if (!commitInfos.isEmpty()) {
            min = commitInfos.get(0).getTime();
        }
        return min;
    }
}
