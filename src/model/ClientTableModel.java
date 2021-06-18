package model;

import controller.Client;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class ClientTableModel extends AbstractTableModel {
    private Vector<Client> list;
    private final String[] columnNames = {"Username", "Address", "Port"};

    public ClientTableModel(Vector<Client> list) {
        this.list = list;
    }

    public Vector<Client> getList() {
        return list;
    }

    public void setList(Vector<Client> list) {
        this.list = list;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Client client = list.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> client.getUsername();
            case 1 -> client.getSocket().getInetAddress().toString();
            case 2 -> client.getSocket().getPort();
            default -> null;
        };
    }
}
