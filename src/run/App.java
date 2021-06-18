package run;

import controller.Client;
import model.ChatRoom;
import view.MainForm;

import java.util.Vector;

public class App {
    public static MainForm mainForm = null;

    public static Vector<Client> clientVector = new Vector<>();

    public static Vector<ChatRoom> chatRoomVector = new Vector<>();

    public static void main(String[] args) {
        mainForm = new MainForm();
    }
}
