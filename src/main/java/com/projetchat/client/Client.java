package com.projetchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
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
    /** La clef AES */
    private SecretKey key;
    private PrintWriter output;
    private BufferedReader console, input;

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

            output = new PrintWriter(socket.getOutputStream(), true);
            console = new BufferedReader(new InputStreamReader(System.in));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Diffie-Hellman
            diffie();

            // Thread pour écouter les messages du serveur
            EcouteHandler ecouteHandler = new EcouteHandler(socket, key);
            new Thread(ecouteHandler).start();

            // Envois du nom
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

    /**
     * Echange de clef via la méthode de Diffie-Hellman (Côté client)
     */
    private void diffie() {
        try {
            // Etape 1: Génération de la paire de clefs du client
            KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("DiffieHellman");
            keyPairGenerator.initialize(4096);
            KeyPair keyPairClient = keyPairGenerator.genKeyPair();

            // Etape 2: Echange des clefs
            byte[] encServeur = Base64.getDecoder().decode(input.readLine());
            output.println(Base64.getEncoder().encodeToString(keyPairClient.getPublic().getEncoded()));

            // Etape 3: Création de l'objet clef publique de serveur
            PublicKey publicKeyServeur = KeyFactory.getInstance("DiffieHellman")
                    .generatePublic(new X509EncodedKeySpec(encServeur));

            // Etape 4: Calcul du secret commun
            KeyAgreement keyAgreementClient = KeyAgreement.getInstance("DiffieHellman");
            keyAgreementClient.init(keyPairClient.getPrivate());
            keyAgreementClient.doPhase(publicKeyServeur, true);
            byte[] secretcommun = keyAgreementClient.generateSecret();

            // Etape 5: Calculs de la clef AES
            key = new SecretKeySpec(secretcommun, 0, 32, "AES"); // AES-256
            System.out.println("🔑 Clef AES: " + Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur lors de la génération des clefs: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture des données: " + e);
        } catch (InvalidKeySpecException e) {
            System.out.println("Erreur: " + e);
        } catch (InvalidKeyException e) {
            System.out.println("Erreur au niveau de la clef: " + e);
        } catch (IllegalStateException e) {
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
        Scanner scanner = new Scanner(System.in);

        // Initialisation
        int port = 10001;
        String adresse = "localhost";
        String nom = scanner.next();


        Client client = new Client(adresse, port, nom);
        client.start();

        scanner.close();
    }
}
