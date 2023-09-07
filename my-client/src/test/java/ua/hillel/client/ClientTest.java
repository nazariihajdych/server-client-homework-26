package ua.hillel.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;


public class ClientTest {

    @Mock
    private Socket socketMock;
    @Mock
    private PrintWriter writerMock;
    @Mock
    private BufferedReader readerMock;
    @Mock
    private Scanner scannerMock;

    private Client client;


    @BeforeEach
    public void setUp() throws IOException {
        openMocks(this);

        when(socketMock.getInputStream()).thenReturn(new ByteArrayInputStream("Test message\n".getBytes()));
        when(socketMock.getOutputStream()).thenReturn(new ByteArrayOutputStream(256));

        client = new Client(socketMock, readerMock, writerMock, scannerMock);
    }

    @Test
    public void readFromServerTest_successfulReading() throws IOException {
        String serverResponse = "Hello, Client!";
        ByteArrayInputStream fakeInputStream = new ByteArrayInputStream(serverResponse.getBytes());
        when(socketMock.getInputStream()).thenReturn(fakeInputStream);

        client.readFromServer(readerMock);

        verify(readerMock).readLine();

    }

    @Test
    public void readFromServerTest_successfulReading2() throws IOException {
        when(scannerMock.nextLine()).thenReturn("-exit");
        client.start();

        verify(socketMock).close();
        verify(scannerMock).close();
        verify(writerMock).close();
        verify(readerMock).close();

    }

    @Test
    public void writeToServerTest_successfulWriting() throws IOException {

        when(scannerMock.nextLine()).thenReturn("message","-exit");
        client.writeToServer(scannerMock, writerMock);

        verify(writerMock).println("message");
    }


}
