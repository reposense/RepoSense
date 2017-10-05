package report;

import com.google.gson.Gson;
import dataObject.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class ContributionSummaryGenerator {

    public static List<RepoContributionSummary> analyzeContribution(List<RepoInfo> repos){
        System.out.println("Generating summary report...");
        List<RepoContributionSummary> result = new ArrayList<>();
        for (RepoInfo repo:repos){
            RepoContributionSummary summary = new RepoContributionSummary(repo);
            summary.setAuthorIntervalContributions(getAuthorIntervalContributions(repo.getCommits()));
            summary.setAuthorFinalContributionMap(repo.getCommits().get(repo.getCommits().size()-1).getAuthorContributionMap());
            result.add(summary);
        }
        System.out.println("done");
        return result;
    }

    private static Map<Author, Map<Date, AuthorIntervalContribution>> getAuthorIntervalContributions(List<CommitInfo> commits){
        //init
        long durationDays = getDurationInDays(commits);
        Map<Author, Map<Date, AuthorIntervalContribution>> result = new HashMap<>();
        for (Author author: commits.get(commits.size()-1).getAuthorContributionMap().keySet()){
            result.put(author,new HashMap<>());
        }
        Date currentDate = commits.get(0).getTime();
        Date nextDate = getNextCutoffDate(currentDate, durationDays);

        initIntervalContributionForNewDate(result, currentDate);
        for (CommitInfo commit: commits){
            if (nextDate.before(commit.getTime())){
                currentDate = new Date(nextDate.getTime());
                nextDate = getNextCutoffDate(nextDate, durationDays);
                initIntervalContributionForNewDate(result,currentDate);
            }
            result.get(commit.getAuthor()).get(currentDate).updateForCommit(commit);
        }
        return result;
    }

    private static void initIntervalContributionForNewDate(Map<Author, Map<Date, AuthorIntervalContribution>> map, Date date){
        for (Map<Date, AuthorIntervalContribution> dateToInvertal : map.values()){
            dateToInvertal.put(date,new AuthorIntervalContribution(0,0));
        }
    }

    private static long getDurationInDays(List<CommitInfo> commits){
        long duration = commits.get(commits.size()-1).getTime().getTime() - commits.get(0).getTime().getTime();
        return TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);

    }

    private static Date getNextCutoffDate(Date current, long totalDuration){
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        if (totalDuration < 30){
            c.add(Calendar.DATE,1);
        } else if (totalDuration < 180) {
            c.add(Calendar.DATE,7);
        } else{
            c.add(Calendar.MONTH,1);
        }
        return c.getTime();
    }
}
