package reposense.util.function;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a task type that encapsulates information about running a task with the possibility of
 * throwing exception.
 *
 * @param <T> Generic input type {@code T}
 */
public abstract class Failable<T> {
    // Empty instance
    private static final Failable<?> EMPTY = new Empty();

    /**
     * Returns an instance of {@code Failable<T>}. Accepts a {@code ThrowableSupplier}
     * and produces an item of type {@code T}. If it fails, then a failed instance of
     * {@code Failable<T>} is returned. This method allows {@code null} to be stored within
     * it.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param supplier Produces objects of type {@code T}.
     * @return Successful instance of {@code Failable<T>} if the {@code ThrowableSupplier}
     *     runs without failure or a failed instance of {@code Failable<T>}.
     */
    public static <T, E extends Throwable> Failable<T> of(
            ThrowableSupplier<? extends T, E> supplier, Function<? super E, ? extends T> exceptionHandler) {
        try {
            return Failable.success(supplier.produce());
        } catch (Throwable throwable) {
            // ensured by type declaration above
            @SuppressWarnings("unchecked")
            E exception = (E) throwable;
            T recoveredItem = exceptionHandler.apply(exception);
            return Failable.success(recoveredItem);
        }
    }

    /**
     * Returns an instance of {@code Failable<T>}. Accepts a {@code Supplier}
     * and produces an item of type {@code T}. This method allows {@code null} to
     * be stored within it.
     *
     * @param <T> Generic type {@code T}.
     * @param supplier Produces objects of type {@code T}.
     * @return Successful instance of {@code Failable<T, CannotFailException>} if
     *     the {@code ThrowableSupplier} runs without failure or a failed instance
     *     of {@code Failable<T>}.
     */
    public static <T> Failable<T> of(Supplier<? extends T> supplier) {
        return Failable.success(supplier.get());
    }

    /**
     * Returns an instance of {@code Failable<T>}. Accepts a {@code ThrowableSupplier}
     * and produces an item of type {@code T}. If it fails, then a failed instance of
     * {@code Failable<T>} is returned. This method converts {@code null} objects
     * into empty instances of {@code Failable<T>}.
     *
     * @param <T> Generic type {@code T}.
     * @param <E> Generic type {@code E} bounded by {@code Throwable}.
     * @param supplier Produces objects of type {@code T}.
     * @return Successful instance of {@code Failable<T>} if the {@code ThrowableSupplier}
     *     runs without failure, or empty instance of {@code Failable<T>} if {@code null}
     *     is produced, or a failed instance of {@code Failable<T>}.
     */
    public static <T, E extends Throwable> Failable<T> ofNullable(
            ThrowableSupplier<? extends T, E> supplier, Function<? super E, ? extends T> exceptionHandler) {
        try {
            T returns = supplier.produce();

            if (returns == null) {
                return Failable.empty();
            }

            return Failable.success(returns);
        } catch (Throwable throwable) {
            // safe as type guaranteed by function declaration
            @SuppressWarnings("unchecked")
            E exception = (E) throwable;
            T item = exceptionHandler.apply(exception);

            return Failable.ofNullable(item);
        }
    }

    /**
     * Creates a successful instance of {@code Failable<T>}, or returns an empty instance
     * if item is {@code null}.
     *
     * @param <T> Generic type {@code T}.
     * @param supplier Provides an item of type {@code T} to store.
     * @return Successful instance of {@code Failable<T>} if item is not {@code null} else
     *     empty instance of {@code Failable<T>}.
     */
    public static <T> Failable<T> ofNullable(Supplier<? extends T> supplier) {
        return Failable.ofNullable(supplier.get());
    }

    /**
     * Creates a successful instance of {@code Failable<T>}, or returns an empty instance
     * if item is {@code null}.
     *
     * @param <T> Generic type {@code T}.
     * @param item Item of type {@code T} to store.
     * @return Successful instance of {@code Failable<T>} if item is not {@code null} else
     *     empty instance of {@code Failable<T>}.
     */
    public static <T> Failable<T> ofNullable(T item) {
        if (item == null) {
            return Failable.empty();
        }

        return Failable.success(item);
    }

