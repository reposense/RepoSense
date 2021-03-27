package reposense.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the data of list of repos that failed to analyze and the reasons for the failed operation.
 */
public class ErrorSummary {
    private static ErrorSummary instance = null;
    private static List<Map<String, String>> errorList = new ArrayList<>();

    public static ErrorSummary getInstance() {
        if (instance == null) {
            instance = new ErrorSummary();
        }
        return instance;
    }

    /**
     * Adds an error message for {@code repoName} with the reason {@code errorMessage} into a list of errors.
     */
    public void addErrorMessage(String repoName, String errorMessage) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("repoName", repoName);
        errorDetails.put("errorMessage", errorMessage);
        if (!errorList.contains(errorDetails)) {
            errorList.add(errorDetails);
        }
    }

    /**
     * Returns a compiled list of repos that failed to analyze and the corresponding reasons.
     */
    public List<Map<String, String>> getErrorList() {
        return errorList;
    }

    /**
     * Clears all previously stored list of errors in {@link ErrorSummary#errorList}.
     */
    public void clearErrorList() {
        errorList.clear();
    }
}
