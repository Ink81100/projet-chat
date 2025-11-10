package com.projetchat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    /** L'adresse du serveur */
    private final String adresse;
    /** Le port du serveur */
    private final int port;
    /** Le nom du client */
    private final String nom;

    /**
     * Le constructeur du client
     * 
     * @param adresse L'adresse du serveur
     * @param port    Le port du serveur
     * @param nom     Le nom du client
     */
    public Client(String adresse, int port, String nom) {
        this.adresse = adresse;
        this.port = port;
        this.nom = nom;
    }

    /** Démarre le client */
    public void start() {
        try (Socket socket = new Socket(adresse, port)) {
            System.out.println("Connecté au serveur.");

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Thread pour écouter les messages du serveur
            EcouteHandler ecouteHandler = new EcouteHandler(socket);
            new Thread(ecouteHandler).start();

            //Envois du nom
            output.println(nom);

            // Envoi des messages depuis la console
            String message;
            while ((message = console.readLine()) != null) {
                output.println(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //Initialisation
        int port = 10001;
        String adresse = "localhost";
        String nom = "Adrien";

        Client client = new Client(adresse, port, nom);
        client.start();
    }
}
