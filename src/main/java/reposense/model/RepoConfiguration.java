package reposense.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.git.GitBranch;
import reposense.git.exception.GitBranchException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Represents configuration information from CSV config file for a single repository.
 */
public class RepoConfiguration {
    public static final String DEFAULT_BRANCH = "HEAD";
    public static final String DEFAULT_EXTRA_OUTPUT_FOLDER_NAME = "";
    public static final long DEFAULT_FILE_SIZE_LIMIT = 500000;
    private static final Logger logger = LogsManager.getLogger(RepoConfiguration.class);

    private RepoLocation location;
    private String branch;
    private String displayName;
    private String outputFolderName;
    private final transient String extraOutputFolderName;
    private transient ZoneId zoneId;
    private transient LocalDateTime sinceDate;
    private transient LocalDateTime untilDate;
    private transient String repoFolderName;

    private transient FileTypeManager fileTypeManager;
    private transient List<String> ignoreGlobList = new ArrayList<>();
    private transient List<String> ignoredAuthorsList = new ArrayList<>();
    private transient AuthorConfiguration authorConfig;
    private transient boolean isStandaloneConfigIgnored;
    private transient boolean isFileSizeLimitIgnored;
    private transient List<CommitHash> ignoreCommitList;
    private transient boolean isLastModifiedDateIncluded;
    private transient boolean isShallowCloningPerformed;
    private transient boolean isFindingPreviousAuthorsPerformed;
    private transient boolean isFormatsOverriding;
    private transient boolean isIgnoreGlobListOverriding;
    private transient boolean isIgnoreCommitListOverriding;
    private transient boolean isIgnoredAuthorsListOverriding;
    private transient long fileSizeLimit;
    private transient boolean isFileSizeLimitOverriding;
    private transient boolean isIgnoredFileAnalysisSkipped;

    public RepoConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public RepoConfiguration(RepoLocation location, String branch) {
        this(location, branch, DEFAULT_EXTRA_OUTPUT_FOLDER_NAME);
    }

    public RepoConfiguration(RepoLocation location, String branch, String extraOutputFolderName) {
        this(location, branch, Collections.emptyList(), Collections.emptyList(),
                RepoConfiguration.DEFAULT_FILE_SIZE_LIMIT, false, false, Collections.emptyList(), false, false, false,
                false, false, false, false, Collections.emptyList(), false, extraOutputFolderName);
    }

    public RepoConfiguration(RepoLocation location, String branch, List<FileType> formats, List<String> ignoreGlobList,
            long fileSizeLimit, boolean isStandaloneConfigIgnored, boolean isFileSizeLimitIgnored,
            List<CommitHash> ignoreCommitList, boolean isFormatsOverriding, boolean isIgnoreGlobListOverriding,
            boolean isIgnoreCommitListOverriding, boolean isFileSizeLimitOverriding, boolean isShallowCloningPerformed,
            boolean isFindingPreviousAuthorsPerformed, boolean isIgnoredFileAnalysisSkipped,
            List<String> ignoredAuthorsList, boolean isIgnoredAuthorsListOverriding) {
        this(location, branch, formats, ignoreGlobList, fileSizeLimit, isStandaloneConfigIgnored,
                isFileSizeLimitIgnored, ignoreCommitList, isFormatsOverriding, isIgnoreGlobListOverriding,
                isIgnoreCommitListOverriding, isFileSizeLimitOverriding, isShallowCloningPerformed,
                isFindingPreviousAuthorsPerformed, isIgnoredFileAnalysisSkipped, ignoredAuthorsList,
                isIgnoredAuthorsListOverriding, DEFAULT_EXTRA_OUTPUT_FOLDER_NAME);
    }

