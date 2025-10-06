package com.sistemaventas.dao;

import com.sistemaventas.modelo.Venta;
import com.sistemaventas.util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la gestión de operaciones CRUD de Venta.
 * <p>
 * Esta clase proporciona métodos para realizar operaciones de base de datos
 * relacionadas con la entidad Venta, incluyendo crear, leer, actualizar
 * y eliminar registros. Implementa el patrón DAO para separar la lógica
 * de acceso a datos de la lógica de negocio.
 * </p>
 * 
 * <p><strong>Operaciones soportadas:</strong></p>
 * <ul>
 *   <li>Crear nuevas ventas</li>
 *   <li>Buscar ventas por ID, cliente, producto o fecha</li>
 *   <li>Obtener todas las ventas</li>
 *   <li>Actualizar información de ventas existentes</li>
 *   <li>Eliminar ventas</li>
 *   <li>Obtener ventas con información de cliente y producto</li>
 * </ul>
 * 
 * <p><strong>Características técnicas:</strong></p>
 * <ul>
 *   <li>Utiliza PreparedStatement para prevenir inyección SQL</li>
 *   <li>Manejo de transacciones con commit/rollback</li>
 *   <li>Mapeo automático de ResultSet a objetos Venta</li>
 *   <li>Gestión automática de recursos con try-with-resources</li>
 *   <li>Manejo de fechas con LocalDate</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see com.sistemaventas.modelo.Venta
 * @see com.sistemaventas.util.ConexionDB
 */
public class VentaDAO {
    
    public boolean guardar(Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (fecha, id_cliente, id_producto, cantidad, precio_unitario, total) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setDate(1, Date.valueOf(venta.getFecha()));
            pstmt.setInt(2, venta.getIdCliente());
            pstmt.setInt(3, venta.getIdProducto());
            pstmt.setInt(4, venta.getCantidad());
            pstmt.setBigDecimal(5, venta.getPrecioUnitario());
            pstmt.setBigDecimal(6, venta.getTotal());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                String sqlId = "SELECT last_insert_rowid() as id";
                try (PreparedStatement pstmtId = conn.prepareStatement(sqlId);
                     ResultSet rs = pstmtId.executeQuery()) {
                    
                    if (rs.next()) {
                        venta.setIdVenta(rs.getInt("id"));
                        conn.commit();
                        return true;
                    }
                }
            }
            
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    public Venta buscarPorId(int id) throws SQLException {
        String sql = "SELECT v.*, c.nombre as nombre_cliente, p.nombre as nombre_producto " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "LEFT JOIN productos p ON v.id_producto = p.id_producto " +
                     "WHERE v.id_venta = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    public List<Venta> obtenerTodas() throws SQLException {
        String sql = "SELECT v.*, c.nombre as nombre_cliente, p.nombre as nombre_producto " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "LEFT JOIN productos p ON v.id_producto = p.id_producto " +
                     "ORDER BY v.fecha DESC, v.id_venta DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
            
            return ventas;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    public boolean actualizar(Venta venta) throws SQLException {
        String sql = "UPDATE ventas SET fecha = ?, id_cliente = ?, id_producto = ?, " +
                     "cantidad = ?, precio_unitario = ?, total = ? WHERE id_venta = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            
            pstmt.setDate(1, Date.valueOf(venta.getFecha()));
            pstmt.setInt(2, venta.getIdCliente());
            pstmt.setInt(3, venta.getIdProducto());
            pstmt.setInt(4, venta.getCantidad());
            pstmt.setBigDecimal(5, venta.getPrecioUnitario());
            pstmt.setBigDecimal(6, venta.getTotal());
            pstmt.setInt(7, venta.getIdVenta());
            
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
        // Primero verificar si la venta existe
        Venta venta = buscarPorId(id);
        if (venta == null) {
            return false;
        }
        
        String sql = "DELETE FROM ventas WHERE id_venta = ?";
        
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
     * Busca ventas por cliente
     */
    public List<Venta> buscarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT v.*, c.nombre as nombre_cliente, p.nombre as nombre_producto " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "LEFT JOIN productos p ON v.id_producto = p.id_producto " +
                     "WHERE v.id_cliente = ? " +
                     "ORDER BY v.fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca ventas por producto
     */
    public List<Venta> buscarPorProducto(int idProducto) throws SQLException {
        String sql = "SELECT v.*, c.nombre as nombre_cliente, p.nombre as nombre_producto " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "LEFT JOIN productos p ON v.id_producto = p.id_producto " +
                     "WHERE v.id_producto = ? " +
                     "ORDER BY v.fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProducto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> buscarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = "SELECT v.*, c.nombre as nombre_cliente, p.nombre as nombre_producto " +
                     "FROM ventas v " +
                     "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                     "LEFT JOIN productos p ON v.id_producto = p.id_producto " +
                     "WHERE v.fecha BETWEEN ? AND ? " +
                     "ORDER BY v.fecha DESC";
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Verifica si un cliente tiene ventas asociadas
     */
    public boolean clienteTieneVentas(int idCliente) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ventas WHERE id_cliente = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Verifica si un producto tiene ventas asociadas
     */
    public boolean productoTieneVentas(int idProducto) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM ventas WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProducto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            throw e;
        }
    }
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta(
            rs.getInt("id_venta"),
            rs.getDate("fecha").toLocalDate(),
            rs.getInt("id_cliente"),
            rs.getInt("id_producto"),
            rs.getInt("cantidad"),
            rs.getBigDecimal("precio_unitario"),
            rs.getBigDecimal("total")
        );
        
        // Agregar nombres si están disponibles
        try {
            venta.setNombreCliente(rs.getString("nombre_cliente"));
            venta.setNombreProducto(rs.getString("nombre_producto"));
        } catch (SQLException e) {
            // Los campos pueden no existir en algunas consultas
        }
        
        return venta;
    }
}