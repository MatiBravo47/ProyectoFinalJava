package com.sistemaventas.vista.tables;

import javax.swing.table.AbstractTableModel;

public class ClienteTableModel extends AbstractTableModel {
    private String[] columns = {"ID", "Nombre", "Email"};
    private Object[][] data = {
            {"1", "Juan Pérez", "juan@mail.com"},
            {"2", "Ana López", "ana@mail.com"}
    };

    @Override
    public int getRowCount() { return data.length; }

    @Override
    public int getColumnCount() { return columns.length; }

    @Override
    public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) { return data[row][col]; }
}
