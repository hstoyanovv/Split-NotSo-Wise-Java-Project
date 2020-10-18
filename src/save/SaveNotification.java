package save;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.file.Files.exists;

public class SaveNotification {
    public static void saveNotification(String name, String info) {
        try {
            if (name == null || info == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            i.printStackTrace();
        }

        final String filename = name + "Notifications.txt";
        Path path = Paths.get(filename);
        if (exists(path)) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.APPEND)) {
                writer.write(info);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            }
        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.CREATE)) {
                writer.write(info);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.toString());
                System.out.println("Could not find file " + filename);
            }
        }
    }
}
