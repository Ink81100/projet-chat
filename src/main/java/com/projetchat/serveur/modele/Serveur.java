package com.projetchat.serveur.modele;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Le serveur de chat.
 * 
 * @author RUIZ Adrien
 */
public class Serveur implements Runnable {
    /** Le gestionnaire de logs */
    private static final Logger logger = LogManager.getLogger(Serveur.class);
    /** Le port du serveur */
    private final int port;

    /**
     * Instencie un nouveau serveur
     */
    public Serveur(int port) {
        this.port = port;
    }

    /**
     * Méthode qui démarre le serveur
     */
    private void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Initialisation de la BDD
            if (!DBHandler.isInit()) {
                DBHandler.creerDB();
            }

            logger.info("Serveur démarré sur le port {}", port);

            while (true) {
                Socket socket = serverSocket.accept();
                logger.info("Nouveau client connecté : {}", socket.getInetAddress());

                // Thread du nouveau client
                ClientHandler client = new ClientHandler(socket);
                Thread thread = new Thread(client, "Client " + socket.getInetAddress());
                thread.setDaemon(true);
                thread.start();
            }
        } catch (SQLException e) {
            logger.fatal("Erreur au niveeau de la base de données : " + e);
        }
    }

    /** 
     * Envois un message à tous les utilisateurs
     */
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