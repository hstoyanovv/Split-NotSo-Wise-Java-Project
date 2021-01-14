package server;

import add.*;
import list.*;
import read.*;
import save.*;
import splitANDpay.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRequestHandler implements Runnable {
    private String name;
    private String password;
    private final int clientNumber;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Map<String, Double> friends;
    private Map<String, Map<String, Double>> groups;

    private final Map<String, String> registeredUsers;
    private final Map<String, ClientRequestHandler> onlineUsers;

    public ClientRequestHandler(Socket socket, Map<String, ClientRequestHandler> onlineUsers, Map<String, String> registeredUsers, int number) {
        this.socket = socket;
        this.registeredUsers = registeredUsers;
        this.onlineUsers = onlineUsers;
        this.clientNumber = number;

        friends = new ConcurrentHashMap<>();
        groups = new ConcurrentHashMap<>();
        try {
            out = new PrintWriter(this.socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public PrintWriter getOutputStream() {
        return out;
    }

    public Map<String, ClientRequestHandler> getOnlineUsers() {
        return onlineUsers;
    }

    public Map<String, Double> getFriends() {
        return friends;
    }

    public Map<String, Map<String, Double>> getGroups() {
        return groups;
    }

    public String getName() {
        return name;
    }

    /*** Update client user and password ***/
    public void updateClients(String nickname, String userPassword) {
        if (nickname == null || userPassword == null) {
            throw new IllegalArgumentException("Username or password is empty! ");
        }
        this.name = nickname;
        this.password = userPassword;
        ClientRequestHandler client = onlineUsers.get(clientNumber + "");
        onlineUsers.remove(clientNumber + "");
        onlineUsers.put(this.name, client);
    }

    /*** Loading user information ***/
    public synchronized boolean validateUser(String nickname, String userPassword) {
        if (nickname == null || userPassword == null) {
            throw new IllegalArgumentException("Username or password is empty! ");
        }
        final String connected = "Connected to the Split(NotSo)Wise! Enjoy!";
        String pass = registeredUsers.get(nickname);
        if (pass != null && pass.equals(userPassword)) {
            friends = ReadFriends.readFriends(nickname);
            groups = ReadGroups.readGroups(nickname);
            out.println(connected);
            out.flush();
            ReadNotification.readNotifications(nickname, friends, out);
            ClearNotifications.clearNotifications(nickname);
            updateClients(nickname, userPassword);
        } else if (pass == null) {
            registeredUsers.put(nickname, userPassword);
            updateClients(nickname, userPassword);
            AddUserToLogin.addUserToLoginFile(nickname, userPassword);
            out.println(connected);
            out.flush();
        } else {
            final String offlineUser = "This nickname already exist! Please choose another!";
            out.println(offlineUser);
            out.flush();
            return false;
        }
        return true;
    }

    /*** Checking commandtype ***/
    public void checkCommandType(String inputLine) {
        if (inputLine == null) {
            throw new IllegalArgumentException("Input must not be null");
        }
        final String[] type = {"Who is online?", "add-friend", "create-group", "split", "split-group",
                "payed", "payed-group", "get-status", "list-friends", "list-groups", "remove-friend"};
        final int regexLimit = 2;
        final int lengthLimit = 1;
        final int command = 0;
        String[] lines = inputLine.split(" ", regexLimit);
        if (inputLine.equals(type[0])) {
            ListOnlineUsers.listOnlineUsers(out, onlineUsers);
        } else if (lines[command].equals(type[1]) && lines.length > lengthLimit) {
            AddFriend.addFriend(lines[lengthLimit], true, this.name, out, friends, registeredUsers, onlineUsers);
        } else if (lines[command].equals(type[2]) && lines.length > lengthLimit) {
            CreateGroup.createGroup(lines[lengthLimit], groups, this, onlineUsers);
        } else if (lines[command].equals(type[3]) && lines.length > lengthLimit) {
            final double delimer = 2;
            SplitBetweenFriends.splitBetweenFriends(lines[lengthLimit], delimer, false, friends, this.name, onlineUsers);
        } else if (lines[command].equals(type[4]) && lines.length > lengthLimit) {
            SplitBetweenGroup.splitBetweenGroup(lines[lengthLimit], this.name, groups, onlineUsers);
        } else if (lines[command].equals(type[5]) && lines.length > lengthLimit) {
            final double delimer = 1;
            SplitBetweenFriends.splitBetweenFriends(lines[lengthLimit], delimer, true, friends, this.name, onlineUsers);
        } else if (lines[command].equals(type[6]) && lines.length > lengthLimit) {
            PayToUserInGroup.payToUserInGroup(lines[lengthLimit], groups, this.name, onlineUsers);
        } else if (lines[command].equals(type[7])) {
            Status.friendStatus(out, friends);
            Status.groupStatus(out, groups, this.name);
        } else if (inputLine.equals(type[8])) {
            ListFriends.listFriends(out, friends);
        } else if (inputLine.equals(type[9])) {
            ListGroups.listGroups(out, groups, this.name);
        } else if (lines[command].equals(type[10])) {
            AddFriend.addFriend(lines[lengthLimit], false, this.name, out, friends, registeredUsers, onlineUsers);
        } else {
            final String message = "Please enter valid command!";
            out.println(message);
            out.flush();
        }
    }

    public void validateStatus() {
        try {
            boolean validateStatus = false;
            while (!validateStatus) {
                String nickname = in.readLine(); // set name and password
                String userPassword = in.readLine();
                validateStatus = validateUser(nickname, userPassword);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            validateStatus();
            String inputLine;
            final String quit = "quit";
            while ((inputLine = in.readLine()) != null) { // read the message from the client
                if (inputLine.equals(quit)) {
                    final String message = "Disconnected from server!";
                    out.println(message);
                    SaveFriends.saveFriends(this.name, friends);
                    SaveGroups.saveAllGroups(this.name, groups);
                    break;
                }
                checkCommandType(inputLine);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                out.close();
                in.close();
                socket.close();
                onlineUsers.remove(this.name);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}

