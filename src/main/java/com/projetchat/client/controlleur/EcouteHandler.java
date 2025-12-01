package com.projetchat.client.controlleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.scene.control.Label;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.projetchat.CryptoHandler;
import com.projetchat.Message;
import com.projetchat.client.modele.Client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Gére l'écoute de message et l'affiche
 */
public class EcouteHandler implements Runnable {
    private final BufferedReader input;
    private SecretKey key;
    private final ScrollPane messagesScroll;
    private final VBox messagesVBox;
    private final ListView<String> listView;
    private final Client client;

    /**
     * Initialise le thread
     * 
     * @param socket le socket de connexion au serveur
     * @throws IOException
     */
    public EcouteHandler(Client client, ScrollPane messagesScroll, VBox messagesVBox, ListView<String> listView,
            Socket socket,
            SecretKey key)
            throws IOException {
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.key = key;
        this.messagesScroll = messagesScroll;
        this.messagesVBox = messagesVBox;
        this.listView = listView;
        this.client = client;

        this.messagesVBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            this.messagesScroll.setVvalue(1.0);
        });

        this.listView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setStyle("-fx-padding: 10;");
                }
            }
        });
    }

    /**
     * Boucle d'éxécution d'écoute
     */
    @Override
    public void run() {
        try {
            // Boucle de lecture
            String reponse;
            while ((reponse = input.readLine()) != null) {
                String json = recois(reponse);
                System.out.println(json);

                Message message = Message.fromJson(json);

                // Gestion des messages
                switch (message.getType()) {
                    case ANNONCE:
                        Label annonce = new Label(String.format("%s | Annonce de %s : %s", message.getDate().toString(),
                                message.getUtilisateur(), message.getContenu()));
                        annonce.setAlignment(Pos.CENTER);

                        Platform.runLater(() -> {
                            messagesVBox.getChildren().add(annonce);
                        });
                        break;

                    case MESSAGE:
                        boolean vous = message.getUtilisateur().equals(client.getNom());
                        BulleMessage bulleMessage = new BulleMessage(message, vous);

                        HBox ligne = new HBox(bulleMessage);

                        if (vous) {
                            ligne.setAlignment(Pos.CENTER_RIGHT);
                        } else {
                            ligne.setAlignment(Pos.CENTER_LEFT);
                        }

                        Platform.runLater(() -> {
                            messagesVBox.getChildren().add(ligne);
                        });
                        break;

                    case LISTSALON:
                        // On récupère les salons
                        String[] salons = message.getContenu().split(";"); // Chaque salon est séparé par un point
                                                                           // virgule

                        // Ajout des salons
                        Platform.runLater(() -> {
                            listView.getItems().clear();
                            for (String salon : salons) {
                                if (!salon.isEmpty()) {
                                    listView.getItems().add(salon);
                                }
                            }
                        });
                        break;

                    default:
                        System.err.println("Type du message pas pris en charge : " + message.getType());
                        break;

                }

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
            System.out.println("📥️ Message reçus: " + new String(decode));
            String message = CryptoHandler.decrypte(decode, key);
            return message;
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
                | BadPaddingException e) {
            System.out.println("Erreur lors du décryptage : " + e);
            return "ERREUR";
        }

    }
}
