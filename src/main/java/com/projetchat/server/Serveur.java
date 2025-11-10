package com.projetchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/*Classe du serveur de chat */
public class Serveur {
    private static final int PORT = 10001;
    private static final Set<ClientHandler> clients = new HashSet<>();

    /**
     * Instencie un nouveau serveur avec ses clefs
     * 
     * @throws NoSuchAlgorithmException
     */
    public Serveur() throws NoSuchAlgorithmException {
    }

    /**
     * Méthode qui démarre le serveur
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + socket.getInetAddress());

                //Thread du nouveau client
                ClientHandler client = new ClientHandler(socket, clients);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        new Serveur().start();
    }
}
