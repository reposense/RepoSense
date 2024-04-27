package reposense.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.git.GitBranch;
import reposense.git.exception.GitBranchException;
import reposense.parser.exceptions.ConfigurationBuildException;
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
    private String branch = DEFAULT_BRANCH;
    private String displayName;
    private String outputFolderName;
    private transient String extraOutputFolderName = DEFAULT_EXTRA_OUTPUT_FOLDER_NAME;
    private transient ZoneId zoneId;
    private transient LocalDateTime sinceDate;
    private transient LocalDateTime untilDate;
    private transient String repoFolderName;

    private transient FileTypeManager fileTypeManager = new FileTypeManager(Collections.emptyList());
    private transient List<String> ignoreGlobList = new ArrayList<>();
    private transient List<String> ignoredAuthorsList = new ArrayList<>();
    private transient AuthorConfiguration authorConfig;
    private transient boolean isStandaloneConfigIgnored = false;
    private transient boolean isFileSizeLimitIgnored = false;
    private transient List<CommitHash> ignoreCommitList = Collections.emptyList();
    private transient boolean isLastModifiedDateIncluded;
    private transient boolean isShallowCloningPerformed = false;
    private transient boolean isFindingPreviousAuthorsPerformed = false;
    private transient boolean isFormatsOverriding = false;
    private transient boolean isIgnoreGlobListOverriding;
    private transient boolean isIgnoreCommitListOverriding = false;
    private transient boolean isIgnoredAuthorsListOverriding;
    private transient long fileSizeLimit = DEFAULT_FILE_SIZE_LIMIT;
    private transient boolean isFileSizeLimitOverriding = false;
    private transient boolean isIgnoredFileAnalysisSkipped = false;

    /**
     * Constructs an empty instance of {@code RepoConfiguration}, which is used by the {@code Builder}
     * to construct new {@code RepoConfiguration} instances.
     */
    private RepoConfiguration() {}

    /**
     * Builds the necessary configurations for RepoConfiguration.
     * Obeys the Builder pattern as described in {@link CliArguments}.
     */
    public static class Builder {
        private String displayName;
        private String outputFolderName;
        private String repoFolderName;
        private RepoConfiguration repoConfiguration;

        /**
         * Returns an empty instance of the RepoConfiguration Builder.
         */
        public Builder() {
            this.repoConfiguration = new RepoConfiguration();
        }

        /**
         * Processes the author configuration of the repository.
         */
        private void processAuthor() {
            this.repoConfiguration.authorConfig = new AuthorConfiguration(
                    this.repoConfiguration.location,
                    this.repoConfiguration.branch);
        }

        /**
         * Processes the branch of the repository.
         */
        private void processBranch() {
            this.repoConfiguration.branch = this.repoConfiguration.location.isEmpty()
                    ? DEFAULT_BRANCH
                    : this.repoConfiguration.branch;
        }

        /**
         * Processes the relevant names of the repository configs.
         */
        private void processNames() {
            String repoName = this.repoConfiguration.location.getRepoName();
            String org = this.repoConfiguration.location.getOrganization();

            String defaultDisplayName = repoName + "[" + this.repoConfiguration.branch + "]";
            String defaultOutputFolderName = repoName + "_" + this.repoConfiguration.branch;
            String defaultRepoFolderName = repoName;

            if (!org.isEmpty()) {
                defaultDisplayName = org + "/" + defaultDisplayName;
                defaultRepoFolderName = org + "_" + defaultRepoFolderName;
                defaultOutputFolderName = org + "_" + defaultOutputFolderName;
            }

            this.repoConfiguration.displayName = Optional.ofNullable(this.displayName)
                    .orElse(defaultDisplayName);
            this.repoConfiguration.outputFolderName = Optional.ofNullable(this.outputFolderName)
                    .orElse(defaultOutputFolderName);
            this.repoConfiguration.repoFolderName = Optional.ofNullable(this.repoFolderName)
                    .orElse(defaultRepoFolderName);
        }

        /**
         * Updates the {@code location} for {@code RepoConfiguration}.
         *
         * @param location A repository location.
         * @return This builder object
         */
        public Builder location(RepoLocation location) {
            this.repoConfiguration.location = location;
            return this;
        }

        /**
         * Updates the {@code branch} for {@code RepoConfiguration}.
         *
         * @param branch Branch of the repository of interest.
         * @return This builder object.
         */
        public Builder branch(String branch) {
            this.repoConfiguration.branch = branch;
            return this;
        }

        /**
         * Updates the {@code displayName} for {@code RepoConfiguration}.
         *
         * @param displayName Display name of the repository.
         * @return This builder object.
         */
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            this.repoConfiguration.displayName = displayName;
            return this;
        }

        /**
         * Updates the {@code outputFolderName} for {@code RepoConfiguration}.
         *
         * @param outputFolderName Output folder name of the repository.
         * @return This builder object.
         */
        public Builder outputFolderName(String outputFolderName) {
            this.outputFolderName = outputFolderName;
            this.repoConfiguration.outputFolderName = outputFolderName;
            return this;
        }

        /**
         * Updates the {@code extraOutputFolderName} for {@code RepoConfiguration}.
         *
         * @param extraOutputFolderName Extra output folder name of the repository.
         * @return This builder object.
         */
        public Builder extraOutputFolderName(String extraOutputFolderName) {
            this.repoConfiguration.extraOutputFolderName = extraOutputFolderName;
            return this;
        }

        /**
         * Updates the {@code zoneId} for {@code RepoConfiguration}.
         *
         * @param zoneId Time-zone of the repository.
         * @return This builder object.
         */
        public Builder zoneId(ZoneId zoneId) {
            this.repoConfiguration.zoneId = zoneId;
            return this;
        }

        /**
         * Updates the {@code sinceDate} for {@code RepoConfiguration}.
         *
         * @param sinceDate Starting date of analysis.
         * @return This builder object.
         */
        public Builder sinceDate(LocalDateTime sinceDate) {
            this.repoConfiguration.sinceDate = sinceDate;
            return this;
        }

        /**
         * Updates the {@code untilDate} for {@code RepoConfiguration}.
         *
         * @param untilDate Ending date of analysis.
         * @return This builder object.
         */
        public Builder untilDate(LocalDateTime untilDate) {
            this.repoConfiguration.untilDate = untilDate;
            return this;
        }

        /**
         * Updates the {@code repoFolderName} for {@code RepoConfiguration}.
         *
         * @param repoFolderName Folder name of the repository.
         * @return This builder object.
         */
        public Builder repoFolderName(String repoFolderName) {
            this.repoFolderName = repoFolderName;
            this.repoConfiguration.repoFolderName = repoFolderName;
            return this;
        }

        /**
         * Updates the {@code fileTypeManager} for {@code RepoConfiguration}.
         *
         * @param fileTypes List of file types and groupings permitted.
         * @return This builder object.
         */
        public Builder fileTypeManager(List<FileType> fileTypes) {
            this.repoConfiguration.fileTypeManager = new FileTypeManager(fileTypes);
            return this;
        }

        /**
         * Updates the {@code ignoreGlobList} for {@code RepoConfiguration}.
         *
         * @param ignoredGlobList List of glob patterns to ignore.
         * @return This builder object.
         */
        public Builder ignoreGlobList(List<String> ignoredGlobList) {
            this.repoConfiguration.ignoreGlobList = ignoredGlobList;
            return this;
        }

        /**
         * Updates the {@code ignoredAuthorsList} for {@code RepoConfiguration}.
         *
         * @param ignoredAuthorsList List of authors to ignore.
         * @return This builder object.
         */
        public Builder ignoredAuthorsList(List<String> ignoredAuthorsList) {
            this.repoConfiguration.ignoredAuthorsList = ignoredAuthorsList;
            return this;
        }

        /**
         * Updates the {@code authorConfig} for {@code RepoConfiguration}.
         *
         * @param authorConfig Author configuration information of the repository.
         * @return This builder object.
         */
        public Builder authorConfig(AuthorConfiguration authorConfig) {
            this.repoConfiguration.authorConfig = authorConfig;
            return this;
        }

        /**
         * Updates the {@code isStandaloneConfigIgnored} for {@code RepoConfiguration}.
         *
         * @param isStandaloneConfigIgnored Checks if standalone config is ignored.
         * @return This builder object.
         */
        public Builder isStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
            this.repoConfiguration.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
            return this;
        }

        /**
         * Updates the {@code isFileSizeLimitIgnored} for {@code RepoConfiguration}.
         *
         * @param isFileSizeLimitIgnored Checks if file size limit is ignored.
         * @return This builder object.
         */
        public Builder isFileSizeLimitIgnored(boolean isFileSizeLimitIgnored) {
            this.repoConfiguration.isFileSizeLimitIgnored = isFileSizeLimitIgnored;
            return this;
        }

        /**
         * Updates the {@code ignoreCommitList} for {@code RepoConfiguration}.
         *
         * @param ignoreCommitList List of commits to ignore.
         * @return This builder object.
         */
        public Builder ignoreCommitList(List<CommitHash> ignoreCommitList) {
            this.repoConfiguration.ignoreCommitList = ignoreCommitList;
            return this;
        }

        /**
         * Updates the {@code isLastModifiedDateIncluded} for {@code RepoConfiguration}.
         *
         * @param isLastModifiedDateIncluded Checks if last modified date is included.
         * @return This builder object.
         */
        public Builder isLastModifiedDateIncluded(boolean isLastModifiedDateIncluded) {
            this.repoConfiguration.isLastModifiedDateIncluded = isLastModifiedDateIncluded;
            return this;
        }

        /**
         * Updates the {@code isShallowCloningPerformed} for {@code RepoConfiguration}.
         *
         * @param isShallowCloningPerformed Checks if shallow cloning is performed.
         * @return This builder object.
         */
        public Builder isShallowCloningPerformed(boolean isShallowCloningPerformed) {
            this.repoConfiguration.isShallowCloningPerformed = isShallowCloningPerformed;
            return this;
        }

        /**
         * Updates the {@code isFindingPreviousAuthorsPerformed} for {@code RepoConfiguration}.
         *
         * @param isFindingPreviousAuthorsPerformed Checks if finding previous authors is performed.
         * @return This builder object.
         */
        public Builder isFindingPreviousAuthorsPerformed(boolean isFindingPreviousAuthorsPerformed) {
            this.repoConfiguration.isFindingPreviousAuthorsPerformed = isFindingPreviousAuthorsPerformed;
            return this;
        }

        /**
         * Updates the {@code isFormatsOverriding} for {@code RepoConfiguration}.
         *
         * @param isFormatsOverriding Checks if file formats are overridden.
         * @return This builder object.
         */
        public Builder isFormatsOverriding(boolean isFormatsOverriding) {
            this.repoConfiguration.isFormatsOverriding = isFormatsOverriding;
            return this;
        }

        /**
         * Updates the {@code isIgnoreGlobListOverriding} for {@code RepoConfiguration}.
         *
         * @param isIgnoreGlobListOverriding Checks if the list of ignored glob is overridden.
         * @return This builder object.
         */
        public Builder isIgnoreGlobListOverriding(boolean isIgnoreGlobListOverriding) {
            this.repoConfiguration.isIgnoreGlobListOverriding = isIgnoreGlobListOverriding;
            return this;
        }

        /**
         * Updates the {@code isIgnoreCommitListOverriding} for {@code RepoConfiguration}.
         *
         * @param isIgnoreCommitListOverriding Checks if the list of ignored commits is overridden.
         * @return This builder object.
         */
        public Builder isIgnoreCommitListOverriding(boolean isIgnoreCommitListOverriding) {
            this.repoConfiguration.isIgnoreCommitListOverriding = isIgnoreCommitListOverriding;
            return this;
        }

        /**
         * Updates the {@code isFileSizeLimitOverriding} for {@code RepoConfiguration}.
         *
         * @param isIgnoredAuthorsListOverriding Checks if the list of ignored authors is overridden.
         * @return This builder object.
         */
        public Builder isIgnoredAuthorsListOverriding(boolean isIgnoredAuthorsListOverriding) {
            this.repoConfiguration.isIgnoredAuthorsListOverriding = isIgnoredAuthorsListOverriding;
            return this;
        }

        /**
         * Updates the {@code fileSizeLimit} for {@code RepoConfiguration}.
         *
         * @param fileSizeLimit File size limit of the repository.
         * @return This builder object.
         */
        public Builder fileSizeLimit(long fileSizeLimit) {
            this.repoConfiguration.fileSizeLimit = fileSizeLimit;
            return this;
        }

        /**
         * Updates the {@code isFileSizeLimitOverriding} for {@code RepoConfiguration}.
         *
         * @param isFileSizeLimitOverriding Checks if the file size limit is overridden.
         * @return This builder object.
         */
        public Builder isFileSizeLimitOverriding(boolean isFileSizeLimitOverriding) {
            this.repoConfiguration.isFileSizeLimitOverriding = isFileSizeLimitOverriding;
            return this;
        }

        /**
         * Updates the {@code isIgnoredFileAnalysisSkipped} for {@code RepoConfiguration}.
         *
         * @param isIgnoredFileAnalysisSkipped Checks if the analysis of ignored files is skipped.
         * @return This builder object.
         */
        public Builder isIgnoredFileAnalysisSkipped(boolean isIgnoredFileAnalysisSkipped) {
            this.repoConfiguration.isIgnoredFileAnalysisSkipped = isIgnoredFileAnalysisSkipped;
            return this;
        }

        /**
         * Builds the {@code RepoConfiguration} object with the necessary configurations.
         *
         * @return {@code RepoConfiguration}.
         * @throws ConfigurationBuildException if there was an issue building the {@code RepoConfiguration}
         *     object.
         */
        public RepoConfiguration build() {
            if (!validate()) {
                throw new ConfigurationBuildException();
            }

            this.processAuthor();
            this.processBranch();
            this.processNames();

            // save a reference to the current built object
            RepoConfiguration toReturn = this.repoConfiguration;

            // reset the internal reference to avoid aliasing
            this.repoConfiguration = new RepoConfiguration();

            // return the reference to the built RepoConfiguration object
            return toReturn;
        }

        /**
         * Checks if the current {@code RepoConfiguration} object contains all the necessary parameters
         * needed to build successfully.
         *
         * @return true if the {@code RepoConfiguration} object contains all the necessary parameters else false
         */
        private boolean validate() {
            return Optional.ofNullable(this.repoConfiguration.location).isPresent();
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
