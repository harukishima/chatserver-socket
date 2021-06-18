package controller;

import model.ChatRoom;
import run.App;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientHandler extends Thread {
    private final Client client;
    private boolean execute;

    public ClientHandler(Client client) {
        this.client = client;
        execute = true;
    }

    public void stopExecute() throws IOException {
        execute = false;
        client.getSocket().close();
    }

    @Override
    public void run() {
        try {
           while(execute) {
               Message message = MessageController.receive(client.getSocket());
               System.out.println(message);
               switch (message.getCode()) {
                   case -2:
                       stopExecute();
                       List<ChatRoom> chatRoomList = ChatRoom.getListByClient(App.chatRoomVector, this.client);
                       for (ChatRoom c : chatRoomList) {
                           c.notifyAllAndRemove(client);
                       }
                       System.out.println("Stop socket" + client.getSocket().toString());
                       break;
                   case 2: case 3:
                       if (!sendToOne(message)) {
                           ChatRoom chatRoomM = ChatRoom.getRoomById(App.chatRoomVector, message.getReceiver());
                           if (chatRoomM != null) {
                               chatRoomM.sendToAllExcept(message, this.client);
                           } else {
                               Message err = new Message(message.getReceiver(), client.getUsername(), "Not found", 3);
                               MessageController.send(client.getSocket(), err);
                           }
                       }
                       break;
                   case 6:
                       Client client = ClientListHandler.getClientByUserName(message.getSender());
                       if (client != null) {
                           ClientListHandler.sendClientList(client);
                       } else {
                           System.out.println("Client is null");
                       }
                       break;
                   case 8:
                       ChatRoom chatRoom = ChatRoom.getRoomById(App.chatRoomVector, message.getReceiver());
                       if (chatRoom != null) {
                            chatRoom.sendInfo(this.client);
                       }
                       break;
                   case 9:
                       ChatRoom room;
                       do {
                           room = new ChatRoom();
                       } while(ChatRoom.roomExist(App.chatRoomVector, room));
                       room.addClient(this.client);
                       App.chatRoomVector.add(room);
                       Message message1 = new Message(room.getRoomId(), message.getSender(), "", 9);
                       MessageController.send(this.client.getSocket(), message1);
                       break;
                   case 10:
                       ChatRoom roomJoin = ChatRoom.getRoomById(App.chatRoomVector, message.getReceiver());
                       if (roomJoin != null) {
                           Message joinSuccess = new Message(message.getReceiver(), this.client.getUsername(), "", 11);
                           MessageController.send(this.client.getSocket(), joinSuccess);
                           roomJoin.addClient(this.client);
                           Message updateList = new Message(roomJoin.getRoomId(), "", "", 7);
                           roomJoin.broadcastMessage(updateList);
                           break;
                       }
                       Message errJoin = new Message("server", this.client.getUsername(), "", -1);
                       MessageController.send(this.client.getSocket(), errJoin);
                       break;
                   default:
               }
           }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                stopExecute();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private boolean sendToOne(Message message) {
        List<Client> result = App.clientVector.stream()
                .filter(a -> Objects.equals(a.getUsername(), message.getReceiver()))
                .collect(Collectors.toList());
        if (result.isEmpty()) {
            return false;
        }
        return MessageController.send(result.get(0).getSocket(), message);
    }
}
