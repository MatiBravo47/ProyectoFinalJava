package com.sistemaventas.vista.cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteForm extends JDialog {
    public ClienteForm(JFrame owner) {
        super(owner, "Cliente - Alta/Modificación", true);
        setSize(360, 220);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(3,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Nombre:"));
        JTextField txtNombre = new JTextField();
        form.add(txtNombre);

        form.add(new JLabel("Email:"));
        JTextField txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Teléfono:"));
        JTextField txtTelefono = new JTextField();
        form.add(txtTelefono);

        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> JOptionPane.showMessageDialog(ClienteForm.this, "Guardar no implementado"));
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }
}
