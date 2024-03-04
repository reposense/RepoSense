package reposense.util.function;

@FunctionalInterface
public interface ThrowableFunction<T, U, E extends Exception> {
    U apply(T t) throws E;
}
