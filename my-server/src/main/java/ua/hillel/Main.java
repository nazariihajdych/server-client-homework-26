package ua.hillel;

import ua.hillel.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        try (Server server = new Server()){
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}