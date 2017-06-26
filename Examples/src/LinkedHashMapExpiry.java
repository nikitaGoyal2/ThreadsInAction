import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by nikigoya on 6/26/2017.
 */
public class LinkedHashMapExpiry {
    public static void main(String[] args) {
        int MAX_ENTRIES = 3;
        LinkedHashMap map = new LinkedHashMap(MAX_ENTRIES + 1, .75F, false) {

            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_ENTRIES;
            }
        };
        map.put(0, "H");
        map.put(1, "E");
        map.put(2, "L");
        map.put(3, "L");
        map.put(0, "H");
        map.put(0, "H");
        map.put(0, "H");
        map.put(4, "O");

        System.out.println("" + map);
    }
}
