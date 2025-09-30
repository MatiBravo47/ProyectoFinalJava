package com.sistemaventas.vista.producto;

import com.sistemaventas.controlador.ProductoController;
import com.sistemaventas.modelo.Producto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Formulario para crear y editar productos
 * @author Matt_
 */
public class ProductoForm extends JDialog {
    
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JSpinner spnStock;
    private Producto productoEditando;
    private boolean productoGuardado = false;
    private ProductoController productoController;
    
    // Constructor para nuevo producto
    public ProductoForm(JFrame owner) {
        this(owner, null);
    }
    
    // Constructor para editar producto existente
    public ProductoForm(JFrame owner, Producto producto) {
        super(owner, producto == null ? "Nuevo Producto" : "Editar Producto", true);
        this.productoEditando = producto;
        this.productoController = new ProductoController();
        
        initComponents();
        setupLayout();
        cargarDatos();
        
        setSize(450, 280);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        txtNombre = new JTextField(25);
        txtPrecio = new JTextField(15);
        
        // Spinner para el stock (facilita el ingreso de números)
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 999999, 1);
        spnStock = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnStock, "#");
        spnStock.setEditor(editor);
        
        // Configurar tooltips
        txtNombre.setToolTipText("Ingrese el nombre del producto");
        txtPrecio.setToolTipText("Ingrese el precio (ejemplo: 1500.50)");
        spnStock.setToolTipText("Ingrese la cantidad en stock");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Nombre (obligatorio)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("Nombre *:");
        lblNombre.setFont(lblNombre.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblNombre, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(txtNombre, gbc);
        
        // Precio (obligatorio)
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel lblPrecio = new JLabel("Precio *:");
        lblPrecio.setFont(lblPrecio.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblPrecio, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel precioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel lblPeso = new JLabel("$ ");
        lblPeso.setFont(lblPeso.getFont().deriveFont(Font.BOLD));
        precioPanel.add(lblPeso);
        precioPanel.add(txtPrecio);
        formPanel.add(precioPanel, gbc);
        
        // Stock (obligatorio)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel lblStock = new JLabel("Stock *:");
        lblStock.setFont(lblStock.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblStock, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spnStock.setPreferredSize(new Dimension(150, 25));
        stockPanel.add(spnStock);
        JLabel lblUnidades = new JLabel(" unidades");
        stockPanel.add(lblUnidades);
        formPanel.add(stockPanel, gbc);
        
        // Información adicional si es edición
        if (productoEditando != null) {
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Información actual"));
            
            BigDecimal valorInventario = productoEditando.getValorInventario();
            JLabel lblInfo = new JLabel(String.format(
                "Valor en inventario: $%.2f", valorInventario
            ));
            lblInfo.setFont(lblInfo.getFont().deriveFont(Font.ITALIC));
            infoPanel.add(lblInfo);
            
            formPanel.add(infoPanel, gbc);
        }
        
        // Nota de campos obligatorios
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
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
        
        btnGuardar.setBackground(new Color(46, 125, 50));
        btnGuardar.setForeground(Color.WHITE);
        
        // Configurar botón Guardar como predeterminado
        getRootPane().setDefaultButton(btnGuardar);
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        // Eventos
        btnGuardar.addActionListener(e -> guardarProducto());
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
        if (productoEditando != null) {
            txtNombre.setText(productoEditando.getNombre());
            txtPrecio.setText(productoEditando.getPrecio().toString());
            spnStock.setValue(productoEditando.getStock());
        } else {
            // Valores por defecto para nuevo producto
            spnStock.setValue(0);
        }
        
        // Focus en el primer campo
        SwingUtilities.invokeLater(() -> txtNombre.requestFocus());
    }
    
    private void guardarProducto() {
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
            
            if (txtPrecio.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "El precio es obligatorio", 
                    "Campo requerido", 
                    JOptionPane.WARNING_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
            
            // Validar formato de precio
            BigDecimal precio;
            try {
                precio = new BigDecimal(txtPrecio.getText().trim());
                
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        "El precio debe ser mayor a cero", 
                        "Precio inválido", 
                        JOptionPane.WARNING_MESSAGE);
                    txtPrecio.requestFocus();
                    return;
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un precio válido (ejemplo: 1500.50)", 
                    "Precio inválido", 
                    JOptionPane.WARNING_MESSAGE);
                txtPrecio.requestFocus();
                return;
            }
            
            // Obtener stock del spinner
            int stock = (Integer) spnStock.getValue();
            
            // Crear o actualizar producto
            if (productoEditando == null) {
                // Nuevo producto
                Producto nuevoProducto = new Producto(
                    txtNombre.getText().trim(),
                    precio,
                    stock
                );
                
                if (productoController.guardarProducto(nuevoProducto)) {
                    productoGuardado = true;
                    JOptionPane.showMessageDialog(this, 
                        "Producto guardado exitosamente", 
                        "Guardado exitoso", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo guardar el producto", 
                        "Error al guardar", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                // Actualizar producto existente
                productoEditando.setNombre(txtNombre.getText().trim());
                productoEditando.setPrecio(precio);
                productoEditando.setStock(stock);
                
                if (productoController.actualizarProducto(productoEditando)) {
                    productoGuardado = true;
                    JOptionPane.showMessageDialog(this, 
                        "Producto actualizado exitosamente", 
                        "Actualización exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "No se pudo actualizar el producto", 
                        "Error al actualizar", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (SQLException e) {
            String mensaje = "Error de base de datos: " + e.getMessage();
            
            // Manejar errores específicos
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                mensaje = "Ya existe un producto con ese nombre";
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
        
        productoGuardado = false;
        dispose();
    }
    
    private boolean hayCambiosSinGuardar() {
        if (productoEditando == null) {
            // Producto nuevo - hay cambios si algún campo tiene datos
            return !txtNombre.getText().trim().isEmpty() ||
                   !txtPrecio.getText().trim().isEmpty() ||
                   (Integer) spnStock.getValue() != 0;
        } else {
            // Producto existente - comparar valores
            String nombreActual = txtNombre.getText().trim();
            String precioActual = txtPrecio.getText().trim();
            int stockActual = (Integer) spnStock.getValue();
            
            String nombreOriginal = productoEditando.getNombre() != null ? productoEditando.getNombre() : "";
            String precioOriginal = productoEditando.getPrecio().toString();
            int stockOriginal = productoEditando.getStock();
            
            return !nombreActual.equals(nombreOriginal) ||
                   !precioActual.equals(precioOriginal) ||
                   stockActual != stockOriginal;
        }
    }
    
    public boolean isProductoGuardado() {
        return productoGuardado;
    }
}