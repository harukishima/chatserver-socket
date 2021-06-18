package model;

import controller.Client;
import controller.Message;
import controller.MessageController;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ChatRoom {
    Vector<Client> clientVector;
    String roomId = "";

    public ChatRoom() {
        int max = 999999;
        int min = 100000;
        int random_int = (int)Math.floor(Math.random()*(max-min+1)+min);
        roomId = "#" + random_int;
        clientVector = new Vector<>();
    }

    public String getRoomId() {
        return roomId;
    }

    public void addClient(Client client) {
        if (!isInRoom(client)) {
            clientVector.add(client);
        }
    }

    public boolean isInRoom(Client client) {
        return clientVector.contains(client);
    }

    public boolean removeClient(Client client) {
        return clientVector.remove(client);
    }

    public synchronized void sendToAll(Message message) {
        String sender = message.getSender();
        message.setBody(sender + ":" + message.getBody());
        message.setSender(roomId);
        for(Client c : clientVector) {
            MessageController.send(c.getSocket(), message);
        }
    }

    public void sendToAllExcept(Message message, Client client) {
        String sender = message.getSender();
        message.setBody(sender + "`" + message.getBody());
        message.setSender(roomId);
        for(Client c : clientVector) {
            if (c != client) {
                MessageController.send(c.getSocket(), message);
            }
        }
    }

    public static boolean roomExist(Vector<ChatRoom> chatRoomVector, ChatRoom chatRoom) {
        List<ChatRoom> list = chatRoomVector.stream()
                .filter(c -> chatRoom.getRoomId().equals(c.getRoomId()))
                .collect(Collectors.toList());
        return list.size() != 0;
    }

    public static ChatRoom getRoomById(Vector<ChatRoom> chatRoomVector, String id) {
        ChatRoom chatRoom = null;
        List<ChatRoom> list = chatRoomVector.stream()
                .filter(c -> id.equals(c.getRoomId()))
                .collect(Collectors.toList());
        if (list.size() > 0) {
            chatRoom = list.get(0);
        }
        return chatRoom;
    }

    public void sendInfo(Client client) {
        StringBuilder str = new StringBuilder();
        for (Client c : clientVector) {
            str.append(c.getUsername());
            str.append(",");
        }
        Message message = new Message(roomId, client.getUsername(), str.toString(), 8);
        MessageController.send(client.getSocket(), message);
    }

    public void broadcastMessage(Message message) {
        for (Client c : clientVector) {
            MessageController.send(c.getSocket(), message);
        }
    }

    public void notifyAllAndRemove(Client client) {
        removeClient(client);
        Message message = new Message(roomId, "", "", 7);
        broadcastMessage(message);
    }

    public static List<ChatRoom> getListByClient(Vector<ChatRoom> chatRoomVector ,Client client) {
        return chatRoomVector.stream()
                .filter(c -> c.isInRoom(client))
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return clientVector.isEmpty();
    }
}
