package com.sistemaventas.vista.tables;

import javax.swing.table.AbstractTableModel;

public class ProductoTableModel extends AbstractTableModel {
    private String[] columns = {"ID", "Nombre", "Precio", "Stock"};
    private Object[][] data = {
            {"1", "Inodoro Ferrum Andina", "1200.00", "10"},
            {"2", "Lavatorio Ferrum Bari", "800.00", "5"}
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
