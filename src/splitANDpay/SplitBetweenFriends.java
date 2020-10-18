package splitANDpay;

import save.SaveNotification;
import save.SaveTransaction;
import serverANDclient.ClientRequestHandler;

import java.util.Map;

import static java.lang.Math.abs;


public class SplitBetweenFriends { // splitting the money for the other guy
    static void splitFriendsForFriend(String friendName, double amount, String reason, boolean payed, String name,
                                      Map<String, ClientRequestHandler> onlineUsers) {
        try {
            if (friendName == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Friendname is empty! " + i.getMessage());
        }

        ClientRequestHandler client = onlineUsers.get(friendName);
        if (client != null && client.getFriends().containsKey(name)) {
            double newAmount = client.getFriends().get(name) - amount;
            client.getFriends().put(name, newAmount);
            if (!payed) {
                final String message = name + " splitted with you " + amount + " " + reason;
                client.getOutputStream().println(message);
                client.getOutputStream().flush();
            } else {
                final String message = name + " approved your payment " + abs(amount) + " [" + reason + "] ";
                client.getOutputStream().println(message);
                client.getOutputStream().flush();
            }
        } else {
            final String notification = "You owe " + name + " " + (amount + "") + " " + reason;
            SaveNotification.saveNotification(friendName, notification);
        }
    }

    public static void splitBetweenFriends(String info, double delimer, boolean payed, Map<String, Double> friends, String name,
                                           Map<String, ClientRequestHandler> onlineUsers) {
        try {
            if (info == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Information is missing! " + i.getMessage());
        }

        String reason;
        String[] words = info.split(" ", 3);
        double amount = Double.valueOf(words[0]) / delimer;
        if (delimer == 1) {
            amount = -amount;
            reason = "payment";
        } else {
            reason = words[2];
        }
        String friendName = words[1];

        if (friends.containsKey(friendName)) {
            double newAmount = friends.get(friendName) + amount;
            friends.put(friendName, newAmount);
            splitFriendsForFriend(friendName, amount, reason, payed, name, onlineUsers);
            if (!payed) {
                SaveTransaction.saveTransaction("split " + info, name);
            } else {
                SaveTransaction.saveTransaction("payed " + info, name);
            }

        }
    }
}
