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
}
