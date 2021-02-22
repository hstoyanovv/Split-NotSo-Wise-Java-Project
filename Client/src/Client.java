import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    private static final int SERVER_PORT = 8080;
    private static Socket socket;
    private static boolean closed = false;

    public static void main(String[] args) {

        try (Socket s = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(s.getOutputStream(), true); // autoflush on
             Scanner scanner = new Scanner(System.in);) {
             socket = s;

            Thread t = new Thread(new Client());
            t.start();

            final String nickname = "Please enter nickname:";
            final String password = "Please enter password:";
            System.out.println(nickname);
            String login = scanner.nextLine().trim();
            writer.println(login);
            System.out.println(password);
            login = scanner.nextLine().trim();
            writer.println(login);

            while (!closed) {
                String message = scanner.nextLine().trim(); // read a line from the console
                writer.println(message); // send the message to the server
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + e.getMessage());
        }
    }

    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String responeLine;

            final String disconnected = "Disconnected from server!";
            while (true) {
                responeLine = reader.readLine();
                System.out.println(responeLine);
                if (responeLine.equals(disconnected)) {
                    closed = true;
                    break;
                }
            }
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
