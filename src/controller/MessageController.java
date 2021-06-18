package controller;

import run.App;

import java.io.*;
import java.net.Socket;

public class MessageController {
    public static Message receive(Socket socket)  {
        Message message = null;
        try {
            ObjectInputStream stream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            message = (Message) stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return message;
    }

    public static boolean send(Socket socket,Message message)  {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            stream.writeObject(message);
            stream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void fireClientListChange() {
        Message message = new Message("server", "broadcast", "", 5);
        System.out.println(message);
        for(Client c : App.clientVector) {
            send(c.getSocket(), message);
        }
    }
}
