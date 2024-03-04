package reposense.util.function;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An {@code Optional<T>} monad that enables both an empty and a fail option.
 *
 * @param <T> Generic Type {@code T}, unbounded to any type.
 */
public abstract class FailableOptional<T> {
    /**
     * Creates a new {@code FailableOptional<T>} object. {@code FailableOptional<T>}
     * can contain {@code null}.
     *
     * @param item Item of type {@code T}.
     * @param <T> Generic Type {@code T}.
     * @return An instance of {@code FailableOptional<T>} instance.
     */
    public static <T> FailableOptional<T> of(T item) {
        return new Present<>(item);
    }

    /**
     * Creates a new {@code FailableOptional<T>} object.
     * This method can allow null values to be stored within the {@code FailableOptional<T>} object.
     *
     * @param supplier Produces an object of type {@code T}, which can also be {@code null}.
     *                 This {@code Supplier} cannot throw any Exceptions.
     * @param <T> Generic type {@code T}.
     * @return {@code FailableOptional<T>} object wrapping an object of type {@code T}.
     */
    public static <T> FailableOptional<T> of(Supplier<T> supplier) {
        return of(supplier.get());
    }

    /**
     * Creates a new {@code FailableOptional<T>} object.
     * This method can allow null values to be stored within the {@code FailableOptional<T>} object.
     *
     * @param supplier Produces an object of type {@code T}, which can also be {@code null}.
     *                 This {@code ThrowableSupplier} can throw Exceptions and will be automatically
     *                 converted into a failed instance of {@code ThrowaableSupplier}.
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded to {@code Exception}.
     * @return {@code FailableOptional<T>} object wrapping an object of type {@code T} or a failed
     *     instance of {@code FailableOptional<T>}.
     */
    public static <T, E extends Exception> FailableOptional<T> of(ThrowableSupplier<T, E> supplier) {
        try {
            return of(supplier.produce());
        } catch (Exception e) {
            return ofFailure(e);
        }
    }

    /**
     * Creates a new {@code FailableOptional<T>} object.
     * {@code null} is automatically converted into an empty instance of {@code FailableOptional<T>} object.
     *
     * @param item Item of type {@code T}.
     * @param <T> Generic Type {@code T}.
     * @return An instance of {@code FailableOptional<T>} instance.
     */
    public static <T> FailableOptional<T> ofNullable(T item) {
        return item == null ? ofAbsent() : of(item);
    }

    /**
     * Creates a new {@code FailableOptional<T>} object.
     * This method converts {@code null} into an empty instance of {@code FailableOptional<T>}.
     *
     * @param supplier Produces an object of type {@code T}, which can also be {@code null}.
     *                 This {@code Supplier} cannot throw any Exceptions.
     * @param <T> Generic type {@code T}.
     * @return {@code FailableOptional<T>} object wrapping an object of type {@code T} or an empty
     *     instance of {@code FailableOptional<T>}.
     */
    public static <T> FailableOptional<T> ofNullable(Supplier<T> supplier) {
        return ofNullable(supplier.get());
    }

    /**
     * Creates a new {@code FailableOptional<T>} object.
     * This method converts {@code null} into an empty instance of {@code FailableOptional<T>}.
     *
     * @param supplier Produces an object of type {@code T}, which can also be {@code null}.
     *                 This {@code ThrowableSupplier} can throw Exceptions and will be automatically
     *                 converted into a failed instance of {@code ThrowaableSupplier}.
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded to {@code Exception}.
     * @return {@code FailableOptional<T>} object wrapping an object of type {@code T} or an empty
     *     instance of {@code FailableOptional<T>}.
     */
    public static <T, E extends Exception> FailableOptional<T> ofNullable(ThrowableSupplier<T, E> supplier) {
        try {
            return ofNullable(supplier.produce());
        } catch (Exception e) {
            return ofFailure(e);
        }
    }

    /**
     * Creates an empty instance of {@code FailableOptional<T>}.
     *
     * @param <T> Generic Type {@code T}.
     * @return Empty instance of {@code FailableOptional<T>}.
     */
    public static <T> FailableOptional<T> ofAbsent() {
        return new Absent<>();
    }

    /**
     * Creates a failed instance of {@code FailableOptional<T>}.
     *
     * @param <T> Generic Type {@code T}.
     * @return Failed instance of {@code FailableOptional<T>}.
     */
    public static <T> FailableOptional<T> ofFailure(Exception e) {
        return new Fail<>(e);
    }

