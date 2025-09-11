package com.sistemaventas.dao;

import com.sistemaventas.modelo.Producto;
import com.sistemaventas.util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    
    public boolean guardar(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (nombre, precio, stock) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, producto.getNombre());
            pstmt.setBigDecimal(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado automáticamente
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setIdProducto(generatedKeys.getInt(1));
                        System.out.println("✓ Producto guardado con ID: " + producto.getIdProducto());
                        return true;
                    }
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            throw e;
        }
    }
    

    public Producto buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
            throw e;
        }
    }
    

    public List<Producto> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM productos ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            
            System.out.println("✓ Productos obtenidos: " + productos.size());
            return productos;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            throw e;
        }
    }
    

    public boolean actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET nombre = ?, precio = ?, stock = ?, fecha_modificacion = CURRENT_TIMESTAMP WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getNombre());
            pstmt.setBigDecimal(2, producto.getPrecio());
            pstmt.setInt(3, producto.getStock());
            pstmt.setInt(4, producto.getIdProducto());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto actualizado: " + producto.getNombre());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            throw e;
        }
    }
    

    public boolean eliminar(int id) throws SQLException {
        // Primero verificar si el producto existe
        Producto producto = buscarPorId(id);
        if (producto == null) {
            System.out.println("⚠ Producto con ID " + id + " no encontrado");
            return false;
        }
        
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Producto eliminado: " + producto.getNombre());
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            throw e;
        }
    }
    

    public List<Producto> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            
            System.out.println("✓ Productos encontrados: " + productos.size());
            return productos;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar productos por nombre: " + e.getMessage());
            throw e;
        }
    }
    

    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ?, fecha_modificacion = CURRENT_TIMESTAMP WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, id);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✓ Stock actualizado para producto ID " + id + ": " + nuevoStock);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            throw e;
        }
    }
    
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getString("nombre"),
            rs.getBigDecimal("precio"),
            rs.getInt("stock")
        );
    }
}