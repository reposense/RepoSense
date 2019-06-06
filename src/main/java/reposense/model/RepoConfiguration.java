package reposense.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import reposense.git.GitBranch;
import reposense.git.exception.GitBranchException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Represents configuration information from CSV config file for a single repository.
 */
public class RepoConfiguration {
    public static final String DEFAULT_BRANCH = "HEAD";
    private static final Logger logger = LogsManager.getLogger(RepoConfiguration.class);
    private final transient String repoFolderName;

    private RepoLocation location;
    private String branch;
    private String displayName;
    private transient Date sinceDate;
    private transient Date untilDate;

    private transient boolean annotationOverwrite = true;
    private transient List<Format> formats;
    private transient int commitNum = 1;
    private transient List<String> ignoreGlobList = new ArrayList<>();
    private transient AuthorConfiguration authorConfig;
    private transient GroupConfiguration groupConfig;
    private transient boolean isStandaloneConfigIgnored;
    private transient List<CommitHash> ignoreCommitList;
    private transient boolean isFormatsOverriding;
    private transient boolean isIgnoreGlobListOverriding;
    private transient boolean isIgnoreCommitListOverriding;

    public RepoConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public RepoConfiguration(RepoLocation location, String branch) {
        this(location, branch, Collections.emptyList(), Collections.emptyList(), false, Collections.emptyList(),
                false, false, false);
    }

    public RepoConfiguration(RepoLocation location, String branch, List<Format> formats, List<String> ignoreGlobList,
            boolean isStandaloneConfigIgnored, List<CommitHash> ignoreCommitList, boolean isFormatsOverriding,
            boolean isIgnoreGlobListOverriding, boolean isIgnoreCommitListOverriding) {
        this.authorConfig = new AuthorConfiguration(location, branch);
        this.groupConfig = new GroupConfiguration(location);
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;
        this.ignoreGlobList = ignoreGlobList;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.formats = formats;
        this.ignoreCommitList = ignoreCommitList;
        this.isFormatsOverriding = isFormatsOverriding;
        this.isIgnoreGlobListOverriding = isIgnoreGlobListOverriding;
        this.isIgnoreCommitListOverriding = isIgnoreCommitListOverriding;

        String organization = location.getOrganization();
        String repoName = location.getRepoName();

        if (organization != null) {
            displayName = organization + "_" + repoName + "_" + branch;
            repoFolderName = organization + "_" + repoName;
        } else {
            displayName = repoName + "_" + branch;
            repoFolderName = repoName;
        }
    }

