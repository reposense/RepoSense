package reposense.model;

import org.junit.jupiter.api.Test;

public class ChartBlurbMapTest {
    @Test
    public void blurbBuilder_testIfBuildsSuccessfully_success() {
        ChartBlurbMap builder = new ChartBlurbMap();
        builder.withRecord("hello", "world");
        ChartBlurbMap newBuilder = new ChartBlurbMap();
        newBuilder.withRecord("hello", "world");

        assert builder.equals(newBuilder);
    }

    @Test
    public void blurbBuilder_testIfBuildsEmpty_success() {
        ChartBlurbMap map1 = new ChartBlurbMap();
        ChartBlurbMap map2 = new ChartBlurbMap();
        assert map1.equals(map2);
    }

    @Test
    public void blurbBuilder_testIfUnequal_success() {
        ChartBlurbMap builder1 = new ChartBlurbMap();
        ChartBlurbMap builder2 = new ChartBlurbMap();

        builder1.withRecord("this", "builder");
        builder2.withRecord("other", "builder");
        assert !builder1.equals(builder2);
    }
}