    public RepoConfiguration(RepoLocation location, String branch, List<FileType> formats, List<String> ignoreGlobList,
            long fileSizeLimit, boolean isStandaloneConfigIgnored, boolean isFileSizeLimitIgnored,
            List<CommitHash> ignoreCommitList, boolean isFormatsOverriding, boolean isIgnoreGlobListOverriding,
            boolean isIgnoreCommitListOverriding, boolean isFileSizeLimitOverriding, boolean isShallowCloningPerformed,
            boolean isFindingPreviousAuthorsPerformed, boolean isIgnoredFileAnalysisSkipped,
            List<String> ignoredAuthorsList, boolean isIgnoredAuthorsListOverriding, String extraOutputFolderName) {
        this.authorConfig = new AuthorConfiguration(location, branch);
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;
        this.ignoreGlobList = ignoreGlobList;
        this.fileSizeLimit = fileSizeLimit;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
        this.fileTypeManager = new FileTypeManager(formats);
        this.ignoreCommitList = ignoreCommitList;
        this.isFormatsOverriding = isFormatsOverriding;
        this.isIgnoreGlobListOverriding = isIgnoreGlobListOverriding;
        this.isIgnoreCommitListOverriding = isIgnoreCommitListOverriding;
        this.isFileSizeLimitOverriding = isFileSizeLimitOverriding;
        this.isShallowCloningPerformed = isShallowCloningPerformed;
        this.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
        this.isIgnoredFileAnalysisSkipped = isIgnoredFileAnalysisSkipped;
        this.ignoredAuthorsList = ignoredAuthorsList;
        this.isIgnoredAuthorsListOverriding = isIgnoredAuthorsListOverriding;
        this.extraOutputFolderName = extraOutputFolderName;

        String organization = location.getOrganization();
        String repoName = location.getRepoName();
        displayName = repoName + "[" + branch + "]";
        outputFolderName = repoName + "_" + branch;
        repoFolderName = repoName;

        if (!organization.isEmpty()) {
            repoFolderName = organization + "_" + repoFolderName;
            displayName = organization + "/" + displayName;
            outputFolderName = organization + "_" + outputFolderName;
        }
    }

    public static void setDatesToRepoConfigs(List<RepoConfiguration> configs,
            LocalDateTime sinceDate, LocalDateTime untilDate) {
        for (RepoConfiguration config : configs) {
            config.setSinceDate(sinceDate);
            config.setUntilDate(untilDate);
        }
    }

    public static void setZoneIdToRepoConfigs(List<RepoConfiguration> configs, ZoneId zoneId) {
        for (RepoConfiguration config : configs) {
            config.setZoneId(zoneId);
        }
    }

    public static void setIsLastModifiedDateIncludedToRepoConfigs(List<RepoConfiguration> configs,
                                                                  boolean isLastModifiedDateIncluded) {
        for (RepoConfiguration config : configs) {
            config.setIsLastModifiedDateIncluded(isLastModifiedDateIncluded);
        }
    }

    public static void setIsShallowCloningPerformedToRepoConfigs(List<RepoConfiguration> configs,
                                                                 boolean isShallowCloningPerformed) {
        if (isShallowCloningPerformed) {
            configs.stream().forEach(config -> config.setIsShallowCloningPerformed(true));
        }
    }

    public static void setIsFindingPreviousAuthorsPerformedToRepoConfigs(List<RepoConfiguration> configs,
                                                                         boolean isFindingPreviousAuthorsPerformed) {
        if (isFindingPreviousAuthorsPerformed) {
            configs.stream().forEach(config -> config.setIsFindingPreviousAuthorsPerformed(true));
        }
    }

    public static void setToFalseIsFindingPreviousAuthorsPerformedToRepoConfigs(List<RepoConfiguration> configs) {
        configs.stream().forEach(config -> config.setIsFindingPreviousAuthorsPerformed(false));
    }

    public static void setHasAuthorConfigFileToRepoConfigs(List<RepoConfiguration> configs,
                                                           boolean setHasAuthorConfigFile) {
        configs.stream().forEach(config -> config.setHasAuthorConfigFile(setHasAuthorConfigFile));
    }

