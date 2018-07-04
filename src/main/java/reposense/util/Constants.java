package reposense.util;

public class Constants {
    public static final String REPOS_ADDRESS = "repos";
    public static final String STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS = "repo_report";
    public static final String TEMPLATE_ZIP_ADDRESS = "/templateZip.zip";
    public static final String GITHUB_URL_ROOT = "https://github.com/";
    private static String[] docTypes = {"java", "adoc"};

    public static String[] getDocTypes() {
        return docTypes;
    }

    public static void setDocTypes(String[] docTypes) {
        Constants.docTypes = docTypes;
    }
}
