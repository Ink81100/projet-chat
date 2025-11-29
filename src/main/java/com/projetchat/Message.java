package com.projetchat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Classe représentant un message
 */
public class Message {
    /** L'auteur du message. */
    private final String utilisateur;
    /** Le contenu du message. */
    private final String contenu;
    /** La date de création du message. */
    public final LocalDateTime date;

    /**
     * Instancie un Objet message.
     * 
     * @param utilisateur L'auteur du message.
     * @param contenu     Le contenu du message.
     */
    public Message(String utilisateur, String contenu) {
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.date = LocalDateTime.now();
    }

    /**
     * Constructeur du message pour Jackson
     * 
     * @param utilisateur L'auteur du message.
     * @param contenu     Le contenu du message.
     * @param date        La date du message
     */
    @JsonCreator
    public Message(
            @JsonProperty("utilisateur") String utilisateur,
            @JsonProperty("contenu") String contenu,
            @JsonProperty("date") LocalDateTime date) {
        this.utilisateur = utilisateur;
        this.contenu = contenu;
        this.date = date;
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

    @Override
    public boolean equals(Object obj) {
        // Vérification de la classe
        if (obj.getClass().equals(Message.class)) {
            Message message = (Message) obj;

            boolean utilisateur = this.getUtilisateur().equals(message.getUtilisateur());
            boolean contenu = this.getContenu().equals(message.getContenu());
            boolean date = this.getDate().equals(message.getDate());

            return utilisateur && contenu && date;
        } else {
            return false;
        }
    }
}