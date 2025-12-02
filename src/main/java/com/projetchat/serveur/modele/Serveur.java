package com.projetchat.serveur.modele;

import com.projetchat.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

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
     * Instencie un serveur
     * @param port le port d'écoute du serveur
     */
    public Serveur(int port) {
        this.port = port;
    }

    /**
     * Démarre le serveur.
     * @throws IOException Si une erreur entrée/sortie arrive.
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
     * @param message Le message à envoyer
     */
    public void broadcast(Message message) {
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