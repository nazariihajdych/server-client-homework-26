package ua.hillel.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server implements AutoCloseable, ConnectionHandler {

    private final static int DEFAULT_PORT = 8000;
    private final Lock lock;
    private final ServerSocket serverSocket;
    private List<Connection> connections = new ArrayList<>();


    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        lock = new ReentrantLock();

    }

    public Server() throws IOException {
        this(DEFAULT_PORT);
    }

    public void start() {
        while (!serverSocket.isClosed()){
            try {
                Socket accept = serverSocket.accept();
                Connection connection = new Connection(accept, this);
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
        lock.lock();
        connections.add(connection);
        connection.sendMessage("SERVER COMMANDS: \n [-exit] - для відключення; \n " +
                "[-file /filePath] - щоб зберегти файл на сервері;");
        connections.forEach(c -> c.sendMessage(String.format("[%s] - успішно підключений", connection.getName())));
        lock.unlock();
    }

    @Override
    public void onMessage(Connection connection, String message) {
        for (Connection connect: connections) {
            if (!connect.equals(connection)){
                connect.sendMessage(String.format("[%s]: %s", connection.getName(), message));
            }
        }
    }

    @Override
    public void onDisconnect(Connection connection) {
        lock.lock();
        connections.remove(connection);
        connections.forEach(c -> c.sendMessage(String.format("[%s] - відєднався", connection.getName())));
        lock.unlock();
    }
}
