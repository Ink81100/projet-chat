package com.projetchat.client.vue.connexion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connexion extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement du fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/connection.fxml"));
        Scene scene = new Scene(loader.load());

        // Paramètrage de la fenêtre
        stage.setTitle("Projet chat - Connexion");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
