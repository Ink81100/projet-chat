package com.projetchat.client.vue;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connexion extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        //CHagement du FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/connection.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Messagerie JavaFX");
        stage.show();
    }
    
}
