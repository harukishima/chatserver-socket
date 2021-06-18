package controller;

import model.ChatRoom;
import model.ClientTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import run.App;
import util.XmlUtils;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientListHandler extends Thread {
    private boolean execute;
    private final ClientTableModel clientTableModel;

    public ClientListHandler(ClientTableModel clientTableModel) {
        this.clientTableModel = clientTableModel;
        execute = true;
    }

    public void stopExecute() {
        this.execute = false;
    }

    @Override
    public void run() {
        while (execute) {
            if (App.clientVector.removeIf(c -> c.getSocket().isClosed())) {
                SwingUtilities.invokeLater(clientTableModel::fireTableDataChanged);
                System.out.println("Remove client");
                MessageController.fireClientListChange();
            }
            App.chatRoomVector.removeIf(ChatRoom::isEmpty);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static String buildXmlClientList() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element rootElement = doc.createElement("clients");
            doc.appendChild(rootElement);
            for (Client c : App.clientVector) {
                Element topElement = doc.createElement("client");
                Element username = doc.createElement("username");
                username.setTextContent(c.getUsername());
                topElement.appendChild(username);
                rootElement.appendChild(topElement);
            }
            return XmlUtils.XmlDocToString(doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void sendClientList(Client client) {
        String xmlString = buildXmlClientList();
        Message message = new Message("server", client.getUsername(), xmlString, 6);
        MessageController.send(client.getSocket(), message);
    }

    public static Client getClientByUserName(String username) {
        List<Client> list = App.clientVector.stream()
                .filter(c -> Objects.equals(username, c.getUsername()))
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }
}
