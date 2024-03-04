package reposense.util.function;

/**
 * Functional interface that defines a Supplier that can throw
 * an Exception on execution.
 *
 * @param <T> The Type of the item that this {@code ThrowableSupplier} can
 *           supply.
 * @param <E> The Type of the Exception that this {@code ThrowableSupplier}
 *           can throw.
 */
@FunctionalInterface
public interface ThrowableSupplier<T, E extends Exception> {
    T produce() throws E;
}
