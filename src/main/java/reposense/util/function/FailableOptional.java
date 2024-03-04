package reposense.util.function;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * {@code Optional<T>} monad that enables both an empty and a
 * fail option.
 *
 * @param <T> Type T, unbounded to any type.
 */
public abstract class FailableOptional<T> {
    public static <T> FailableOptional<T> of(Supplier<T> supplier) {
        return of(supplier.get());
    }

    public static <T, E extends Exception> FailableOptional<T> of(ThrowableSupplier<T, E> supplier) {
        try {
            return of(supplier.produce());
        } catch (Exception e) {
            return ofFailure(e);
        }
    }

    public static <T> FailableOptional<T> ofNullable(Supplier<T> supplier) {
        return ofNullable(supplier.get());
    }

    public static <T, E extends Exception> FailableOptional<T> ofNullable(ThrowableSupplier<T, E> supplier) {
        try {
            return ofNullable(supplier.produce());
        } catch (Exception e) {
            return ofFailure(e);
        }
    }

    public static <T> FailableOptional<T> of(T item) {
        return new Present<>(item);
    }

    public static <T> FailableOptional<T> ofNullable(T item) {
        return item == null ? ofAbsent() : of(item);
    }

    public static <T> FailableOptional<T> ofAbsent() {
        return new Absent<>();
    }

    public static <T> FailableOptional<T> ofFailure(Exception e) {
        return new Fail<>(e);
    }

    public abstract FailableOptional<T> ifPresent(Runnable runner);
    public abstract <E extends Exception> FailableOptional<T> ifPresent(ThrowableConsumer<T, E> consumer);
    public abstract FailableOptional<T> ifAbsent(Runnable runner);
    public abstract <E extends Exception> FailableOptional<T> ifAbsent(ThrowableConsumer<T, E> consumer);
    public abstract FailableOptional<T> ifFail(Runnable runner);
    public abstract <E extends Exception, F extends Exception> FailableOptional<T> ifFail(
            ThrowableConsumer<? super E, ? extends F> consumer);

    public abstract <U, E extends Exception> FailableOptional<U> map(ThrowableFunction<? super T,
            ? extends U, E> function);
    public abstract <U, E extends Exception> FailableOptional<U> flatMap(
            ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function);
    public abstract FailableOptional<T> filter(Predicate<T> predicate);
    public abstract T get() throws NoSuchElementException;
    public abstract T orElse(T item);
    public abstract T orElseThrow(Exception e) throws Exception;
    public abstract boolean isPresent();
    public abstract boolean isAbsent();
    public abstract boolean isFail();
    public abstract FailableOptional<T> ifFailOfType(List<Class<? extends Exception>> exList);
    public abstract <U, E extends Exception> FailableOptional<U> recover(ThrowableSupplier<U, E> supplier);

    private static class Present<T> extends FailableOptional<T> {
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
        public <U, E extends Exception> FailableOptional<U> flatMap(ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
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

    private static class Absent<T> extends FailableOptional<T> {

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
        public <U, E extends Exception> FailableOptional<U> flatMap(ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
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

        public <E extends Exception, F extends Exception> FailableOptional<T> ifFail(ThrowableConsumer<? super E, ? extends F> consumer) {
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
        public <U, E extends Exception> FailableOptional<U> flatMap(ThrowableFunction<? super T, ? extends FailableOptional<U>, E> function) {
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