    /**
     * Creates a successful instance of {@code Failable<T>}.
     *
     * @param <T> Generic type {@code T}.
     * @param item Item of type {@code T} to store.
     * @return Successful instance of {@code Failable<T>}.
     */
    public static <T> Failable<T> success(T item) {
        return new Success<>(item);
    }

    /**
     * Creates an empty instance of {@code Failable<T>}.
     *
     * @param <T> Generic type {@code T}.
     * @return Empty instance of {@code Failable<T>}.
     */
    public static <T> Failable<T> empty() {
        // safe as empty contains nothing, and no monadic actions will cause it to turn into anything else
        @SuppressWarnings("unchecked")
        Failable<T> failed = (Failable<T>) Failable.EMPTY;
        return failed;
    }

    /**
     * Returns the item stored in this {@code Failable<T>} instance.
     *
     * @return Item of type {@code T}, or throws an exception if there is no item stored.
     */
    public abstract T get();

    /**
     * Returns the item stored in this {@code Failable<T>} instance, or the input item if this
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
     * @return This instance if the predicate test passses, else an empty {@code Failable<T>} instance.
     */
    public abstract Failable<T> filter(Predicate<? super T> predicate);

    /**
     * Maps this instance's item to a new item of type {@code U}. The mapping function cannot fail
     * (throw exceptions).
     *
     * @param function {@code Function} that accepts an object of type {@code T} and returns a new
     *     object of type {@code U}.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U>} instance
     */
    public abstract <U> Failable<U> map(Function<? super T, ? extends U> function);

    /**
     * Maps this instance's item to a new item of type {@code U}. The mapping function may fail
     * and throw an exception.
     *
     * @param throwableFunction {@code Function} that accepts an object of type {@code T} and returns a new
     *     object of type {@code U}. This function may fail and throw an exception.
     * @param <Z> Generic type {@code Z} bounded by {@code Throwable}.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U>} instance
     */
    public abstract <U, Z extends Throwable> Failable<U> map(
            ThrowableFunction<? super T, ? extends U, Z> throwableFunction,
            Function<? super Z, ? extends U> exceptionHandler);

    /**
     * Maps this instance's item to a new {@code Failable<U>} object. The mapping function cannot fail
     * (throw exceptions).
     *
     * @param function {@code Function} that accepts an object of type {@code T} and returns a
     *     new {@code Failable<U>} object.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U>} instance
     */
    public abstract <U> Failable<U> flatMap(
            Function<? super T, ? extends Failable<U>> function);

    /**
     * Maps this instance's item to a new {@code Failable<U>} object. The mapping function may fail
     * and throw an exception of the same type {@code E}.
     *
     * @param throwableFunction {@code Function} that accepts an object of type {@code T} and returns a
     *     new {@code Failable<U>} object. This function may fail and throw an exception.
     * @param <U> Generic type {@code U}.
     * @return {@code Failable<U>} instance
     */
    public abstract <U, E extends Throwable> Failable<U> flatMap(
            ThrowableFunction<? super T, ? extends Failable<U>, E> throwableFunction,
            Function<? super E, ? extends Failable<U>> exceptionHandler);

    /**
     * Checks if this instance is a present instance of {@code Failable<T>}.
     *
     * @return true if this instance is an instance of {@code Failable<T>} else false.
     */
    public abstract boolean isPresent();

    /**
     * Checks if this instance is an absent instance of {@code Failable<T>}.
     *
     * @return true if this instance is an absent instance of {@code Failable<T>} else false.
     */
    public abstract boolean isAbsent();

