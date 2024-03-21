package reposense.util.function;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a task type that encapsulates information about running a task with the possibility of
 * throwing exception.
 *
 * @param <T> Generic input type {@code T}
 * @param <E> Generic exception type {@code E}
 */
public abstract class Failable<T, E extends Throwable> {
    // Empty instance
    private static final Failable<?, ?> EMPTY = new Empty<>();

    /**
     * Returns an instance of {@code Failable<T, E>}. Accepts a {@code ThrowableSupplier}
     * and produces an item of type {@code T}. If it fails, then a failed instance of
     * {@code Failable<T, E>} is returned. This method allows {@code null} to be stored within
     * it.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param supplier Produces objects of type {@code T}.
     * @return Successful instance of {@code Failable<T, E>} if the {@code ThrowableSupplier}
     *     runs without failure or a failed instance of {@code Failable<T, E>}.
     */
    public static <T, E extends Throwable> Failable<T, E> of(
            ThrowableSupplier<? extends T, E> supplier) {
        try {
            return Failable.success(supplier.produce());
        } catch (Throwable throwable) {
            return Failable.ifFailElseThrow(throwable);
        }
    }

    /**
     * Returns an instance of {@code Failable<T, E>}. Accepts a {@code ThrowableSupplier}
     * and produces an item of type {@code T}. If it fails, then a failed instance of
     * {@code Failable<T, E>} is returned. This method converts {@code null} objects
     * into empty instances of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param supplier Produces objects of type {@code T}.
     * @return Successful instance of {@code Failable<T, E>} if the {@code ThrowableSupplier}
     *     runs without failure, or empty instance of {@code Failable<T, E>} if {@code null}
     *     is produced, or a failed instance of {@code Failable<T, E>}.
     */
    public static <T, E extends Throwable> Failable<T, E> ofNullable(
            ThrowableSupplier<? extends T, E> supplier) {
        try {
            T returns = supplier.produce();

            if (returns == null) {
                return Failable.empty();
            }

            return Failable.success(returns);
        } catch (Throwable throwable) {
            return Failable.ifFailElseThrow(throwable);
        }
    }

    /**
     * Creates a successful instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param item Item of type {@code T} to store.
     * @return Successful instance of {@code Failable<T, E>}.
     */
    public static <T, E extends Throwable> Failable<T, E> success(T item) {
        // can just cast as no exception thrown
        @SuppressWarnings("unchecked")
        Failable<T, E> succ = (Failable<T, E>) new Success<>(item);
        return succ;
    }

    /**
     * Creates an empty instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @return Empty instance of {@code Failable<T, E>}.
     */
    public static <T, E extends Throwable> Failable<T, E> empty() {
        // safe as empty contains nothing, and no monadic actions will cause it to turn into anything else
        @SuppressWarnings("unchecked")
        Failable<T, E> failed = (Failable<T, E>) Failable.EMPTY;
        return failed;
    }

    /**
     * Creates a failed instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param throwable Object of type {@code E} that represents the thrown exception this
     *     {@code Failable<T, E>} encapsulates.
     * @return Failed instance of {@code Failable<T, E>}.
     */
    public static <T, E extends Throwable> Failable<T, E> fail(E throwable) {
        return new Fail<>(throwable);
    }

    /**
     * Checks if the thrown exception is of the correct generic type using Java Reflections.
     * Experimental method that requires deeper inspection to ensure that it is correct;
     * can be replaced by an unsafe cast if necessary.
     *
     * @param throwable {@code Throwable} object to check the type of.
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @return {@code Failable<T, E>} instance if the exception type is verified.
     * @throws ClassCastException if the function throws an exception that is not of the expected
     *     {@code Throwable} type.
     */
    private static <T, E extends Throwable> Failable<T, E> ifFailElseThrow(Throwable throwable) {
        if (new Fail<>(throwable).getExceptionClass().isInstance(throwable)) {
            @SuppressWarnings("unchecked")
            Failable<T, E> failed = (Failable<T, E>) new Fail<>(throwable);
            return failed;
        }

        throw new ClassCastException("Exception class does not match specifications");
    }

    /**
     * Changes the {@code Throwable} exception type to a new type.
     *
     * @param exception {@code Throwable} type {@code Z} to change to.
     * @param <Z> Generic type {@code Z} bounded by {@code Throwable}.
     * @return {@code Failable<T, Z>} with new exception type {@code Z}.
     */
    public abstract <Z extends Throwable> Failable<T, Z> failWith(Z exception);

