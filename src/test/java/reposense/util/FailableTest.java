package reposense.util;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.util.function.CannotFailException;
import reposense.util.function.Failable;

public class FailableTest {
    private static final Failable<Integer, CannotFailException> PRESENT = Failable.success(123);
    private static final Failable<Integer, CannotFailException> ABSENT = Failable.empty();
    private static final Failable<Integer, Exception> FAIL = Failable.fail(new Exception());

    @Test
    public void isPresent_testAll_success() {
        Assertions.assertTrue(PRESENT.isPresent());
        Assertions.assertFalse(ABSENT.isPresent());
        Assertions.assertFalse(FAIL.isPresent());
    }

    @Test
    public void isAbsent_testAll_success() {
        Assertions.assertFalse(PRESENT.isAbsent());
        Assertions.assertTrue(ABSENT.isAbsent());
        Assertions.assertFalse(FAIL.isAbsent());
    }

    @Test
    public void isFail_testAll_success() {
        Assertions.assertFalse(PRESENT.isFailed());
        Assertions.assertFalse(ABSENT.isFailed());
        Assertions.assertTrue(FAIL.isFailed());
    }

    @Test
    public void of_nullItem_success() {
        Assertions.assertNull(Failable.success(null).get());
    }

    @Test
    public void of_nonNullItem_success() {
        Assertions.assertEquals(Failable.success("hello world").get(), "hello world");
    }

    @Test
    public void of_nullItemSupplier_success() {
        Assertions.assertNull(Failable.success(null).get());
    }

    @Test
    public void of_nonNullItemSupplier_success() {
        Assertions.assertEquals(Failable.success(1234).get(), 1234);
    }

    @Test
    public void of_thrownException_becomesFailure() {
        Assertions.assertTrue(Failable.of(() -> {
            throw new Exception();
        }).isFailed());
    }

    @Test
    public void ofNullable_nullItem_success() {
        Assertions.assertEquals(Failable.ofNullable(() -> null), Failable.empty());
    }

    @Test
    public void ofNullable_nonNullItem_success() {
        Assertions.assertEquals(Failable.ofNullable(() -> "hello world").get(), "hello world");
    }

    @Test
    public void ofNullable_nullItemSupplier_success() {
        Assertions.assertEquals(Failable.ofNullable(() -> null), Failable.empty());
    }

    @Test
    public void ofNullable_nonNullItemSupplier_success() {
        Assertions.assertEquals(Failable.ofNullable(() -> 1234).get(), 1234);
    }

    @Test
    public void ofNullable_thrownException_becomesFailure() {
        Assertions.assertTrue(Failable.ofNullable(() -> {
            throw new Exception();
        }).isFailed());
    }

    @Test
    public void ofAbsent_returnsSameInstance_success() {
        Assertions.assertSame(Failable.empty(), Failable.empty());
    }

    @Test
    public void ofAbsent_noStoredValueCheck_success() {
        Assertions.assertThrows(NoSuchElementException.class, () -> Failable.empty().get());
    }

    @Test
    public void ofFailure_invalidFailureNull_throwsException() {
        Assertions.assertDoesNotThrow(() -> Failable.fail(null));
    }

    @Test
    public void ofFailure_validFailureException_success() {
        Assertions.assertDoesNotThrow(() -> Failable.fail(new Exception("should not be thrown")));
    }

    @Test
    public void ifPresent_testAll_success() {
        int[] testArray = {-1, -1, -1};

        PRESENT.ifPresent(() -> testArray[0] = 1);
        ABSENT.ifPresent(() -> testArray[1] = 1);
        FAIL.ifPresent(() -> testArray[2] = 1);

        Assertions.assertArrayEquals(testArray, new int[]{1, -1, -1});
    }

    @Test
    public void ifAbsent_testAll_success() {
        int[] testArray = {-1, -1, -1};

        PRESENT.ifAbsent(() -> testArray[0] = 1);
        ABSENT.ifAbsent(() -> testArray[1] = 1);
        FAIL.ifAbsent(() -> testArray[2] = 1);

        Assertions.assertArrayEquals(testArray, new int[]{-1, 1, -1});
    }

    @Test
    public void ifFailed_testAll_success() {
        int[] testArray = {-1, -1, -1, -1};

        PRESENT.ifFailed(() -> testArray[0] = 1);
        PRESENT.ifFailed(x -> testArray[0] = 2);
        ABSENT.ifFailed(() -> testArray[1] = 1);
        ABSENT.ifFailed(x -> testArray[1] = 2);
        FAIL.ifFailed(() -> testArray[2] = 1);
        FAIL.ifFailed(x -> {
            if (x instanceof Exception) {
                testArray[3] = 2;
            }
        });

        Assertions.assertArrayEquals(testArray, new int[]{-1, -1, 1, 2});
    }

    @Test
    public void map_testAll_success() {
        Assertions.assertEquals(PRESENT.map(x -> "String").get(), "String");
        Assertions.assertEquals(ABSENT.map(x -> "String"), ABSENT);
        Assertions.assertEquals(FAIL.map(x -> "String"), FAIL);
    }

    @Test
    public void map_throwingFunctionTestAll_success() {
        Assertions.assertInstanceOf(FAIL.getClass(), PRESENT.map(x -> {
            throw new Exception();
        }));
        Assertions.assertInstanceOf(ABSENT.getClass(), ABSENT.map(x -> {
            throw new Exception();
        }));

        Failable<Integer, Exception> failedMapping = FAIL.map(x -> {
            throw new Exception();
        });
        Assertions.assertInstanceOf(FAIL.getClass(), failedMapping);
        Assertions.assertThrows(Exception.class, failedMapping::ifFailThenThrow);
    }

