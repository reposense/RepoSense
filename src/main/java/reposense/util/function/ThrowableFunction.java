package reposense.util.function;

/**
 * Functional interface that defines a Supplier that can throw
 * an Exception on execution.
 *
 * @param <T> The Input Type of the item that this {@code ThrowableFunction}.
 * @param <U> The Return Type of this {@code ThrowableFunction}.
 * @param <E> The Type of the Exception that this {@code ThrowableFunction}.
 *           can throw.
 */
@FunctionalInterface
public interface ThrowableFunction<T, U, E extends Exception> {
    U apply(T t) throws E;
}