    /**
     * Returns the item stored in this {@code Failable<T, E>} instance.
     *
     * @return Item of type {@code T}, or throws an exception if there is no item stored.
     */
    public abstract T get();

    /**
     * Returns the item stored in this {@code Failable<T, E>} instance, or the input item if this
     * instance does not contain any items.
     *
     * @param item Item of type {@code T} to return if this instance has failed or is empty.
     * @return This instance's item of type {@code T}, or the input item of type {@code T}.
     */
    public abstract T orElse(T item);

    /**
     * Tests the item stored in this instance against some {@code Predicate}.
     *
     * @param predicate {@code Predicate} to test this instance's item against.
     * @return This instance if the predicate test passses, else an empty {@code Failable<T, E>} instance.
     */
    public abstract Failable<T, E> filter(Predicate<? super T> predicate);

    /**
     * Maps this instance's item to a new item of type {@code U}. The mapping function cannot fail
     * (throw exceptions).
     *
     * @param function {@code Function} that accepts an object of type {@code T} and returns a new
     *     object of type {@code U}.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U, E>} instance
     */
    public abstract <U> Failable<U, E> unfailableMap(Function<? super T, ? extends U> function);

    /**
     * Maps this instance's item to a new item of type {@code U}. The mapping function may fail
     * and throw an exception.
     *
     * @param throwableFunction {@code Function} that accepts an object of type {@code T} and returns a new
     *     object of type {@code U}. This function may fail and throw an exception.
     * @param <Z> Generic type {@code Z} bounded by {@code Throwable}.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U, E>} instance
     */
    public abstract <U, Z extends Throwable> Failable<U, Z> map(
            ThrowableFunction<? super T, ? extends U, Z> throwableFunction);

    /**
     * Maps this instance's item to a new {@code Failable<U, E>} object. The mapping function cannot fail
     * (throw exceptions).
     *
     * @param function {@code Function} that accepts an object of type {@code T} and returns a
     *     new {@code Failable<U, E>} object.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U, E>} instance
     */
    public abstract <U> Failable<U, E> unfailableFlatMap(
            Function<? super T, ? extends Failable<? extends U, E>> function);

    /**
     * Maps this instance's item to a new {@code Failable<U, Z>} object. The mapping function may fail
     * and throw an exception.
     *
     * @param throwableFunction {@code Function} that accepts an object of type {@code T} and returns a
     *     new {@code Failable<U, Z>} object. This function may fail and throw an exception.
     * @param <Z> Generic type {@code Z} bounded by {@code Throwable}.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U, E>} instance
     */
    public abstract <U, Z extends Throwable> Failable<U, Z> flatMap(
            ThrowableFunction<? super T, ? extends Failable<? extends U, Z>, Z> throwableFunction);

    /**
     * Attempts to resolve this instance to an empty instance of {@code Failable<T, E>}.
     *
     * @param consumer {@code Consumer} object that consumes the possible exception thrown.
     * @return Empty {@code Failable<T, E>} instance.
     */
    public abstract Failable<T, E> resolve(Consumer<? super E> consumer);

    /**
     * Attempts to resolve this instance to a successful instance of {@code Failable<T, E>}.
     *
     * @param consumer {@code Consumer} object that consumes the possible exception thrown.
     * @param with Item of type {@code T} that the new resolved {@code Failable<T, E>} will contain.
     * @return Successful {@code Failable<T, E>} instance.
     */
    public abstract Failable<T, E> resolve(Consumer<? super E> consumer, T with);

    /**
     * Checks if this instance is a present instance of {@code Failable<T, E>}.
     *
     * @return true if this instance is an instance of {@code Failable<T, E>} else false.
     */
    public abstract boolean isPresent();

    /**
     * Checks if this instance is an absent instance of {@code Failable<T, E>}.
     *
     * @return true if this instance is an absent instance of {@code Failable<T, E>} else false.
     */
    public abstract boolean isAbsent();

    /**
     * Checks if this instance is a failed instance of {@code Failable<T, E>}.
     *
     * @return true if this instance is a failed instance of {@code Failable<T, E>} else false.
     */
    public abstract boolean isFailed();

    /**
     * Executes a {@code Runnable} object if this instance is a present instance of {@code Failable<T, E>}.
     *
     * @param runner {@code Runnable} object to run.
     * @return This instance.
     */
    public abstract Failable<T, E> ifPresent(Runnable runner);