    @Test
    public void unfailableMap_testAll_success() {
        Assertions.assertEquals(PRESENT.unfailableMap(x -> "String").get(), "String");
        Assertions.assertEquals(ABSENT.unfailableMap(x -> "String"), ABSENT);
        Assertions.assertThrows(Exception.class, () -> FAIL.unfailableMap(x -> "String").ifFailThenThrow());
    }

    @Test
    public void flatMap_testAll_success() {
        Assertions.assertEquals(PRESENT.flatMap(x -> Failable.success("String")).get(), "String");
        Assertions.assertEquals(ABSENT.flatMap(x -> Failable.success("String")), ABSENT);
        Assertions.assertEquals(FAIL.flatMap(x -> Failable.success("String")), FAIL);
    }

    @Test
    public void flatMap_throwingFunctionTestAll_success() throws Throwable {
        Assertions.assertInstanceOf(FAIL.getClass(), PRESENT.flatMap(x -> {
            throw new Exception();
        }));
        Assertions.assertInstanceOf(ABSENT.getClass(), ABSENT.map(x -> {
            throw new Exception();
        }));

        // mapping turns the failed instance to the new exception class,
        // BUT it will only contain the first exception
        Failable<Integer, IllegalAccessError> failedMapping = FAIL.flatMap(x -> {
            throw new IllegalAccessError();
        });
        Assertions.assertInstanceOf(FAIL.getClass(), failedMapping);

        // users must be aware of what exceptions that can cause it to fail before considering
        // what to catch/throw
        Assertions.assertThrows(Exception.class, failedMapping::ifFailThenThrow);
    }

    @Test
    public void unfailableFlatMap_testAll_success() {
        Assertions.assertEquals(PRESENT.unfailableFlatMap(x -> Failable.success("String")).get(), "String");
        Assertions.assertEquals(ABSENT.unfailableFlatMap(x -> Failable.success("String")), ABSENT);
        Assertions.assertEquals(FAIL.unfailableFlatMap(x -> Failable.success("String")), FAIL);
    }

    @Test
    public void filter_presentFailable_passPredicate() {
        Assertions.assertSame(PRESENT.filter(x -> x > 0), PRESENT);
    }

    @Test
    public void filter_absentFailable_success() {
        Assertions.assertSame(ABSENT.filter(x -> x < 0), ABSENT);
    }

    @Test
    public void filter_failedFailable_success() {
        Assertions.assertEquals(FAIL.filter(x -> x < 0), FAIL);
    }

    @Test
    public void get_testAll_success() {
        Assertions.assertEquals(PRESENT.get(), 123);
        Assertions.assertThrows(NoSuchElementException.class, ABSENT::get);
        Assertions.assertThrows(NoSuchElementException.class, FAIL::get);
    }

    @Test
    public void orElse_testAll_success() {
        Assertions.assertEquals(PRESENT.orElse(999), 123);
        Assertions.assertEquals(ABSENT.orElse(999), 999);
        Assertions.assertEquals(FAIL.orElse(999), 999);
    }

    @Test
    public void ifFailThenThrow_testAll_success() {
        Assertions.assertDoesNotThrow(PRESENT::ifFailThenThrow);
        Assertions.assertDoesNotThrow(ABSENT::ifFailThenThrow);
        Assertions.assertThrows(Exception.class, FAIL::ifFailThenThrow);
    }

    @Test
    public void failWith_testAll_success() {
        Assertions.assertDoesNotThrow(() -> PRESENT.failWith(new Throwable()).ifFailThenThrow());
        Assertions.assertDoesNotThrow(() -> ABSENT.failWith(new Throwable()).ifFailThenThrow());
        Assertions.assertThrows(Throwable.class, () -> FAIL.failWith(new Throwable()).ifFailThenThrow());
    }

    @Test
    public void resolve_withoutReplacingValue_success() {
        Assertions.assertEquals(FAIL.resolve(x -> Assertions.assertInstanceOf(Exception.class, x)), ABSENT);
    }

    @Test
    public void resolve_withReplacingValue_success() {
        Assertions.assertEquals(FAIL.resolve(x -> Assertions.assertInstanceOf(Exception.class, x), 1).get(), 1);
    }

    @Test
    public void equals_presentFailable_multipleTests() {
        Assertions.assertSame(PRESENT, PRESENT);
        Assertions.assertEquals(PRESENT, PRESENT);
        Assertions.assertNotEquals(PRESENT, ABSENT);
        Assertions.assertNotEquals(PRESENT, FAIL);
        Assertions.assertNotEquals(PRESENT, Failable.success("String"));
    }

    @Test
    public void equals_absentFailable_multipleTests() {
        Assertions.assertSame(ABSENT, ABSENT);
        Assertions.assertEquals(ABSENT, ABSENT);
        Assertions.assertNotEquals(ABSENT, PRESENT);
        Assertions.assertNotEquals(ABSENT, FAIL);
    }

    @Test
    public void equals_failedFailable_multipleTests() {
        Assertions.assertSame(FAIL, FAIL);
        Assertions.assertEquals(FAIL, FAIL);
        Assertions.assertNotEquals(FAIL, PRESENT);
        Assertions.assertNotEquals(FAIL, ABSENT);
    }
}
