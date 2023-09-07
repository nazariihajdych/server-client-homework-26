package ua.hillel.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Scanner scanner;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.scanner = new Scanner(System.in);
    }

    public Client(Socket socket, BufferedReader reader, PrintWriter writer, Scanner scanner) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.scanner = scanner;
    }

    public void start(){

        try (socket) {
            readFromServer(reader);
            writeToServer(scanner, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void readFromServer(BufferedReader reader){
        Thread readThread = new Thread(() -> {
            try (reader) {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        readThread.setDaemon(true);
        readThread.start();
    }

    public void writeToServer(Scanner scanner, PrintWriter writer){
        while (true) {
            String input = scanner.nextLine();
            writer.println(input);
            if (input.equalsIgnoreCase("-exit")) {
                writer.close();
                scanner.close();
                break;
            }
        }
    }
}
