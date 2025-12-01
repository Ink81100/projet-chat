package com.projetchat.serveur.modele;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.projetchat.CryptoHandler;
import com.projetchat.Message;
import com.projetchat.Message.Type;

/**
 * Classe permettant de gérer le client.
 * 
 * @author RUIZ Adrien
 */
public class ClientHandler implements Runnable {
    /** Le gestionnaire de logs */
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    /** Le dictionnaire des salons */
    private static final Map<String, Set<ClientHandler>> salons = new HashMap<>();
    // Initialisation du salon générale
    static {
        salons.put("Générale", new HashSet<>());
    }

    /** Le socket de connexion */
    private Socket socket;

    /** La clef de chiffrement AES */
    private SecretKey key;

    /** L'entrée */
    private final BufferedReader input;

    /** La sortie */
    private final PrintWriter output;

    /** L'ensemble des threads des clients existant */
    private static Set<ClientHandler> clientsThread = new HashSet<>();
    /** Le nom du client */
    private String clientName;
    /** Le salon du client */
    private String salon;

    /**
     * Crée un nouveau gestionnaire de client
     * 
     * @param socket le socket de connexion
     * @throws IOException Si une Erreur I/O se déclenche
     */
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        output = new PrintWriter(this.socket.getOutputStream(), true);

        // Ajout du thread
        clientsThread.add(this);
    }

    @Override
    public void run() {
        // Echange de clef
        diffie();
        try {
            // Lecture du nom
            clientName = recois(input.readLine());
            broadcast(new Message(Type.ANNONCE, "Serveur", clientName + " a rejoint le serveur !"));
            logger.info("{} a rejoint le serveur", clientName);

            // Transmission de la liste des salons
            envoisSalons();

            // Boucle de Lecture de message
            boolean run = true;
            while (run) {
                // Décryptage et reception du message en json
                String json = recois(input.readLine());
                System.out.println(json);

                // Conversion en Message
                Message message = Message.fromJson(json);

                // Gestion du type du message
                switch (message.getType()) {
                    case MESSAGE:
                        // Envois
                        envoisMessageSalon(message);
                        logger.info("<{}> {}", clientName, message.getContenu());

                        // Vérification du message pour le bye
                        if (message.getContenu().equals("bye")) {
                            run = false;
                        }
                        break;
                    case COMMAND:
                        String commande = message.getContenu().split(" ")[0];
                        String valeur = message.getContenu().split(" ")[1];

                        switch (commande) {
                            case "join":
                                // Vérification de l'existance du salon
                                if (!salons.keySet().contains(valeur)) {
                                    // Création du salon
                                    salons.put(valeur, new HashSet<>());
                                    logger.info("création du salon {}", valeur);
                                    envoisSalons();
                                }

                                // On retire le client du précédent salon
                                if (salon != null) {
                                    salons.remove(salon);
                                }

                                // Ajout du client dans le salon
                                salon = valeur;
                                salons.get(salon).add(this);
                                logger.info("{} a rejoins le salon: {}", clientName, salon);
                                break;

                            default:
                                logger.error("{}: Commande inconnus", commande);
                                break;
                        }

                    default:
                        break;
                }

                // Stockage du message dans la BDD
                DBHandler.addMessage(message);
            }
        } catch (IOException e) {
            logger.error("Une errreu est survenu : {}", e);
        } finally {
            close();
        }
    }

    private void envoisSalons() {
        StringBuilder strSalons = new StringBuilder();

        // Itération sur les salons
        for (String nomSalon : salons.keySet()) {
            strSalons.append(nomSalon);
            strSalons.append(';');
        }

        // On retire le point virgule en trop
        strSalons.deleteCharAt(strSalons.length() - 1);

        // Création du message
        Message message = new Message(Type.LISTSALON, "Serveur", strSalons.toString());

        // Transmissions de la liste
        envois(message);
    }

    /**
     * Envois un message a l'ensemble des membres d'un salon
     * 
     * @param message Le message à transmettre
     */
    private void envoisMessageSalon(Message message) {
        for (ClientHandler client : salons.get(salon)) {
            client.envois(message);
        }
    }

    /**
     * Ferme la connection avec le client
     */
    private void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // On retire le client
        clientsThread.remove(this);
        salons.get(salon).remove(this);
        broadcast(new Message(Type.ANNONCE, "Serveur", "❌ " + clientName + " a quitté le chat."));
        logger.info("{} à quitter le chat", clientName);
    }

    /**
     * Echange de clef via la méthode de Diffie-Hellman (Côté serveur)
     */
    private void diffie() {
        logger.info("Début d'échange des clefs");
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

            logger.info(
                    "Clef publique DH serveur : {}", Arrays.toString(keyPairServeur.getPublic().getEncoded()));
            logger.info("Signature serveur : {}", Arrays.toString(signer.sign()));
            logger.info(
                    "clef publique RSA Serveur : {}", Arrays.toString(keyPairServeurSign.getPublic().getEncoded()));

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
                throw new SignatureException("Signature du client invalide");
            } else {
                logger.info("Signature du client vérifiée");
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
            logger.info("Clef AES: {}", Base64.getEncoder().encodeToString(key.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Erreur lors de la génération des clefs: " + e);
            close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture des données: " + e);
            close();
        } catch (InvalidKeySpecException e) {
            System.out.println("Erreur: " + e);
            close();
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Erreur au niveau de la clef: " + e);
            close();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            close();
            e.printStackTrace();
        } catch (SignatureException e) {
            System.out.println("Erreur lors de la signature : " + e);
            close();
        }
    }

    /**
     * Crypte et envois un message au client
     * 
     * @param message le message à envoyer
     */
    private void envois(Message message) {
        try {
            // Conversion en json
            String json = message.toJson();

            // Cryptage
            String cipherText = Base64.getEncoder().encodeToString(CryptoHandler.crypte(json, key));

            // Envois
            output.println(cipherText);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            logger.error("Erreur lors de cryptage du message : {}", e);
        } catch (JsonProcessingException e) {
            logger.error("Erreur lors de la conversion en son du message ");
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
            logger.error("Erreur lors du décryptage : {}", e);
            return "ERREUR";
        }
    }

    /**
     * Transmet un message à l'ensemble des clients
     * 
     * @param message le message à envoyer
     */
    protected static void broadcast(Message message) {
        for (ClientHandler client : clientsThread) {
            client.envois(message);
        }
    }
}