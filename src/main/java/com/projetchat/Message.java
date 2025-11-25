package com.projetchat;

import java.time.LocalDateTime;

/**
 * Classe représentant un message
 */
public class Message {
    /** L'utilisateur à l'origine du message. */
    private final String utilisateur;
    /** Le contenu du message. */
    private final String contenu;
    /** La date de création du message. */
    private final LocalDateTime date;

    /**
     * Instancie un Objet message.
     * 
     * @param utilisateur L'envoyeur du message.
     * @param contenu     Le contenu du message.
     */
    public Message(String utilisateur, String contenu) {
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    }

    /**
     * Renvois l'utilisateur à la source du message.
     * 
     * @return l'utilisateur à la source du message.
     */
    public String getUtilisateur() {
        return utilisateur;
    }

    /**
     * Renvoie le contenu du message.
     * 
     * @return le texte du message.
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * Renvoie la date et l'heure d'envoi du message.
     * 
     * @return la date et l'heure de création du message.
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Message [utilisateur=" + utilisateur + ", contenu=" + contenu + ", date=" + date + "]";
    }
}