    /**
     * Consumes the item stored in this instance if this instance is a present instance of {@code Failable<T, E>}.
     *
     * @param consumer {@code Consumer} object to consume the item stored in this instance.
     * @return This instance.
     */
    public abstract Failable<T, E> ifPresent(Consumer<? super T> consumer);

    /**
     * Executes a {@code Runnable} object if this instance is a absent instance of {@code Failable<T, E>}.
     *
     * @param runner {@code Runnable} object to run.
     * @return This instance.
     */
    public abstract Failable<T, E> ifAbsent(Runnable runner);

    /**
     * Consumes the exception stored in this instance if this instance is a failed instance of {@code Failable<T, E>}.
     *
     * @param consumer {@code Consumer} object to consume the exception stored in this instance.
     * @return This instance.
     */
    public abstract Failable<T, E> ifFailed(Consumer<? super E> consumer);

    /**
     * Executes a {@code Runnable} object if this instance is a failed instance of {@code Failable<T, E>}.
     *
     * @param runner {@code Runnable} object to run.
     * @return This instance.
     */
    public abstract Failable<T, E> ifFailed(Runnable runner);

    /**
     * Throws the exception stored in this {@code Failable<T, E>} object only if this instance is a failed
     * instance, otherwise return this instance as is.
     *
     * @return This instance if this instance is not a failed instance.
     * @throws E if this instance is a failed instance, else no exceptions are thrown.
     */
    public abstract Failable<T, E> ifFailThenThrow() throws E;

    /**
     * Successful instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     */
    private static final class Success<T> extends Failable<T, CannotFailException> {
        private final T item;

        private Success(T item) {
            this.item = item;
        }

        @Override
        public <Z extends Throwable> Failable<T, Z> failWith(Z exception) {
            return Failable.success(this.item);
        }

        @Override
        public T get() {
            return this.item;
        }

        @Override
        public T orElse(T item) {
            return this.item;
        }

        @Override
        public <U> Failable<U, CannotFailException> unfailableMap(Function<? super T, ? extends U> function) {
            return Failable.of(() -> function.apply(this.item));
        }

        @Override
        public Failable<T, CannotFailException> filter(Predicate<? super T> predicate) {
            if (predicate.test(this.item)) {
                return this;
            }

            return Failable.empty();
        }

        @Override
        public <U, Z extends Throwable> Failable<U, Z> map(
                ThrowableFunction<? super T, ? extends U, Z> throwableFunction) {
            try {
                return Failable.of(() -> throwableFunction.apply(this.item));
            } catch (Throwable throwable) {
                return Failable.ifFailElseThrow(throwable);
            }
        }

        @Override
        public <U> Failable<U, CannotFailException> unfailableFlatMap(
                Function<? super T, ? extends Failable<? extends U, CannotFailException>> function) {
            @SuppressWarnings("unchecked")
            Failable<U, CannotFailException> current = (Failable<U, CannotFailException>) function.apply(this.item);
            return current;
        }

        @Override
        public <U, Z extends Throwable> Failable<U, Z> flatMap(
                ThrowableFunction<? super T, ? extends Failable<? extends U, Z>, Z> throwableFunction) {
            try {
                @SuppressWarnings("unchecked")
                Failable<U, Z> current = (Failable<U, Z>) throwableFunction.apply(this.item);
                return current;
            } catch (Throwable throwable) {
                return Failable.ifFailElseThrow(throwable);
            }
        }

