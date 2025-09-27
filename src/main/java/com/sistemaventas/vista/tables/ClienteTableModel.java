package com.sistemaventas.vista.tables;

import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Cliente;

import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {
    
    private String[] columns = {"ID", "Nombre", "Teléfono", "Email"};
    private List<Cliente> clientes = new ArrayList<>();
    private ClienteDAO clienteDAO = new ClienteDAO();
    
    public ClienteTableModel() {
    }
    
    /**
     * Carga todos los clientes desde la base de datos
     */
    public void cargarDatos() {
        try {
            List<Cliente> nuevosClientes = clienteDAO.obtenerTodos();
            
            // Limpiar la lista actual
            clientes.clear();
            
            // Agregar los nuevos datos
            clientes.addAll(nuevosClientes);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
            
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
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
            System.err.println("Error al buscar clientes: " + e.getMessage());
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
            case 2: return cliente.getTelefono() != null ? cliente.getTelefono() : "";
            case 3: return cliente.getEmail() != null ? cliente.getEmail() : "";
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0: return Integer.class;
            case 1:
            case 2:
            case 3: return String.class;
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
                System.out.println("Cliente agregado y tabla actualizada");
                return true;
            } else {
                System.out.println("No se pudo guardar el cliente");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al agregar cliente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo agregar el cliente: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza un cliente existente en la base de datos
     */
    public boolean actualizarCliente(Cliente cliente) {
        try {
            System.out.println(" Actualizando cliente ID: " + cliente.getIdCliente());
            
            boolean actualizado = clienteDAO.actualizar(cliente);
            
            if (actualizado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                System.out.println("Cliente actualizado y tabla refrescada");
                return true;
            } else {
                System.out.println("No se pudo actualizar el cliente");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
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
                System.out.println("Eliminando cliente: " + cliente.getNombre());
                
                boolean eliminado = clienteDAO.eliminar(cliente.getIdCliente());
                
                if (eliminado) {
                    // Recargar todos los datos para asegurar consistencia
                    cargarDatos();
                    System.out.println("Cliente eliminado y tabla actualizada");
                    return true;
                } else {
                    System.out.println("No se pudo eliminar el cliente");
                    return false;
                }
                
            } catch (SQLException e) {
                System.err.println("Error al eliminar cliente: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("No se pudo eliminar el cliente: " + e.getMessage(), e);
            }
        }
        
        return false;
    }
    
    /**
     * Obtiene estadísticas de los datos actuales en la tabla
     */
    public String getEstadisticas() {
        if (clientes.isEmpty()) {
            return "No hay clientes registrados";
        }
        
        int totalClientes = clientes.size();
        long conEmail = clientes.stream()
            .filter(c -> c.getEmail() != null && !c.getEmail().trim().isEmpty())
            .count();
        long conTelefono = clientes.stream()
            .filter(c -> c.getTelefono() != null && !c.getTelefono().trim().isEmpty())
            .count();
        
        return String.format(
            "Total: %d clientes | Con email: %d (%.1f%%) | Con teléfono: %d (%.1f%%)",
            totalClientes,
            conEmail, (conEmail * 100.0 / totalClientes),
            conTelefono, (conTelefono * 100.0 / totalClientes)
        );
    }
}
