package com.projetchat.client.controlleur;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.projetchat.Message;
import com.projetchat.Message.Type;
import com.projetchat.client.modele.Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;

/**
 * Le contrôleur de la fenêtre de connection.
 * 
 * @author RUIZ Adrien
 */
public class ContrClient implements Initializable {
    /** Le client */
    private Client client;

    /** Le thread d'écoute du client */
    private Thread ecouteThread;

    /** La zone d'entrée du nom d'utilisateur */
    @FXML
    private TextField nomField;

    /** La zone d'entrer de l'ip du serveur */
    @FXML
    private TextField ipField;

    /** La zone d'entrer du port du server */
    @FXML
    private Spinner<Integer> portSpinner;

    /** La zone d'entrer du message */
    @FXML
    private TextField textFieldEnvois;

    /** La zone d'affichage */
    @FXML
    private ScrollPane messagesScroll;

    /** La zone qui contient les bulles de messages */
    @FXML
    private VBox messagesVBox;

    /** La liste des salons */
    @FXML
    private ListView<String> salonList;

    /** Bouton pour créer un salon */
    @FXML
    private Button createSalonButton;

    /**
     * Démarre la communication du client
     */
    @FXML
    private void startClient() {
        try {
            // Récupération des valeurs d'entrer
            String nom = nomField.getText();
            String ip = ipField.getText();
            int port = portSpinner.getValue();

            client = new Client(nom, ip, port);

            client.start();

            // Transmission du nom du client au serveur
            client.envois(nom);

            // On démarre l'écoute
            initEcoute();

            // On autorise l'édition de texte
            textFieldEnvois.setEditable(true);

            salonList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
                if (selected != null) {
                    envoyerCommande("join", selected);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envois une commande au serveur
     * 
     * @param commande la commande
     * @param valeur   la valeur
     */
    private void envoyerCommande(String commande, String valeur) {
        Message message = new Message(Type.COMMAND, client.getNom(), commande + " " + valeur);
        client.envois(message);
    }

    /**
     * Initialise le thread d'écoute
     * 
     * @throws IOException Si une Erreur I/O se déclenche
     */
    private void initEcoute() throws IOException {
        EcouteHandler ecouteHandler = new EcouteHandler(client, messagesScroll, messagesVBox, salonList,
                client.getSocket(),
                client.getKey());
        ecouteThread = new Thread(ecouteHandler);
        ecouteThread.setDaemon(true);
        ecouteThread.setName("Ecoute");
        ecouteThread.start();
    }

    /**
     * Envoisd un message au serveur
     */
    @FXML
    private void envoisServeur() {
        // On récupère le message
        Message message = new Message(Type.MESSAGE, client.getNom(), textFieldEnvois.getText());

        // On le transmet
        client.envois(message);

        if (message.getContenu().equals("bye")) {
            // Fermeture de la connexion
            bye();
        }

        // On eneleve le texte entré
        textFieldEnvois.setText("");
    }

    @FXML
    private void creerSalon() {
        // Boîte de dialogue
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau salon");
        dialog.setHeaderText("Créer un salon");
        dialog.setContentText("Nom du salon :");

        dialog.showAndWait().ifPresent(nomSalon -> {
            System.out.println("Salon choisi: " + nomSalon);
            Message message = new Message(Type.COMMAND, client.getNom(), "join " + nomSalon);
            client.envois(message);
        });
    }

    /**
     * Arrêt de la communication au serveur
     */
    private void bye() {
        // Arrêt d'écoute de message
        client.close();

        // On bloque la zone de texte et le bouton
        textFieldEnvois.setEditable(false);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        portSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 10001));
    }
}
