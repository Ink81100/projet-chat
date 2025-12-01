package com.projetchat.client.controlleur;

import com.projetchat.Message;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Classe perméteznt de représenter un message sour la forme d'une bulle
 */
public class BulleMessage extends VBox{
    public BulleMessage(Message message) {
    // Auteur
    Label auteurLabel = new Label(message.getUtilisateur());
    auteurLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

    // Contenu
    Label contenuLabel = new Label(message.getContenu());
    contenuLabel.setWrapText(true);
    contenuLabel.setStyle("-fx-font-size: 16px;");

    // Date
    Label dateLabel = new Label(message.getDate().toString());
    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

    // Ajout des labels
    this.getChildren().addAll(auteurLabel, contenuLabel, dateLabel);
    }
}
