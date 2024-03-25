package reposense.util;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.util.function.Failable;

public class FailableTest {
    private static final Failable<Integer> PRESENT = Failable.success(123);
    private static final Failable<Integer> ABSENT = Failable.empty();

    @Test
    public void isPresent_testAll_success() {
        Assertions.assertTrue(PRESENT.isPresent());
        Assertions.assertFalse(ABSENT.isPresent());
    }

    @Test
    public void isAbsent_testAll_success() {
        Assertions.assertFalse(PRESENT.isAbsent());
        Assertions.assertTrue(ABSENT.isAbsent());
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
    public void of_thrownException_recovered() {
        Assertions.assertEquals(Failable.<Integer, IllegalAccessError>of(() -> {
            throw new IllegalAccessError();
        }, x -> 1).get(), 1);
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
    public void ofNullable_thrownException_recovered() {
        Assertions.assertEquals(Failable.<Integer, IllegalAccessError>ofNullable(() -> {
            throw new IllegalAccessError();
        }, x -> 1).get(), 1);
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
    public void ifPresent_testAll_success() {
        int[] testArray = {-1, -1};

        PRESENT.ifPresent(() -> testArray[0] = 1);
        ABSENT.ifPresent(() -> testArray[1] = 1);

        Assertions.assertArrayEquals(testArray, new int[]{1, -1});
    }

    @Test
    public void ifAbsent_testAll_success() {
        int[] testArray = {-1, -1};

        PRESENT.ifAbsent(() -> testArray[0] = 1);
        ABSENT.ifAbsent(() -> testArray[1] = 1);

        Assertions.assertArrayEquals(testArray, new int[]{-1, 1});
    }
    @Test
    public void map_testAll_success() {
        Assertions.assertEquals(PRESENT.map(x -> "String").get(), "String");
        Assertions.assertEquals(ABSENT.map(x -> "String"), ABSENT);
    }

    @Test
    public void map_throwingFunctionTestAll_success() {
        Assertions.assertInstanceOf(PRESENT.getClass(), PRESENT.map(x -> {
            throw new Exception();
        }, x -> 1000));
        Assertions.assertInstanceOf(ABSENT.getClass(), ABSENT.map(x -> {
            throw new Exception();
        }, x -> 1));
    }

    @Test
    public void unfailableMap_testAll_success() {
        Assertions.assertEquals(PRESENT.map(x -> "String").get(), "String");
        Assertions.assertEquals(ABSENT.map(x -> "String"), ABSENT);
    }

    @Test
    public void flatMap_testAll_success() {
        Assertions.assertEquals(PRESENT.flatMap(x -> Failable.success("String")).get(), "String");
        Assertions.assertEquals(ABSENT.flatMap(x -> Failable.success("String")), ABSENT);
    }

    @Test
    public void flatMap_throwingFunctionTestAll_success() {
        Assertions.assertInstanceOf(PRESENT.getClass(), PRESENT.flatMap(x -> Failable.success(2)));
        Assertions.assertInstanceOf(ABSENT.getClass(), PRESENT.flatMap(x -> Failable.empty()));

        Assertions.assertInstanceOf(ABSENT.getClass(), ABSENT.map(x -> {
            throw new Exception();
        }, x -> 100));
    }

    @Test
    public void unfailableFlatMap_testAll_success() {
        Assertions.assertEquals(PRESENT.flatMap(x -> Failable.success("String")).get(), "String");
        Assertions.assertEquals(ABSENT.flatMap(x -> Failable.success("String")), ABSENT);
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
    public void get_testAll_success() {
        Assertions.assertEquals(PRESENT.get(), 123);
        Assertions.assertThrows(NoSuchElementException.class, ABSENT::get);
    }

    @Test
    public void orElse_testAll_success() {
        Assertions.assertEquals(PRESENT.orElse(999), 123);
        Assertions.assertEquals(ABSENT.orElse(999), 999);
    }

    @Test
    public void equals_presentFailable_multipleTests() {
        Assertions.assertSame(PRESENT, PRESENT);
        Assertions.assertEquals(PRESENT, PRESENT);
        Assertions.assertNotEquals(PRESENT, ABSENT);
        Assertions.assertNotEquals(PRESENT, Failable.success("String"));
    }

    @Test
    public void equals_absentFailable_multipleTests() {
        Assertions.assertSame(ABSENT, ABSENT);
        Assertions.assertEquals(ABSENT, ABSENT);
        Assertions.assertNotEquals(ABSENT, PRESENT);
    }
}
