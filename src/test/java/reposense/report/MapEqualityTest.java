package reposense.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapEqualityTest {

    @Test
    public void check_map_equality() {
        String a = "a";
        String b = "b";
        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();

        // map1:  map2:  map3:
        assertEquals(map1, map2);
        assertEquals(map1, map3);

        //map1: a,b map2: a,b map3:
        map1.put(a, b);
        map2.put(a, b);
        assertEquals(map1, map2);
        assertNotEquals(map1, map3);
        assertNotEquals(map2, map3);

        //map1: a,b map2: a,b map3: a,b
        map3.put(a, b);
        assertEquals(map1, map3);
        assertEquals(map2, map3);
    }
}
