package com.projetchat.server;

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
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.projetchat.CryptoHandler;

/**
 * Classe permettant de gérer le client
 */
public class ClientHandler implements Runnable {
    /** Le socket de connexion */
    private Socket socket;

    /** La clef de chiffrement AES */
    private SecretKey key;

    private final BufferedReader input;
    private final PrintWriter output;
    /** L'ensemble des clients existant */
    private Set<ClientHandler> clients;
    /** Le nom du client */
    private String clientName;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.clients = clients;
        input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        output = new PrintWriter(this.socket.getOutputStream(), true);
        // Echange de clef
        diffie();
    }

    @Override
    public void run() {
        try {
            // Lecture du nom
            clientName = recois(input.readLine());
            broadcast("📢 " + clientName + " a rejoint le chat !");
            System.out.println("📢 " + clientName + " a rejoint le chat !");

            // Boucle de Lecture de message
            String message;
            while ((message = input.readLine()) != null) {
                broadcast("💬 " + clientName + " : " + recois(message));
                System.out.println("💬 " + clientName + " : " + recois(message));
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
            System.out.println("❌ " + clientName + " a quitté le chat.");
        }
    }

    /**
     * Echange de clef via la méthode de Diffie-Hellman (Côté serveur)
     */
    private void diffie() {
        try {
            // Etape 1: Génération des paires de clefs du serveur

            // Diffie-Hellman
            KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance("DiffieHellman");
            keyPairGenerator.initialize(4096);
            KeyPair keyPairServeur = keyPairGenerator.genKeyPair();

            // Signature RSA
            // Génération de la paire
            KeyPairGenerator keyPairGeneratorSign = java.security.KeyPairGenerator.getInstance("RSA");
            keyPairGeneratorSign.initialize(2048);
            KeyPair keyPairServeurSign = keyPairGeneratorSign.genKeyPair();
            // Objet Signature
            Signature signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(keyPairServeurSign.getPrivate());

            // Etape 2: Echange des clefs
            // Serveur
            signer.update(keyPairServeur.getPublic().getEncoded());
            String signatureServeur64 = Base64.getEncoder().encodeToString(signer.sign());
            String pubKeyServ64 = Base64.getEncoder().encodeToString(keyPairServeur.getPublic().getEncoded());
            String pubKeyServRSA64 = Base64.getEncoder().encodeToString(keyPairServeurSign.getPublic().getEncoded());

            System.out.println("🔑 Clef publique DH serveur : " + Arrays.toString(keyPairServeur.getPublic().getEncoded()));
            System.out.println("📝 Signature serveur : "+ Arrays.toString(signer.sign()));
            System.out.println("🔑 clef publique RSA Serveur : " + Arrays.toString(keyPairServeurSign.getPublic().getEncoded()));

            System.out.println(Arrays.equals(signer.sign(), signer.sign()));

            // Envois
            System.out.println("📨 Transmission des données vers le client");
            output.println(pubKeyServ64);// Message
            output.println(signatureServeur64);// Signature
            output.println(pubKeyServRSA64);// Clef publique RSA
            output.flush();

            // Client
            // Reception
            System.out.println("📥️ Reception des données du client");
            byte[] bytePubKeyClient = Base64.getDecoder().decode(input.readLine());
            byte[] byteSignatureClient = Base64.getDecoder().decode(input.readLine());
            byte[] bytesPubKeyClientRSA = Base64.getDecoder().decode(input.readLine());

            // Etape 3: Vérification de la signature du client
            PublicKey pubKeyClientRSA = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytesPubKeyClientRSA));
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(pubKeyClientRSA);
            verifier.update(bytePubKeyClient);

            boolean verified = verifier.verify(byteSignatureClient);
            if (!verified) {
                throw new SignatureException("❌ Signature du client invalide !");
            } else {
                System.out.println("✅ Signature du client vérifiée !");
            }

            // Etape 4: Calcul du secret commun
            PublicKey pubKeyClient = KeyFactory.getInstance("DiffieHellman")
                    .generatePublic(new X509EncodedKeySpec(bytePubKeyClient));

            KeyAgreement keyAgreementServeur = KeyAgreement.getInstance("DiffieHellman");
            keyAgreementServeur.init(keyPairServeur.getPrivate());
            keyAgreementServeur.doPhase(pubKeyClient, true);
            byte[] secretcommun = keyAgreementServeur.generateSecret();

            // Etape 5: Calculs de la clef AES
            key = new SecretKeySpec(secretcommun, 0, 32, "AES"); // AES-256
            System.out.println("🔑 Clef AES: " + Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur lors de la génération des clefs: " + e);
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture des données: " + e);
        } catch (InvalidKeySpecException e) {
            System.out.println("Erreur: " + e);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Erreur au niveau de la clef: " + e);
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            System.out.println("Erreur lors de la signature : " + e);
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
            System.out.println("🔓️ Message décrypté: " + message);
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