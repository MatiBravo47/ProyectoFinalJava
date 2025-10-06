package com.sistemaventas.vista.tables;

import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Cliente;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {
    
    private String[] columns = {"ID", "Nombre", "DNI", "Teléfono", "Email"};
    private List<Cliente> clientes = new ArrayList<>();
    private ClienteDAO clienteDAO = new ClienteDAO();
    
    public ClienteTableModel() {
        // No cargar datos automáticamente en el constructor
    }
    
    /**
     * Carga todos los clientes desde la base de datos
     */
    public void cargarDatos() {
        try {
            System.out.println("Cargando clientes desde la base de datos...");
            List<Cliente> nuevosClientes = clienteDAO.obtenerTodos();
            
            // Limpiar la lista actual
            clientes.clear();
            
            // Agregar los nuevos datos
            clientes.addAll(nuevosClientes);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // En caso de error, mantener una lista vacía
            clientes.clear();
            fireTableDataChanged();
            
            // Re-lanzar como RuntimeException para que la vista pueda manejarlo
            throw new RuntimeException("Error al cargar datos de clientes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca clientes por nombre
     */
    public void buscarPorNombre(String nombre) {
        try {
            List<Cliente> clientesEncontrados;
            
            if (nombre == null || nombre.trim().isEmpty()) {
                clientesEncontrados = clienteDAO.obtenerTodos();
            } else {
                clientesEncontrados = clienteDAO.buscarPorNombre(nombre.trim());
            }
            
            // Limpiar la lista actual
            clientes.clear();
            
            // Agregar los resultados de la búsqueda
            clientes.addAll(clientesEncontrados);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getRowCount() { 
        return clientes.size(); 
    }

    @Override
    public int getColumnCount() { 
        return columns.length; 
    }

    @Override
    public String getColumnName(int col) { 
        return columns[col]; 
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row < 0 || row >= clientes.size()) {
            return null;
        }
        
        Cliente cliente = clientes.get(row);
        
        switch (col) {
            case 0: return cliente.getIdCliente();
            case 1: return cliente.getNombre();
            case 2: return cliente.getDni() != null ? cliente.getDni() : "";
            case 3: return cliente.getTelefono() != null ? cliente.getTelefono() : "";
            case 4: return cliente.getEmail() != null ? cliente.getEmail() : "";
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0: return Integer.class;
            case 1:
            case 2:
            case 3:
            case 4: return String.class;
            default: return Object.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false; // Las celdas no son editables directamente
    }
    
    /**
     * Obtiene el cliente en la fila especificada
     */
    public Cliente getClienteAt(int row) {
        if (row >= 0 && row < clientes.size()) {
            return clientes.get(row);
        }
        return null;
    }
    
    /**
     * Agrega un nuevo cliente a la base de datos y actualiza la tabla
     */
    public boolean agregarCliente(Cliente cliente) {
        try {
            System.out.println("Agregando nuevo cliente: " + cliente.getNombre());
            
            boolean guardado = clienteDAO.guardar(cliente);
            
            if (guardado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo agregar el cliente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza un cliente existente en la base de datos
     */
    public boolean actualizarCliente(Cliente cliente) {
        try {
            boolean actualizado = clienteDAO.actualizar(cliente);
            
            if (actualizado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo actualizar el cliente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina un cliente de la base de datos
     */
    public boolean eliminarCliente(int row) {
        if (row >= 0 && row < clientes.size()) {
            Cliente cliente = clientes.get(row);
            
            try {
                boolean eliminado = clienteDAO.eliminar(cliente.getIdCliente());
                
                if (eliminado) {
                    // Recargar todos los datos para asegurar consistencia
                    cargarDatos();
                    return true;
                } else {
                    return false;
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("No se pudo eliminar el cliente: " + e.getMessage(), e);
            }
        }
        
        return false;
    }
}
