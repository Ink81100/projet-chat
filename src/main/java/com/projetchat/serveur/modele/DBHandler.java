package com.projetchat.serveur.modele;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Permet de stocké les messages dans une base de données
 * 
 * 
 * @author RUIZ Adrien
 */
public final class DBHandler {
    // Le lien vers la base de données
    private final String url;

    public DBHandler(String url) {
        this.url =  "jdbc:sqlite:" + url;
    }

    /**
     * Créer la base de données et Initialise la table messages
     * <p>
     * Si la table existait déjà, elle sera supprimée
     * 
     * @throws IOException
     * @throws SQLException
     */
    public void creerDB() throws SQLException, IOException {
        // Initialisation
        Connection connection = DriverManager.getConnection(url);

        // DROP la table si elle existe
        PreparedStatement dropStatement = connection.prepareStatement("DROP TABLE IF EXISTS messages;");
        dropStatement.execute();

        PreparedStatement iniStatement = connection.prepareStatement(
                "CREATE TABLE messages ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
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
    public boolean isInit() {
        try {
            Connection connection = DriverManager.getConnection(url);

            // Vérification de la présence la table
            PreparedStatement tableStatement = connection.prepareStatement(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='messages';");

            ResultSet tableResultSet = tableStatement.executeQuery();

            if (!tableResultSet.next()){
                return false;
            }

            // Vérification de la présence des collones.
            Map<String, String> collones = new HashMap<>();
            // Ajout des collones
            collones.put("id", "INTEGER");
            collones.put("utilisateur", "TEXT");
            collones.put("message", "TEXT");
            collones.put("date", "DATETIME");

            PreparedStatement colStatement = connection.prepareStatement(
                "PRAGMA table_info(messages);"
            );

            ResultSet colResultSet = colStatement.executeQuery();

            while (colResultSet.next()) {
                String colNom = colResultSet.getString("name");
                String colType = colResultSet.getString("type");

                if (!collones.remove(colNom, colType)) return false;
            }

            connection.close();
            return collones.isEmpty();

        } catch (SQLException e) {
            System.out.println("La table n'est pas initialisée : " + e);
            return false;
        }
    }
}