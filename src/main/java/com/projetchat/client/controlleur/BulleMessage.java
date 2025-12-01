package com.projetchat.client.controlleur;

import com.projetchat.Message;

import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Classe perméteznt de représenter un message sour la forme d'une bulle
 */
public class BulleMessage extends VBox {
    public BulleMessage(Message message, boolean vous) {
        // Auteur
        Label auteurLabel = new Label(message.getUtilisateur());

        // Contenu
        Label contenuLabel = new Label(message.getContenu());
        contenuLabel.setWrapText(true);
        contenuLabel.setMaxWidth(Double.MAX_VALUE);

        // Date
        Label dateLabel = new Label(message.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        // Style général
        this.setSpacing(4);
        if (vous) {
            this.setStyle(
                    "-fx-padding: 10; -fx-background-color: linear-gradient(to top left, #0b57d0 20%, #1b79f3 100%); -fx-background-radius: 10;");
            auteurLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #fcfdff;");
            contenuLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #fcfdff;");

        } else {
            this.setStyle("-fx-padding: 10; -fx-background-color: #eef3fc; -fx-background-radius: 10;");
            auteurLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            contenuLabel.setStyle("-fx-font-size: 16px;");
        }

        // Ajout des labels
        this.getChildren().addAll(auteurLabel, contenuLabel, dateLabel);

        // Redimensionnement de la bulle
        this.setMaxWidth(Region.USE_PREF_SIZE);
        this.setMinWidth(Region.USE_PREF_SIZE);
        this.setPrefWidth(Region.USE_COMPUTED_SIZE);

    }
}
