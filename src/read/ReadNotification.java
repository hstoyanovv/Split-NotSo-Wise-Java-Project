package read;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Map;

import static java.nio.file.Files.exists;

/**
 * Displays notifications when the user has been offline
 **/
public class ReadNotification {

    public static void readNotificationFromFriend(String line, Map<String, Double> friends) {
        String[] words = line.split(" ", 5);
        String friendName = words[2];
        if (friends.containsKey(friendName)) {
            double amount = Double.valueOf(words[3]);
            double newAmount = friends.get(friendName) - amount;
            friends.put(friendName, newAmount);
        }
    }

    public static void readNotifications(String nickname, Map<String, Double> friends, PrintWriter out) {
        try {
            if (nickname == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Username is empty! " + i.getMessage());
        }

        final String filename = nickname + "Notifications.txt";
        Path file = Path.of(filename);
        if (exists(file)) {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(filename));) {
                while ((line = reader.readLine()) != null) {
                    readNotificationFromFriend(line, friends);
                    out.println(line);
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            }
        }
    }
}
