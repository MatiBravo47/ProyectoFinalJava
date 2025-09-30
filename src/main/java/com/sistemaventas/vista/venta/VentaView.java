package com.sistemaventas.vista.venta;

import com.sistemaventas.controlador.VentaController;
import com.sistemaventas.modelo.Venta;
import com.sistemaventas.vista.tables.VentaTableModel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Vista principal para gestión de ventas
 * @author Matt_
 */
public class VentaView extends JFrame {
    
    private VentaTableModel tableModel;
    private JTable tabla;
    private JLabel lblTotalVentas;
    private VentaController ventaController;
    
    public VentaView() {
        setTitle("ABM Ventas");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        this.ventaController = new VentaController();
        
        initComponents();
        setupLayout();
        setupEvents();
        
        // Cargar datos iniciales
        actualizarLista();
    }
    
    private void initComponents() {
        // Modelo y tabla
        tableModel = new VentaTableModel();
        tabla = new JTable(tableModel);
        
        // Configurar tabla
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        
        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);  // Fecha
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);  // Cliente
        tabla.getColumnModel().getColumn(3).setPreferredWidth(200);  // Producto
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);   // Cantidad
        tabla.getColumnModel().getColumn(5).setPreferredWidth(120);  // Precio Unit.
        tabla.getColumnModel().getColumn(6).setPreferredWidth(120);  // Total
        
        // Label para total
        lblTotalVentas = new JLabel("Total de ventas: $0.00");
        lblTotalVentas.setFont(lblTotalVentas.getFont().deriveFont(Font.BOLD, 14f));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior - Título y filtros
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("Gestión de Ventas", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnMostrarTodas = new JButton("📋 Todas las Ventas");
        JButton btnFiltrarFechas = new JButton("📅 Por Fechas");
        JButton btnRefrescar = new JButton("🔄 Actualizar");
        
        panelFiltros.add(btnMostrarTodas);
        panelFiltros.add(btnFiltrarFechas);
        panelFiltros.add(btnRefrescar);
        
        // Eventos de filtros
        btnMostrarTodas.addActionListener(e -> mostrarTodas());
        btnFiltrarFechas.addActionListener(e -> filtrarPorFechas());
        btnRefrescar.addActionListener(e -> actualizarLista());
        
        panelSuperior.add(panelFiltros, BorderLayout.SOUTH);
        
        // Panel central - Tabla con información
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Ventas"));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel lblCantidad = new JLabel("Total de registros: 0");
        
        panelInfo.add(lblCantidad);
        panelInfo.add(lblTotalVentas);
        
        panelCentral.add(panelInfo, BorderLayout.SOUTH);
        
        // Panel inferior - Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNueva = new JButton("➕ Nueva Venta");
        JButton btnVerDetalles = new JButton("👁️ Ver Detalles");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        JButton btnReporte = new JButton("📊 Generar Reporte");
        JButton btnVolver = new JButton("← Volver al Menú");
        
        // Colores para los botones
        btnNueva.setBackground(new Color(46, 125, 50));
        btnNueva.setForeground(Color.WHITE);
        
        btnVerDetalles.setBackground(new Color(117, 117, 117));
        btnVerDetalles.setForeground(Color.WHITE);
        
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);
        
        btnReporte.setBackground(new Color(25, 118, 210));
        btnReporte.setForeground(Color.WHITE);
        
        btnVolver.setBackground(new Color(96, 96, 96));
        btnVolver.setForeground(Color.WHITE);
        
        // Tamaños uniformes
        Dimension buttonSize = new Dimension(160, 35);
        btnNueva.setPreferredSize(buttonSize);
        btnVerDetalles.setPreferredSize(buttonSize);
        btnEliminar.setPreferredSize(buttonSize);
        btnReporte.setPreferredSize(buttonSize);
        btnVolver.setPreferredSize(buttonSize);
        
        panelBotones.add(btnNueva);
        panelBotones.add(btnVerDetalles);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnReporte);
        panelBotones.add(btnVolver);
        
        // Eventos de botones
        btnNueva.addActionListener(e -> nuevaVenta());
        btnVerDetalles.addActionListener(e -> verDetalles());
        btnEliminar.addActionListener(e -> eliminarVenta());
        btnReporte.addActionListener(e -> generarReporte());
        btnVolver.addActionListener(e -> dispose());
        
        // Agregar paneles al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        
        // Actualizar labels de información cuando cambie la tabla
        tableModel.addTableModelListener(e -> {
            lblCantidad.setText("Total de registros: " + tableModel.getRowCount());
            BigDecimal totalVentas = tableModel.calcularTotalVentas();
            lblTotalVentas.setText(String.format("Total de ventas: $%.2f", totalVentas));
        });
    }
    
    private void setupEvents() {
        // Doble clic en la tabla para ver detalles
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    verDetalles();
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
    
    private void mostrarTodas() {
        actualizarLista();
    }
    
    private void filtrarPorFechas() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JTextField txtFechaInicio = new JTextField();
        JTextField txtFechaFin = new JTextField();
        
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtFechaInicio.setText(inicioMes.format(formatter));
        txtFechaFin.setText(hoy.format(formatter));
        
        panel.add(new JLabel("Fecha inicio (dd/MM/yyyy):"));
        panel.add(txtFechaInicio);
        panel.add(new JLabel("Fecha fin (dd/MM/yyyy):"));
        panel.add(txtFechaFin);
        panel.add(new JLabel(""));
        panel.add(new JLabel("(Ejemplo: 01/01/2025)"));
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Filtrar por Rango de Fechas", 
            JOptionPane.OK_CANCEL_OPTION, 
            JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDate fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim(), formatter);
                LocalDate fechaFin = LocalDate.parse(txtFechaFin.getText().trim(), formatter);
                
                if (fechaInicio.isAfter(fechaFin)) {
                    JOptionPane.showMessageDialog(this,
                        "La fecha de inicio no puede ser posterior a la fecha de fin",
                        "Fechas inválidas",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                tableModel.buscarPorFechas(fechaInicio, fechaFin);
                
                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this,
                        "No se encontraron ventas en el rango de fechas especificado",
                        "Sin resultados",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Se encontraron " + tableModel.getRowCount() + " venta(s)",
                        "Filtro aplicado",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error en el formato de fecha. Use: dd/MM/yyyy",
                    "Formato inválido",
                    JOptionPane.ERROR_MESSAGE);
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    private void nuevaVenta() {
        VentaForm form = new VentaForm(this);
        form.setVisible(true);
        
        if (form.isVentaGuardada()) {
            actualizarLista();
        }
    }
    
    private void verDetalles() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione una venta para ver sus detalles",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Venta ventaSeleccionada = tableModel.getVentaAt(selectedRow);
        
        if (ventaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            String detalles = String.format(
                "═══════════════════════════════════\n" +
                "  DETALLES DE LA VENTA\n" +
                "═══════════════════════════════════\n\n" +
                "ID Venta: %d\n" +
                "Fecha: %s\n" +
                "Cliente: %s\n" +
                "Producto: %s\n" +
                "Cantidad: %d unidades\n" +
                "Precio Unitario: $%.2f\n" +
                "TOTAL: $%.2f\n\n" +
                "═══════════════════════════════════",
                ventaSeleccionada.getIdVenta(),
                ventaSeleccionada.getFecha().format(formatter),
                ventaSeleccionada.getNombreCliente() != null ? 
                    ventaSeleccionada.getNombreCliente() : "ID: " + ventaSeleccionada.getIdCliente(),
                ventaSeleccionada.getNombreProducto() != null ? 
                    ventaSeleccionada.getNombreProducto() : "ID: " + ventaSeleccionada.getIdProducto(),
                ventaSeleccionada.getCantidad(),
                ventaSeleccionada.getPrecioUnitario(),
                ventaSeleccionada.getTotal()
            );
            
            JTextArea textArea = new JTextArea(detalles);
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JOptionPane.showMessageDialog(this,
                textArea,
                "Detalles de la Venta",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void eliminarVenta() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione una venta para eliminar",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Venta ventaSeleccionada = tableModel.getVentaAt(selectedRow);
        
        if (ventaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            String mensaje = String.format(
                "¿Está seguro de que desea eliminar esta venta?\n\n" +
                "Fecha: %s\n" +
                "Cliente: %s\n" +
                "Producto: %s\n" +
                "Cantidad: %d unidades\n" +
                "Total: $%.2f\n\n" +
                "ATENCIÓN: El stock del producto será restaurado.\n" +
                "Esta acción no se puede deshacer.",
                ventaSeleccionada.getFecha().format(formatter),
                ventaSeleccionada.getNombreCliente() != null ? 
                    ventaSeleccionada.getNombreCliente() : "ID: " + ventaSeleccionada.getIdCliente(),
                ventaSeleccionada.getNombreProducto() != null ? 
                    ventaSeleccionada.getNombreProducto() : "ID: " + ventaSeleccionada.getIdProducto(),
                ventaSeleccionada.getCantidad(),
                ventaSeleccionada.getTotal()
            );
            
            int respuesta = JOptionPane.showConfirmDialog(this,
                mensaje,
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    
                    if (ventaController.eliminarVenta(ventaSeleccionada.getIdVenta())) {
                        actualizarLista();
                        
                        JOptionPane.showMessageDialog(this,
                            "Venta eliminada exitosamente.\nEl stock ha sido restaurado.",
                            "Eliminación exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "No se pudo eliminar la venta",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Error al eliminar venta:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }
    
    private void generarReporte() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay ventas para generar el reporte",
                "Sin datos",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder reporte = new StringBuilder();
        reporte.append("═══════════════════════════════════════════════════════\n");
        reporte.append("                  REPORTE DE VENTAS\n");
        reporte.append("═══════════════════════════════════════════════════════\n\n");
        reporte.append(String.format("Fecha de generación: %s\n", 
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        reporte.append(String.format("Total de registros: %d\n\n", tableModel.getRowCount()));
        
        BigDecimal totalGeneral = BigDecimal.ZERO;
        int cantidadTotal = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Venta venta = tableModel.getVentaAt(i);
            if (venta != null) {
                totalGeneral = totalGeneral.add(venta.getTotal());
                cantidadTotal += venta.getCantidad();
                
                reporte.append(String.format("Venta #%d\n", venta.getIdVenta()));
                reporte.append(String.format("  Fecha: %s\n", 
                    venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                reporte.append(String.format("  Cliente: %s\n", 
                    venta.getNombreCliente() != null ? venta.getNombreCliente() : "ID: " + venta.getIdCliente()));
                reporte.append(String.format("  Producto: %s\n", 
                    venta.getNombreProducto() != null ? venta.getNombreProducto() : "ID: " + venta.getIdProducto()));
                reporte.append(String.format("  Cantidad: %d unidades\n", venta.getCantidad()));
                reporte.append(String.format("  Total: $%.2f\n\n", venta.getTotal()));
            }
        }
        
        reporte.append("═══════════════════════════════════════════════════════\n");
        reporte.append(String.format("Total de unidades vendidas: %d\n", cantidadTotal));
        reporte.append(String.format("TOTAL GENERAL: $%.2f\n", totalGeneral));
        reporte.append("═══════════════════════════════════════════════════════\n");
        
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 305));
        
        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Reporte de Ventas",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actualizarLista() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            System.out.println("Actualizando lista de ventas...");
            
            tableModel.cargarDatos();
            
            System.out.println("Lista actualizada: " + tableModel.getRowCount() + " ventas");
            
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