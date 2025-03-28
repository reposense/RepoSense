package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepoBlurbMapTest {
    @Test
    public void blurbBuilder_testIfBuildsSuccessfully_success() {
        RepoBlurbMap builder = new RepoBlurbMap();
        builder.withRecord("hello", "world");
        RepoBlurbMap newBuilder = new RepoBlurbMap();
        newBuilder.withRecord("hello", "world");

        Assertions.assertEquals(builder, newBuilder);
    }

    @Test
    public void blurbBuilder_testIfBuildsEmpty_success() {
        RepoBlurbMap map1 = new RepoBlurbMap();
        RepoBlurbMap map2 = new RepoBlurbMap();
        Assertions.assertEquals(map1, map2);
    }

    @Test
    public void blurbBuilder_testIfUnequal_success() {
        RepoBlurbMap builder1 = new RepoBlurbMap();
        RepoBlurbMap builder2 = new RepoBlurbMap();

        builder1.withRecord("this", "builder");
        builder2.withRecord("other", "builder");
        Assertions.assertNotEquals(builder1, builder2);
    }
}
