package ua.hillel.server;

public interface ConnectionHandler {
    void onConnect(Connection connection);
    void onMessage(Connection connection, String message);
    void onDisconnect(Connection connection);
}
