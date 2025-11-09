package com.projetchat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

import javax.crypto.SecretKey;

/**
 * Classe permettant de gérer le client
 */
public class ClientHandler implements Runnable {
    /** Le socket de connexion */
    private Socket socket;

    /** La clef de chiffrement AES */
    private static SecretKey key;

    private BufferedReader input;
    private PrintWriter output;
    private Set<ClientHandler> clients;
    private String clientName;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public static void setKey(SecretKey secretKey) {
        key = secretKey;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            output.println("Entrez votre nom : ");
            clientName = input.readLine();
            broadcast("📢 " + clientName + " a rejoint le chat !");

            String message;
            while ((message = input.readLine()) != null) {
                broadcast("💬 " + clientName + " : " + message);
            }
        } catch (IOException e) {
            System.out.println("Client déconnecté : " + clientName);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this);
            broadcast("❌ " + clientName + " a quitté le chat.");
        }
    }

    /**
     * Transmet un message à l'ensemble des clients
     * 
     * @param message le message à transmettre
     */
    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.output.println(message);
        }
    }
}
