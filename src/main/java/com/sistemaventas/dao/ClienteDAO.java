package com.sistemaventas.dao;

import com.sistemaventas.modelo.Cliente;
import com.sistemaventas.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para manejo de operaciones CRUD de Cliente
 * @author Matt_
 */
public class ClienteDAO {
    
    public boolean guardar(Cliente cliente) throws SQLException {
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
    
    public Cliente buscarPorId(int id) throws SQLException {
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
    
    public boolean actualizar(Cliente cliente) throws SQLException {
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
    
    public boolean eliminar(int id) throws SQLException {
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
    
    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
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
    
    public Cliente buscarPorEmail(String email) throws SQLException {
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
    
    public Cliente buscarPorDni(String dni) throws SQLException {
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