    public static void setDatesToRepoConfigs(
            List<RepoConfiguration> configs, Optional<Date> sinceDate, Optional<Date> untilDate) {
        for (RepoConfiguration config : configs) {
            config.setSinceDate(sinceDate.orElse(null));
            // set untilDate in summary.json to the current date of generation if it is not provided
            config.setUntilDate(untilDate.orElse(new Date()));
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

    /**
     * Merges a {@code RepoConfiguration} from {@code repoConfigs} with an {@code GroupConfiguration} from
     * {@code groupConfigs} if their {@code RepoLocation} matches
     */
    public static void mergeGroups(List<RepoConfiguration> repoConfigs, List<GroupConfiguration> groupConfigs) {
        for (GroupConfiguration groupConfig : groupConfigs) {
            if (groupConfig.getLocation().isEmpty()) {
                continue;
            }

            RepoConfiguration matchingRepoConfig = getMatchingRepoConfigForGroups(repoConfigs, groupConfig);

            if (matchingRepoConfig == null) {
                logger.warning(String.format(
                        "Repository %s is not found in repo-config.csv.", groupConfig.getLocation()));
                continue;
            }

            matchingRepoConfig.setGroupConfiguration(groupConfig);
        }

        for (GroupConfiguration groupConfig : groupConfigs) {
            if (groupConfig.getLocation().isEmpty()) {
                for (RepoConfiguration repoConfig : repoConfigs) {
                    repoConfig.addGroups(groupConfig.getGroupList());
                }
            }
        }
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
     * Iterates through {@code repoConfigs} to find a {@code RepoConfiguration} with {@code RepoLocation}
     * that matches {@code groupConfig}. Returns {@code null} if no match is found.
     */
    private static RepoConfiguration getMatchingRepoConfigForGroups(
            List<RepoConfiguration> repoConfigs, GroupConfiguration groupConfig) {
        for (RepoConfiguration repoConfig : repoConfigs) {
            if (repoConfig.getLocation().equals(groupConfig.getLocation())) {
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
        // only assign the new values when all the fields in {@code standaloneConfig} pass the validations.
        Format.validateFormats(standaloneConfig.getFormats());
        CommitHash.validateCommits(standaloneConfig.getIgnoreCommitList());

        if (!isIgnoreGlobListOverriding) {
            ignoreGlobList = standaloneConfig.getIgnoreGlobList();
        }
        if (!isFormatsOverriding) {
            formats = Format.convertStringsToFormats(standaloneConfig.getFormats());
        }
        if (!isIgnoreCommitListOverriding) {
            ignoreCommitList = CommitHash.convertStringsToCommits(standaloneConfig.getIgnoreCommitList());
        }
        authorConfig.update(standaloneConfig, ignoreGlobList);
    }

    /**
     * Attempts to find matching {@code Author} given a name and an email.
     * If no matching {@code Author} is found, {@code Author#UNKNOWN_AUTHOR} is returned.
     */
    public Author getAuthor(String name, String email) {
        return authorConfig.getAuthor(name, email);
    }

    /**
     * Updates branch with {@code currentBranch} if default branch is specified.
     */
    public void updateBranch(String currentBranch) {
        if (branch.equals(DEFAULT_BRANCH)) {
            setBranch(currentBranch);
        }
    }

    /**
     * Gets the current branch and updates branch with current branch if default branch is specified.
     */
    public void updateBranch() throws GitBranchException {
        if (branch.equals(DEFAULT_BRANCH)) {
            String currentBranch = GitBranch.getCurrentBranch(getRepoRoot());
            setBranch(currentBranch);
        }
    }

    public String getRepoRoot() {
        String path = FileUtil.REPOS_ADDRESS + File.separator + getRepoFolderName() + File.separator;

        if (!getRepoName().isEmpty()) {
            path += getRepoName() + File.separator;
        }

        return path;
    }

    public String getRepoFolderName() {
        return repoFolderName;
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
                && ignoreGlobList.equals(otherRepoConfig.ignoreGlobList)
                && isStandaloneConfigIgnored == otherRepoConfig.isStandaloneConfigIgnored
                && formats.equals(otherRepoConfig.formats)
                && isFormatsOverriding == otherRepoConfig.isFormatsOverriding
                && isIgnoreGlobListOverriding == otherRepoConfig.isIgnoreGlobListOverriding
                && isIgnoreCommitListOverriding == otherRepoConfig.isIgnoreCommitListOverriding;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        updateDisplayName(branch);
        this.branch = branch;
        authorConfig.setBranch(branch);
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
        return ignoreGlobList;
    }

    public void setIgnoreGlobList(List<String> ignoreGlobList) {
        this.ignoreGlobList = ignoreGlobList;
    }

    public List<CommitHash> getIgnoreCommitList() {
        return ignoreCommitList;
    }

    public void setIgnoreCommitList(List<CommitHash> ignoreCommitList) {
        this.ignoreCommitList = ignoreCommitList;
    }

    public List<Author> getAuthorList() {
        return authorConfig.getAuthorList();
    }

    public List<Group> getGroupList() {
        return groupConfig.getGroupList();
    }

    public void addAuthor(Author author) {
        authorConfig.addAuthor(author, this.getIgnoreGlobList());
    }

    public void addAuthors(List<Author> authorList) {
        authorConfig.addAuthors(authorList, this.getIgnoreGlobList());
    }

    public void addGroups(List<Group> groupList) {
        groupConfig.addGroups(groupList);
    }

    public void setAuthorConfiguration(AuthorConfiguration authorConfig) {
        this.authorConfig = authorConfig;
        for (Author author : authorConfig.getAuthorList()) {
            AuthorConfiguration.propagateIgnoreGlobList(author, ignoreGlobList);
        }
    }

    public void setGroupConfiguration(GroupConfiguration groupConfig) {
        this.groupConfig = groupConfig;
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

    /**
     * Clears groups information and sets the {@code groupList} to {@code RepoConfiguration}.
     */
    public void setGroupList(List<Group> groupList) {
        groupConfig.setGroupList(groupList);
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
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
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
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
    }

    public RepoLocation getLocation() {
        return location;
    }

    public String getOrganization() {
        return location.getOrganization();
    }

    public boolean isStandaloneConfigIgnored() {
        return isStandaloneConfigIgnored;
    }

    public boolean isFormatsOverriding() {
        return isFormatsOverriding;
    }

    public boolean isIgnoreGlobListOverriding() {
        return isIgnoreGlobListOverriding;
    }

    public boolean isIgnoreCommitListOverriding() {
        return isIgnoreCommitListOverriding;
    }
}
