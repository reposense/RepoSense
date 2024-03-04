package reposense.util.function;

@FunctionalInterface
public interface ThrowableSupplier<T, E extends Exception> {
    T produce() throws E;
}
