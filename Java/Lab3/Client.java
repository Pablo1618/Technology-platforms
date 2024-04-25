package org.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 16969;
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Scanner scanner = new Scanner(System.in)
        ) {
            logger.info("Połączono z serwerem: " + SERVER_ADDRESS + ":" + SERVER_PORT);

            // Wprowadzanie liczby obiektów
            System.out.print("Podaj liczbę wiadomości do wysłania: ");
            int messageCount = scanner.nextInt();
            objectOutputStream.writeInt(messageCount);
            objectOutputStream.flush();

            // Wprowadzanie i wysylanie obiektow message
            for (int i = 0; i < messageCount; i++) {
                System.out.print("Podaj treść wiadomości " + (i + 1) + ": ");
                String content = scanner.next();
                Message message = new Message(i + 1, content);
                objectOutputStream.writeObject(message);
                objectOutputStream.flush();
            }

            // Odbiot potwierdzenia
            String confirmation = (String) objectInputStream.readObject();
            logger.info("Potwierdzenie od serwera: " + confirmation);

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Błąd połączenia z serwerem: " + e.getMessage(), e);
        }
    }
}

