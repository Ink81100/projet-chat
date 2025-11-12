package com.projetchat.server.vue;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServeurViewer extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement du fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/serverViewer.fxml"));
        Scene scene = new Scene(loader.load());

        // Paramètrage de la fenêtre
        stage.setTitle("ProjLoget chat - Serveur");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
