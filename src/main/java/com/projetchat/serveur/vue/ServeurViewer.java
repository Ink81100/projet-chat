package com.projetchat.serveur.vue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServeurViewer extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Chargement du fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/serveurViewer.fxml"));
        Scene scene = new Scene(loader.load());

        // Paramètrage de la fenêtre
        stage.setTitle("Projet chat - Serveur");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    /**
     * Lance l'application java
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
