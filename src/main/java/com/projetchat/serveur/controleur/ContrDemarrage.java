package com.projetchat.serveur.controleur;

import java.io.PrintStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.projetchat.serveur.modele.Serveur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

/**
 * Le contrôleur du l'interface du serveur
 */
public class ContrDemarrage extends Control implements Initializable {
    /** La zone d'entrée du port*/
    @FXML
    private Spinner<Integer> spinnerPort;

    /** Le bouton de démarrage */
    @FXML
    private Button startButton;

    /**La zone d'affichage de la console */
    @FXML
    private TextArea textAreaConsole;

    /** Initialise la zone d'entrée du port et l'affichage de la console */
     @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        spinnerPort.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 1));
        // Redirection de l'affichage de la console
        ConsoleOutputStream console = new ConsoleOutputStream(textAreaConsole);
        PrintStream ps = new PrintStream(console);
        System.setOut(ps);
        System.setErr(ps);
    }

    /** Démarre le serveur */
    @FXML
    private void startServer() {
        startButton.isDisable();
        System.out.println("Démarrage du serveur...");
        try {
            int port = spinnerPort.getValue();
            Serveur serveur = new Serveur(port);
            Thread thread = new Thread(serveur);
            thread.setDaemon(true);// Arrête le serveur lorsque l'ui se ferme
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
