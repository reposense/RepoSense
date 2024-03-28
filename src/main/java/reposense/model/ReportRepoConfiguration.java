package reposense.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single repository configuration in the overall report
 * configuration.
 */
public class ReportRepoConfiguration {
    public static final String DEFAULT_REPO = "https://github.com/user/repo";
    public static final String DEFAULT_BRANCH = "main";
    public static final List<String> DEFAULT_AUTHOR_NAMES = List.of(
            "johnDoe", "John Doe", "my home PC"
    );
    public static final String DEFAULT_BLURB = "This is a very long multiline blurb that spans multiple\n"
            + "lines. This would be interpreted literally (with whitespaces\n"
            + "and everything) and can be parsed as such.\n"
            + "\n"
            + "See https://yaml-multiline.info/ for more information on how you can\n"
            + "incorporate multiline strings into your config file!";

    public static final ReportRepoConfiguration DEFAULT_INSTANCE = new ReportRepoConfiguration();

    static {
        DEFAULT_INSTANCE.repo = DEFAULT_REPO;
        DEFAULT_INSTANCE.branch = DEFAULT_BRANCH;
        DEFAULT_INSTANCE.blurb = DEFAULT_BLURB;
        DEFAULT_INSTANCE.authorNames = DEFAULT_AUTHOR_NAMES;
    }

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("authorNames")
    private List<String> authorNames;

    @JsonProperty("blurb")
    private String blurb;

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    public String getBranch() {
        return branch == null ? DEFAULT_BRANCH : branch;
    }

    public List<String> getAuthorNames() {
        return authorNames == null ? DEFAULT_AUTHOR_NAMES : authorNames;
    }

    public String getBlurb() {
        return blurb == null ? DEFAULT_BLURB : blurb;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportRepoConfiguration) {
            ReportRepoConfiguration rrc = (ReportRepoConfiguration) obj;
            return rrc.getRepo().equals(this.getRepo())
                    && rrc.getBranch().equals(this.getBranch())
                    && rrc.getAuthorNames().equals(this.getAuthorNames())
                    && rrc.getBlurb().equals(this.getBlurb());
        }

        return false;
    }
}