    /**
     * Merges a {@link RepoConfiguration} from {@code repoConfigs} with an {@link AuthorConfiguration} from
     * {@code authorConfigs} if their {@link RepoLocation} and branch matches.
     */
    public static void merge(List<RepoConfiguration> repoConfigs, List<AuthorConfiguration> authorConfigs) {
        for (AuthorConfiguration authorConfig : authorConfigs) {
            if (authorConfig.getLocation().isEmpty()) {
                for (RepoConfiguration repoConfig : repoConfigs) {
                    repoConfig.addAuthors(authorConfig.getAuthorList());
                }
                continue;
            }

            List<RepoConfiguration> locationMatchingRepoConfigs =
                    getMatchingRepoConfigsByLocation(repoConfigs, authorConfig.getLocation());

            if (locationMatchingRepoConfigs.isEmpty()) {
                logger.warning(String.format(
                        "Repository %s is not found in repo-config.csv.",
                        authorConfig.getLocation()));
                continue;
            }
            if (authorConfig.isDefaultBranch()) {
                locationMatchingRepoConfigs.forEach(matchingRepoConfig -> {
                    matchingRepoConfig.addAuthors(authorConfig.getAuthorList());
                });
                continue;
            }

            RepoConfiguration branchMatchingRepoConfig = getMatchingRepoConfig(repoConfigs, authorConfig);

            if (branchMatchingRepoConfig == null) {
                if (!authorConfig.isDefaultBranch()) {
                    logger.warning(String.format(
                            "Repository %s (branch %s) is not found in repo-config.csv.",
                            authorConfig.getLocation(), authorConfig.getBranch()));
                }
                continue;
            }

            branchMatchingRepoConfig.addAuthors(authorConfig.getAuthorList());
        }
    }

    /**
     * Sets the list of groups in {@code groupConfigs} to the respective {@code repoConfigs}.
     */
    public static void setGroupConfigsToRepos(List<RepoConfiguration> repoConfigs,
            List<GroupConfiguration> groupConfigs) {
        for (GroupConfiguration groupConfig : groupConfigs) {
            List<RepoConfiguration> matchingRepoConfigs;
            if (groupConfig.getLocation().isEmpty()) {
                matchingRepoConfigs = repoConfigs;
            } else {
                matchingRepoConfigs = getMatchingRepoConfigsByLocation(repoConfigs,
                        groupConfig.getLocation());
            }
            if (matchingRepoConfigs.isEmpty()) {
                logger.warning(String.format(
                        "Repository %s is not found in repo-config.csv.", groupConfig.getLocation()));
                continue;
            }
            matchingRepoConfigs.forEach(matchingRepoConfig -> {
                matchingRepoConfig.addGroups(groupConfig.getGroupsList());
            });
        }
    }

    /**
     * Iterates through {@code repoConfigs} to find a {@link RepoConfiguration} with {@link RepoLocation} and branch
     * that matches {@code authorConfig}. Returns {@code null} if no match is found.
     */
    private static RepoConfiguration getMatchingRepoConfig(List<RepoConfiguration> repoConfigs,
            AuthorConfiguration authorConfig) {
        if (authorConfig.isDefaultBranch()) {
            return null;
        }
        for (RepoConfiguration repoConfig : repoConfigs) {
            if (repoConfig.getLocation().equals(authorConfig.getLocation())
                    && repoConfig.getBranch().equals(authorConfig.getBranch())) {
                return repoConfig;
            }
        }
        return null;
    }

    /**
     * Returns a list of {@link RepoConfiguration} where the {@link RepoLocation} of a {@link RepoConfiguration}
     * in the list of {@code configs} matches {@code targetRepoLocation}.
     */
    private static List<RepoConfiguration> getMatchingRepoConfigsByLocation(List<RepoConfiguration> configs,
            RepoLocation targetRepoLocation) {
        return configs.stream().filter(config -> config.getLocation().equals(targetRepoLocation))
                .collect(Collectors.toList());
    }

