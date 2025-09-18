package com.sistemaventas.vista.venta;

import com.sistemaventas.vista.tables.VentaTableModel;

import javax.swing.*;
import java.awt.*;

public class VentaView extends JFrame {
    public VentaView() {
        setTitle("ABM Ventas");
        setSize(800,420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        VentaTableModel model = new VentaTableModel();
        JTable tabla = new JTable(model);

        JPanel panelBotones = new JPanel();
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        btnNuevo.addActionListener(e -> new VentaForm(this).setVisible(true));
        btnEditar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Editar no implementado"));
        btnEliminar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Eliminar no implementado"));
        btnActualizar.addActionListener(e -> JOptionPane.showMessageDialog(this, "Actualizar no implementado"));

        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
}
