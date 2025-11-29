package com.projetchat.client.vue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * L'application JavaFX du client
 */
public class ClientViewer extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        //Chargement du FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/clientViewer.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Messagerie JavaFX");
        stage.show();
    }

    /**
     * Lance l'application java
     * @param args les arguments de lancement
     */
    public static void main(String[] args) {
        launch(args);
    }
}
