package ua.hillel.server;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

public class Connection {
    private final static String DEFAULT_DIRECTORY_PATH = "/Users/nazar/IdeaProjects/server-client/my-server/src/main/java/ua/hillel/files";
    private final Socket socket;
    private static int clientNumber;
    private final LocalDateTime connectTime = LocalDateTime.now();
    private final ConnectionHandler handler;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private String clientName;

    public Connection(Socket socket, ConnectionHandler handler) throws IOException {
        this.socket = socket;
        this.handler = handler;

        clientNumber++;

        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        doConnection();
    }

    private void doConnection() {
        new Thread(() -> {
            try {

                clientName = String.format("client-%d", clientNumber);
                handler.onConnect(this);

                String message;
                while ((message = reader.readLine()) != null) {
                    if (message.equalsIgnoreCase("-exit")) {
                        writer.close();
                        reader.close();
                        socket.close();
                        break;
                    } else if (message.toLowerCase().startsWith("-file")) {
                        saveFile(message);
                    } else {
                        handler.onMessage(this, message);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                handler.onDisconnect(this);
            }

        }).start();
    }

    protected void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }

    protected void saveFile(String filePath) throws IOException {
        Path original = Paths.get(filePath.substring("-file".length()).trim());

        String[] splitFilePath = filePath.split("/");
        String fileName = splitFilePath[splitFilePath.length - 1];
        Path copied = Paths.get(DEFAULT_DIRECTORY_PATH + "/" + fileName);

        if (original.toFile().exists()) {
            Files.copy(original, copied, StandardCopyOption.REPLACE_EXISTING);
            sendMessage("Файл " + fileName + " успішно збережений!");
        } else {
            sendMessage("Неправильний шлях до файлу!");
        }
    }

    public String getName() {
        return clientName;
    }


    @Override
    public String toString() {
        return "Connection {" +
                "socket = " + socket +
                ", connectTime = " + connectTime +
                ", clientName = '" + clientName + '\'' +
                '}';
    }
}
