package list;

import java.io.PrintWriter;
import java.util.Map;

import static java.lang.Math.abs;

/**
 * Displays what you owe and what they owe you
 **/
public class Status {
    public static void friendStatus(PrintWriter out, Map<String, Double> friends) {
        final String friendName = "Friends:";
        out.println(friendName);
        out.flush();
        for (Map.Entry<String, Double> entry : friends.entrySet()) {
            double amount = entry.getValue();
            if (amount > 0) {
                final String message = entry.getKey() + " Ows You " + amount;
                out.println(message);
                out.flush();
            } else {
                final String message = "You Ows " + entry.getKey() + " " + abs(amount);
                out.println(message);
                out.flush();
            }
        }
    }

    public static void groupStatus(PrintWriter out, Map<String, Map<String, Double>> groups, String name) {
        final String groupName = "Groups:";
        out.println(groupName);
        out.flush();
        for (Map.Entry<String, Map<String, Double>> entry : groups.entrySet()) {
            out.println(entry.getKey());
            out.flush();
            Map<String, Double> users = entry.getValue();
            for (Map.Entry<String, Double> friend : users.entrySet()) {
                if (!friend.getKey().equals(name)) {
                    double amount = friend.getValue();
                    if (amount > 0) {
                        final String message = friend.getKey() + " Ows You " + amount;
                        out.println(message);
                        out.flush();
                    } else {
                        final String message = "You Ows " + friend.getKey() + " " + abs(amount);
                        out.println(message);
                        out.flush();
                    }
                }
            }
        }
    }
}
