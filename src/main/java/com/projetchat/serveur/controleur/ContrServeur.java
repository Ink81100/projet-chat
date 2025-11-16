package com.projetchat.serveur.controleur;

import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.projetchat.serveur.modele.Serveur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    /** Initialise la zone d'entrée du port et l'affichage de la console */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        spinnerPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 1));

        // Redirection de l'affichage de la console
        ConsoleOutputStream console = new ConsoleOutputStream(textAreaConsole);
        PrintStream ps = new PrintStream(console);
        System.setOut(ps);
        System.setErr(ps);

        textFieldEnvois.setDisable(true);
        sendButton.setDisable(true);
    }

    /** Envois un message à tous les utilisateurs */
    @FXML
    private void envoisServeur() {
        String msg = textFieldEnvois.getText();
        System.out.println("serveur : " + msg);
        textFieldEnvois.clear();
    }

    /** Démarre le serveur */
    @FXML
    private void startServer() {
        startButton.setDisable(true);
        System.out.println("Démarrage du serveur...");
        textFieldEnvois.setDisable(false);
        sendButton.setDisable(false);
        try {
            int port = spinnerPort.getValue();
            Serveur serveur = new Serveur(port);
            Thread thread = new Thread(serveur);
            thread.setDaemon(true);// Arrête le serveur lorsque l'ui se ferme
            thread.start();
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setContentText(e.getMessage());
            e.printStackTrace();
            alert.showAndWait();
        }
    }

}
