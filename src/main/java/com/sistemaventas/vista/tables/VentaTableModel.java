package com.sistemaventas.vista.tables;

import javax.swing.table.AbstractTableModel;

public class VentaTableModel extends AbstractTableModel {
    private String[] columns = {"ID", "Fecha", "Cliente", "Total"};
    private Object[][] data = {
            {"1", "2025-09-01", "Juan Pérez", "2500.00"},
            {"2", "2025-09-05", "Ana López", "1800.00"}
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
