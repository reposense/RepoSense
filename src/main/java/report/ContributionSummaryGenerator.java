package report;

import dataObject.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class ContributionSummaryGenerator {

    public static Map<String, RepoContributionSummary> analyzeContribution(List<RepoInfo> repos){
        System.out.println("Generating summary report...");
        Map<String, RepoContributionSummary> result = new HashMap<>();
        for (RepoInfo repo:repos){
            RepoContributionSummary summary = new RepoContributionSummary(repo);
            summary.setAuthorIntervalContributions(getAuthorIntervalContributions(repo.getCommits()));
            summary.setAuthorFinalContributionMap(repo.getCommits().get(repo.getCommits().size()-1).getAuthorContributionMap());
            summary.setAuthorRushiness(getAuthorRushiness(summary.getAuthorIntervalContributions()));
            result.put(repo.getDirectoryName(),summary);
        }
        System.out.println("done");
        return result;
    }

    private static Map<Author, Float> getAuthorRushiness(Map<Author, List<AuthorIntervalContribution>> intervalContributionMaps) {
        Map<Author, Float> result = new HashMap<>();
        for (Author author : intervalContributionMaps.keySet()){
            float totalRush = 0;
            List<AuthorIntervalContribution> contributions = intervalContributionMaps.get(author);
            for (int i = 1;i<contributions.size();i++){
                int previous = contributions.get(i-1).getTotalContribution();
                int current = contributions.get(i).getTotalContribution();
                if (current> previous){
                    if (previous==0){
                        totalRush += 1.0;
                    }else {
                        totalRush += (current - previous) * 1.0 / previous;
                    }
                }
            }
            result.put(author,totalRush/contributions.size());
        }
        return result;
    }

    private static Map<Author, List<AuthorIntervalContribution>> getAuthorIntervalContributions(List<CommitInfo> commits){
        //init
        long durationDays = getDurationInDays(commits);
        Map<Author, List<AuthorIntervalContribution>> result = new HashMap<>();
        for (Author author: commits.get(commits.size()-1).getAuthorContributionMap().keySet()){
            result.put(author,new ArrayList<>());
        }
        Date currentDate = commits.get(0).getTime();
        Date nextDate = getNextCutoffDate(currentDate, durationDays);

        initIntervalContributionForNewDate(result, currentDate, nextDate);
        for (CommitInfo commit: commits){
            if (nextDate.before(commit.getTime())){
                currentDate = new Date(nextDate.getTime());
                nextDate = getNextCutoffDate(nextDate, durationDays);
                initIntervalContributionForNewDate(result,currentDate, nextDate);
            }
            List<AuthorIntervalContribution> tempList = result.get(commit.getAuthor());
            tempList.get(tempList.size()-1).updateForCommit(commit);
        }
        return result;
    }

    private static void initIntervalContributionForNewDate(Map<Author, List<AuthorIntervalContribution>> map, Date fromDate, Date toDate){
        for (List<AuthorIntervalContribution> dateToInvertal : map.values()){
            //dials back one minute so that github api can include the commit on the time itself
            dateToInvertal.add(new AuthorIntervalContribution(0,0, getOneMinuteBefore(fromDate), toDate));
        }
    }

    private static long getDurationInDays(List<CommitInfo> commits){
        long duration = commits.get(commits.size()-1).getTime().getTime() - commits.get(0).getTime().getTime();
        return TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);

    }

    private static Date getOneMinuteBefore(Date current){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.MINUTE,-1);
        return c.getTime();
    }
    private static Date getNextCutoffDate(Date current, long totalDuration){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
//        if (totalDuration < 30){
//            c.add(Calendar.DATE,1);
//        } else if (totalDuration < 180) {
//            c.add(Calendar.DATE,7);
//        } else{
//            c.add(Calendar.MONTH,1);
//        }
        c.add(Calendar.DATE,7);
        return c.getTime();
    }
}
