package com.sistemaventas.vista.producto;

import com.sistemaventas.modelo.Producto;
import com.sistemaventas.vista.tables.ProductoTableModel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Vista principal para gestión de productos
 * @author Matt_
 */
public class ProductoView extends JFrame {
    
    private ProductoTableModel tableModel;
    private JTable tabla;
    private JTextField txtBuscar;
    private JLabel lblValorTotal;
    
    public ProductoView() {
        setTitle("ABM Productos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        setupLayout();
        setupEvents();
        
        // Cargar datos iniciales
        actualizarLista();
    }
    
    private void initComponents() {
        // Modelo y tabla
        tableModel = new ProductoTableModel();
        tabla = new JTable(tableModel);
        
        // Configurar tabla
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        
        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120); // Precio
        tabla.getColumnModel().getColumn(3).setPreferredWidth(100); // Stock
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150); // Valor Inventario
        
        // Campo de búsqueda
        txtBuscar = new JTextField(25);
        txtBuscar.setToolTipText("Escriba el nombre del producto a buscar...");
        
        // Label para valor total
        lblValorTotal = new JLabel("Valor total del inventario: $0.00");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior - Búsqueda y título
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("Gestión de Productos", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar producto:"));
        panelBusqueda.add(txtBuscar);
        
        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        JButton btnStockBajo = new JButton("⚠️ Stock Bajo");
        JButton btnRefrescar = new JButton("🔄 Actualizar");
        
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);
        panelBusqueda.add(btnStockBajo);
        panelBusqueda.add(btnRefrescar);
        
        // Eventos de búsqueda
        btnBuscar.addActionListener(e -> buscarProductos());
        btnMostrarTodos.addActionListener(e -> mostrarTodos());
        btnStockBajo.addActionListener(e -> mostrarStockBajo());
        btnRefrescar.addActionListener(e -> actualizarLista());
        txtBuscar.addActionListener(e -> buscarProductos()); // Enter en el campo
        
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        
        // Panel central - Tabla con información
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Productos"));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblCantidad = new JLabel("Total de productos: 0");
        lblValorTotal.setFont(lblValorTotal.getFont().deriveFont(Font.BOLD));
        
        panelInfo.add(lblCantidad);
        panelInfo.add(lblValorTotal);
        
        panelCentral.add(panelInfo, BorderLayout.SOUTH);
        
        // Panel inferior - Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("➕ Nuevo Producto");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnVerDetalles = new JButton("👁️ Ver Detalles");
        JButton btnAjustarStock = new JButton("📦 Ajustar Stock");
        JButton btnVolver = new JButton("← Volver al Menú");
        
        // Colores para los botones
        btnNuevo.setBackground(new Color(46, 125, 50));
        btnNuevo.setForeground(Color.WHITE);
        