    /**
     * Executes a {@code Runnable} object if this instance is a present instance of {@code Failable<T>}.
     *
     * @param runner {@code Runnable} object to run.
     * @return This instance.
     */
    public abstract Failable<T> ifPresent(Runnable runner);

    /**
     * Consumes the item stored in this instance if this instance is a present instance of {@code Failable<T>}.
     *
     * @param consumer {@code Consumer} object to consume the item stored in this instance.
     * @return This instance.
     */
    public abstract Failable<T> ifPresent(Consumer<? super T> consumer);

    /**
     * Executes a {@code Runnable} object if this instance is a absent instance of {@code Failable<T>}.
     *
     * @param runner {@code Runnable} object to run.
     * @return This instance.
     */
    public abstract Failable<T> ifAbsent(Runnable runner);

    /**
     * Successful instance of {@code Failable<T>}.
     *
     * @param <T> Generic type {@code T}.
     */
    private static final class Success<T> extends Failable<T> {
        private final T item;

        private Success(T item) {
            this.item = item;
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
        public Failable<T> filter(Predicate<? super T> predicate) {
            if (predicate.test(this.item)) {
                return this;
            }

            return Failable.empty();
        }

        @Override
        public <U> Failable<U> map(Function<? super T, ? extends U> function) {
            return Failable.of(() -> function.apply(this.item));
        }

        @Override
        public <U, Z extends Throwable> Failable<U> map(
                ThrowableFunction<? super T, ? extends U, Z> throwableFunction,
                Function<? super Z, ? extends U> exceptionHandler) {
            return Failable.of(() -> throwableFunction.apply(this.item), exceptionHandler);
        }

        @Override
        public <U> Failable<U> flatMap(Function<? super T, ? extends Failable<U>> function) {
            return function.apply(this.item);
        }

        @Override
        public <U, E extends Throwable> Failable<U> flatMap(
                ThrowableFunction<? super T, ? extends Failable<U>, E> throwableFunction,
                Function<? super E, ? extends Failable<U>> exceptionHandler) {
            try {
                return throwableFunction.apply(this.item);
            } catch (Throwable throwable) {
                @SuppressWarnings("unchecked")
                E exception = (E) throwable;
                return (exceptionHandler.apply(exception));
            }
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
        public Failable<T> ifPresent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public Failable<T> ifPresent(Consumer<? super T> consumer) {
            consumer.accept(this.item);
            return this;
        }

        @Override
        public Failable<T> ifAbsent(Runnable runner) {
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
     * Empty instance of {@code Failable<T>}.
     */
    private static final class Empty extends Failable<Object> {
        private Empty() {

        }

        @Override
        public Object get() {
            throw new NoSuchElementException("Empty instance of Failable contains no items");
        }

        @Override
        public Object orElse(Object item) {
            return item;
        }

        @Override
        public Failable<Object> filter(Predicate<? super Object> predicate) {
            return Failable.empty();
        }

        @Override
        public <U> Failable<U> map(Function<? super Object, ? extends U> function) {
            return Failable.empty();
        }

        @Override
        public <U, Z extends Throwable> Failable<U> map(
                ThrowableFunction<? super Object, ? extends U, Z> throwableFunction,
                Function<? super Z, ? extends U> exceptionHandler) {
            return Failable.empty();
        }

        @Override
        public <U> Failable<U> flatMap(Function<? super Object, ? extends Failable<U>> function) {
            return Failable.empty();
        }

        @Override
        public <U, E extends Throwable> Failable<U> flatMap(
                ThrowableFunction<? super Object, ? extends Failable<U>, E> throwableFunction,
                Function<? super E, ? extends Failable<U>> exceptionHandler) {
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
        public Failable<Object> ifPresent(Runnable runner) {
            return this;
        }

        @Override
        public Failable<Object> ifPresent(Consumer<? super Object> consumer) {
            return this;
        }

        @Override
        public Failable<Object> ifAbsent(Runnable runner) {
            runner.run();
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Empty;
        }
    }
}
