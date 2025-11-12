package com.projetchat.server.controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class ContrDemarrage extends Control{
    /**La zone d'entrée */
    @FXML
    private Spinner<Integer> port;

    /** Le bouton de démarrage */
    @FXML
    private Button startButton;

    /** Initialise la zone d'entrée du port */
    @FXML
    public void initSpinner() {
        port.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 65535, 1));
    }

    @FXML
    private void startServer() {
        //TODO: Faire la zone démarrage du serveur
    }

}
