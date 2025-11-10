package com.projetchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

/*Classe du serveur de chat */
public class Serveur {
    private static final int PORT = 10001;
    private static final Set<ClientHandler> clients = new HashSet<>();
    private final SecretKey AESKey;

    /**
     * Instencie un nouveau serveur avec ses clefs
     * 
     * @throws NoSuchAlgorithmException
     */
    public Serveur() throws NoSuchAlgorithmException {
        // Génération de la clef AES
        AESKey = GenereClef.AESKeyGenerator();
        ClientHandler.setKey(AESKey);
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
