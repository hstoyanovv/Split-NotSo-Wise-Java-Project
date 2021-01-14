package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static final int SERVER_PORT = 8080;
    private static final Map<String, ClientRequestHandler> onlineUsers = new ConcurrentHashMap<>();
    private static final Map<String, String> registeredUsers = new ConcurrentHashMap<>();
    private static int size = 0;

    static void readUsers() {
        final String filename = "Login.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filename));) {
            String line;
            final int regexLimit = 2;
            while ((line = reader.readLine()) != null) {
                String[] login = line.split(" ", regexLimit);
                registeredUsers.put(login[0], login[1]);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + filename);
        }
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);) {

            System.out.println("Server started and listening for connect requests");
            readUsers();

            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket, onlineUsers, registeredUsers, size);
                onlineUsers.put("" + size++, clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            System.out.println("Could not find file " + e.getMessage());
        }
    }

}
