package com.projetchat.serveur.modele;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/* Classe du serveur de chat */
public class Serveur implements Runnable {
    /** Le port du serveur */
    private final int port;

    /**
     * Instencie un nouveau serveur
     * 
     * @throws NoSuchAlgorithmException
     */
    public Serveur(int port) {
        this.port = port;
    }

    /**
     * Méthode qui démarre le serveur
     */
    private void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + socket.getInetAddress());

                // Thread du nouveau client
                ClientHandler client = new ClientHandler(socket);
                new Thread(client).start();
            }
        }
    }

    /** Envois un message à tous les utilisateurs */
    public void broadcast(String message) {
        ClientHandler.broadcast(message);
    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
