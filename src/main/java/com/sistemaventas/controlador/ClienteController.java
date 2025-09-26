package com.sistemaventas.controlador;

import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Cliente;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para manejar la lógica de negocio de Cliente
 * Actúa como intermediario entre la Vista y el DAO
 * @author Matt_
 */
public class ClienteController {
    
    private ClienteDAO clienteDAO;
    
    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }
    
    /**
     * Guarda un nuevo cliente
     * @param cliente Cliente a guardar
     * @return true si se guardó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public boolean guardarCliente(Cliente cliente) throws SQLException {
        // Validaciones de negocio
        validarCliente(cliente);
        
        // Verificar email único si se proporcionó
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            Cliente existente = clienteDAO.buscarPorEmail(cliente.getEmail());
            if (existente != null) {
                throw new IllegalArgumentException("Ya existe un cliente con ese email");
            }
        }
        
        return clienteDAO.guardar(cliente);
    }
    
    /**
     * Actualiza un cliente existente
     * @param cliente Cliente a actualizar
     * @return true si se actualizó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public boolean actualizarCliente(Cliente cliente) throws SQLException {
        // Validaciones de negocio
        validarCliente(cliente);
        
        // Verificar que el cliente existe
        Cliente existente = clienteDAO.buscarPorId(cliente.getIdCliente());
        if (existente == null) {
            throw new IllegalArgumentException("El cliente no existe en la base de datos");
        }
        
        // Verificar email único si se proporcionó y cambió
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            Cliente clienteConEmail = clienteDAO.buscarPorEmail(cliente.getEmail());
            if (clienteConEmail != null && clienteConEmail.getIdCliente() != cliente.getIdCliente()) {
                throw new IllegalArgumentException("Ya existe otro cliente con ese email");
            }
        }
        
        return clienteDAO.actualizar(cliente);
    }
    
    /**
     * Elimina un cliente por ID
     * @param id ID del cliente a eliminar
     * @return true si se eliminó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si el cliente tiene ventas asociadas
     */
    public boolean eliminarCliente(int id) throws SQLException {
        // Verificar que el cliente existe
        Cliente cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }
        
        // TODO: Verificar si el cliente tiene ventas asociadas
        // Esta verificación se implementará cuando tengamos VentaDAO
        // if (tieneVentasAsociadas(id)) {
        //     throw new IllegalArgumentException("No se puede eliminar el cliente porque tiene ventas asociadas");
        // }
        
        return clienteDAO.eliminar(id);
    }
    
    /**
     * Busca un cliente por ID
     * @param id ID del cliente
     * @return Cliente encontrado o null si no existe
     * @throws SQLException si hay error en la base de datos
     */
    public Cliente buscarClientePorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        
        return clienteDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todos los clientes
     * @return Lista de todos los clientes
     * @throws SQLException si hay error en la base de datos
     */
    public List<Cliente> obtenerTodosLosClientes() throws SQLException {
        return clienteDAO.obtenerTodos();
    }
    
    /**
     * Busca clientes por nombre
     * @param nombre Nombre a buscar (puede ser parcial)
     * @return Lista de clientes que coinciden
     * @throws SQLException si hay error en la base de datos
     */
    public List<Cliente> buscarClientesPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodosLosClientes();
        }
        
        return clienteDAO.buscarPorNombre(nombre.trim());
    }
    
    /**
     * Busca un cliente por email
     * @param email Email del cliente
     * @return Cliente encontrado o null si no existe
     * @throws SQLException si hay error en la base de datos
     */
    public Cliente buscarClientePorEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        
        return clienteDAO.buscarPorEmail(email.trim());
    }
    
    /**
     * Valida que un cliente tenga los datos mínimos requeridos
     * @param cliente Cliente a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        
        // Validar nombre obligatorio
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        
        // Validar longitud del nombre
        if (cliente.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres");
        }
        
        // Validar teléfono si se proporcionó
        if (cliente.getTelefono() != null && cliente.getTelefono().trim().length() > 20) {
            throw new IllegalArgumentException("El teléfono no puede exceder los 20 caracteres");
        }
        
        // Validar email si se proporcionó
        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            String email = cliente.getEmail().trim();
            if (email.length() > 100) {
                throw new IllegalArgumentException("El email no puede exceder los 100 caracteres");
            }
            if (!esEmailValido(email)) {
                throw new IllegalArgumentException("El formato del email no es válido");
            }
        }
    }
    
    /**
     * Valida formato básico de email
     * @param email Email a validar
     * @return true si el formato es válido
     */
    private boolean esEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Validación básica: debe contener @ y al menos un punto después del @
        int arrobaPos = email.indexOf("@");
        if (arrobaPos <= 0 || arrobaPos == email.length() - 1) {
            return false;
        }
        
        String parteLocal = email.substring(0, arrobaPos);
        String parteDominio = email.substring(arrobaPos + 1);
        
        // La parte local no puede estar vacía
        if (parteLocal.trim().isEmpty()) {
            return false;
        }
        
        // La parte del dominio debe tener al menos un punto
        if (!parteDominio.contains(".") || parteDominio.startsWith(".") || parteDominio.endsWith(".")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Obtiene estadísticas básicas de clientes
     * @return String con información estadística
     */
    public String obtenerEstadisticas() {
        try {
            List<Cliente> clientes = clienteDAO.obtenerTodos();
            int total = clientes.size();
            
            long conEmail = clientes.stream()
                .filter(c -> c.getEmail() != null && !c.getEmail().trim().isEmpty())
                .count();
                
            long conTelefono = clientes.stream()
                .filter(c -> c.getTelefono() != null && !c.getTelefono().trim().isEmpty())
                .count();
            
            return String.format(
                "Total de clientes: %d\n" +
                "Con email: %d (%.1f%%)\n" +
                "Con teléfono: %d (%.1f%%)",
                total,
                conEmail, total > 0 ? (conEmail * 100.0 / total) : 0,
                conTelefono, total > 0 ? (conTelefono * 100.0 / total) : 0
            );
            
        } catch (SQLException e) {
            return "Error al obtener estadísticas: " + e.getMessage();
        }
    }
}