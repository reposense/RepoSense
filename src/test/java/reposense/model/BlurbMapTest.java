package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlurbMapTest {
    @Test
    public void blurbBuilder_testIfBuildsSuccessfully_success() {
        BlurbMap builder = new BlurbMap();
        builder.withRecord("hello", "world");
        BlurbMap newBuilder = new BlurbMap();
        newBuilder.withRecord("hello", "world");

        Assertions.assertEquals(builder, newBuilder);
    }

    @Test
    public void blurbBuilder_testIfBuildsEmpty_success() {
        BlurbMap map1 = new BlurbMap();
        BlurbMap map2 = new BlurbMap();
        Assertions.assertEquals(map1, map2);
    }

    @Test
    public void blurbBuilder_testIfUnequal_success() {
        BlurbMap builder1 = new BlurbMap();
        BlurbMap builder2 = new BlurbMap();

        builder1.withRecord("this", "builder");
        builder2.withRecord("other", "builder");
        Assertions.assertNotEquals(builder1, builder2);
    }
}
