package com.sistemaventas.vista;

import com.sistemaventas.vista.cliente.ClienteView;
import com.sistemaventas.vista.producto.ProductoView;
import com.sistemaventas.vista.venta.VentaView;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    public MainView() {
        setTitle("Sistema de Ventas - Menú Principal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(360, 240);
        setLocationRelativeTo(null);

        JPanel p = new JPanel(new GridLayout(3,1,10,10));
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JButton btnClientes = new JButton("ABM Clientes");
        JButton btnProductos = new JButton("ABM Productos");
        JButton btnVentas = new JButton("ABM Ventas");

        btnClientes.addActionListener(e -> new ClienteView().setVisible(true));
        btnProductos.addActionListener(e -> new ProductoView().setVisible(true));
        btnVentas.addActionListener(e -> new VentaView().setVisible(true));

        p.add(btnClientes);
        p.add(btnProductos);
        p.add(btnVentas);

        add(p);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainView().setVisible(true);
        });
    }
}
