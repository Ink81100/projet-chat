package com.projetchat.client.controlleur;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.projetchat.client.modele.Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Le contrôleur de la fenêtre de connection.
 * 
 * @author RUIZ Adrien
 */
public class ContrClient implements Initializable{
    /** Le client */
    private Client client;

    /** Lazone d'entrée du nom d'utilisateur */
    @FXML
    private TextField nomField;

    /* La zone d'entrer de l'ip du serveur */
    @FXML
    private TextField ipField;

    /* La zone d'entrer du port du server */
    @FXML
    private Spinner<Integer> portSpinner;

    /** La zone d'entrer du message */
    @FXML
    private TextField textFieldEnvois;

    /** La zone d'affichage des messages */
    @FXML
    private TextArea textAreaConsole;

    @FXML
    private void startClient() {
        try {
            // Récupération des valeurs d'entrer
            String nom = nomField.getText();
            String ip = ipField.getText();
            int port = portSpinner.getValue();

            client = new Client(ip, port);
            
            client.start();

            //Transmission du nom du client au serveur
            client.envois(nom);

            // On démarre l'écoute
            initEcoute();
        } catch (NumberFormatException e) {
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise le thread d'écoute
     * @throws IOException
     */
    private void initEcoute() throws IOException{
        EcouteHandler ecouteHandler = new EcouteHandler(textAreaConsole, client.getSocket(), client.getKey());
        Thread thread = new Thread(ecouteHandler);
        thread.setDaemon(true);
        thread.setName("Ecoute");
        thread.start();
    }

    @FXML
    private void envoisServeur() {
        // On récupère le message
        String message = textFieldEnvois.getText();

        // On le transmet
        client.envois(message);

        if (message.equals("bye")) {
            // Fermeture de la connexion
            client.close();
            
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        portSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 10001));
    }
}
