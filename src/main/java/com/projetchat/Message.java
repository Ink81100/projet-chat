package com.projetchat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDateTime;

/**
 * Classe représentant un message
 */
public class Message {
        /**
     * Instancie un Objet message.
     *
     * @param type        le type du message
     * @param utilisateur L'auteur du message.
     * @param contenu     Le contenu du message.
     */
    public Message(Type type, String utilisateur, String contenu) {
        this.type = type;
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    };

    /** Le type du message */
    private final Type type;
    /** L'auteur du message. */
    private final String utilisateur;
    /** Le contenu du message. */
    private final String contenu;
    /** La date de création du message. */
    private final LocalDateTime date;

    /**
     * Constructeur du message pour Jackson
     * @param type        le type du message
     * @param utilisateur L'auteur du message.
     * @param contenu     Le contenu du message.
     * @param date        La date du message
     */
    @JsonCreator
    public Message(
            @JsonProperty("type") Type type,
            @JsonProperty("utilisateur") String utilisateur,
            @JsonProperty("contenu") String contenu,
            @JsonProperty("date") LocalDateTime date) {
        this.type = type;
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.date = date;
    }

/**
     * Les différent type de messages possibles
     */
    public static enum Type {
        /**
         * Une annonce, Exemple: un client à rejoint le serveur
         */
        ANNONCE,
        /**
         * Une commande, Exemple: rejoindre un salon
         */
        COMMAND,
        /**
         * La liste des utilisateur
         */
        LISTUSER,
        /**
         * La liste des salons disponibles
         */
        LISTSALON,
        /**
         * Un simple message
         */
        MESSAGE
    }

    /**
     * Créer un message à partir d'un json.
     * 
     * @param json Le message en json.
     * @return Le message.
     * @throws JsonMappingException    Si une erreur se produit lors de la lecture
     *                                 du json.
     * @throws JsonProcessingException Si une erreur se produit lors de la
     *                                 conversion en Message.
     */
    public static Message fromJson(String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        Message message = mapper.readValue(json, Message.class);
        return message;
    }

    /**
     * Renvois le Json du message
     * <p>
     * Exemple:
     * <p>
     * {@code}
     * 
     * @return le message en format json
     * @throws JsonProcessingException Si une erreur se produit lors de la
     *                                 conversion en json
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(this);
        return json;
    }

    /**
     * Renvois le type du message
     * 
     * @return le type du message
     */
    public Type getType() {
        return type;
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
    public boolean equals(Object obj) {
        // Vérification de la classe
        if (obj.getClass().equals(Message.class)) {
            Message message = (Message) obj;

            boolean type = this.getType().equals(message.getType());
            boolean utilisateur = this.getUtilisateur().equals(message.getUtilisateur());
            boolean contenu = this.getContenu().equals(message.getContenu());
            boolean date = this.getDate().equals(message.getDate());

            return type && utilisateur && contenu && date;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Message [type=" + type + ", utilisateur=" + utilisateur + ", contenu=" + contenu + ", date=" + date
                + "]";
    }
}