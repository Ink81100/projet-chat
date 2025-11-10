package com.projetchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.projetchat.CryptoHandler;

/**
 * Classe permettant de gérer le client
 */
public class ClientHandler implements Runnable {
    /** Le socket de connexion */
    private Socket socket;

    /** La clef de chiffrement AES */
    private static SecretKey key;

    private BufferedReader input;
    private PrintWriter output;
    private Set<ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public static void setKey(SecretKey secretKey) {
        key = secretKey;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            //Transmission de la clef AES
            String key64 = Base64.getEncoder().encodeToString(key.getEncoded());
            output.println(key64);
            System.out.println("🔑 Transmission de la clef AES : " + key64 );

            //Lecture du nom
            clientName = recois(input.readLine());
            broadcast("📢 " + clientName + " a rejoint le chat !");

            //Boucle de Lecture de message
            String message;
            while ((message = input.readLine()) != null) {

                broadcast("💬 " + clientName + " : " + recois(message));
            }
        } catch (IOException e) {
            System.out.println("Client déconnecté : " + clientName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            broadcast("❌ " + clientName + " a quitté le chat.");
        }
    }

    /**
     * Crypte et envois un message au client
     * 
     * @param message le message à envoyer
     */
    private void envois(String message) {
        try {
            String cipherText = Base64.getEncoder().encodeToString(CryptoHandler.crypte(message, key));
            output.println(cipherText);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println("Erreur lors de cryptage du message : " + e);
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
            String message = CryptoHandler.decrypte(decode, key);
            return message;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println("Erreur lors du décryptage : " + e);
            return "ERREUR";
        }

    }

    /**
     * Transmet un message à l'ensemble des clients
     * 
     * @param message le message à envoyer
     */
    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.envois(message);
        }
    }
}
