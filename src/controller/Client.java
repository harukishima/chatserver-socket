package controller;

import java.net.Socket;

public class Client {
    private final Socket socket;
    private final String username;
    private final ClientHandler clientHandler;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        clientHandler = new ClientHandler(this);
        clientHandler.start();
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }
}
