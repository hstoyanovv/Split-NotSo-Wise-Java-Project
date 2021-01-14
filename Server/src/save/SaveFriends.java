package save;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;


public class SaveFriends {
    public static void saveFriends(String name, Map<String, Double> friends) {
        final String filename = name + "Friends.bin";
        Path file = Path.of(filename);
        try (var oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(friends);
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + filename);
        }
    }
}
