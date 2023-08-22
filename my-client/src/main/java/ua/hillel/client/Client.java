package ua.hillel.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client implements AutoCloseable{

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }
    public void requestServer(){

        Scanner scanner = new Scanner(System.in);
        AtomicBoolean exitPoint = new AtomicBoolean(false);

        new Thread(() -> {
            try {
                while (!exitPoint.get()) {
                    System.out.println(reader.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

                while (true) {
                    String input = scanner.nextLine();
                    writer.println(input);
                    if (input.equalsIgnoreCase("-exit")) {
                        exitPoint.set(true);
                        break;
                    }
                }

    }

    @Override
    public void close() throws Exception {
        socket.close();
        writer.close();
        reader.close();
    }
}
