package reposense.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.ParseException;
import reposense.util.FileUtil;

public class RepoConfiguration {
    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?)\\.git$");

    private String location;
    private String repoName;
    private String branch;
    private String displayName;
    private Date sinceDate;
    private Date untilDate;

    private transient boolean needCheckStyle = false;
    private transient List<String> formats;
    private transient int commitNum = 1;
    private transient List<String> ignoreDirectoryList = new ArrayList<>();
    private transient List<Author> authorList = new ArrayList<>();
    private transient TreeMap<String, Author> authorAliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();
    private transient boolean annotationOverwrite = true;


    /**
     * Creates a {@code RepoConfiguration}.
     * {@code location} must be a Github .git link or a {@code Path}.
     */
    public RepoConfiguration(String location, String branch) throws ParseException {
        this.location = location;
        this.branch = branch;

        verifyLocation(location);
        Matcher matcher = GIT_REPOSITORY_LOCATION_PATTERN.matcher(location);

        if (matcher.matches()) {
            String organization = matcher.group("org");
            repoName = matcher.group("repoName");
            displayName = organization + "_" + repoName + "_" + branch;
        } else {
            repoName = Paths.get(location).getFileName().toString();
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
     * Sets all {@code RepoConfiguration} in {@code configs} to have {@code formats} set.
     */
    public static void setFormatsToRepoConfigs(List<RepoConfiguration> configs, List<String> formats) {
        configs.forEach(config -> config.setFormats(formats));
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
        return location.hashCode();
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getRepoRoot() {
        return FileUtil.getRepoDirectory(displayName, getRepoName());
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
        this.branch = branch;
    }

    public boolean isAnnotationOverwrite() {
        return annotationOverwrite;
    }

    public void setAnnotationOverwrite(boolean annotationOverwrite) {
        this.annotationOverwrite = annotationOverwrite;
    }

    public List<String> getIgnoreDirectoryList() {
        return ignoreDirectoryList;
    }

    public void setIgnoreDirectoryList(List<String> ignoreDirectoryList) {
        this.ignoreDirectoryList = ignoreDirectoryList;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
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

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        this.formats = formats;
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorDisplayNameMap.put(author, displayName);
    }

    public void setAuthorAliases(Author author, String... aliases) {
        for (String alias : aliases) {
            authorAliasMap.put(alias, author);
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLocation() {
        return location;
    }

    public String getRepoName() {
        return repoName;
    }

    private void verifyLocation(String location) throws ParseException {
        Path pathLocation = Paths.get(location);
        boolean isPathLocation = Files.exists(pathLocation);
        boolean isGitLocation = false;

        try {
            new URL(location);
            isGitLocation = true;
        } catch (MalformedURLException mue) {
            // Ignore exception
        }

        if (!isPathLocation && !isGitLocation) {
            throw new ParseException("Location is invalid");
        }
    }
}
