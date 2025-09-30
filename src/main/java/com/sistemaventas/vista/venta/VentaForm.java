package com.sistemaventas.vista.venta;

import com.sistemaventas.controlador.VentaController;
import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.modelo.Cliente;
import com.sistemaventas.modelo.Producto;
import com.sistemaventas.modelo.Venta;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario para registrar ventas
 * @author Matt_
 */
public class VentaForm extends JDialog {
    
    private JComboBox<ComboItem> cmbClientes;
    private JComboBox<ComboItem> cmbProductos;
    private JSpinner spnCantidad;
    private JTextField txtPrecioUnitario;
    private JTextField txtTotal;
    private JTextField txtFecha;
    private JLabel lblStockDisponible;
    
    private boolean ventaGuardada = false;
    private VentaController ventaController;
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    
    public VentaForm(JFrame owner) {
        super(owner, "Registrar Nueva Venta", true);
        
        this.ventaController = new VentaController();
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
        
        initComponents();
        setupLayout();
        cargarDatos();
        
        setSize(500, 450);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        cmbClientes = new JComboBox<>();
        cmbProductos = new JComboBox<>();
        
        // Spinner para cantidad
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 9999, 1);
        spnCantidad = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnCantidad, "#");
        spnCantidad.setEditor(editor);
        
        txtPrecioUnitario = new JTextField(15);
        txtPrecioUnitario.setEditable(false);
        txtPrecioUnitario.setBackground(Color.LIGHT_GRAY);
        
        txtTotal = new JTextField(15);
        txtTotal.setEditable(false);
        txtTotal.setBackground(Color.LIGHT_GRAY);
        txtTotal.setFont(txtTotal.getFont().deriveFont(Font.BOLD, 14f));
        
        // Campo de fecha con fecha actual por defecto
        txtFecha = new JTextField(15);
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtFecha.setToolTipText("Formato: dd/MM/yyyy");
        
        lblStockDisponible = new JLabel("Stock disponible: -");
        lblStockDisponible.setFont(lblStockDisponible.getFont().deriveFont(Font.ITALIC));
        
        // Evento cuando cambia el producto seleccionado
        cmbProductos.addActionListener(e -> actualizarPrecioYStock());
        
        // Evento cuando cambia la cantidad
        spnCantidad.addChangeListener(e -> calcularTotal());
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fecha
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblFecha = new JLabel("Fecha *:");
        lblFecha.setFont(lblFecha.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblFecha, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(txtFecha, gbc);
        
        // Cliente
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel lblCliente = new JLabel("Cliente *:");
        lblCliente.setFont(lblCliente.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblCliente, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbClientes, gbc);
        
        // Producto
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel lblProducto = new JLabel("Producto *:");
        lblProducto.setFont(lblProducto.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblProducto, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(cmbProductos, gbc);
        
        // Stock disponible
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(lblStockDisponible, gbc);
        
        // Cantidad
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        JLabel lblCantidad = new JLabel("Cantidad *:");
        lblCantidad.setFont(lblCantidad.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblCantidad, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel cantidadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        spnCantidad.setPreferredSize(new Dimension(150, 25));
        cantidadPanel.add(spnCantidad);
        JLabel lblUnidades = new JLabel(" unidades");
        cantidadPanel.add(lblUnidades);
        formPanel.add(cantidadPanel, gbc);
        
        // Precio Unitario
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Precio Unitario:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel precioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel lblPeso = new JLabel("$ ");
        precioPanel.add(lblPeso);
        precioPanel.add(txtPrecioUnitario);
        formPanel.add(precioPanel, gbc);
        
        // Total
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        formPanel.add(lblTotal, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel lblPesoTotal = new JLabel("$ ");
        lblPesoTotal.setFont(lblPesoTotal.getFont().deriveFont(Font.BOLD, 14f));
        totalPanel.add(lblPesoTotal);
        totalPanel.add(txtTotal);
        formPanel.add(totalPanel, gbc);
        
        // Nota de campos obligatorios
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(lblNota.getFont().deriveFont(Font.ITALIC, 10f));
        lblNota.setForeground(Color.GRAY);
        formPanel.add(lblNota, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("💾 Registrar Venta");
        JButton btnCancelar = new JButton("Cancelar");
        
        btnGuardar.setPreferredSize(new Dimension(150, 35));
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        
        btnGuardar.setBackground(new Color(46, 125, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(btnGuardar.getFont().deriveFont(Font.BOLD));
        
        // Configurar botón Guardar como predeterminado
        getRootPane().setDefaultButton(btnGuardar);
        
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        // Eventos
        btnGuardar.addActionListener(e -> registrarVenta());
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
        try {
            // Cargar clientes
            List<Cliente> clientes = clienteDAO.obtenerTodos();
            cmbClientes.addItem(new ComboItem(0, "-- Seleccione un cliente --"));
            for (Cliente c : clientes) {
                cmbClientes.addItem(new ComboItem(c.getIdCliente(), c.getNombre()));
            }
            
            // Cargar productos
            List<Producto> productos = productoDAO.obtenerTodos();
            cmbProductos.addItem(new ComboItem(0, "-- Seleccione un producto --"));
            for (Producto p : productos) {
                String displayText = String.format("%s (Stock: %d)", p.getNombre(), p.getStock());
                cmbProductos.addItem(new ComboItem(p.getIdProducto(), displayText));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void actualizarPrecioYStock() {
        ComboItem itemSeleccionado = (ComboItem) cmbProductos.getSelectedItem();
        
        if (itemSeleccionado != null && itemSeleccionado.getId() > 0) {
            try {
                Producto producto = productoDAO.buscarPorId(itemSeleccionado.getId());
                
                if (producto != null) {
                    txtPrecioUnitario.setText(producto.getPrecio().toString());
                    lblStockDisponible.setText(String.format("Stock disponible: %d unidades", producto.getStock()));
                    
                    // Ajustar el límite del spinner según el stock
                    SpinnerNumberModel model = (SpinnerNumberModel) spnCantidad.getModel();
                    model.setMaximum(producto.getStock());
                    
                    // Si la cantidad actual excede el stock, ajustarla
                    int cantidadActual = (Integer) spnCantidad.getValue();
                    if (cantidadActual > producto.getStock()) {
                        spnCantidad.setValue(Math.min(1, producto.getStock()));
                    }
                    
                    calcularTotal();
                }
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Error al cargar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            txtPrecioUnitario.setText("");
            txtTotal.setText("");
            lblStockDisponible.setText("Stock disponible: -");
        }
    }
    
    private void calcularTotal() {
        ComboItem itemProducto = (ComboItem) cmbProductos.getSelectedItem();
        
        if (itemProducto != null && itemProducto.getId() > 0 && !txtPrecioUnitario.getText().isEmpty()) {
            try {
                BigDecimal precio = new BigDecimal(txtPrecioUnitario.getText());
                int cantidad = (Integer) spnCantidad.getValue();
                
                BigDecimal total = precio.multiply(BigDecimal.valueOf(cantidad));
                txtTotal.setText(String.format("%.2f", total));
                
            } catch (NumberFormatException e) {
                txtTotal.setText("");
            }
        } else {
            txtTotal.setText("");
        }
    }
    
    private void registrarVenta() {
        try {
            // Validar fecha
            if (txtFecha.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "La fecha es obligatoria",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
                txtFecha.requestFocus();
                return;
            }
            
            LocalDate fecha;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fecha = LocalDate.parse(txtFecha.getText().trim(), formatter);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use: dd/MM/yyyy",
                    "Fecha inválida",
                    JOptionPane.WARNING_MESSAGE);
                txtFecha.requestFocus();
                return;
            }
            
            // Validar cliente
            ComboItem itemCliente = (ComboItem) cmbClientes.getSelectedItem();
            if (itemCliente == null || itemCliente.getId() <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un cliente",
                    "Cliente requerido",
                    JOptionPane.WARNING_MESSAGE);
                cmbClientes.requestFocus();
                return;
            }
            
            // Validar producto
            ComboItem itemProducto = (ComboItem) cmbProductos.getSelectedItem();
            if (itemProducto == null || itemProducto.getId() <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Debe seleccionar un producto",
                    "Producto requerido",
                    JOptionPane.WARNING_MESSAGE);
                cmbProductos.requestFocus();
                return;
            }
            
            int cantidad = (Integer) spnCantidad.getValue();
            
            // Verificar stock nuevamente
            Producto producto = productoDAO.buscarPorId(itemProducto.getId());
            if (producto.getStock() < cantidad) {
                JOptionPane.showMessageDialog(this,
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                        producto.getStock(), cantidad),
                    "Stock insuficiente",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear la venta
            Venta nuevaVenta = new Venta();
            nuevaVenta.setFecha(fecha);
            nuevaVenta.setIdCliente(itemCliente.getId());
            nuevaVenta.setIdProducto(itemProducto.getId());
            nuevaVenta.setCantidad(cantidad);
            nuevaVenta.setPrecioUnitario(producto.getPrecio());
            nuevaVenta.setTotal(nuevaVenta.calcularTotal());
            
            // Registrar la venta
            if (ventaController.registrarVenta(nuevaVenta)) {
                ventaGuardada = true;
                
                String mensaje = String.format(
                    "¡Venta registrada exitosamente!\n\n" +
                    "Cliente: %s\n" +
                    "Producto: %s\n" +
                    "Cantidad: %d unidades\n" +
                    "Total: $%.2f",
                    itemCliente.getTexto().split(" - ")[0],
                    producto.getNombre(),
                    cantidad,
                    nuevaVenta.getTotal()
                );
                
                JOptionPane.showMessageDialog(this,
                    mensaje,
                    "Venta registrada",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo registrar la venta",
                    "Error al guardar",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error de base de datos: " + e.getMessage(),
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
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea cancelar?",
            "Cancelar operación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            ventaGuardada = false;
            dispose();
        }
    }
    
    public boolean isVentaGuardada() {
        return ventaGuardada;
    }
    
    // Clase auxiliar para manejar items del ComboBox
    private static class ComboItem {
        private int id;
        private String texto;
        
        public ComboItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }
        
        public int getId() {
            return id;
        }
        
        public String getTexto() {
            return texto;
        }
        
        @Override
        public String toString() {
            return texto;
        }
    }
}