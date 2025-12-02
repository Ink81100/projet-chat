package com.projetchat.serveur.modele;

import com.projetchat.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Permet de gérer les messages dans une base de données
 * 
 * 
 * @author RUIZ Adrien
 */
public final class DBHandler {
    /** Le gestionnaire de logs */
    private static final Logger logger = LogManager.getLogger(DBHandler.class);

    /**
     * Le lien vers la base de données
     */
    private static String url = "jdbc:sqlite:messages.db";

    /**
     * Créer la base de données et Initialise la table messages
     * <p>
     * Si la table existait déjà, elle sera supprimée
     * 
     * @throws IOException Si une Erreur I/O se déclenche
     * @throws SQLException Si une Erreur SQL se déclenche
     */
    public static void creerDB() throws SQLException, IOException {
        // Initialisation
        Connection connection = DriverManager.getConnection(url);

        // DROP la table si elle existe
        PreparedStatement dropStatement = connection.prepareStatement("DROP TABLE IF EXISTS messages;");
        dropStatement.execute();

        PreparedStatement iniStatement = connection.prepareStatement(
                "CREATE TABLE messages ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "type TEXT, "
                        + "utilisateur TEXT, "
                        + "message TEXT, "
                        + "date DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ");");
        iniStatement.execute();
        connection.close();
    }

    /**
     * Permet de savoir si la base de données a été initialisé.
     * 
     * @return {@code true} si elle est initialisé, sinon {@code false}.
     */
    public static boolean isInit() {
        try (
                Connection connection = DriverManager.getConnection(url);

                // Vérification de la présence la table
                PreparedStatement tableStatement = connection.prepareStatement(
                        "SELECT name FROM sqlite_master WHERE type='table' AND name='messages';");

                ResultSet tableResultSet = tableStatement.executeQuery();) {

            if (!tableResultSet.next()) {
                return false;
            }

            // Vérification de la présence des collones.
            Map<String, String> collones = new HashMap<>();
            // Ajout des collones
            collones.put("id", "INTEGER");
            collones.put("type", "TEXT");
            collones.put("utilisateur", "TEXT");
            collones.put("message", "TEXT");
            collones.put("date", "DATETIME");

            PreparedStatement colStatement = connection.prepareStatement(
                    "PRAGMA table_info(messages);");

            ResultSet colResultSet = colStatement.executeQuery();

            while (colResultSet.next()) {
                String colNom = colResultSet.getString("name");
                String colType = colResultSet.getString("type");

                if (!collones.remove(colNom, colType))
                    return false;
            }

            connection.close();
            return collones.isEmpty();

        } catch (SQLException e) {
            System.out.println("La table n'est pas initialisée : " + e);
            return false;
        }
    }

    /**
     * Ajoute un message à la base de données
     * @param message le message
     */
    public static void addMessage(Message message) {
        try (
                // Connection à la base de données
                Connection connection = DriverManager.getConnection(url);
                // Requête préparer (Evite les injections SQL)
                PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO messages (type, utilisateur, message, date) VALUES (?, ?, ?, ?)");) {

            insertStatement.setString(1, message.getType().toString());
            insertStatement.setString(2, message.getUtilisateur());
            insertStatement.setString(3, message.getContenu());
            insertStatement.setString(4, message.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Insertion
            insertStatement.execute();
        } catch (SQLException e) {
            logger.error("Erreur lors de l'ajout d'un message dans la base de données : {}", e);
        }
    }

    /**
     * Renvois le nombre de message dans la BDD
     * @return le nombre de message dans la BDD
     */
    public static int size() {
        try (
                Connection connection = DriverManager.getConnection(url);
                PreparedStatement countStatement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM messages;");
                ResultSet resultSet = countStatement.executeQuery();) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else
                throw new SQLException("Aucun résultat");

        } catch (SQLException e) {
            logger.error("Erreur lors du calculs du nombre de messages : {}", e);
            return 0;
        }

    }

    /**
     * Met à jours le liens vers la base de données
     * 
     * @param url Le liens vers la base de données
     */
    public static void setUrl(String url) {
        DBHandler.url = "jdbc:sqlite:" + url;
    }
}