    /**
     * Executes the provided {@code Runner} if this current instance of
     * {@code FailableOptional<T>} contains a value.
     *
     * @param runner {@code Runner} instance that executes if this {@code FailableOptional<T>}
     *                             contains a value
     * @return This {@code FailableOptional<T>} object
     */
    public abstract FailableOptional<T> ifPresent(Runnable runner);

    /**
     * Executes the provided {@code ThrowableConsumer<T, E>} if this current instance of
     * {@code FailableOptional<T>} contains a value.
     *
     * @param consumer {@code ThrowableConsumer<T, E>} instance that consumes the stored
     *                                                value if present.
     * @param <E> Generic Type {@code E} bounded by Exception.
     * @return This {@code ThrowableConsumer<T, E>} instance.
     */
    public abstract <E extends Exception> FailableOptional<T> ifPresent(ThrowableConsumer<T, E> consumer);

    /**
     * Executes the provided {@code Runner} if this current instance of
     * {@code FailableOptional<T>} does not contains a value.
     *
     * @param runner {@code Runner} instance that executes if this {@code FailableOptional<T>}
     *                             does not contains a value.
     * @return This {@code FailableOptional<T>} object.
     */
    public abstract FailableOptional<T> ifAbsent(Runnable runner);

    /**
     * Executes the provided {@code ThrowableConsumer<T, E>} if this current instance of
     * {@code FailableOptional<T>} does not contains a value.
     *
     * @param consumer {@code ThrowableConsumer<T, E>} instance that attempts to consume
     *                                                an object of type {@code T}.
     * @param <E> Generic Type {@code E} bounded by Exception.
     * @return This {@code ThrowableConsumer<T, E>} instance.
     */
    public abstract <E extends Exception> FailableOptional<T> ifAbsent(ThrowableConsumer<T, E> consumer);

    /**
     * Executes the provided {@code Runner} if this current instance of
     * {@code FailableOptional<T>} has failed.
     *
     * @param runner {@code Runner} instance that executes if this {@code FailableOptional<T>}
     *                             has failed.
     * @return This {@code FailableOptional<T>} object.
     */
    public abstract FailableOptional<T> ifFail(Runnable runner);

    /**
     * Executes the provided {@code ThrowableConsumer<T, E>} if this current instance of
     * {@code FailableOptional<T>} has failed.
     *
     * @param consumer {@code ThrowableConsumer<T, E>} instance that attempts to consume
     *                                                an object of type {@code T}.
     * @param <E> Generic Type {@code E} bounded by Exception.
     * @return This {@code ThrowableConsumer<T, E>} instance.
     */
    public abstract <E extends Exception, F extends Exception> FailableOptional<T> ifFail(
            ThrowableConsumer<? super E, ? extends F> consumer);

    /**
     * Maps the stored value in this {@code FailableOptional<T>} into another
     * {@code FailableOptional<U>} containing items of Type {@code U}.
     * If this {@code FailableOptional<T>} contains nothing or has failed,
     * this instance is returned as is.
     *
     * @param function {@code ThrowableFunction<T, U, E>} that takes an object of Type {@code T}
     *                                                   and returns Type {@code U} and might throw
     *                                                   Exception of Type {@code E}.
     * @param <U> Generic Type {@code U}, representing the return type of the new {@code FailableOptional<U>}.
     * @param <E> Generic Type {@code E}, representing the Type of the Exception that might be thrown.
     * @return This instance, if this instance is empty or has failed, otherwise, a new mapped instance
     *     of {@code FailableOptional<U>}.
     */
    public abstract <U, E extends Exception> FailableOptional<U> map(ThrowableFunction<? super T,
            ? extends U, E> function);

    /**
     * Maps the stored value into another new instance of {@code FailableOptional<U>}. Unlike {@code map},
     * this method requires that functions itself return the new instance of {@code FailableOptional<U>}.
     *
     * @param function {@code ThrowableFunction<T, U, E>} that takes an object of Type {@code T}
     *                                                    and returns Type {@code FailableOptional<U>} and
     *                                                    might throw Exception of Type {@code E}.
     * @param <U> Generic Type {@code U}, representing the return type of the new {@code FailableOptional<U>}.
     * @param <E> Generic Type {@code E}, representing the Type of the Exception that might be thrown.
     * @return This instance, if this instance is empty or has failed, otherwise, a new mapped instance
     *      of {@code FailableOptional<U>}.
     */
    public abstract <U, E extends Exception> FailableOptional<U> flatMap(
            ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function);

    /**
     * Checks if the stored item fulfills the predicate, and if so, return this instance, and if not,
     * return an empty instance of {@code FailableOptional<T>}.
     *
     * @param predicate {@code Predicate} that tests an item of Type {@code T}.
     * @return This instance if the predicate evaluates to true, else an empty instance of
     *     {@code FailableOptional<T>}.
     */
    public abstract FailableOptional<T> filter(Predicate<T> predicate);

