package splitANDpay;

import save.SaveNotification;
import save.SaveTransaction;
import server.ClientRequestHandler;

import java.util.Map;

public class SplitBetweenGroup {
    static void splitBetweenGroupForOthers(String groupName, Map<String, Double> users, double amount, String reason,
                                           String name, Map<String, ClientRequestHandler> onlineUsers) {
        if (groupName == null) {
            throw new IllegalArgumentException("Groupname is empty! ");
        }
        for (Map.Entry<String, Double> person : users.entrySet()) {
            if (!person.getKey().equals(name)) {
                ClientRequestHandler client = onlineUsers.get(person.getKey());
                if (client != null) {
                    Map<String, Double> group = client.getGroups().get(groupName);
                    for (Map.Entry<String, Double> user : group.entrySet()) {
                        if (user.getKey().equals(name)) {
                            double newAmount = user.getValue() - amount;
                            group.put(name, newAmount);
                            final String message = name + " splitted " + amount + " in group " + groupName + " " + reason;
                            client.getOutputStream().println(message);
                            client.getOutputStream().flush();
                            break;
                        }
                    }
                } else {
                    final String notification = "You owe " + name + " " + "group:" + groupName + " " + amount + " " + reason;
                    SaveNotification.saveNotification(person.getKey(), notification);
                }
            }
        }
    }

    public static void splitBetweenGroup(String info, String name, Map<String, Map<String, Double>> groups,
                                         Map<String, ClientRequestHandler> onlineUsers) {
        try {
            if (info == null) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException i) {
            System.out.println("Information is missing! " + i.getMessage());
        }

        String[] words = info.split(" ", 3);
        String groupName = words[1];
        String reason = words[2];
        Map<String, Double> group = groups.get(groupName);
        if (group != null) {
            final double delimer = group.size();
            double amount = Double.valueOf(words[0]) / delimer;
            for (Map.Entry<String, Double> user : group.entrySet()) {
                if (!user.getKey().equals(name)) {
                    double newAmount = user.getValue() + amount;
                    group.put(user.getKey(), newAmount);
                }
            }
            final String typeName = "split-group ";
            SaveTransaction.saveTransaction(typeName + info, name);
            splitBetweenGroupForOthers(groupName, group, amount, reason, name, onlineUsers);
        }
    }
}
