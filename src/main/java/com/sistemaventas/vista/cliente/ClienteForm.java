package com.sistemaventas.vista.cliente;

import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Cliente;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ClienteForm extends JDialog {
    
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private Cliente clienteEditando;
    private boolean clienteGuardado = false;
    private ClienteDAO clienteDAO;
    
    // Constructor para nuevo cliente
    public ClienteForm(JFrame owner) {
        this(owner, null);
    }
    
    // Constructor para editar cliente existente
    public ClienteForm(JFrame owner, Cliente cliente) {
        super(owner, cliente == null ? "Nuevo Cliente" : "Editar Cliente", true);
        this.clienteEditando = cliente;
        this.clienteDAO = new ClienteDAO();
        
        initComponents();
        setupLayout();
        cargarDatos();
        
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        txtNombre = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtEmail = new JTextField(20);
        
        // Configurar campos
        txtNombre.setToolTipText("Ingrese el nombre completo del cliente");
        txtTelefono.setToolTipText("Ingrese el teléfono (opcional)");
        txtEmail.setToolTipText("Ingrese el email (opcional)");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre (obligatorio)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre *:");
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblNombre, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtNombre, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtTelefono, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(txtEmail, gbc);
        
        // Nota de campos obligatorios
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC, 10f));
        lblNota.setForeground(Color.GRAY);
        formPanel.add(lblNota, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setPreferredSize(new Dimension(100, 30));
        btnCancelar.setPreferredSize(new Dimension(100, 30));
        
        // Configurar botón Guardar como predeterminado
        getRootPane().setDefaultButton(btnGuardar);
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        // Eventos
        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> cancelar());
        
        // Escape para cancelar
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cancelar();
            }
        });
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        if (clienteEditando != null) {
            txtNombre.setText(clienteEditando.getNombre());
            txtTelefono.setText(clienteEditando.getTelefono());
            txtEmail.setText(clienteEditando.getEmail());
        }
        
        // Focus en el primer campo
        SwingUtilities.invokeLater(() -> txtNombre.requestFocus());
    }
    
    private void guardarCliente() {
        try {
            // Validar campos obligatorios
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El nombre es obligatorio", 
                    "Campo requerido", 
                    JOptionPane.WARNING_MESSAGE);
                txtNombre.requestFocus();
                return;
            }
            
            // Validar email si se ingresó
            String email = txtEmail.getText().trim();
            if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un email válido", 
                    "Email inválido", 
                    JOptionPane.WARNING_MESSAGE);
                txtEmail.requestFocus();
                return;
            }
            
            // Crear o actualizar cliente
            if (clienteEditando == null) {
                // Nuevo cliente
                Cliente nuevoCliente = new Cliente(
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim(),
                    email.isEmpty() ? null : email
                );
                
                if (clienteDAO.guardar(nuevoCliente)) {
                    clienteGuardado = true;
                    JOptionPane.showMessageDialog(this, 
                        "Cliente guardado exitosamente", 
                        "Guardado exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo guardar el cliente", 
                        "Error al guardar", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                // Actualizar cliente existente
                clienteEditando.setNombre(txtNombre.getText().trim());
                clienteEditando.setTelefono(txtTelefono.getText().trim().isEmpty() ? null : txtTelefono.getText().trim());
                clienteEditando.setEmail(email.isEmpty() ? null : email);
                
                if (clienteDAO.actualizar(clienteEditando)) {
                    clienteGuardado = true;
                    JOptionPane.showMessageDialog(this, 
                        "Cliente actualizado exitosamente", 
                        "Actualización exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo actualizar el cliente", 
                        "Error al actualizar", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            String mensaje = "Error de base de datos: " + e.getMessage();
            
            // Manejar errores específicos
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                if (e.getMessage().contains("email")) {
                    mensaje = "Ya existe un cliente con ese email";
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                mensaje, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
                
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                e.getMessage(), 
                "Datos inválidos", 
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error inesperado: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelar() {
        // Verificar si hay cambios sin guardar
        if (hayCambiosSinGuardar()) {
            int respuesta = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de que desea salir sin guardar los cambios?", 
                "Cambios sin guardar", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
                
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        clienteGuardado = false;
        dispose();
    }
    
    private boolean hayCambiosSinGuardar() {
        if (clienteEditando == null) {
            // Cliente nuevo - hay cambios si algún campo tiene texto
            return !txtNombre.getText().trim().isEmpty() ||
                   !txtTelefono.getText().trim().isEmpty() ||
                   !txtEmail.getText().trim().isEmpty();
        } else {
            // Cliente existente - comparar valores
            String nombreActual = txtNombre.getText().trim();
            String telefonoActual = txtTelefono.getText().trim();
            String emailActual = txtEmail.getText().trim();
            
            String nombreOriginal = clienteEditando.getNombre() != null ? clienteEditando.getNombre() : "";
            String telefonoOriginal = clienteEditando.getTelefono() != null ? clienteEditando.getTelefono() : "";
            String emailOriginal = clienteEditando.getEmail() != null ? clienteEditando.getEmail() : "";
            
            return !nombreActual.equals(nombreOriginal) ||
                   !telefonoActual.equals(telefonoOriginal) ||
                   !emailActual.equals(emailOriginal);
        }
    }
    
    public boolean isClienteGuardado() {
        return clienteGuardado;
    }
}
