package reposense.util;

/**
 * Keeps track of progress of analyses of repos.
 */
public class ProgressTracker {
    private int current = 0;
    private final int total;

    public ProgressTracker(int total) {
        this.total = total;
    }

    public void incrementProgress() {
        current += 1;
        assert(current <= total);
    }

    public String getProgress() {
        return "[" + current + "/" + total + "]";
    }
}
