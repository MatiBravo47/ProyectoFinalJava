package com.sistemaventas.dao;

import com.sistemaventas.modelo.Cliente;
import com.sistemaventas.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la gestión de operaciones CRUD de Cliente.
 * <p>
 * Esta clase proporciona métodos para realizar operaciones de base de datos
 * relacionadas con la entidad Cliente, incluyendo crear, leer, actualizar
 * y eliminar registros. Implementa el patrón DAO para separar la lógica
 * de acceso a datos de la lógica de negocio.
 * </p>
 * 
 * <p><strong>Operaciones soportadas:</strong></p>
 * <ul>
 *   <li>Crear nuevos clientes</li>
 *   <li>Buscar clientes por ID, nombre, email o DNI</li>
 *   <li>Obtener todos los clientes</li>
 *   <li>Actualizar información de clientes existentes</li>
 *   <li>Eliminar clientes</li>
 * </ul>
 * 
 * <p><strong>Características técnicas:</strong></p>
 * <ul>
 *   <li>Utiliza PreparedStatement para prevenir inyección SQL</li>
 *   <li>Manejo de transacciones con commit/rollback</li>
 *   <li>Mapeo automático de ResultSet a objetos Cliente</li>
 *   <li>Gestión automática de recursos con try-with-resources</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see com.sistemaventas.modelo.Cliente
 * @see com.sistemaventas.util.ConexionDB
 */
public class ClienteDAO {
    
