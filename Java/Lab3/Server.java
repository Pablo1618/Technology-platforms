package org.example;
import java.io.*;
import java.net.*;
import java.util.logging.*;

public class Server {
    private static final int PORT = 16969;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Serwer nasłuchuje na porcie " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nowe połączenie zaakceptowane: " + clientSocket);

                new ServerThread(clientSocket).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Błąd serwera: " + e.getMessage(), e);
        }
    }
}

class ServerThread extends Thread {
    private final Socket clientSocket;
    private static final Logger logger = Logger.getLogger(ServerThread.class.getName());

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
    public void run() {
        try (
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            logger.info("Obsługa połączenia dla klienta: " + clientSocket);
            int messageCount = objectInputStream.readInt();
            logger.info("Klient chce wysłać " + messageCount + " wiadomości");
            // Odbieranie i wyswietlanie wiadomosci od klienta
            for (int i = 0; i < messageCount; i++) {
                Message message = (Message) objectInputStream.readObject();
                logger.info("Wiadomość " + (i + 1) + " od klienta: " + message.getContent());

                if (message.getContent().equals("kill")) {
                    logger.info("Otrzymano komendę 'kill'. Wyłączanie serwera...");
                    if (clientSocket != null && !clientSocket.isClosed() && clientSocket.isConnected()) {
                        try {
                            clientSocket.close();
                            System.out.println("Serwer zostal wylaczony");
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "Błąd zamykania połączenia: " + ex.getMessage(), ex);
                        }
                    }
                    break;
                }
            }
            // Potwierdzenie do klienta
            objectOutputStream.writeObject("Wiadomości odebrane pomyślnie");
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Błąd obsługi klienta: " + e.getMessage(), e);
        }
    }
}