        btnEditar.setBackground(new Color(25, 118, 210));
        btnEditar.setForeground(Color.WHITE);
        
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);
        
        btnVerDetalles.setBackground(new Color(117, 117, 117));
        btnVerDetalles.setForeground(Color.WHITE);
        
        btnAjustarStock.setBackground(new Color(255, 152, 0));
        btnAjustarStock.setForeground(Color.WHITE);
        
        btnVolver.setBackground(new Color(96, 96, 96));
        btnVolver.setForeground(Color.WHITE);
        
        // Tamaños uniformes
        Dimension buttonSize = new Dimension(140, 35);
        btnNuevo.setPreferredSize(buttonSize);
        btnEditar.setPreferredSize(buttonSize);
        btnEliminar.setPreferredSize(buttonSize);
        btnVerDetalles.setPreferredSize(buttonSize);
        btnAjustarStock.setPreferredSize(buttonSize);
        btnVolver.setPreferredSize(buttonSize);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVerDetalles);
        panelBotones.add(btnAjustarStock);
        panelBotones.add(btnVolver);
        
        // Eventos de botones
        btnNuevo.addActionListener(e -> nuevoProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnVerDetalles.addActionListener(e -> verDetallesProducto());
        btnAjustarStock.addActionListener(e -> ajustarStock());
        btnVolver.addActionListener(e -> dispose());
        
        // Agregar paneles al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        
        // Actualizar labels de información cuando cambie la tabla
        tableModel.addTableModelListener(e -> {
            lblCantidad.setText("Total de productos: " + tableModel.getRowCount());
            BigDecimal valorTotal = tableModel.calcularValorTotalInventario();
            lblValorTotal.setText(String.format("Valor total del inventario: $%.2f", valorTotal));
        });
    }
    
    private void setupEvents() {
        // Doble clic en la tabla para editar
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    editarProducto();
                }
            }
        });
        
        // Evento de cierre de ventana
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                dispose();
            }
        });
    }
    
    private void buscarProductos() {
        String termino = txtBuscar.getText().trim();
        
        if (termino.isEmpty()) {
            mostrarTodos();
            return;
        }
        
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tableModel.buscarPorNombre(termino);
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No se encontraron productos con el término: \"" + termino + "\"", 
                    "Sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error durante la búsqueda:\n" + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void mostrarTodos() {
        txtBuscar.setText("");
        actualizarLista();
    }
    
    private void mostrarStockBajo() {
        String input = JOptionPane.showInputDialog(this, 
            "Ingrese el umbral de stock bajo:", 
            "Filtrar Stock Bajo", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int umbral = Integer.parseInt(input.trim());
                
                if (umbral < 0) {
                    JOptionPane.showMessageDialog(this, 
                        "El umbral debe ser mayor o igual a cero", 
                        "Valor inválido", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                tableModel.filtrarStockBajo(umbral);
                
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, 
                        "No hay productos con stock menor a " + umbral, 
                        "Sin resultados", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Se encontraron " + tableModel.getRowCount() + " producto(s) con stock bajo", 
                        "Filtro aplicado", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese un número válido", 
                    "Valor inválido", 
                    JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error al filtrar:\n" + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    private void nuevoProducto() {
        ProductoForm form = new ProductoForm(this, null);
        form.setVisible(true);
        
        if (form.isProductoGuardado()) {
            actualizarLista();
        }
    }
    
    private void editarProducto() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un producto para editar", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto productoSeleccionado = tableModel.getProductoAt(selectedRow);
        
        if (productoSeleccionado != null) {
            ProductoForm form = new ProductoForm(this, productoSeleccionado);
            form.setVisible(true);
            
            if (form.isProductoGuardado()) {
                actualizarLista();
            }
        }
    }
    
    private void eliminarProducto() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un producto para eliminar", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto productoSeleccionado = tableModel.getProductoAt(selectedRow);
        
        if (productoSeleccionado != null) {
            String mensaje = String.format(
                "¿Está seguro de que desea eliminar el producto?\n\n" +
                "Nombre: %s\n" +
                "Precio: $%.2f\n" +
                "Stock: %d unidades\n" +
                "Valor en inventario: $%.2f\n\n" +
                "Esta acción no se puede deshacer.",
                productoSeleccionado.getNombre(),
                productoSeleccionado.getPrecio(),
                productoSeleccionado.getStock(),
                productoSeleccionado.getValorInventario()
            );
            
            int respuesta = JOptionPane.showConfirmDialog(this, 
                mensaje, 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    tableModel.eliminarProducto(selectedRow);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Producto eliminado exitosamente", 
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar producto:\n" + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }
    
    private void verDetallesProducto() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un producto para ver sus detalles", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto productoSeleccionado = tableModel.getProductoAt(selectedRow);
        
        if (productoSeleccionado != null) {
            String detalles = String.format(
                "═══════════════════════════════════\n" +
                "  DETALLES DEL PRODUCTO\n" +
                "═══════════════════════════════════\n\n" +
                "ID: %d\n" +
                "Nombre: %s\n" +
                "Precio unitario: $%.2f\n" +
                "Stock disponible: %d unidades\n" +
                "Valor total en inventario: $%.2f\n\n" +
                "═══════════════════════════════════",
                productoSeleccionado.getIdProducto(),
                productoSeleccionado.getNombre(),
                productoSeleccionado.getPrecio(),
                productoSeleccionado.getStock(),
                productoSeleccionado.getValorInventario()
            );
            
            JTextArea textArea = new JTextArea(detalles);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JOptionPane.showMessageDialog(this, 
                textArea, 
                "Detalles del Producto", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void ajustarStock() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un producto para ajustar su stock", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Producto productoSeleccionado = tableModel.getProductoAt(selectedRow);
        
        if (productoSeleccionado != null) {
            String mensaje = String.format(
                "Producto: %s\nStock actual: %d unidades\n\nIngrese el nuevo stock:",
                productoSeleccionado.getNombre(),
                productoSeleccionado.getStock()
            );
            
            String input = JOptionPane.showInputDialog(this, 
                mensaje, 
                "Ajustar Stock", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int nuevoStock = Integer.parseInt(input.trim());
                    
                    if (nuevoStock < 0) {
                        JOptionPane.showMessageDialog(this, 
                            "El stock no puede ser negativo", 
                            "Valor inválido", 
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Actualizar el stock del producto
                    productoSeleccionado.setStock(nuevoStock);
                    tableModel.actualizarProducto(productoSeleccionado);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Stock actualizado exitosamente", 
                        "Actualización exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Por favor ingrese un número válido", 
                        "Valor inválido", 
                        JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al actualizar stock:\n" + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public void actualizarLista() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            System.out.println("Actualizando lista de productos...");
            
            tableModel.cargarDatos();
            
            System.out.println("Lista actualizada: " + tableModel.getRowCount() + " productos");
            
        } catch (Exception e) {
            System.err.println("Error al actualizar lista: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error al actualizar la lista:\n" + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
}