package view;

import controller.*;
import model.ClientTableModel;
import run.App;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.NumberFormat;

public class MainForm extends JFrame {
    private JFormattedTextField portField;
    private JButton startServerButton;
    private JButton stopServerButton;
    private JPanel panel;
    private JTable clientTable;
    private ServerListener serverListener = null;
    private ClientTableModel clientTableModel;
    private ClientListHandler clientListHandler;

    public MainForm() {
        add(panel);
        setTitle("Chat server");
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        initForm();
    }

    private void initForm() {
        portField.setValue(5678);
        startServerButton.addActionListener(e -> startServer());
        stopServerButton.addActionListener(e -> stopServer());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                serverDisconnect();
            }
        });
        clientTableModel = new ClientTableModel(App.clientVector);
        clientTable.setModel(clientTableModel);
    }

    private void createUIComponents() {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setGroupingUsed(false);
        NumberFormatter numberFormatter = new NumberFormatter(numberFormat);
        numberFormatter.setValueClass(Integer.class);
        numberFormatter.setAllowsInvalid(false);
        portField = new JFormattedTextField(numberFormatter);
    }

    private void startServer() {
        portField.setEditable(false);
        startServerButton.setEnabled(false);
        stopServerButton.setEnabled(true);
        serverListener = new ServerListener((Integer) portField.getValue(), clientTableModel);
        clientListHandler = new ClientListHandler(clientTableModel);
        clientListHandler.start();
    }

    private void stopServer() {
        portField.setEditable(true);
        stopServerButton.setEnabled(false);
        startServerButton.setEnabled(true);
        serverDisconnect();
    }

    private void serverDisconnect() {
        Message disconnect = new Message("server", "boardcast", "", -3);
        try {
            for (Client c : App.clientVector) {
                MessageController.send(c.getSocket(), disconnect);
                c.getClientHandler().stopExecute();
            }
            App.clientVector.clear();
            App.chatRoomVector.clear();
            if (serverListener != null) {
                serverListener.stopExecute();
            }
            if (clientListHandler != null) {
                clientListHandler.stopExecute();
            }
            clientTable.updateUI();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
