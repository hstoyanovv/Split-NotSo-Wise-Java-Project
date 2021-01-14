package add;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AddUserToLogin {

    public static void addUserToLoginFile(String nickname, String password) {
        if (nickname == null || password == null) {
            throw new IllegalArgumentException("Username or password is empty! ");
        }
        final String filename = "Login.txt";
        Path path = Paths.get(filename);
        try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.APPEND)) {
            writer.write(nickname + " ");
            writer.write(password);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + filename);
        }
    }
}
