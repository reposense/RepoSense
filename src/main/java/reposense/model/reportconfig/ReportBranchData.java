package reposense.model.reportconfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.parser.LocalDateTimeParser;
import reposense.parser.exceptions.InvalidDatesException;
import reposense.system.LogsManager;

/**
 * Represents a single entry of a branch in the YAML config file.
 */
public class ReportBranchData {
    public static final String DEFAULT_BRANCH = "HEAD";
    public static final Long DEFAULT_FILE_SIZE_LIMIT = 1000000L;

    private final Logger logger = LogsManager.getLogger(ReportBranchData.class);
    private final String branch;
    private final String blurb;
    private final List<ReportAuthorDetails> reportAuthorDetails;
    private final List<String> ignoreGlobList;
    private final List<String> ignoreAuthorList;
    private final Long fileSizeLimit;
    private LocalDateTime sinceDate;
    private LocalDateTime untilDate;

    @JsonCreator
    public ReportBranchData(
            @JsonProperty("branch") String branch,
            @JsonProperty("blurb") String blurb,
            @JsonProperty("authors") List<ReportAuthorDetails> reportAuthorDetails,
            @JsonProperty("ignore-glob-list") List<String> ignoreGlobList,
            @JsonProperty("ignore-authors-list") List<String> ignoreAuthorList,
            @JsonProperty("file-size-limit") Long fileSizeLimit,
            @JsonProperty("since") String sinceDate,
            @JsonProperty("until") String untilDate) {
        this.branch = branch == null ? DEFAULT_BRANCH : branch;
        this.blurb = blurb == null ? "" : blurb;
        this.reportAuthorDetails = reportAuthorDetails == null ? new ArrayList<>() : reportAuthorDetails;
        this.ignoreGlobList = ignoreGlobList == null ? new ArrayList<>() : ignoreGlobList;
        this.ignoreAuthorList = ignoreAuthorList == null ? new ArrayList<>() : ignoreAuthorList;
        this.fileSizeLimit = fileSizeLimit == null ? DEFAULT_FILE_SIZE_LIMIT : fileSizeLimit;

        this.sinceDate = safeParseDate(sinceDate, true);
        this.untilDate = safeParseDate(untilDate, false);
    }

    /**
     * Sets the date as {@code null} if the date string format in {@code report-config.yaml} is invalid.
     * Simply ignores invalid date input and set it as null.
     * Note: Only validates date input format. Other logical validation isn't done (e.g. since date < until date)
     */
    private LocalDateTime safeParseDate(String dateStr, boolean isSinceDate) {
        if (dateStr == null) {
            return null;
        }
        try {
            return LocalDateTimeParser.parse(dateStr, isSinceDate);
        } catch (InvalidDatesException e) {
            String ignoreDateMessage;
            if (isSinceDate) {
                ignoreDateMessage = "\nIgnore provided since date for current branch ...";
            } else {
                ignoreDateMessage = "\nIgnore provided until date for current branch ...";
            }
            logger.log(Level.WARNING, e.getMessage() + ignoreDateMessage, e);
            return null;
        }
    }

    public String getBranch() {
        return branch;
    }

    public String getBlurb() {
        return blurb;
    }

    public List<ReportAuthorDetails> getReportAuthorDetails() {
        return reportAuthorDetails;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList;
    }

    public List<String> getIgnoreAuthorList() {
        return ignoreAuthorList;
    }

    public Long getFileSizeLimit() {
        return fileSizeLimit;
    }

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public LocalDateTime getUntilDate() {
        return untilDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportBranchData) {
            ReportBranchData rbd = (ReportBranchData) obj;
            return this.getBranch().equals(rbd.getBranch())
                    && this.getBlurb().equals(rbd.getBlurb())
                    && this.getReportAuthorDetails().equals(rbd.getReportAuthorDetails())
                    && this.getIgnoreGlobList().equals(rbd.getIgnoreGlobList())
                    && this.getIgnoreAuthorList().equals(rbd.getIgnoreAuthorList())
                    && this.getFileSizeLimit().equals(rbd.getFileSizeLimit());
        }

        return false;
    }
}
