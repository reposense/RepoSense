package reposense.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Represents configuration information for a single repository.
 */
public class RepoConfiguration {
    public static final String DEFAULT_BRANCH = "HEAD";
    private static final Logger logger = LogsManager.getLogger(RepoConfiguration.class);

    private RepoLocation location;
    private String branch;
    private String displayName;
    private Date sinceDate;
    private Date untilDate;

    private transient boolean needCheckStyle = false;
    private transient boolean annotationOverwrite = true;
    private transient int commitNum = 1;
    private transient RepoCsvConfiguration repoCsvConfig;
    private transient AuthorConfiguration authorConfig;

    public RepoConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public RepoConfiguration(RepoLocation location, String branch) {
        this(location, branch, new RepoCsvConfiguration(location, branch));
    }

    public RepoConfiguration(RepoLocation location, String branch, RepoCsvConfiguration repoCsvConfig) {
        this.authorConfig = new AuthorConfiguration(location, branch);
        this.repoCsvConfig = repoCsvConfig;
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;

        String organization = location.getOrganization();
        String repoName = location.getRepoName();

        if (organization != null) {
            displayName = organization + "_" + repoName + "_" + branch;
        } else {
            displayName = repoName + "_" + branch;
        }
    }

    public static void setDatesToRepoConfigs(
            List<RepoConfiguration> configs, Optional<Date> sinceDate, Optional<Date> untilDate) {
        for (RepoConfiguration config : configs) {
            config.setSinceDate(sinceDate.orElse(null));
            config.setUntilDate(untilDate.orElse(null));
        }
    }

    /**
     * Merges a {@code RepoConfiguration} from {@code repoConfigs} with an {@code AuthorConfiguration} from
     * {@code authorConfigs} if their {@code RepoLocation} and branch matches
     */
    public static void merge(List<RepoConfiguration> repoConfigs, List<AuthorConfiguration> authorConfigs) {
        for (AuthorConfiguration authorConfig : authorConfigs) {
            if (authorConfig.getLocation().isEmpty()) {
                continue;
            }

            RepoConfiguration matchingRepoConfig = getMatchingRepoConfig(repoConfigs, authorConfig);

            if (matchingRepoConfig == null) {
                logger.warning(String.format(
                        "Repository %s is not found in repo-config.csv.", authorConfig.getLocation()));
                continue;
            }

            matchingRepoConfig.setAuthorConfiguration(authorConfig);
        }

        for (AuthorConfiguration authorConfig : authorConfigs) {
            if (authorConfig.getLocation().isEmpty()) {
                for (RepoConfiguration repoConfig : repoConfigs) {
                    repoConfig.addAuthors(authorConfig.getAuthorList());
                }
            }
        }
    }

    public static List<RepoConfiguration> getRepoConfigurationList(List<RepoCsvConfiguration> repoCsvConfigs) {
        List<RepoConfiguration> repoConfigs = new ArrayList<>();
        for (RepoCsvConfiguration repoCsvConfig : repoCsvConfigs) {
            RepoLocation location = repoCsvConfig.getLocation();
            String branch = repoCsvConfig.getBranch();
            repoConfigs.add(new RepoConfiguration(location, branch, repoCsvConfig));
        }
        return repoConfigs;
    }

    /**
     * Iterates through {@code repoConfigs} to find a {@code RepoConfiguration} with {@code RepoLocation} and branch
     * that matches {@code authorConfig}. Returns {@code null} if no match is found.
     */
    private static RepoConfiguration getMatchingRepoConfig(
            List<RepoConfiguration> repoConfigs, AuthorConfiguration authorConfig) {
        for (RepoConfiguration repoConfig : repoConfigs) {
            if (repoConfig.getLocation().equals(authorConfig.getLocation())
                    && repoConfig.getBranch().equals(authorConfig.getBranch())) {
                return repoConfig;
            }
        }
        return null;
    }

    /**
     * Sets {@code formats} to {@code RepoConfiguration} in {@code configs} if its format list is empty.
     */
    public static void setFormatsToRepoConfigs(List<RepoConfiguration> configs, List<Format> formats) {
        configs.stream().filter(config -> config.getFormats().isEmpty())
                        .forEach(config -> config.setFormats(formats));
    }

    /**
     * Sets {@code isStandaloneConfigIgnored} to all {@code RepoConfiguration} in {@code configs}.
     */
    public static void setStandaloneConfigIgnoredToRepoConfigs(
            List<RepoConfiguration> configs, boolean isStandaloneConfigIgnored) {
        configs.stream().forEach(config -> config.setStandaloneConfigIgnored(isStandaloneConfigIgnored));
    }

