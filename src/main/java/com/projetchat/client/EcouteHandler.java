package com.projetchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.projetchat.CryptoHandler;

/**
 * Gére l'écoute de message et l'affiche
 */
public class EcouteHandler implements Runnable {
    private final BufferedReader input;
    private SecretKey key;

    /**
     * Initialise le thread
     * @param socket le socket de connexion au serveur
     * @throws IOException
     */
    public EcouteHandler(Socket socket, SecretKey key) throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.key = key;
    }

    /**
     * Boucle d'éxécution d'écoute
     */
    @Override
    public void run() {
        

        try {
            //Boucle d'éxécution
            String reponse;
            while ((reponse = input.readLine()) != null) {
                System.out.println(recois(reponse));
            }
        } catch (IOException e) {
            System.out.println("Déconnecté du serveur.");
        }
    }

    /**
     * Décrypte un message réceptionné
     * 
     * @param message64 Le message en base 64
     * @return Le message décrypté
     */
    private String recois(String message64) {
        try {
            // Decodage du message
            byte[] decode = Base64.getDecoder().decode(message64);
            System.out.println(new String(decode));
            String message = CryptoHandler.decrypte(decode, key);
            return message;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println("Erreur lors du décryptage : " + e);
            return "ERREUR";
        }

    }
}
