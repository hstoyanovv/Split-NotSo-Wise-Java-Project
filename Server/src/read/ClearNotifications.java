package read;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.exists;

public class ClearNotifications {
    public static void clearNotifications(String nickname) {
        final String filename = nickname + "Notifications.txt";
        Path file = Path.of(filename);
        if (exists(file)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename));) {
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            }
        }
    }

}
