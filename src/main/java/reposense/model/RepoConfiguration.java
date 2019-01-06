package reposense.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;

import reposense.system.LogsManager;
import reposense.util.FileUtil;

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
    private transient List<Format> formats;
    private transient int commitNum = 1;
    private transient List<String> ignoreGlobList = new ArrayList<>();
    private transient List<Author> authorList = new ArrayList<>();
    private transient TreeMap<String, Author> authorAliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();
    private transient boolean isStandaloneConfigIgnored;
    private transient List<CommitHash> ignoreCommitList;

    public RepoConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public RepoConfiguration(RepoLocation location, String branch) {
        this(location, branch, Collections.emptyList(), Collections.emptyList(), false, Collections.emptyList());
    }

    public RepoConfiguration(RepoLocation location, String branch, List<Format> formats, List<String> ignoreGlobList,
            boolean isStandaloneConfigIgnored, List<CommitHash> ignoreCommitList) {
        this.location = location;
        this.branch = location.isEmpty() ? "" : branch;
        this.ignoreGlobList = ignoreGlobList;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
        this.formats = formats;
        this.ignoreCommitList = ignoreCommitList;

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
     * Merges a {@code RepoConfiguration} from {@code repoConfigs} with another from {@code authorConfigs}
     * if {@code location} and {@code branch} matches.
     */
    public static void merge(List<RepoConfiguration> repoConfigs, List<RepoConfiguration> authorConfigs) {
        for (RepoConfiguration authorConfig : authorConfigs) {
            if (authorConfig.location.isEmpty()) {
                for (RepoConfiguration repoConfig : repoConfigs) {
                    repoConfig.addAuthors(authorConfig.getAuthorList());
                }
                continue;
            }

            int index = repoConfigs.indexOf(authorConfig);

            if (index == -1) {
                logger.warning(String.format(
                        "Repository %s is not found in repo-config.csv.", authorConfig.getLocation()));
                continue;
            }

            RepoConfiguration repoConfig = repoConfigs.get(index);
            repoConfig.addAuthors(authorConfig.getAuthorList());
        }
    }

    /**
     * Sets {@code formats} to {@code RepoConfiguration} in {@code configs} if its format list is empty.
     */
    public static void setFormatsToRepoConfigs(List<RepoConfiguration> configs, List<Format> formats) {
        configs.stream().filter(config -> config.getFormats().isEmpty())
                        .forEach(config -> config.setFormats(formats));
    }

    /**
     * Clears authors information and use the information provided from {@code standaloneConfig}.
     */
    public void update(StandaloneConfig standaloneConfig) {
        List<Author> newAuthorList = new ArrayList<>();
        TreeMap<String, Author> newAuthorAliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<Author, String> newAuthorDisplayNameMap = new HashMap<>();
        List<String> newIgnoreGlobList = standaloneConfig.getIgnoreGlobList();

        for (StandaloneAuthor sa : standaloneConfig.getAuthors()) {
            Author author = new Author(sa);
            author.appendIgnoreGlobList(newIgnoreGlobList);

            newAuthorList.add(author);
            newAuthorDisplayNameMap.put(author, author.getDisplayName());
            List<String> aliases = new ArrayList<>(author.getAuthorAliases());
            aliases.add(author.getGitId());
            aliases.forEach(alias -> newAuthorAliasMap.put(alias, author));
        }

        Format.validateFormats(standaloneConfig.getFormats());
        CommitHash.validateCommits(standaloneConfig.getIgnoreCommitList());

        // only assign the new values when all the fields in {@code standaloneConfig} pass the validations.
        authorList = newAuthorList;
        authorAliasMap = newAuthorAliasMap;
        authorDisplayNameMap = newAuthorDisplayNameMap;
        ignoreGlobList = newIgnoreGlobList;
        formats = Format.convertStringsToFormats(standaloneConfig.getFormats());
        ignoreCommitList = CommitHash.convertStringsToCommits(standaloneConfig.getIgnoreCommitList());
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

        return hashCode() == ((RepoConfiguration) other).hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, branch);
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
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
        return authorList;
    }

    public void setAuthorDetails(Author author) {
        // Set GitHub Id as default alias
        addAuthorAliases(author, Arrays.asList(author.getGitId()));

        addAuthorAliases(author, author.getAuthorAliases());

        setAuthorDisplayName(author, author.getDisplayName());

        // Propagate RepoConfiguration IgnoreGlobList to Author
        author.appendIgnoreGlobList(this.getIgnoreGlobList());
    }

    public void addAuthor(Author author) {
        authorList.add(author);
        setAuthorDetails(author);
    }

    public boolean containsAuthor(Author author) {
        return authorList.contains(author);
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
        authorAliasMap.clear();
        authorDisplayNameMap.clear();

        authorList.forEach(author -> setAuthorDetails(author));
    }

    public void addAuthors(List<Author> authorList) {
        for (Author author: authorList) {
            if (containsAuthor(author)) {
                logger.warning(String.format("Skipping author as %s already in repository %s",
                        author.getGitId(), getDisplayName()));
                continue;
            }
            addAuthor(author);
        }
    }

    public TreeMap<String, Author> getAuthorAliasMap() {
        return authorAliasMap;
    }

    public void setAuthorAliasMap(TreeMap<String, Author> authorAliasMap) {
        this.authorAliasMap = authorAliasMap;
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
        authorDisplayNameMap.put(author, displayName);
    }

    public void addAuthorAliases(Author author, List<String> aliases) {
        aliases.forEach(alias -> authorAliasMap.put(alias, author));
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRepoName() {
        return location.getRepoName();
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
}
