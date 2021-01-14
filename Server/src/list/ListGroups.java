package list;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Print GroupList
 **/
public class ListGroups {
    public static void listGroups(PrintWriter out, Map<String, Map<String, Double>> groups, String name) {
        for (Map.Entry<String, Map<String, Double>> entry : groups.entrySet()) {
            out.println(entry.getKey());
            out.flush();
            Map<String, Double> users = entry.getValue();
            for (Map.Entry<String, Double> user : users.entrySet()) {
                out.println(user.getKey());
                out.flush();
            }
        }
    }
}
