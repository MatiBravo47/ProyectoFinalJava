package com.sistemaventas.vista.producto;

import javax.swing.*;
import java.awt.*;

public class ProductoForm extends JDialog {
    public ProductoForm(JFrame owner) {
        super(owner, "Producto - Alta/Modificación", true);
        setSize(360, 220);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(3,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField();
        form.add(txtNombre);

        form.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField();
        form.add(txtPrecio);

        form.add(new JLabel("Stock:"));
        JTextField txtStock = new JTextField();
        form.add(txtStock);

        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> JOptionPane.showMessageDialog(ProductoForm.this, "Guardar no implementado"));
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }
}
