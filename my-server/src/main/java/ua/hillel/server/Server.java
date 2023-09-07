package ua.hillel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server implements AutoCloseable, ConnectionHandler {

    private final static int DEFAULT_PORT = 8000;
    private final ServerSocket serverSocket;
    private final List<Connection> connections = new ArrayList<>();


    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

    }

    public Server() throws IOException {
        this(DEFAULT_PORT);
    }

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                Connection connection = new Connection(clientSocket, this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void close() throws Exception {
        serverSocket.close();
    }

    @Override
    public void onConnect(Connection connection) {
        connections.add(connection);
        connection.sendMessage("SERVER COMMANDS: \n [-exit] - для відключення; \n " +
                "[-file /filePath] - щоб зберегти файл на сервері;");
        connections.forEach(c -> c.sendMessage(String.format("[%s] - успішно підключений", connection.getName())));
    }

    @Override
    public void onMessage(Connection connection, String message) {
        for (Connection connect : connections) {
            if (!connect.equals(connection)) {
                connect.sendMessage(String.format("[%s]: %s", connection.getName(), message));
            }
        }
    }

    @Override
    public void onDisconnect(Connection connection) {
        connections.remove(connection);
        connections.forEach(c -> c.sendMessage(String.format("[%s] - відєднався", connection.getName())));
    }

    public List<Connection> getConnections() {
        return connections;
    }
}
