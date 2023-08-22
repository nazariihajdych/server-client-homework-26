package ua.hillel;

import ua.hillel.client.Client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try (Client client = new Client("localhost", 8000);) {

            client.requestServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}