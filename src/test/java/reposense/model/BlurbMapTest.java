package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlurbMapTest {
    @Test
    public void blurbBuilder_testIfBuildsSuccessfully_success() {
        BlurbMap.Builder builder = new BlurbMap.Builder();
        builder.withRecord("hello", "world");
        BlurbMap.Builder newBuilder = new BlurbMap.Builder();
        newBuilder.withRecord("hello", "world");

        Assertions.assertEquals(builder.build(), newBuilder.build());
    }

    @Test
    public void blurbBuilder_testIfBuildsEmpty_success() {
        BlurbMap map1 = new BlurbMap.Builder().build();
        BlurbMap map2 = new BlurbMap.Builder().build();
        Assertions.assertEquals(map1, map2);
    }

    @Test
    public void blurbBuilder_testIfUnequal_success() {
        BlurbMap.Builder builder1 = new BlurbMap.Builder();
        BlurbMap.Builder builder2 = new BlurbMap.Builder();

        builder1.withRecord("this", "builder");
        builder2.withRecord("other", "builder");
        Assertions.assertNotEquals(builder1.build(), builder2.build());
    }
}
