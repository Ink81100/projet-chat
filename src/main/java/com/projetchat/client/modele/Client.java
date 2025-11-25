package com.projetchat.client.modele;

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
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
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
    /** Le socket de connexion */
    private Socket socket;

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
        try {
            socket = new Socket(adresse, port);
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
            boolean run = true;
            while (run) {
                String message = console.readLine();
                envois(message, key, output);
                
                if (message.equals("bye")) {
                    run = false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        System.out.println("Fermeture de la communication avec le server...");
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Fermuture de communnication avec le serveur échouer : " + e);
        }
    }

    /**
     * Echange de clef via la méthode de Diffie-Hellman (Côté client)
     */
    private void diffie() {
        try {
            // Étape 1 : Génération des paires de clefs du client

            // Diffie-Hellman
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman");
            keyPairGenerator.initialize(4096);
            KeyPair keyPairClient = keyPairGenerator.generateKeyPair();

            // Signature RSA
            KeyPairGenerator keyPairGeneratorSign = KeyPairGenerator.getInstance("RSA");
            keyPairGeneratorSign.initialize(2048); // 2048 pour de vraies signatures
            KeyPair keyPairClientSign = keyPairGeneratorSign.generateKeyPair();

            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(keyPairClientSign.getPrivate());

            // Étape 2 : Réception des données du serveur
            System.out.println("📥️ Reception des données du serveur");
            byte[] bytesPubKeyServeur = Base64.getDecoder().decode(input.readLine());
            byte[] bytesSignatureServeur = Base64.getDecoder().decode(input.readLine());
            byte[] bytesPubKeyServeurRSA = Base64.getDecoder().decode(input.readLine());

            System.out.println("🔑 Clef publique DH serveur : " + Arrays.toString(bytesPubKeyServeur));
            System.out.println("📝 Signature serveur : " + Arrays.toString(bytesSignatureServeur));
            System.out.println("🔑 clef publique RSA Serveur : " + Arrays.toString(bytesPubKeyServeurRSA));

            // Étape 3 : Vérification de la signature du serveur
            PublicKey pubKeyServeurRSA = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytesPubKeyServeurRSA));

            boolean verified = CryptoHandler.verifSign(bytesPubKeyServeur, bytesSignatureServeur, pubKeyServeurRSA);

            if (!verified) {
                throw new SignatureException("❌ Signature du serveur invalide !");
            } else {
                System.out.println("✅ Signature du serveur vérifiée !");
            }

            // Étape 4 : Envoi des clefs du client
            byte[] pubKeyClientBytes = keyPairClient.getPublic().getEncoded();
            signer.update(pubKeyClientBytes);
            byte[] signatureClient = signer.sign();

            // Encode en Base64 pour envoi texte
            String pubKeyClient64 = Base64.getEncoder().encodeToString(pubKeyClientBytes);
            String signatureClient64 = Base64.getEncoder().encodeToString(signatureClient);
            String pubKeyClientRSA64 = Base64.getEncoder().encodeToString(keyPairClientSign.getPublic().getEncoded());

            // Envois
            System.out.println("📨 Transmission des données vers le client");
            output.println(pubKeyClient64);
            output.println(signatureClient64);
            output.println(pubKeyClientRSA64);
            output.flush();

            // Étape 5 : Calcul du secret commun
            PublicKey publicKeyServeur = KeyFactory.getInstance("DiffieHellman")
                    .generatePublic(new X509EncodedKeySpec(bytesPubKeyServeur));

            KeyAgreement keyAgreementClient = KeyAgreement.getInstance("DiffieHellman");
            keyAgreementClient.init(keyPairClient.getPrivate());
            keyAgreementClient.doPhase(publicKeyServeur, true);
            byte[] secretCommun = keyAgreementClient.generateSecret();

            // Étape 6 : Dérivation de la clé AES
            key = new SecretKeySpec(secretCommun, 0, 32, "AES"); // AES-256
            System.out.println("🔑 Clé AES : " + Base64.getEncoder().encodeToString(key.getEncoded()));

        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur algorithme: " + e);
        } catch (IOException e) {
            System.out.println("Erreur I/O: " + e);
        } catch (InvalidKeySpecException e) {
            System.out.println("Erreur KeySpec: " + e);
        } catch (InvalidKeyException e) {
            System.out.println("Erreur de clé: " + e);
        } catch (SignatureException e) {
            System.out.println("Erreur de signature: " + e);
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