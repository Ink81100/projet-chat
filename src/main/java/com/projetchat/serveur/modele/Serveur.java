package com.projetchat.serveur.modele;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/*Classe du serveur de chat */
public class Serveur implements Runnable{
    /** Le port du serveur */
    private final int port;
    /** L'ensemble des clients */
    private final Set<ClientHandler> clients = new HashSet<>();

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
    private void start() throws IOException{
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur démarré sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté : " + socket.getInetAddress());

                //Thread du nouveau client
                ClientHandler client = new ClientHandler(socket, clients);
                clients.add(client);
                new Thread(client).start();
            }
        }
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
