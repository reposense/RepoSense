package reposense.util;

import junit.framework.AssertionFailedError;

/**
 * A set of assertion methods useful for writing tests.
 */
public class AssertUtil {

    /**
     * Asserts that the {@code callable} throws the {@code expected} Exception.
     */
    public static void assertThrows(Class<? extends Throwable> expected, VoidCallable callable) {
        assertThrows(expected, null, callable);
    }

    /**
     * Asserts that the {@code callable} throws the {@code expectedException} and the {@code expectedMessage}.
     * <p>
     * If there's no need for the verification of the exception's error message, call
     * {@code assertThrows(Class<? extends Throwable>, VoidCallable)} instead.
     * {@see assertThrows(Class<? extends Throwable>, VoidCallable)}
     *
     * @throws AssertionFailedError if the {@code expectedException} is not thrown or {@code expectedMessage}
     * is not correct after calling {@code callable}.
     */
    public static void assertThrows(Class<? extends Throwable> expectedException, String expectedMessage,
            VoidCallable callable) {
        try {
            callable.call();
        } catch (Throwable actualException) {
            String errorMessage;

            if (!actualException.getClass().isAssignableFrom(expectedException)) {
                errorMessage = String.format("Expected exception thrown: %s, actual: %s",
                        expectedException.getName(), actualException.getClass().getName());
            } else if (expectedMessage != null && !expectedMessage.equals(actualException.getMessage())) {
                errorMessage = String.format(
                        "Expected message thrown: %s, actual: %s", expectedMessage, actualException.getMessage());
            } else {
                return;
            }

            throw new AssertionFailedError(errorMessage);
        }

        throw new AssertionFailedError(String.format(
                "Expected %s to be thrown, but nothing was thrown.", expectedException.getName()));
    }

    /**
     * Represents a function which does not return anything and may throw an exception.
     */
    @FunctionalInterface
    public interface VoidCallable {
        void call() throws Exception;
    }
}
