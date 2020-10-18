package list;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Print friendList
 **/
public class ListFriends {
    public static void listFriends(PrintWriter out, Map<String, Double> friends) {
        for (Map.Entry<String, Double> entry : friends.entrySet()) {
            out.println(entry.getKey());
            out.flush();
        }
    }
}
