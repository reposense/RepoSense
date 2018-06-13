package reposense.report;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import reposense.analyzer.FileInfoAnalyzer;
import reposense.analyzer.FileInfoExtractor;
import reposense.analyzer.FileResultAggregator;
import reposense.dataobject.AuthorContributionSummary;
import reposense.dataobject.FileInfo;
import reposense.dataobject.FileResult;
import reposense.dataobject.RepoConfiguration;
import reposense.util.FileUtil;

/**
 *
 */
public class AuthorshipReporter {

    /**
     * Generates the authorship report for each repo in {@code config} at {@code reportReportDirectory}.
     */
    public static void generateAuthorshipReport(RepoConfiguration config, String repoReportDirectory) {
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        List<FileResult> fileResults = fileInfos.stream()
                .map(fileInfo -> FileInfoAnalyzer.analyzeFile(config, fileInfo))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        AuthorContributionSummary authorshipSummary = FileResultAggregator.aggregateFileResult(
                fileResults, config.getAuthorList());

        FileUtil.writeJsonFile(authorshipSummary, getIndividualAuthorshipPath(repoReportDirectory));
    }

    private static String getIndividualAuthorshipPath(String repoReportDirectory) {
        return repoReportDirectory + "/authorship.json";
    }

}