    /**
     * Guarda un nuevo cliente en la base de datos.
     * <p>
     * Este método inserta un nuevo registro de cliente en la tabla 'clientes'
     * y asigna automáticamente el ID generado al objeto cliente. Utiliza
     * transacciones para asegurar la integridad de los datos.
     * </p>
     * 
     * <p><strong>Proceso:</strong></p>
     * <ol>
     *   <li>Deshabilita auto-commit para manejo manual de transacciones</li>
     *   <li>Inserta el cliente con los datos proporcionados</li>
     *   <li>Obtiene el ID generado automáticamente</li>
     *   <li>Asigna el ID al objeto cliente</li>
     *   <li>Confirma la transacción si todo es exitoso</li>
     * </ol>
     * 
     * @param cliente el cliente a guardar (no puede ser null)
     * @return true si el cliente se guardó exitosamente, false en caso contrario
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el cliente es null
     */
    public boolean guardar(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        
        String sql = "INSERT INTO clientes (nombre, dni, telefono, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Deshabilitar auto-commit para manejar la transacción manualmente
            conn.setAutoCommit(false);
            
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getDni());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getEmail());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                String sqlId = "SELECT last_insert_rowid() as id";
                try (PreparedStatement pstmtId = conn.prepareStatement(sqlId);
                     ResultSet rs = pstmtId.executeQuery()) {
                    
                    if (rs.next()) {
                        cliente.setIdCliente(rs.getInt("id"));
                        
                        // Confirmar la transacción
                        conn.commit();
                        
                        return true;
                    }
                }
            }
            
            // Si llegamos aquí, algo salió mal
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca un cliente por su identificador único.
     * <p>
     * Este método realiza una consulta a la base de datos para encontrar
     * un cliente específico utilizando su ID. Si no se encuentra el cliente,
     * retorna null.
     * </p>
     * 
     * @param id el identificador único del cliente a buscar
     * @return el cliente encontrado, o null si no existe
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es menor o igual a cero
     */
    public Cliente buscarPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Obtiene todos los clientes registrados en el sistema.
     * <p>
     * Este método recupera todos los clientes de la base de datos
     * y los retorna ordenados alfabéticamente por nombre. Si no hay
     * clientes registrados, retorna una lista vacía.
     * </p>
     * 
     * @return una lista con todos los clientes, ordenados por nombre
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Cliente> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM clientes ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
            
            return clientes;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Actualiza la información de un cliente existente.
     * <p>
     * Este método modifica los datos de un cliente en la base de datos
     * utilizando su ID. Actualiza todos los campos del cliente excepto
     * el ID, que se mantiene como identificador único.
     * </p>
     * 
     * @param cliente el cliente con los datos actualizados (no puede ser null)
     * @return true si la actualización fue exitosa, false si no se encontró el cliente
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el cliente es null o no tiene ID válido
     */
    public boolean actualizar(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null");
        }
        if (cliente.getIdCliente() <= 0) {
            throw new IllegalArgumentException("El cliente debe tener un ID válido para actualizar");
        }
        
        String sql = "UPDATE clientes SET nombre = ?, dni = ?, telefono = ?, email = ? WHERE id_cliente = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getDni());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setInt(5, cliente.getIdCliente());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
            
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Elimina un cliente de la base de datos.
     * <p>
     * Este método elimina permanentemente un cliente de la base de datos
     * utilizando su ID. Primero verifica que el cliente exista antes
     * de proceder con la eliminación.
     * </p>
     * 
     * @param id el identificador único del cliente a eliminar
     * @return true si el cliente fue eliminado exitosamente, false si no se encontró
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el ID es menor o igual a cero
     */
    public boolean eliminar(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        
        // Primero verificar si el cliente existe
        Cliente cliente = buscarPorId(id);
        if (cliente == null) {
            return false;
        }
        
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setInt(1, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
            
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca clientes por nombre utilizando coincidencia parcial.
     * <p>
     * Este método realiza una búsqueda de clientes cuyo nombre contenga
     * el texto especificado. La búsqueda es case-insensitive y utiliza
     * el operador LIKE con comodines para encontrar coincidencias parciales.
     * </p>
     * 
     * @param nombre el nombre o parte del nombre a buscar (no puede ser null ni vacío)
     * @return una lista de clientes que coinciden con el criterio de búsqueda
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el nombre es null o vacío
     */
    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser null o vacío");
        }
        
        String sql = "SELECT * FROM clientes WHERE nombre LIKE ? ORDER BY nombre";
        List<Cliente> clientes = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
            
            return clientes;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca un cliente por su dirección de email.
     * <p>
     * Este método realiza una búsqueda exacta de un cliente utilizando
     * su dirección de email. Como el email es único en el sistema,
     * retorna un solo cliente o null si no se encuentra.
     * </p>
     * 
     * @param email la dirección de email a buscar (no puede ser null ni vacío)
     * @return el cliente encontrado, o null si no existe
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el email es null o vacío
     */
    public Cliente buscarPorEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser null o vacío");
        }
        
        String sql = "SELECT * FROM clientes WHERE email = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca un cliente por su número de DNI.
     * <p>
     * Este método realiza una búsqueda exacta de un cliente utilizando
     * su número de DNI. Como el DNI es único en el sistema,
     * retorna un solo cliente o null si no se encuentra.
     * </p>
     * 
     * @param dni el número de DNI a buscar (no puede ser null ni vacío)
     * @return el cliente encontrado, o null si no existe
     * @throws SQLException si ocurre un error de base de datos
     * @throws IllegalArgumentException si el DNI es null o vacío
     */
    public Cliente buscarPorDni(String dni) throws SQLException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser null o vacío");
        }
        
        String sql = "SELECT * FROM clientes WHERE dni = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, dni);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Cliente.
     * <p>
     * Este método privado convierte los datos obtenidos de la base de datos
     * en un objeto Cliente. Se utiliza internamente por todos los métodos
     * de consulta para mantener consistencia en el mapeo.
     * </p>
     * 
     * @param rs el ResultSet con los datos del cliente
     * @return un objeto Cliente con los datos mapeados
     * @throws SQLException si ocurre un error al acceder a los datos del ResultSet
     */
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("nombre"),
            rs.getString("dni"),
            rs.getString("telefono"),
            rs.getString("email")
        );
    }
}