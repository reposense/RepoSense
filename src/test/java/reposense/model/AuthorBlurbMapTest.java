package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorBlurbMapTest {
    @Test
    public void blurbBuilder_testIfBuildsSuccessfully_success() {
        AuthorBlurbMap builder = new AuthorBlurbMap();
        builder.withRecord("hello", "world");
        AuthorBlurbMap newBuilder = new AuthorBlurbMap();
        newBuilder.withRecord("hello", "world");

        Assertions.assertEquals(builder, newBuilder);
    }

    @Test
    public void blurbBuilder_testIfBuildsEmpty_success() {
        AuthorBlurbMap map1 = new AuthorBlurbMap();
        AuthorBlurbMap map2 = new AuthorBlurbMap();
        Assertions.assertEquals(map1, map2);
    }

    @Test
    public void blurbBuilder_testIfUnequal_success() {
        AuthorBlurbMap builder1 = new AuthorBlurbMap();
        AuthorBlurbMap builder2 = new AuthorBlurbMap();

        builder1.withRecord("this", "builder");
        builder2.withRecord("other", "builder");
        Assertions.assertNotEquals(builder1, builder2);
    }
}
