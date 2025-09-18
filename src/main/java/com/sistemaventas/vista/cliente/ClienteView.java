package com.sistemaventas.vista.cliente;

import com.sistemaventas.vista.tables.ClienteTableModel;

import javax.swing.*;
import java.awt.*;

public class ClienteView extends JFrame {
    public ClienteView() {
        setTitle("ABM Clientes");
        setSize(700, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        ClienteTableModel model = new ClienteTableModel();
        JTable tabla = new JTable(model);

        JPanel panelBotones = new JPanel();
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        btnNuevo.addActionListener(e -> new ClienteForm(this).setVisible(true));
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
