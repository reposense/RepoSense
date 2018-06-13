package reposense.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class Constants {
    public static final String REPOS_ADDRESS = "repos";
    public static final String STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS = "repo_report";
    public static final String TEMPLATE_ZIP_ADDRESS = "/templateZip.zip";
    public static final String GITHUB_URL_ROOT = "https://github.com/";

    public static final String LOG_SPLITTER = "\\|";
    public static final DateFormat GIT_ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat REPORT_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static final DateFormat GIT_LOG_SINCE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+08:00");
    public static final DateFormat GIT_LOG_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59+08:00");
    public static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd";

    public static final String REUSED_TAG = "//@reused";
}
