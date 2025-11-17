package com.projetchat.serveur.controleur;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.projetchat.serveur.modele.Serveur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Le contrôleur du l'interface du serveur
 */
public class ContrServeur extends Control implements Initializable {
    /** Le gestionnaire de logs */
    private static final Logger logger = LogManager.getLogger(ContrServeur.class);

    /** Le serveur ce chat */
    private Serveur serveur;

    /** La zone d'entrée du port */
    @FXML
    private Spinner<Integer> spinnerPort;

    /** Le bouton de démarrage */
    @FXML
    private Button startButton;

    /** Le bouton d'envois de message (côté serveur) */
    @FXML
    private Button sendButton;

    /** La zone d'affichage de la console */
    @FXML
    private TextArea textAreaConsole;

    /** Zone d'entréer de texte (côté serveur) */
    @FXML
    private TextField textFieldEnvois;

    /** Initialise la zone d'entrée du port et l'affichage des logs */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // Initialisation de 
        spinnerPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 1));

        // Affichage des logs dans l'ui
        // TODO: Appender log4j2
        TextAreaAppender.setTextArea(textAreaConsole);

        textFieldEnvois.setDisable(true);
        sendButton.setDisable(true);
    }

    /** Envois un message à tous les utilisateurs */
    @FXML
    private void envoisServeur() {
        String msg = textFieldEnvois.getText();
        logger.info("Le serveur envois : " + msg);
        textFieldEnvois.clear();
        serveur.broadcast("[Serveur] : " + msg);
    }

    /** Démarre le serveur */
    @FXML
    private void startServer() {
        startButton.setDisable(true);
        logger.info("Démarrage du serveur...");
        textFieldEnvois.setDisable(false);
        sendButton.setDisable(false);
        int port = spinnerPort.getValue();
        serveur = new Serveur(port);
        Thread thread = new Thread(serveur);
        thread.setDaemon(true);
        thread.start();
    }
}
