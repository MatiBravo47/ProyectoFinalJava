package com.sistemaventas.vista.venta;

import javax.swing.*;
import java.awt.*;

public class VentaForm extends JDialog {
    public VentaForm(JFrame owner) {
        super(owner, "Venta - Alta/Modificación", true);
        setSize(420, 260);
        setLocationRelativeTo(owner);

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        form.add(new JLabel("Fecha (YYYY-MM-DD):"));
        JTextField txtFecha = new JTextField();
        form.add(txtFecha);

        form.add(new JLabel("Cliente:"));
        JTextField txtCliente = new JTextField();
        form.add(txtCliente);

        form.add(new JLabel("Total:"));
        JTextField txtTotal = new JTextField();
        form.add(txtTotal);

        form.add(new JLabel("Observaciones:"));
        JTextField txtObs = new JTextField();
        form.add(txtObs);

        JPanel botones = new JPanel();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> JOptionPane.showMessageDialog(VentaForm.this, "Guardar no implementado"));
        btnCancelar.addActionListener(e -> dispose());

        botones.add(btnGuardar);
        botones.add(btnCancelar);

        add(form, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }
}
