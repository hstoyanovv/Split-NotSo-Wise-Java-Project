import org.junit.Before;
import org.junit.Test;
import server.ClientRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.abs;
import static junit.framework.TestCase.*;
import static org.junit.Assert.assertEquals;


public class ClientRequestHandlerTest {
    final String mimi = "Mimi";
    final String icko = "Icko";
    final String misho = "Misho";
    final String mimiPassword = "88";
    final String ickoPassword = "333";
    final String mishoPassword = "123";
    final int mimiNumber = 0;
    final int ickoNumber = 1;
    final int mishoNumber = 2;

    Map<String, ClientRequestHandler> onlineUsers = new ConcurrentHashMap<>();
    Map<String, String> registeredUsers = new ConcurrentHashMap<>();

    @Before
    public void setUp() {
        try (ServerSocket serverSocket = new ServerSocket(222);
             Socket socket = new Socket("localhost", 222);) {
            ClientRequestHandler userMimi = new ClientRequestHandler(socket, onlineUsers, registeredUsers, mimiNumber);
            ClientRequestHandler userIcko = new ClientRequestHandler(socket, onlineUsers, registeredUsers, ickoNumber);
            ClientRequestHandler userMisho = new ClientRequestHandler(socket, onlineUsers, registeredUsers, mishoNumber);
            registeredUsers.put(mimi, mimiPassword);
            registeredUsers.put(icko, ickoPassword);
            registeredUsers.put(misho, mishoPassword);
            onlineUsers.put(mimiNumber + "", userMimi);
            onlineUsers.put(ickoNumber + "", userIcko);
            onlineUsers.put(mishoNumber + "", userMisho);
            userMimi.updateClients(mimi, mimiPassword);
            userIcko.updateClients(icko, ickoPassword);
            userMisho.updateClients(misho, mishoPassword);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = NullPointerException.class)
    public void testInvalidClient() {
        new ClientRequestHandler(null, null, null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCheckCommand() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        client.checkCommandType(null);
    }

    @Test
    public void testAddFriend() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        client.checkCommandType("add-friend Icko");

        assertNotNull(client.getFriends());
    }

    @Test
    public void testSplittingBetweenFriends() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        client.checkCommandType("add-friend Icko");

        final String info = "10 Icko fmi";
        client.checkCommandType("split " + info);
        Map<String, Double> friends = client.getFriends();

        final double expectedAmount = friends.get(icko);
        final double actualAmount = 5.0;

        assertTrue(actualAmount == expectedAmount);
    }


    @Test
    public void testStatus() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        client.checkCommandType("add-friend Icko");
        final String info = "qkite Icko Mimi Misho";
        client.checkCommandType("create-group " + info);
        client.checkCommandType("get-status");
    }

    @Test
    public void testCreatingGroup() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        final String info = "qkite Icko Mimi Misho";
        client.checkCommandType("create-group " + info);

        final String groupName = "qkite";
        Map<String, Map<String, Double>> groups = client.getGroups();
        Map<String, Double> group = groups.get(groupName);

        assertNotNull(group);
    }

    @Test
    public void testReadFriends() {
        ClientRequestHandler client = onlineUsers.get(misho);
        Map<String, Double> friends = client.getFriends();
        friends.remove(mimi);
        assertNotNull(client.getFriends());

    }

    @Test
    public void testListGroups() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        final String info = "90Kids Icko Mimi Misho";
        client.checkCommandType("create-group " + info);
        client.checkCommandType("add-friend Icko");
        client.checkCommandType("list-friends");
        client.checkCommandType("list-groups");
    }

    @Test
    public void testSplitBetweenGroup() {
        ClientRequestHandler client = onlineUsers.get(mimi);
        final String groupInfo = "qkite Icko Mimi Misho";
        client.checkCommandType("create-group " + groupInfo);
        final String split = "12 qkite fmi";
        client.checkCommandType("split-group " + split);

        final String groupName = "qkite";
        Map<String, Map<String, Double>> groups = client.getGroups();
        Map<String, Double> group = groups.get(groupName);
        double actualAmount = 0;
        for (Map.Entry<String, Double> user : group.entrySet()) {
            actualAmount += abs(user.getValue());
        }
        final double expectedAmount = 8.0;

        assertTrue(actualAmount == expectedAmount);

    }

    @Test
    public void testValidteIfNotRegister() {
        final String host = "localhost";
        try (ServerSocket serverSocket = new ServerSocket(222);
             Socket socket = new Socket(host, 222);) {
            final int clientNumber = 4;
            ClientRequestHandler client = new ClientRequestHandler(socket, onlineUsers, registeredUsers, clientNumber);
            onlineUsers.put(clientNumber + "", client);
            final String userName = "Pesho";
            final String userPassword = "666";

            client.validateUser(userName, userPassword);
            assertEquals(client.getName(), userName);
        } catch (IOException e) {
            e.getMessage();
        }
    }

    @Test
    public void testValidateIfRegister() {
        final String host = "localhost";
        try (ServerSocket serverSocket = new ServerSocket(333);
             Socket socket = new Socket(host, 333);) {
            final int clientNumber = 5;
            final String userName = "Petko";
            final String userPassword = "888";
            ClientRequestHandler client = new ClientRequestHandler(socket, onlineUsers, registeredUsers, clientNumber);
            onlineUsers.put(clientNumber + "", client);
            registeredUsers.put(userName, userPassword);
            client.validateUser(userName, userPassword);

            assertNotNull(client.getFriends());

            onlineUsers.remove(userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPaymentInGroup() {

        ClientRequestHandler client = onlineUsers.get(mimi);
        ClientRequestHandler client1 = onlineUsers.get(icko);
        final String groupName = "qkite";
        final String groupInfo = "qkite Icko Mimi Misho";
        client.checkCommandType("create-group " + groupInfo);
        final String split = "12 qkite fmi";
        client.checkCommandType("split-group " + split);

        final String info = "qkite 3.0 Icko";
        client.checkCommandType("payed-group " + info);

        Map<String, Double> group = client1.getGroups().get(groupName);
        double actualAmount = 0;
        for (Map.Entry<String, Double> user : group.entrySet()) {
            actualAmount += abs(user.getValue());
        }
        final double expectedAmount = 1.0;

        assertTrue(actualAmount == expectedAmount);

    }
}