    /**
     * Clears authors information and use the information provided from {@code standaloneConfig}.
     */
    public void update(StandaloneConfig standaloneConfig) {
        authorConfig.update(standaloneConfig);
        repoCsvConfig.update(standaloneConfig);
    }

    public String getRepoRoot() {
        String path = FileUtil.REPOS_ADDRESS + File.separator + getRepoName() + File.separator;

        if (!getRepoName().isEmpty()) {
            path += getRepoName() + File.separator;
        }

        return path;
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (!(other instanceof RepoConfiguration)) {
            return false;
        }

        RepoConfiguration otherRepoConfig = (RepoConfiguration) other;

        return location.equals(otherRepoConfig.location)
                && branch.equals(otherRepoConfig.branch)
                && authorConfig.equals(otherRepoConfig.authorConfig)
                && repoCsvConfig.equals(otherRepoConfig.repoCsvConfig);
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorConfig.getAuthorDisplayNameMap();
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        authorConfig.setAuthorDisplayNameMap(authorDisplayNameMap);
    }

    public int getCommitNum() {
        return commitNum;
    }

    public void setCommitNum(int commitNum) {
        this.commitNum = commitNum;
    }

    public boolean isNeedCheckStyle() {
        return needCheckStyle;
    }

    public void setNeedCheckStyle(boolean needCheckStyle) {
        this.needCheckStyle = needCheckStyle;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        updateDisplayName(branch);
        this.branch = branch;
        authorConfig.setBranch(branch);
        repoCsvConfig.setBranch(branch);
    }

    public void updateDisplayName(String branch) {
        this.displayName = displayName.substring(0, displayName.lastIndexOf('_') + 1) + branch;
    }

    public boolean isAnnotationOverwrite() {
        return annotationOverwrite;
    }

    public void setAnnotationOverwrite(boolean annotationOverwrite) {
        this.annotationOverwrite = annotationOverwrite;
    }

    public List<String> getIgnoreGlobList() {
        return repoCsvConfig.getIgnoreGlobList();
    }

    public void setIgnoreGlobList(List<String> ignoreGlobList) {
        repoCsvConfig.setIgnoreGlobList(ignoreGlobList);
    }

    public List<CommitHash> getIgnoreCommitList() {
        return repoCsvConfig.getIgnoreCommitList();
    }

    public void setIgnoreCommitList(List<CommitHash> ignoreCommitList) {
        repoCsvConfig.setIgnoreCommitList(ignoreCommitList);
    }

    public List<Author> getAuthorList() {
        return authorConfig.getAuthorList();
    }

    public void addAuthor(Author author) {
        authorConfig.addAuthor(author, this.getIgnoreGlobList());
    }

    public void addAuthors(List<Author> authorList) {
        authorConfig.addAuthors(authorList, this.getIgnoreGlobList());
    }

    public void setAuthorConfiguration(AuthorConfiguration authorConfig) {
        this.authorConfig = authorConfig;
        List<String> ignoreGlobList = getIgnoreGlobList();
        for (Author author : authorConfig.getAuthorList()) {
            AuthorConfiguration.propagateIgnoreGlobList(author, ignoreGlobList);
        }
    }

    public boolean containsAuthor(Author author) {
        return authorConfig.containsAuthor(author);
    }

    /**
     * Clears authors information and sets the {@code authorList} to {@code RepoConfiguration}.
     */
    public void setAuthorList(List<Author> authorList) {
        authorConfig.setAuthorList(authorList);
        authorConfig.resetAuthorInformation(this.getIgnoreGlobList());
    }

    public Map<String, Author> getAuthorEmailsAndAliasesMap() {
        return authorConfig.getAuthorEmailsAndAliasesMap();
    }

    public void setAuthorEmailsAndAliasesMap(Map<String, Author> authorEmailsAndAliasesMap) {
        authorConfig.setAuthorEmailsAndAliasesMap(authorEmailsAndAliasesMap);
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
    }

    public List<Format> getFormats() {
        return repoCsvConfig.getFormats();
    }

    public void setFormats(List<Format> formats) {
        repoCsvConfig.setFormats(formats);
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorConfig.setAuthorDisplayName(author, displayName);
    }

    public void addAuthorEmailsAndAliasesMapEntry(Author author, List<String> values) {
        authorConfig.addAuthorEmailsAndAliasesMapEntry(author, values);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRepoName() {
        return location.getRepoName();
    }

    public void setStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
        repoCsvConfig.setStandaloneConfigIgnored(isStandaloneConfigIgnored);
    }

    public RepoLocation getLocation() {
        return location;
    }

    public String getOrganization() {
        return location.getOrganization();
    }

    public boolean isStandaloneConfigIgnored() {
        return repoCsvConfig.isStandaloneConfigIgnored();
    }
}
