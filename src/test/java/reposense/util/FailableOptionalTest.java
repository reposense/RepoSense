package reposense.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.util.function.FailableOptional;

public class FailableOptionalTest {
    private static final FailableOptional<Integer> PRESENT = FailableOptional.of(123);
    private static final FailableOptional<Integer> ABSENT = FailableOptional.ofAbsent();
    private static final FailableOptional<Integer> FAIL = FailableOptional.ofFailure(new Exception());

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
        Assertions.assertFalse(PRESENT.isFail());
        Assertions.assertFalse(ABSENT.isFail());
        Assertions.assertTrue(FAIL.isFail());
    }

    @Test
    public void of_nullItem_success() {
        Assertions.assertNull(FailableOptional.<Integer>of(null).get());
    }

    @Test
    public void of_nonNullItem_success() {
        Assertions.assertEquals(FailableOptional.of("hello world").get(), "hello world");
    }

    @Test
    public void of_nullItemSupplier_success() {
        Assertions.assertNull(FailableOptional.of(() -> null).get());
    }

    @Test
    public void of_nonNullItemSupplier_success() {
        Assertions.assertEquals(FailableOptional.of(() -> 1234).get(), 1234);
    }

    @Test
    public void of_thrownException_becomesFailure() {
        Assertions.assertTrue(FailableOptional.of(() -> {
            throw new Exception();
        }).isFail());
    }

    @Test
    public void ofNullable_nullItem_success() {
        Assertions.assertEquals(FailableOptional.<Integer>ofNullable(null), FailableOptional.ofAbsent());
    }

    @Test
    public void ofNullable_nonNullItem_success() {
        Assertions.assertEquals(FailableOptional.ofNullable("hello world").get(), "hello world");
    }

    @Test
    public void ofNullable_nullItemSupplier_success() {
        Assertions.assertEquals(FailableOptional.ofNullable(() -> null), FailableOptional.ofAbsent());
    }

    @Test
    public void ofNullable_nonNullItemSupplier_success() {
        Assertions.assertEquals(FailableOptional.ofNullable(() -> 1234).get(), 1234);
    }

    @Test
    public void ofNullable_thrownException_becomesFailure() {
        Assertions.assertTrue(FailableOptional.ofNullable(() -> {
            throw new Exception();
        }).isFail());
    }

    @Test
    public void ofAbsent_returnsSameInstance_success() {
        Assertions.assertSame(FailableOptional.ofAbsent(), FailableOptional.ofAbsent());
    }

    @Test
    public void ofAbsent_noStoredValueCheck_success() {
        Assertions.assertThrows(NoSuchElementException.class, () -> FailableOptional.ofAbsent().get());
    }

    @Test
    public void ofFailure_invalidFailureNull_throwsException() {
        Assertions.assertThrows(NullPointerException.class, () -> FailableOptional.ofFailure(null));
    }

    @Test
    public void ofFailure_validFailureException_success() {
        Assertions.assertDoesNotThrow(() -> FailableOptional.ofFailure(new Exception("should not be thrown")));
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
    public void ifPresent_throwingConsumer_success() {
        FailableOptional<Integer> presentThrown = PRESENT.ifPresent(x -> {
            throw new Exception();
        });
        FailableOptional<Integer> absentThrown = ABSENT.ifPresent(x -> {
            throw new Exception();
        });
        FailableOptional<Integer> failThrown = FAIL.ifPresent(x -> {
            throw new Exception();
        });

        Assertions.assertTrue(presentThrown.isFail());
        Assertions.assertTrue(absentThrown.isAbsent());
        Assertions.assertTrue(failThrown.isFail());
    }

    @Test
    public void ifAbsent_testAll_success() {
        int[] testArray = {-1, -1, -1};

        PRESENT.ifAbsent(() -> testArray[0] = 1);
        ABSENT.ifAbsent(() -> testArray[1] = 1);
        FAIL.ifAbsent(() -> testArray[2] = 1);

        System.out.println(Arrays.toString(testArray));

        Assertions.assertArrayEquals(testArray, new int[]{-1, 1, -1});
    }

    @Test
    public void ifFail_testAll_success() {
        int[] testArray = {-1, -1, -1};

        PRESENT.ifFail(x -> testArray[0] = 1);
        ABSENT.ifFail(x -> testArray[1] = 1);
        FAIL.ifFail(x -> testArray[2] = 1);

        Assertions.assertArrayEquals(testArray, new int[]{-1, -1, 1});
    }

    @Test
    public void ifFail_throwingConsumer_success() {
        FailableOptional<Integer> presentThrown = PRESENT.ifFail(x -> {
            throw new Exception();
        });
        FailableOptional<Integer> absentThrown = ABSENT.ifFail(x -> {
            throw new Exception();
        });
        FailableOptional<Integer> failThrown = FAIL.ifFail(x -> {
            throw new Exception();
        });

        Assertions.assertTrue(presentThrown.isPresent());
        Assertions.assertTrue(absentThrown.isAbsent());
        Assertions.assertTrue(failThrown.isFail());
    }

    @Test
    public void recover_presentFailableOptional_success() {
        FailableOptional<Integer> recovered = PRESENT.recover(() -> 1);
        Assertions.assertEquals(recovered, PRESENT);
    }

    @Test
    public void recover_absentFailableOptional_success() {
        FailableOptional<Integer> recovered = ABSENT.recover(() -> 2);
        Assertions.assertEquals(recovered, ABSENT);
    }

    @Test
    public void recover_failFailableOptional_success() {
        FailableOptional<Integer> recovered = FAIL.recover(() -> 9999);
        Assertions.assertTrue(recovered.isPresent());
        Assertions.assertEquals(recovered.get(), 9999);
    }

    @Test
    public void map_testAll_success() {
        Assertions.assertEquals(PRESENT.map(x -> "String").get(), "String");
        Assertions.assertSame(ABSENT.map(x -> "String"), ABSENT);
        Assertions.assertSame(FAIL.map(x -> "String"), FAIL);
    }

    @Test
    public void nullableMap_testAll_success() {
        Assertions.assertSame(PRESENT.nullableMap(x -> null), ABSENT);
        Assertions.assertSame(ABSENT.nullableMap(x -> null), ABSENT);
        Assertions.assertSame(FAIL.nullableMap(x -> null), FAIL);
    }

    @Test
    public void flatMap_testAll_success() {
        Assertions.assertEquals(PRESENT.flatMap(x -> FailableOptional.of("String")).get(), "String");
        Assertions.assertSame(ABSENT.flatMap(x -> FailableOptional.of("String")), ABSENT);
        Assertions.assertSame(FAIL.flatMap(x -> FailableOptional.of("String")), FAIL);
    }

    @Test
    public void filter_presentFailableOptional_passPredicate() {
        Assertions.assertSame(PRESENT.filter(x -> x > 0), PRESENT);
    }

    @Test
    public void filter_absentFailableOptional_success() {
        Assertions.assertSame(ABSENT.filter(x -> x < 0), ABSENT);
    }

    @Test
    public void filter_failedFailableOptional_success() {
        Assertions.assertSame(FAIL.filter(x -> x < 0), FAIL);
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
    public void orElseThrow_testAll_success() {
        Assertions.assertEquals(PRESENT.orElse(999), 123);
        Assertions.assertThrows(Exception.class, () -> ABSENT.orElseThrow(new Exception()));
        Assertions.assertThrows(Exception.class, () -> FAIL.orElseThrow(new Exception()));
    }

    @Test
    public void ifFailOfType_testAll_failure() {
        Assertions.assertSame(PRESENT.ifFailOfType(Arrays.asList(Exception.class, IllegalArgumentException.class)),
                PRESENT);
        Assertions.assertSame(ABSENT.ifFailOfType(Arrays.asList(Exception.class, IllegalArgumentException.class)),
                ABSENT);
        Assertions.assertSame(FAIL.ifFailOfType(Arrays.asList(Exception.class, IllegalArgumentException.class)), FAIL);
        Assertions.assertSame(FAIL.ifFailOfType(Arrays.asList(IllegalArgumentException.class)), ABSENT);
    }

    @Test
    public void equals_presentFailableOptional_multipleTests() {
        Assertions.assertSame(PRESENT, PRESENT);
        Assertions.assertEquals(PRESENT, PRESENT);
        Assertions.assertNotEquals(PRESENT, ABSENT);
        Assertions.assertNotEquals(PRESENT, FAIL);
        Assertions.assertNotEquals(PRESENT, FailableOptional.of("String"));
    }

    @Test
    public void equals_absentFailableOptional_multipleTests() {
        Assertions.assertSame(ABSENT, ABSENT);
        Assertions.assertEquals(ABSENT, ABSENT);
        Assertions.assertNotEquals(ABSENT, PRESENT);
        Assertions.assertNotEquals(ABSENT, FAIL);
    }

    @Test
    public void equals_failedFailableOptional_multipleTests() {
        Assertions.assertSame(FAIL, FAIL);
        Assertions.assertEquals(FAIL, FAIL);
        Assertions.assertNotEquals(FAIL, PRESENT);
        Assertions.assertNotEquals(FAIL, ABSENT);
    }
}
