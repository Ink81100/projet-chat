package com.projetchat.serveur.controleur;

import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**Classe permetant d'écrire l'affichage de la console dans une textArea */
public class ConsoleOutputStream extends OutputStream {
    private final TextArea console;

    /**
     * Constructeur de ConsoleOutputStream
     * @param console
     */
    public ConsoleOutputStream(TextArea console) {
        this.console = console;
    }

    @Override
    public void write(int b) {
        appendText(String.valueOf((char) b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
        appendText(new String(b, off, len));
    }

    /**
     * Ajoute le texte
     * @param text le texte à ajouter
     */
    private void appendText(String text) {
        Platform.runLater(() -> console.appendText(text));
    }
}