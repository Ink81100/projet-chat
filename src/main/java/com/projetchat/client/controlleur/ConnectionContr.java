package com.projetchat.client.controlleur;

import com.projetchat.client.modele.Client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Le contrôleur de la fenêtre de connection.
 * 
 * @author RUIZ Adrien
 */
public class ConnectionContr {

    /** Lazone d'entrée du nom d'utilisateur */
    @FXML
    private TextField nomField;

    /* La zone d'entrer de l'ip du serveur */
    @FXML
    private TextField ipField;

    /* La zone d'entrer du port du server */
    @FXML
    private TextField portField;

    @FXML
    private void onConnect() {
        try {
            // Récupération des valeurs d'entrer
            String nom = nomField.getText();
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());

            Client client = new Client(ip, port, nom);
            client.start();

            /*
             * ChatController controller = loader.getController();
             * controller.setUsername(nomField.getText());
             * 
             * Stage stage = (Stage) nomField.getScene().getWindow();
             * FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
             * Scene scene = new Scene(loader.load());
             * stage.setScene(scene);
             */

        } catch (NumberFormatException e) {
            System.out.println();
        }
    }
}
