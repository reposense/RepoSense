package report;

import dataObject.*;

import java.util.*;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class ContributionSummaryGenerator {

    public static Map<String, RepoContributionSummary> analyzeContribution(List<RepoInfo> repos, List<RepoConfiguration> configs){
        System.out.println("Generating summary report...");
        Map<String, RepoContributionSummary> result = new HashMap<>();
        HashSet<Author> suspiciousAuthors = new HashSet<>(); //authors with bugs that I didnt catch
        for (RepoInfo repo:repos){
            RepoContributionSummary summary = new RepoContributionSummary(repo);
            summary.setFromDate(configs.get(0).getFromDate());
            summary.setToDate(configs.get(0).getToDate());
            summary.setAuthorWeeklyIntervalContributions(getAuthorIntervalContributions(repo.getCommits(),7,suspiciousAuthors));
            summary.setAuthorDailyIntervalContributions(getAuthorIntervalContributions(repo.getCommits(),1,suspiciousAuthors));
            summary.setAuthorFinalContributionMap(repo.getCommits().get(repo.getCommits().size()-1).getAuthorContributionMap());
            summary.setAuthorConsistency(getAuthorConsistency(summary.getAuthorDailyIntervalContributions()));
            summary.setAuthorDisplayNameMap(repo.getAuthorDisplayNameMap());
            result.put(repo.getDirectoryName(),summary);
        }
        if (!suspiciousAuthors.isEmpty()) {
            System.out.println("PLEASE NOTE, BELOW IS THE LIST OF SUSPICIOUS AUTHORS:");
            for (Author author : suspiciousAuthors) {
                System.out.println(author);
            }
        }
        System.out.println("done!Congrats!");
        return result;
    }

    private static Map<Author, Float> getAuthorConsistency(Map<Author, List<AuthorIntervalContribution>> intervalContributionMaps) {
        Map<Author, Float> result = new HashMap<>();
        for (Author author : intervalContributionMaps.keySet()){
            List<AuthorIntervalContribution> contributions = intervalContributionMaps.get(author);
            result.put(author,(1/getContributionVariance(contributions)));
        }
        return result;
    }

    private static float getContributionVariance(List<AuthorIntervalContribution> contributions){
        //get mean
        float total = 0;
        for (AuthorIntervalContribution contribution : contributions) {
            total += contribution.getTotalContribution();
        }
        float mean = total / contributions.size();
        float variance = 0;
        for (AuthorIntervalContribution contribution : contributions) {
            variance += Math.pow((mean-contribution.getTotalContribution()),2);
        }
        return variance / contributions.size();
    }

    private static Map<Author, List<AuthorIntervalContribution>> getAuthorIntervalContributions(List<CommitInfo> commits, int intervalLength, Set<Author> suspiciousAuthors){
        //init
        Map<Author, List<AuthorIntervalContribution>> result = new HashMap<>();
        for (Author author: commits.get(commits.size()-1).getAuthorContributionMap().keySet()){
            result.put(author,new ArrayList<>());
        }
        Date currentDate = getStartOfDate(commits.get(0).getTime());
        Date nextDate = getNextCutoffDate(currentDate, intervalLength);
        initIntervalContributionForNewDate(result, currentDate, nextDate);

        for (CommitInfo commit: commits){
            while (nextDate.before(commit.getTime())){
                currentDate = new Date(nextDate.getTime());
                nextDate = getNextCutoffDate(nextDate, intervalLength);
                initIntervalContributionForNewDate(result,currentDate, nextDate);
            }
            List<AuthorIntervalContribution> tempList = result.get(commit.getAuthor());
            if (tempList==null){
                //there's no matching author for this commits, meaning there's probably something wrong with CSV input?
                suspiciousAuthors.add(commit.getAuthor());
            }else {
                tempList.get(tempList.size() - 1).updateForCommit(commit);
            }
        }
        return result;
    }

    private static void initIntervalContributionForNewDate(Map<Author, List<AuthorIntervalContribution>> map, Date fromDate, Date toDate){
        for (List<AuthorIntervalContribution> dateToInvertal : map.values()){
            //dials back one minute so that github api can include the commit on the time itself
            dateToInvertal.add(new AuthorIntervalContribution(0,0, fromDate, toDate));
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

    private static Date getNextCutoffDate(Date current, int intervalLength){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.DATE,intervalLength);
        return c.getTime();
    }
}
