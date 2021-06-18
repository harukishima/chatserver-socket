package controller;

import model.ClientTableModel;
import run.App;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class NewClientHandler extends Thread {
    private final Socket socket;
    private final ClientTableModel clientTableModel;

    public NewClientHandler(Socket socket, ClientTableModel clientTableModel) {
        this.clientTableModel = clientTableModel;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Message message = MessageController.receive(socket);
            System.out.println("Connection from " + socket + "\n" + message.toString());
//            if (message == null) {
//                System.out.println("Cannot read message");
//                return;
//            }
            for (Client c : App.clientVector) {
                if (c.getUsername().equals(message.getSender())) {
                    System.out.println("Username's already exist! REJECT!");
                    Message e = new Message("server", "message", "Username existed", -1);
                    MessageController.send(socket, e);
                    socket.close();
                    return;
                }
            }
            Message res = new Message("server", message.getSender(), "Success", 1);
            System.out.println(res);
            MessageController.send(socket, res);
            Client client = new Client(socket, message.getSender());
            App.clientVector.add(client);
            SwingUtilities.invokeLater(clientTableModel::fireTableDataChanged);
            MessageController.fireClientListChange();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
