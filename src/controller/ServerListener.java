package controller;

import model.ClientTableModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener implements Runnable {
    private final int port;
    private final Thread thread;
    private boolean execute;
    private ClientTableModel clientTableModel;
    ServerSocket serverSocket = null;

    public ServerListener(int port, ClientTableModel clientTableModel) {
        this.clientTableModel = clientTableModel;
        this.port = port;
        thread = new Thread(this);
        execute = true;
        thread.start();
    }

    public Thread getThread() {
        return thread;
    }

    public void stopExecute() {
        execute = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (execute) {
                System.out.println("Accepting connection");
                Socket socket = serverSocket.accept();
                NewClientHandler newClientHandler = new NewClientHandler(socket, clientTableModel);
                newClientHandler.start();
//                Message message = MessageController.receive(socket);
//                System.out.println("Connection from " + socket + "\n" + message.toString());
//                if (message == null) {
//                    System.out.println("Cannot read message");
//                    continue;
//                }
//                for (Client c : App.clientVector) {
//                    if (c.getUsername().equals(message.getSender())) {
//                        System.out.println("Username's already exist! REJECT!");
//                        Message e = new Message("server", "message", "Username existed", -1);
//                        MessageController.send(socket, e);
//                        socket.close();
//                        break;
//                    }
//                }
//
//                Client client = new Client(socket, message.getSender());
//                App.clientVector.add(client);
//                Message res = new Message("server", client.getUsername(), "Success", 1);
//                System.out.println(res);
//                MessageController.send(client.getSocket(), res);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
