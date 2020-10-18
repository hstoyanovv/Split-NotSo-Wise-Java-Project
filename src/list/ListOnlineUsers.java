package list;

import serverANDclient.ClientRequestHandler;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Print Online users
 **/
public class ListOnlineUsers {
    public static void listOnlineUsers(PrintWriter out, Map<String, ClientRequestHandler> onlineUsers) {
        for (ClientRequestHandler client : onlineUsers.values()) {
            out.println(client.getName());
            out.flush();
        }
    }
}
