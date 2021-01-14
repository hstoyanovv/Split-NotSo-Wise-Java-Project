package splitANDpay;

import save.SaveNotification;
import server.ClientRequestHandler;

import java.util.Map;

public class PayToUserInGroup {
    static void setPaymentToUser(String nickname, String groupName, double amount, String name,
                                 Map<String, ClientRequestHandler> onlineUsers) {
        if (nickname == null || groupName == null) {
            throw new IllegalArgumentException("nickname or groupname must not be null! ");
        }

        ClientRequestHandler client = onlineUsers.get(nickname);
        if (client != null) {
            Map<String, Double> userGroup = client.getGroups().get(groupName);
            if (userGroup != null) {
                for (Map.Entry<String, Double> user : userGroup.entrySet()) {
                    if (user.getKey().equals(name)) {
                        double newAmount = user.getValue() + amount;
                        userGroup.put(name, newAmount);
                        break;
                    }
                }
            }
        } else {
            final String notification = name + " approved your payment to group " + groupName + " " + (amount + "");
            SaveNotification.saveNotification(nickname, notification);
        }
    }

    public static void payToUserInGroup(String info, Map<String, Map<String, Double>> groups, String name,
                                        Map<String, ClientRequestHandler> onlineUsers) {
        if (info != null) {
            String[] words = info.split(" ", 3);
            String groupName = words[0];
            Map<String, Double> group = groups.get(groupName);

            if (group != null) {
                double amount = Double.parseDouble(words[1]);
                String nickname = words[2];
                for (Map.Entry<String, Double> user : group.entrySet()) {
                    if (user.getKey().equals(nickname)) {
                        double newAmount = user.getValue() - amount;
                        group.put(nickname, newAmount);
                        break;
                    }
                }
                setPaymentToUser(nickname, groupName, amount, name, onlineUsers);
            }
        }
    }
}
