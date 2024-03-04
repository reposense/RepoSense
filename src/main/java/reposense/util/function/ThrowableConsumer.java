package reposense.util.function;

@FunctionalInterface
public interface ThrowableConsumer<T, E extends Exception> {
    void consume(T t) throws E;
}