    /**
     * Sets {@code formats} to {@link RepoConfiguration} in {@code configs} if its format list is empty.
     */
    public static void setFormatsToRepoConfigs(List<RepoConfiguration> configs, List<FileType> formats) {
        for (RepoConfiguration config : configs) {
            if (!config.fileTypeManager.hasSpecifiedFormats()) {
                config.fileTypeManager.setFormats(formats);
            }
        }
    }

    /**
     * Sets each {@link RepoConfiguration} in {@code configs} to ignore its standalone config, if
     * {@code ignoreAllStandaloneConfigs} is true.
     */
    public static void setStandaloneConfigIgnoredToRepoConfigs(List<RepoConfiguration> configs,
            boolean ignoreAllStandaloneConfigs) {
        if (ignoreAllStandaloneConfigs) {
            configs.forEach(config -> config.setStandaloneConfigIgnored(true));
        }
    }

    /**
     * Sets each {@link RepoConfiguration} in {@code configs} to ignore its filesize limit, if
     * {@code ignoreFilesizeLimit} is true.
     */
    public static void setFileSizeLimitIgnoredToRepoConfigs(List<RepoConfiguration> configs,
                                                            boolean ignoreFileSizeLimit) {
        if (ignoreFileSizeLimit) {
            configs.forEach(config -> config.setFileSizeLimitIgnored(true));
        }
    }

    /**
     * Checks if any of the {@code configs} is finding previous authors for commit analysis.
     */
    public static boolean isAnyRepoFindingPreviousAuthors(List<RepoConfiguration> configs) {
        return configs.stream().anyMatch(RepoConfiguration::isFindingPreviousAuthorsPerformed);
    }

    /**
     * Clears existing information related to this repository and its authors, and replaces it with information from
     * {@code standaloneConfig}.
     */
    public void update(StandaloneConfig standaloneConfig) {
        // only assign the new values when all the fields in {@code standaloneConfig} pass the validations.
        List<FileType> replacementFileTypes = FileType.convertFormatStringsToFileTypes(standaloneConfig.getFormats());
        CommitHash.validateCommits(standaloneConfig.getIgnoreCommitList());

        if (!isIgnoreGlobListOverriding) {
            ignoreGlobList = standaloneConfig.getIgnoreGlobList();
        }
        if (!isFormatsOverriding) {
            fileTypeManager.setFormats(replacementFileTypes);
        }
        if (!isIgnoreCommitListOverriding) {
            ignoreCommitList = CommitHash.convertStringsToCommits(standaloneConfig.getIgnoreCommitList());
        }
        if (!isIgnoredAuthorsListOverriding) {
            ignoredAuthorsList = standaloneConfig.getIgnoreAuthorList();
        }
        if (!isFileSizeLimitOverriding) {
            fileSizeLimit = standaloneConfig.getFileSizeLimit();
        }
        authorConfig.update(standaloneConfig, ignoreGlobList);
    }

    /**
     * Returns the matching {@link Author} given a {@code name} and an {@code email}.
     * If no matching {@link Author} is found, {@link Author#UNKNOWN_AUTHOR} is returned.
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
     *
     * @throws GitBranchException if current branch cannot be retrieved.
     */
    public void updateBranch() throws GitBranchException {
        if (branch.equals(DEFAULT_BRANCH)) {
            String currentBranch = GitBranch.getCurrentBranch(getRepoRoot());
            setBranch(currentBranch);
        }
    }

    /**
     * Gets the path to the root folder of the repository.
     */
    public String getRepoRoot() {
        String path = FileUtil.REPOS_ADDRESS + File.separator + getRepoFolderName() + File.separator;

        if (!getRepoName().isEmpty()) {
            if (!extraOutputFolderName.isEmpty()) {
                path += extraOutputFolderName + File.separator;
            }

            path += getRepoName() + File.separator;
        }

        return path;
    }

