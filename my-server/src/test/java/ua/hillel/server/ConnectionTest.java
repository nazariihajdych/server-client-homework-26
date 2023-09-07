package ua.hillel.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConnectionTest {

    @Mock
    private Socket socketMock;
    @Mock
    private ConnectionHandler handlerMock;
    @Mock
    private BufferedReader readerMock;

    private Connection connection;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);


        when(socketMock.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[64]));
        when(socketMock.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(readerMock.readLine()).thenReturn("Test message", "-file /test/file.txt", "-exit");

        connection = new Connection(socketMock, handlerMock);

    }

    @Test
    public void sendMessageTest_successSending() throws IOException {
        connection.sendMessage("Test message");

        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) socketMock.getOutputStream();
        assertTrue(outputStream.toString().contains("Test message"));
    }

    @Test
    public void saveFileTest_successSave() throws IOException {
        String filePath = "-file /Users/nazar/IdeaProjects/server-client/my-server/src/test/java/ua/hillel/filesTest/file.txt";
        Path originalPath = Paths.get("/Users/nazar/IdeaProjects/server-client/my-server/src/test/java/ua/hillel/filesTest/file.txt");

        connection.saveFile(filePath);

        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) socketMock.getOutputStream();
        assertTrue(outputStream.toString().contains("Файл file.txt успішно збережений!"));

        assertTrue(originalPath.toFile().exists());
    }

    @Test
    public void saveFile_InvalidPath() throws IOException {
        String filePath = "-file /invalid/file/path.txt";

        connection.saveFile(filePath);

        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) socketMock.getOutputStream();
        assertTrue(outputStream.toString().contains("Неправильний шлях до файлу!"));
    }


}
