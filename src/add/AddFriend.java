package add;

import serverANDclient.ClientRequestHandler;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Remove friend is equal to denied request
 **/
public class AddFriend {
    public static void addFriend(String nickname, boolean flag, String userName, PrintWriter out,
                                 Map<String, Double> friends, Map<String, String> registeredUsers, Map<String, ClientRequestHandler> onlineUsers) {
        try {
            if (nickname == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Username is empty! " + i.getMessage());
        }
        String name = registeredUsers.get(nickname);
        String message;
        if (name != null) {
            ClientRequestHandler user = onlineUsers.get(nickname); // get onlineUsers
            if (user != null && flag) {
                message = userName + " Added you to a friend!";
                friends.put(nickname, 0.0);
                user.getFriends().put(userName, 0.0);
                user.getOutputStream().println(message);
                user.getOutputStream().flush();
            } else if (user != null) {
                message = userName + " removed you from friends!";
                friends.remove(nickname);
                user.getFriends().remove(userName);
                user.getOutputStream().println(message);
                user.getOutputStream().flush();
            } else {
                message = "User is not online to add/remove in friends sorry!";
                out.println(message);
                out.flush();
            }
        } else {
            message = "This user doesn't exist!";
            out.println(message);
            out.flush();
        }
    }
}
