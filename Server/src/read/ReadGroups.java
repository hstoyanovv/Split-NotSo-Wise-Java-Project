package read;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.Files.exists;

/**
 * Loading your groups
 **/
public class ReadGroups {
    public static Map<String, Map<String, Double>> readGroups(String nickname) {
        if (nickname == null) {
            throw new IllegalArgumentException("Username is empty! ");
        }
        final String filename = nickname + "Groups.bin";
        Map<String, Map<String, Double>> groups = new ConcurrentHashMap<>();
        Path file = Path.of(filename);
        if (exists(file)) {
            try (var ois = new ObjectInputStream(Files.newInputStream(file))) {
                groups = (ConcurrentHashMap) ois.readObject();
            } catch (EOFException e) {
                System.out.println(e.getMessage());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            } catch (ClassNotFoundException e) {
                System.out.println("Caught Exception: " + e.getMessage());
            }
        }
        return groups;
    }
}
