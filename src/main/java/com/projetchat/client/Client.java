package com.projetchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.projetchat.CryptoHandler;

public class Client {
    /** L'adresse du serveur */
    private final String adresse;
    /** Le port du serveur */
    private final int port;
    /** Le nom du client */
    private final String nom;

    /**
     * Le constructeur du client
     * 
     * @param adresse L'adresse du serveur
     * @param port    Le port du serveur
     * @param nom     Le nom du client
     */
    public Client(String adresse, int port, String nom) {
        this.adresse = adresse;
        this.port = port;
        this.nom = nom;
    }

    /** Démarre le client */
    public void start() {
        try (Socket socket = new Socket(adresse, port)) {
            System.out.println("Connecté au serveur.");

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Réception de la clef AES
            String key64 = input.readLine();
            System.out.println("🔑 Reception de la clef AES: "+ key64);
            byte[] decode = Base64.getDecoder().decode(key64);
            SecretKey key = new SecretKeySpec(decode, "AES");

           

            // Thread pour écouter les messages du serveur
            EcouteHandler ecouteHandler = new EcouteHandler(socket, key);
            new Thread(ecouteHandler).start();

            //Envois du nom
            envois(nom, key, output);

            // Envoi des messages depuis la console
            String message;
            while ((message = console.readLine()) != null) {
                envois(message, key, output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void envois(String message, SecretKey key, PrintWriter output) {
        try {
            String cipherText = Base64.getEncoder().encodeToString(CryptoHandler.crypte(message, key));
            output.println(cipherText);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println("Erreur lors de cryptage du message : " + e);
        }
    }

    public static void main(String[] args) {
        //Initialisation
        int port = 10001;
        String adresse = "localhost";
        String nom = "Adrien";

        Client client = new Client(adresse, port, nom);
        client.start();
    }
}
