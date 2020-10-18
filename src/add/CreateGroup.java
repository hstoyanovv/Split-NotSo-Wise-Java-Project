package add;

import serverANDclient.ClientRequestHandler;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup {

    static void createGroupForOthers(String[] nicknames, String groupName, ClientRequestHandler current,
                                     Map<String, ClientRequestHandler> onlineUsers) {
        if (nicknames == null || groupName == null) {
            throw new IllegalArgumentException();
        }
        for (String user : nicknames) {
            ClientRequestHandler client = onlineUsers.get(user);
            if (client != current) {
                Map<String, Double> userToFriends = new HashMap<>();
                for (String person : nicknames) {
                    userToFriends.put(person, 0.0);
                }
                if (client != null) {
                    client.getGroups().put(groupName, userToFriends);
                    final String notification = current.getName() + " created group " + groupName + " with you and other guys!";
                    client.getOutputStream().println(notification);
                    client.getOutputStream().flush();
                } else {
                    AddToOfflineGroup.addGroupToOfflineUser(user, userToFriends, groupName);
                    //final String notification = name + " created group " + groupName + " with you and other guys!";
                    //saveNotification(user, notification);
                }
            }
        }
    }

    public static void createGroup(String info, Map<String, Map<String, Double>> groups, ClientRequestHandler current,
                                   Map<String, ClientRequestHandler> onlineUsers) {
        if (info != null) {
            String[] words = info.split(" ", 2);
            if (words.length > 1 && !words[1].isEmpty()) {
                String[] nicknames = words[1].split(" ");
                Map<String, Double> users = new HashMap<>();
                String groupName = words[0];
                for (String nickname : nicknames) {
                    users.put(nickname, 0.0);
                }
                groups.put(groupName, users);
                createGroupForOthers(nicknames, groupName, current, onlineUsers); // creating that group for others
            }
        }
    }
}
