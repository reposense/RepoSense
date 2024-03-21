package reposense.util.function;

/**
 * Functional interface that defines a Consumer that can throw
 * an Exception on execution.
 *
 * @param <T> The Type of the item that this {@code ThrowableConsumer} can
 *           consume.
 * @param <E> The Type of the Exception that this {@code ThrowableConsumer}
 *           can throw.
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {
    void consume(T t) throws E;
}