        @Override
        public Failable<T, CannotFailException> resolve(Consumer<? super CannotFailException> consumer) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> resolve(Consumer<? super CannotFailException> consumer, T with) {
            return this;
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
        public boolean isFailed() {
            return false;
        }

        @Override
        public Failable<T, CannotFailException> ifPresent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifPresent(Consumer<? super T> consumer) {
            consumer.accept(this.item);
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifAbsent(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailed(Consumer<? super CannotFailException> consumer) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailed(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailThenThrow() throws CannotFailException {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (obj instanceof Success) {
                Success<?> success = (Success<?>) obj;
                return success.item.equals(this.item);
            }

            return false;
        }
    }

    /**
     * Empty instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     */
    private static final class Empty<T> extends Failable<T, CannotFailException> {
        private Empty() {

        }

        @Override
        public <Z extends Throwable> Failable<T, Z> failWith(Z exception) {
            return Failable.empty();
        }

        @Override
        public T get() {
            throw new NoSuchElementException("No element in Failable");
        }

        @Override
        public T orElse(T item) {
            return item;
        }

        @Override
        public <U> Failable<U, CannotFailException> unfailableMap(Function<? super T, ? extends U> function) {
            return Failable.empty();
        }

        @Override
        public Failable<T, CannotFailException> filter(Predicate<? super T> predicate) {
            return Failable.empty();
        }

        @Override
        public <U, Z extends Throwable> Failable<U, Z> map(
                ThrowableFunction<? super T, ? extends U, Z> throwableFunction) {
            return Failable.empty();
        }

        @Override
        public <U> Failable<U, CannotFailException> unfailableFlatMap(
                Function<? super T, ? extends Failable<? extends U, CannotFailException>> function) {
            return Failable.empty();
        }

        @Override
        public <U, Z extends Throwable> Failable<U, Z> flatMap(
                ThrowableFunction<? super T, ? extends Failable<? extends U, Z>, Z> throwableFunction) {
            return Failable.empty();
        }

        @Override
        public Failable<T, CannotFailException> resolve(Consumer<? super CannotFailException> consumer) {
            return Failable.empty();
        }

        @Override
        public Failable<T, CannotFailException> resolve(Consumer<? super CannotFailException> consumer, T with) {
            return Failable.empty();
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
        public boolean isFailed() {
            return false;
        }

        @Override
        public Failable<T, CannotFailException> ifPresent(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifPresent(Consumer<? super T> consumer) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifAbsent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailed(Consumer<? super CannotFailException> consumer) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailed(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, CannotFailException> ifFailThenThrow() throws CannotFailException {
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Empty;
        }
    }

    /**
     * Failed instance of {@code Failable<T, E>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     */
    private static final class Fail<T, E extends Throwable> extends Failable<T, E> {
        private final E exception;

        private Fail(E exception) {
            this.exception = exception;
        }

        private Class<?> getExceptionClass() {
            return exception.getClass();
        }

        @Override
        public <Z extends Throwable> Failable<T, Z> failWith(Z exception) {
            return Failable.fail(exception);
        }

        @Override
        public T get() {
            throw new NoSuchElementException("No element in Failable");
        }

        @Override
        public T orElse(T item) {
            return item;
        }

        @Override
        public <U> Failable<U, E> unfailableMap(Function<? super T, ? extends U> function) {
            return Failable.fail(this.exception);
        }

        @Override
        public Failable<T, E> filter(Predicate<? super T> predicate) {
            return Failable.fail(this.exception);
        }

        @Override
        public <U, Z extends Throwable> Failable<U, Z> map(
                ThrowableFunction<? super T, ? extends U, Z> throwableFunction) {
            return Failable.ifFailElseThrow(this.exception);
        }

        @Override
        public <U> Failable<U, E> unfailableFlatMap(Function<? super T, ? extends Failable<? extends U, E>> function) {
            return Failable.fail(this.exception);
        }

        public <U, Z extends Throwable> Failable<U, Z> flatMap(
                ThrowableFunction<? super T, ? extends Failable<? extends U, Z>, Z> throwableFunction) {
            return Failable.ifFailElseThrow(this.exception);
        }

        @Override
        public Failable<T, E> resolve(Consumer<? super E> consumer) {
            return Failable.empty();
        }

        @Override
        public Failable<T, E> resolve(Consumer<? super E> consumer, T with) {
            consumer.accept(this.exception);
            return Failable.success(with);
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
        public boolean isFailed() {
            return true;
        }

        @Override
        public Failable<T, E> ifPresent(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, E> ifPresent(Consumer<? super T> consumer) {
            return this;
        }

        @Override
        public Failable<T, E> ifAbsent(Runnable runner) {
            return this;
        }

        @Override
        public Failable<T, E> ifFailed(Consumer<? super E> consumer) {
            consumer.accept(this.exception);
            return this;
        }

        @Override
        public Failable<T, E> ifFailed(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public Failable<T, E> ifFailThenThrow() throws E {
            throw this.exception;
        }

        @Override
        public String toString() {
            return this.exception.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (obj instanceof Fail) {
                Fail<?, ?> failed = (Fail<?, ?>) obj;
                return failed.exception.equals(this.exception);
            }

            return false;
        }
    }
}
