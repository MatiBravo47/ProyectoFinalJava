package com.sistemaventas.vista.cliente;

import com.sistemaventas.modelo.Cliente;
import com.sistemaventas.vista.tables.ClienteTableModel;
//import com.sistemaventas.vista.MainView;

import javax.swing.*;
import java.awt.*;

public class ClienteView extends JFrame {
    
    private ClienteTableModel tableModel;
    private JTable tabla;
    private JTextField txtBuscar;
    
    public ClienteView() {
        setTitle("ABM Clientes");
        setSize(900, 600);
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
        tableModel = new ClienteTableModel();
        tabla = new JTable(tableModel);
        
        // Configurar tabla
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(25);
        
        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(25);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120); // Teléfono
        tabla.getColumnModel().getColumn(3).setPreferredWidth(200); // Email
        
        // Campo de búsqueda
        txtBuscar = new JTextField(25);
        txtBuscar.setToolTipText("Escriba el nombre del cliente a buscar...");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel superior - Búsqueda y título
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Título
        JLabel lblTitulo = new JLabel("Gestión de Clientes", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.BOLD, 16f));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.add(lblTitulo, BorderLayout.NORTH);
        
        // Búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar cliente:"));
        panelBusqueda.add(txtBuscar);
        
        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        JButton btnRefrescar = new JButton("Actualizar");
        
        panelBusqueda.add(btnBuscar);
        panelBusqueda.add(btnMostrarTodos);
        panelBusqueda.add(btnRefrescar);
        
        // Eventos de búsqueda
        btnBuscar.addActionListener(e -> buscarClientes());
        btnMostrarTodos.addActionListener(e -> mostrarTodos());
        btnRefrescar.addActionListener(e -> actualizarLista());
        txtBuscar.addActionListener(e -> buscarClientes()); // Enter en el campo
        
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        
        // Panel central - Tabla con información
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de información
        JLabel lblInfo = new JLabel("Total de clientes: 0");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelCentral.add(lblInfo, BorderLayout.SOUTH);
        
        // Panel inferior - Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        JButton btnNuevo = new JButton("Nuevo Cliente");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVer = new JButton("Ver Detalles");
        
        // Colores para los botones
        btnNuevo.setBackground(new Color(46, 125, 50));
        btnNuevo.setForeground(Color.WHITE);
        
        btnEditar.setBackground(new Color(25, 118, 210));
        btnEditar.setForeground(Color.WHITE);
        
        btnEliminar.setBackground(new Color(211, 47, 47));
        btnEliminar.setForeground(Color.WHITE);
        
        btnVer.setBackground(new Color(117, 117, 117));
        btnVer.setForeground(Color.WHITE);
        
        // Tamaños uniformes
        Dimension buttonSize = new Dimension(140, 35);
        btnNuevo.setPreferredSize(buttonSize);
        btnEditar.setPreferredSize(buttonSize);
        btnEliminar.setPreferredSize(buttonSize);
        btnVer.setPreferredSize(buttonSize);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVer);
        
        // Eventos de botones
        btnNuevo.addActionListener(e -> nuevoCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnVer.addActionListener(e -> verDetallesCliente());
        
        // Agregar paneles al frame
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        
        // Actualizar el label de información cuando cambie la tabla
        tableModel.addTableModelListener(e -> {
            lblInfo.setText("Total de clientes: " + tableModel.getRowCount());
        });
    }
    
    private void setupEvents() {
        // Doble clic en la tabla para editar
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    editarCliente();
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
    
    private void buscarClientes() {
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
                    "No se encontraron clientes con el término: \"" + termino + "\"", 
                    "Sin resultados", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Se encontraron " + tableModel.getRowCount() + " cliente(s)", 
                    "Búsqueda completada", 
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
    
    private void nuevoCliente() {
        ClienteForm form = new ClienteForm(this, null);
        form.setVisible(true);
        
        // Actualizar la lista automáticamente después de cerrar el formulario
        if (form.isClienteGuardado()) {
            actualizarLista();
            JOptionPane.showMessageDialog(this, 
                "Cliente agregado exitosamente", 
                "Operación exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editarCliente() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un cliente para editar", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cliente clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        if (clienteSeleccionado != null) {
            ClienteForm form = new ClienteForm(this, clienteSeleccionado);
            form.setVisible(true);
            
            // Actualizar la lista automáticamente después de cerrar el formulario
            if (form.isClienteGuardado()) {
                actualizarLista();
                JOptionPane.showMessageDialog(this, 
                    "Cliente actualizado exitosamente", 
                    "Operación exitosa", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void eliminarCliente() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un cliente para eliminar", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cliente clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        if (clienteSeleccionado != null) {
            // Mostrar diálogo de confirmación más detallado
            String mensaje = String.format(
                "¿Está seguro de que desea eliminar al cliente?\n\n" +
                "Nombre: %s\n" +
                "Teléfono: %s\n" +
                "Email: %s\n\n" +
                "Esta acción no se puede deshacer.",
                clienteSeleccionado.getNombre(),
                clienteSeleccionado.getTelefono() != null ? clienteSeleccionado.getTelefono() : "No especificado",
                clienteSeleccionado.getEmail() != null ? clienteSeleccionado.getEmail() : "No especificado"
            );
            
            int respuesta = JOptionPane.showConfirmDialog(this, 
                mensaje, 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
            
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    tableModel.eliminarCliente(selectedRow);
                    
                    JOptionPane.showMessageDialog(this, 
                        "Cliente eliminado exitosamente", 
                        "Eliminación exitosa", 
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar cliente:\n" + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }
    
    private void verDetallesCliente() {
        int selectedRow = tabla.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un cliente para ver sus detalles", 
                "Selección requerida", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Cliente clienteSeleccionado = tableModel.getClienteAt(selectedRow);
        
        if (clienteSeleccionado != null) {
            String detalles = String.format(
                "ID: %d\n" +
                "Nombre: %s\n" +
                "Teléfono: %s\n" +
                "Email: %s",
                clienteSeleccionado.getIdCliente(),
                clienteSeleccionado.getNombre(),
                clienteSeleccionado.getTelefono() != null ? clienteSeleccionado.getTelefono() : "No especificado",
                clienteSeleccionado.getEmail() != null ? clienteSeleccionado.getEmail() : "No especificado"
            );
            
            JOptionPane.showMessageDialog(this, 
                detalles, 
                "Detalles del Cliente", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void actualizarLista() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            tableModel.cargarDatos();
            
            System.out.println("Lista actualizada: " + tableModel.getRowCount() + " clientes");
            
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