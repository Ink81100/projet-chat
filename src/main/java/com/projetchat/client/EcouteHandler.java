package com.projetchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Gére l'écoute de message et l'affiche
 */
public class EcouteHandler implements Runnable {
    private final BufferedReader input;

    /**
     * Initialise le thread
     * @param socket le socket de connexion au serveur
     * @throws IOException
     */
    public EcouteHandler(Socket socket) throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Boucle d'éxécution d'écoute
     */
    @Override
    public void run() {
        String reponse;
        try {
            while ((reponse = input.readLine()) != null) {
                System.out.println(reponse);
            }
        } catch (IOException e) {
            System.out.println("Déconnecté du serveur.");
        }
    }
}
