package add;

import save.SaveGroups;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.Files.exists;

public class AddToOfflineGroup {

    public static void addGroupToOfflineUser(String nickname, Map<String, Double> group, String groupName) {
        if (nickname == null || group == null) {
            throw new IllegalArgumentException();
        }
        final String filename = nickname + "Groups.bin";
        Path file = Path.of(filename);
        Map<String, Map<String, Double>> groups = new ConcurrentHashMap<>();
        if (exists(file)) {
            try (var ois = new ObjectInputStream(Files.newInputStream(file))) {

                while (true) {
                    groups = (ConcurrentHashMap) ois.readObject();
                }
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
        groups.put(groupName, group);
        SaveGroups.saveAllGroups(nickname, groups); // saves all groups in which the user participates
    }
}
