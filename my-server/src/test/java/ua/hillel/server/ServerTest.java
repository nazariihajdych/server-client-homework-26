package ua.hillel.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class ServerTest {

    @Mock
    private ServerSocket serverSocketMock;
    @Mock
    private Socket socketMock;
    @Mock
    private Connection connectionMock;
    private Server server;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        server = new Server(serverSocketMock);
        when(socketMock.getInputStream()).thenReturn(new ByteArrayInputStream("message".getBytes()));
        when(socketMock.getOutputStream()).thenReturn(new ByteArrayOutputStream(256));
        when(serverSocketMock.accept()).thenReturn(socketMock);
        when(connectionMock.getName()).thenReturn("test-client");

    }

    @Test
    public void startTest_success() throws IOException {
        server.start();

        verify(serverSocketMock, atLeastOnce()).accept();
        verify(connectionMock).sendMessage("SERVER COMMANDS: \n [-exit] - для відключення; \n " +
                "[-file /filePath] - щоб зберегти файл на сервері;");
    }

    @Test
    public void onConnectTest_successAction() {
        server.onConnect(connectionMock);
        assertNotNull(server.getConnections());
        assertEquals(server.getConnections().size(), 1);
        verify(connectionMock).sendMessage("SERVER COMMANDS: \n [-exit] - для відключення; \n " +
                "[-file /filePath] - щоб зберегти файл на сервері;");
    }

    @Test
    public void onMessageTest_successSending() throws IOException {
        server.onConnect(connectionMock);
        server.onMessage(connectionMock, "Private message");
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) socketMock.getOutputStream();
        assertTrue(outputStream.toString().contains("[test-client]: Private message"));

    }

    @Test
    public void onDisconnectTest_successConnectionRemoving() {
        server.onConnect(connectionMock);
        server.onConnect(connectionMock);
        server.onDisconnect(connectionMock);
        assertEquals(server.getConnections().size(), 1);
        verify(connectionMock).sendMessage("[test-client] - відєднався");
    }
}
