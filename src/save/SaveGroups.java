package save;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class SaveGroups {
    public static void saveAllGroups(String nickname, Map<String, Map<String, Double>> groups) {
        try {
            if (nickname == null || groups == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Username or password is empty! " + i.getMessage());
        }

        final String filename = nickname + "Groups.bin";
        Path file = Path.of(filename);
        try (var oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(groups);
            oos.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + filename);
        }
    }
}