    /**
     * Gets the name of the folder containing the cloned repository; the parent directory of the repo's root folder.
     */
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
                && ignoredAuthorsList.equals(otherRepoConfig.ignoredAuthorsList)
                && fileSizeLimit == otherRepoConfig.fileSizeLimit
                && isStandaloneConfigIgnored == otherRepoConfig.isStandaloneConfigIgnored
                && fileTypeManager.equals(otherRepoConfig.fileTypeManager)
                && isLastModifiedDateIncluded == otherRepoConfig.isLastModifiedDateIncluded
                && isFormatsOverriding == otherRepoConfig.isFormatsOverriding
                && isShallowCloningPerformed == otherRepoConfig.isShallowCloningPerformed
                && isIgnoreGlobListOverriding == otherRepoConfig.isIgnoreGlobListOverriding
                && isIgnoreCommitListOverriding == otherRepoConfig.isIgnoreCommitListOverriding
                && isIgnoredAuthorsListOverriding == otherRepoConfig.isIgnoredAuthorsListOverriding
                && isFileSizeLimitOverriding == otherRepoConfig.isFileSizeLimitOverriding
                && isFileSizeLimitIgnored == otherRepoConfig.isFileSizeLimitIgnored
                && isIgnoredFileAnalysisSkipped == otherRepoConfig.isIgnoredFileAnalysisSkipped;
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorConfig.getAuthorDisplayNameMap();
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        authorConfig.setAuthorDisplayNameMap(authorDisplayNameMap);
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        updateDisplayName(branch);
        updateOutputFolderName(branch);
        this.branch = branch;
        authorConfig.setBranch(branch);
    }

    /**
     * Updates the branch in the {@code displayName} to the current {@code branch}.
     */
    public void updateDisplayName(String branch) {
        this.displayName = displayName.substring(0, displayName.lastIndexOf('[') + 1) + branch + "]";
    }

    /**
     * Updates the branch in the {@code outputFolderName} to the current {@code branch}.
     */
    public void updateOutputFolderName(String branch) {
        this.outputFolderName = outputFolderName.substring(0, outputFolderName.lastIndexOf('_') + 1) + branch;
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

    public long getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setIgnoreCommitList(List<CommitHash> ignoreCommitList) {
        this.ignoreCommitList = ignoreCommitList;
    }

    public List<String> getIgnoredAuthorsList() {
        return this.ignoredAuthorsList;
    }

    public void setIgnoredAuthorsList(List<String> ignoredAuthorsList) {
        this.ignoredAuthorsList = ignoredAuthorsList;
    }

    public boolean isIgnoredAuthorsListOverriding() {
        return this.isIgnoredAuthorsListOverriding;
    }

    public void setIsLastModifiedDateIncluded(boolean lastModifiedDateIncluded) {
        this.isLastModifiedDateIncluded = lastModifiedDateIncluded;
    }

    public void setIsShallowCloningPerformed(boolean isShallowCloningPerformed) {
        this.isShallowCloningPerformed = isShallowCloningPerformed;
    }

    public void setIsFindingPreviousAuthorsPerformed(boolean isFindingPreviousAuthorsPerformed) {
        this.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
    }

    public boolean isLastModifiedDateIncluded() {
        return this.isLastModifiedDateIncluded;
    }

    public boolean isShallowCloningPerformed() {
        return this.isShallowCloningPerformed;
    }

    public void setIsIgnoredAuthorsListOverriding(boolean isIgnoredAuthorsListOverriding) {
        this.isIgnoredAuthorsListOverriding = isIgnoredAuthorsListOverriding;
    }

    public void removeIgnoredAuthors() {
        authorConfig.removeIgnoredAuthors(ignoredAuthorsList);
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
        for (Author author : authorConfig.getAuthorList()) {
            AuthorConfiguration.propagateIgnoreGlobList(author, ignoreGlobList);
        }
    }

    public boolean containsAuthor(Author author) {
        return authorConfig.containsAuthor(author);
    }

    /**
     * Clears authors information and sets the {@code authorList} to {@link RepoConfiguration}.
     */
    public void setAuthorList(List<Author> authorList) {
        authorConfig.clear();
        authorConfig.setAuthorList(authorList);
        authorConfig.buildFromAuthorList();
        authorList.forEach(author -> AuthorConfiguration.propagateIgnoreGlobList(author, this.getIgnoreGlobList()));
    }

    public void setHasAuthorConfigFile(boolean hasAuthorConfigFile) {
        authorConfig.setHasAuthorConfigFile(hasAuthorConfigFile);
    }

    public Map<String, Author> getAuthorNamesToAuthorMap() {
        return authorConfig.getAuthorNamesToAuthorMap();
    }

    public void setAuthorNamesToAuthorMap(Map<String, Author> authorNamesToAuthorMap) {
        authorConfig.setAuthorNamesToAuthorMap(authorNamesToAuthorMap);
    }

    public Map<String, Author> getAuthorEmailsToAuthorMap() {
        return authorConfig.getAuthorEmailsToAuthorMap();
    }

    public void setAuthorEmailsToAuthorMap(Map<String, Author> authorEmailsToAuthorMap) {
        authorConfig.setAuthorEmailsToAuthorMap(authorEmailsToAuthorMap);
    }

    public void clearAuthorDetailsToAuthorMap() {
        authorConfig.clearAuthorDetailsToAuthorMap();
    }

    public void setFormats(List<FileType> formats) {
        fileTypeManager.setFormats(formats);
    }

    private void setGroups(List<FileType> groups) {
        fileTypeManager.setGroups(groups);
    }

    public void addGroups(List<FileType> groups) {
        fileTypeManager.addGroups(groups);
    }

    /**
     * Returns all format or group types (depending on whether the user has specified a custom grouping).
     */
    public List<FileType> getAllFileTypes() {
        return fileTypeManager.getAllFileTypes();
    }

    public FileType getFileType(String fileName) {
        return fileTypeManager.getFileType(fileName);
    }

    public FileTypeManager getFileTypeManager() {
        return fileTypeManager;
    }

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(LocalDateTime sinceDate) {
        this.sinceDate = sinceDate;
    }

    public LocalDateTime getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(LocalDateTime untilDate) {
        this.untilDate = untilDate;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorConfig.setAuthorDisplayName(author, displayName);
    }

    public void addAuthorNamesToAuthorMapEntry(Author author, String name) {
        authorConfig.addAuthorNamesToAuthorMapEntry(author, name);
    }

    public void addAuthorNamesToAuthorMapEntry(Author author, List<String> names) {
        authorConfig.addAuthorNamesToAuthorMapEntry(author, names);
    }

    public void addAuthorEmailsToAuthorMapEntry(Author author, List<String> emails) {
        authorConfig.addAuthorEmailsToAuthorMapEntry(author, emails);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRepoName() {
        return location.getRepoName();
    }

    public String getOutputFolderName() {
        return outputFolderName;
    }

    public void setStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
    }

    public void setFileSizeLimitIgnored(boolean isFileSizeLimitIgnored) {
        this.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
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

    public boolean isFileSizeLimitIgnored() {
        return isFileSizeLimitIgnored;
    }

    public boolean isIgnoredFileAnalysisSkipped() {
        return isIgnoredFileAnalysisSkipped;
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

    public boolean isFileSizeLimitOverriding() {
        return isFileSizeLimitOverriding;
    }

    public boolean isFindingPreviousAuthorsPerformed() {
        return isFindingPreviousAuthorsPerformed;
    }

    public AuthorConfiguration getAuthorConfig() {
        return authorConfig;
    }
}