    /**
     * Returns the item stored in this {@code FailableOptional<T>}.
     *
     * @return Stored item of Type {@code T}
     * @throws NoSuchElementException if this {@code FailableOptional<T>} does not contain any values
     *     or has failed.
     */
    public abstract T get() throws NoSuchElementException;

    /**
     * Returns the item stored in this {@code FailableOptional<T>}, and if
     * there is no such value, return the input parameter {@code item}.
     *
     * @param item The value to return if this {@code FailableOptional<T>} is empty or has failed.
     * @return The stored item or the input item.
     */
    public abstract T orElse(T item);

    /**
     * Returns the item stored in this {@code FailableOptional<T>}, and if
     * there is no such value, return the input parameter {@code item}.
     *
     * @param e The Exception to throw if this {@code FailableOptional<T>} is empty or has failed.
     * @return The stored item or throws an Exception.
     * @throws Exception if there are no items stored in this {@code FailableOptional<T>} instance.
     */
    public abstract T orElseThrow(Exception e) throws Exception;

    /**
     * Checks if this {@code FailableOptional<T>} instance contains a value.
     *
     * @return true if this instance of {@code FailableOptional<T>} is not empty, false otherwise.
     */
    public abstract boolean isPresent();

    /**
     * Checks if this {@code FailableOptional<T>} instance does not contains a value.
     *
     * @return true if this instance of {@code FailableOptional<T>} is empty, false otherwise.
     */
    public abstract boolean isAbsent();

    /**
     * Checks if this {@code FailableOptional<T>} instance has failed.
     *
     * @return true if this instance of {@code FailableOptional<T>} has failed, false otherwise.
     */
    public abstract boolean isFail();

    /**
     * Verifies that a failed instance of {@code FailableOptional<T>} has failed due to the
     * input list of Exceptions to check.
     *
     * @param exList List of Exception Classes to verify the failure Exception cause.
     * @return This instance if this instance has failed and is due to any one of the reasons specified
     *     in the {@code exList} argument, or an empty {@code FailableOptional<T>} instance otherwise.
     */
    public abstract FailableOptional<T> ifFailOfType(List<Class<? extends Exception>> exList);

    /**
     * Attempts to recover a failed instance by providing an item of Type {@code U}, which will be
     * wrapped up in another {@code FailableOptional<U>} object.
     *
     * @param supplier A {@code ThrowableSupplier<U, E>} instance that provides an item of Type {@code U},
     *                 which may throw an Exception of Type {@code E}.
     * @param <U> Generic Type {@code U} represents the Type of the object returned by the
     *           {@code ThrowableSupplier<U, E>}.
     * @param <E> Generic Type {@code E} bounded by Exception.
     * @return A new {@code FailableOptional<U>} object.
     */
    public abstract <U, E extends Exception> FailableOptional<U> recover(ThrowableSupplier<U, E> supplier);

    /**
     * Represents a {@code ThrowableSupplier} that contains a value.
     *
     * @param <T> Generic Type {@code T}.
     */
    private static final class Present<T> extends FailableOptional<T> {
        private final T item;

        private Present(T item) {
            this.item = item;
        }

        @Override
        public FailableOptional<T> ifPresent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifPresent(ThrowableConsumer<T, E> consumer) {
            try {
                consumer.consume(this.item);
            } catch (Exception e) {
                return FailableOptional.ofFailure(e);
            }

            return this;
        }

