package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matanghao1 on 5/6/17.
 */
public class Constants {
    public static final String REPORT_ADDRESS = "report";
    public static final String REPOS_ADDRESS = "repos";
    public static final String STATIC_LIB_TEMPLATE_ADDRESS = "template/static/";
    public static final String STATIC_SUMMARY_REPORT_FILE_ADDRESS = "template/index.html";
    public static final String STATIC_SUMMARY_REPORT_DETAIL_FILE_ADDRESS = "template/detail.html";


    public static final String STATIC_INDIVIDUAL_REPORT_TEMPLATE_ADDRESS = "template/repo_report/";


    public static final String GITHUB_URL_ROOT = "https://github.com/";


    public static final String LOG_SPLITTER = "\\|";
    public static final DateFormat GIT_ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat REPORT_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    public static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";


    public static final String CSV_SPLITTER = ",";


}