        @Override
        public FailableOptional<T> ifAbsent(Runnable runner) {
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifAbsent(ThrowableConsumer<T, E> consumer) {
            return null;
        }

        @Override
        public FailableOptional<T> ifFail(Runnable runner) {
            return this;
        }

        public <E extends Exception, F extends Exception> FailableOptional<T> ifFail(
                ThrowableConsumer<? super E, ? extends F> consumer) {
            return this;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> map(ThrowableFunction<? super T, ? extends U, E> function) {
            try {
                return FailableOptional.ofNullable(function.apply(this.item));
            } catch (Exception e) {
                return FailableOptional.ofFailure(e);
            }
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> flatMap(
                ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
            try {
                return function.apply(this.item);
            } catch (Exception e) {
                return FailableOptional.ofFailure(e);
            }
        }

        @Override
        public FailableOptional<T> filter(Predicate<T> predicate) {
            if (predicate.test(this.item)) {
                return this;
            }

            return FailableOptional.ofAbsent();
        }

        @Override
        public T get() throws NoSuchElementException {
            return this.item;
        }

        @Override
        public T orElse(T item) {
            return this.item;
        }

        @Override
        public T orElseThrow(Exception e) {
            return this.item;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public boolean isFail() {
            return false;
        }

        @Override
        public final FailableOptional<T> ifFailOfType(List<Class<? extends Exception>> exList) {
            return this;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> recover(ThrowableSupplier<U, E> supplier) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Represents a {@code ThrowableSupplier} that does not contain any value.
     *
     * @param <T> Generic Type {@code T}.
     */
    private static final class Absent<T> extends FailableOptional<T> {

        @Override
        public FailableOptional<T> ifPresent(Runnable runner) {
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifPresent(ThrowableConsumer<T, E> consumer) {
            return this;
        }

        @Override
        public FailableOptional<T> ifAbsent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifAbsent(ThrowableConsumer<T, E> consumer) {
            return this;
        }

        @Override
        public FailableOptional<T> ifFail(Runnable runner) {
            return this;
        }

        public <E extends Exception, F extends Exception> FailableOptional<T> ifFail(
                ThrowableConsumer<? super E, ? extends F> consumer) {
            return this;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> map(ThrowableFunction<? super T, ? extends U, E> function) {
            @SuppressWarnings("unchecked")
            FailableOptional<U> failed = (FailableOptional<U>) this;
            return failed;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> flatMap(
                ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
            @SuppressWarnings("unchecked")
            FailableOptional<U> failed = (FailableOptional<U>) this;
            return failed;
        }

        @Override
        public FailableOptional<T> filter(Predicate<T> predicate) {
            return this;
        }

        @Override
        public T get() throws NoSuchElementException {
            throw new NoSuchElementException();
        }

        @Override
        public T orElse(T item) {
            return item;
        }

        @Override
        public T orElseThrow(Exception e) throws Exception {
            throw e;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isAbsent() {
            return true;
        }

        @Override
        public boolean isFail() {
            return false;
        }

        @Override
        public FailableOptional<T> ifFailOfType(List<Class<? extends Exception>> exList) {
            return this;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> recover(ThrowableSupplier<U, E> supplier) {
            throw new NoSuchElementException();
        }
    }

    /**
     * Represents a {@code ThrowableSupplier} that has failed.
     *
     * @param <T> Generic Type {@code T}.
     */
    private static class Fail<T> extends FailableOptional<T> {
        private final Exception exception;

        private Fail(Exception e) {
            this.exception = e;
        }

        @Override
        public FailableOptional<T> ifPresent(Runnable runner) {
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifPresent(ThrowableConsumer<T, E> consumer) {
            return this;
        }

        @Override
        public FailableOptional<T> ifAbsent(Runnable runner) {
            return this;
        }

        @Override
        public <E extends Exception> FailableOptional<T> ifAbsent(ThrowableConsumer<T, E> consumer) {
            return this;
        }

        @Override
        public FailableOptional<T> ifFail(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public <E extends Exception, F extends Exception> FailableOptional<T> ifFail(
                ThrowableConsumer<? super E, ? extends F> consumer) {
            try {
                @SuppressWarnings("unchecked")
                E except = (E) this.exception;
                consumer.consume(except);
            } catch (Exception e) {
                return FailableOptional.ofFailure(e);
            }

            return this;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> map(ThrowableFunction<? super T, ? extends U, E> function) {
            @SuppressWarnings("unchecked")
            FailableOptional<U> failed = (FailableOptional<U>) this;
            return failed;
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> flatMap(
                ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
            @SuppressWarnings("unchecked")
            FailableOptional<U> failed = (FailableOptional<U>) this;
            return failed;
        }

        @Override
        public FailableOptional<T> filter(Predicate<T> predicate) {
            return this;
        }

        @Override
        public T get() throws NoSuchElementException {
            throw new NoSuchElementException();
        }

        @Override
        public T orElse(T item) {
            return item;
        }

        @Override
        public T orElseThrow(Exception e) throws Exception {
            throw e;
        }

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public boolean isAbsent() {
            return false;
        }

        @Override
        public boolean isFail() {
            return true;
        }

        @Override
        public FailableOptional<T> ifFailOfType(List<Class<? extends Exception>> exList) {
            for (Class<? extends Exception> e : exList) {
                if (this.exception.getClass().getSimpleName().equals(e.getSimpleName())) {
                    return this;
                }
            }

            return FailableOptional.ofAbsent();
        }

        @Override
        public <U, E extends Exception> FailableOptional<U> recover(ThrowableSupplier<U, E> supplier) {
            return FailableOptional.of(supplier);
        }
    }
}
