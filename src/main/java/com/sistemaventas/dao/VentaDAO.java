package com.sistemaventas.dao;

import com.sistemaventas.modelo.Venta;
import com.sistemaventas.modelo.Cliente;
import com.sistemaventas.modelo.Producto;
import com.sistemaventas.util.ConexionDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO refactorizado para Venta trabajando con objetos Cliente y Producto
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 2.0
 */
public class VentaDAO {
    
    private ClienteDAO clienteDAO;
    private ProductoDAO productoDAO;
    
    public VentaDAO() {
        this.clienteDAO = new ClienteDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    public boolean guardar(Venta venta) throws SQLException {
        if (venta.getCliente() == null || venta.getProducto() == null) {
            throw new IllegalArgumentException("La venta debe tener cliente y producto");
        }
        
        String sql = "INSERT INTO ventas (fecha, id_cliente, id_producto, cantidad, precio_unitario, total) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(venta.getFecha()));
                pstmt.setInt(2, venta.getCliente().getIdCliente());
                pstmt.setInt(3, venta.getProducto().getIdProducto());
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
                        }
                    }
                    
                    // Actualizar stock del producto
                    Producto producto = venta.getProducto();
                    int nuevoStock = producto.getStock() - venta.getCantidad();
                    actualizarStockProducto(conn, producto.getIdProducto(), nuevoStock);
                    
                    conn.commit();
                    System.out.println("✓ Venta guardada con ID: " + venta.getIdVenta());
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            System.err.println("Error al guardar venta: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public Venta buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT v.*, 
                   c.nombre as cliente_nombre, c.dni as cliente_dni, 
                   c.telefono as cliente_telefono, c.email as cliente_email,
                   p.nombre as producto_nombre, p.precio as producto_precio, 
                   p.stock as producto_stock
            FROM ventas v
            JOIN clientes c ON v.id_cliente = c.id_cliente
            JOIN productos p ON v.id_producto = p.id_producto
            WHERE v.id_venta = ?
        """;
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVentaCompleta(rs);
                }
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar venta por ID: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Venta> obtenerTodas() throws SQLException {
        String sql = """
            SELECT v.*, 
                   c.nombre as cliente_nombre, c.dni as cliente_dni, 
                   c.telefono as cliente_telefono, c.email as cliente_email,
                   p.nombre as producto_nombre, p.precio as producto_precio, 
                   p.stock as producto_stock
            FROM ventas v
            JOIN clientes c ON v.id_cliente = c.id_cliente
            JOIN productos p ON v.id_producto = p.id_producto
            ORDER BY v.fecha DESC, v.id_venta DESC
        """;
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ventas.add(mapearVentaCompleta(rs));
            }
            
            System.out.println("✓ Ventas obtenidas: " + ventas.size());
            return ventas;
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
            throw e;
        }
    }
    
    public boolean actualizar(Venta venta) throws SQLException {
        if (venta.getCliente() == null || venta.getProducto() == null) {
            throw new IllegalArgumentException("La venta debe tener cliente y producto");
        }
        
        // Primero obtenemos la venta original para ajustar el stock
        Venta ventaOriginal = buscarPorId(venta.getIdVenta());
        if (ventaOriginal == null) {
            return false;
        }
        
        String sql = "UPDATE ventas SET fecha = ?, id_cliente = ?, id_producto = ?, " +
                     "cantidad = ?, precio_unitario = ?, total = ? WHERE id_venta = ?";
        
        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDate(1, Date.valueOf(venta.getFecha()));
                pstmt.setInt(2, venta.getCliente().getIdCliente());
                pstmt.setInt(3, venta.getProducto().getIdProducto());
                pstmt.setInt(4, venta.getCantidad());
                pstmt.setBigDecimal(5, venta.getPrecioUnitario());
                pstmt.setBigDecimal(6, venta.getTotal());
                pstmt.setInt(7, venta.getIdVenta());
                
                int filasAfectadas = pstmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Ajustar stock
                    if (ventaOriginal.getIdProducto() == venta.getProducto().getIdProducto()) {
                        // Mismo producto: ajustar diferencia
                        Producto producto = venta.getProducto();
                        int diferencia = venta.getCantidad() - ventaOriginal.getCantidad();
                        if (diferencia != 0) {
                            int nuevoStock = producto.getStock() - diferencia;
                            actualizarStockProducto(conn, producto.getIdProducto(), nuevoStock);
                        }
                    } else {
                        // Producto diferente: devolver stock original y descontar nuevo
                        Producto productoOriginal = ventaOriginal.getProducto();
                        int stockDevuelto = productoOriginal.getStock() + ventaOriginal.getCantidad();
                        actualizarStockProducto(conn, productoOriginal.getIdProducto(), stockDevuelto);
                        
                        Producto productoNuevo = venta.getProducto();
                        int stockDescontado = productoNuevo.getStock() - venta.getCantidad();
                        actualizarStockProducto(conn, productoNuevo.getIdProducto(), stockDescontado);
                    }
                    
                    conn.commit();
                    System.out.println("✓ Venta actualizada: ID " + venta.getIdVenta());
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            System.err.println("Error al actualizar venta: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public boolean eliminar(int id) throws SQLException {
        // Obtener la venta para devolver el stock
        Venta venta = buscarPorId(id);
        if (venta == null) {
            System.out.println("⚠ Venta con ID " + id + " no encontrada");
            return false;
        }
        
        String sql = "DELETE FROM ventas WHERE id_venta = ?";
        
        Connection conn = null;
        try {
            conn = ConexionDB.getConexion();
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                
                int filasAfectadas = pstmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    // Devolver el stock al producto
                    Producto producto = venta.getProducto();
                    int stockRestaurado = producto.getStock() + venta.getCantidad();
                    actualizarStockProducto(conn, producto.getIdProducto(), stockRestaurado);
                    
                    conn.commit();
                    System.out.println("✓ Venta eliminada: ID " + id);
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            System.err.println("Error al eliminar venta: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public List<Venta> buscarPorCliente(int idCliente) throws SQLException {
        String sql = """
            SELECT v.*, 
                   c.nombre as cliente_nombre, c.dni as cliente_dni, 
                   c.telefono as cliente_telefono, c.email as cliente_email,
                   p.nombre as producto_nombre, p.precio as producto_precio, 
                   p.stock as producto_stock
            FROM ventas v
            JOIN clientes c ON v.id_cliente = c.id_cliente
            JOIN productos p ON v.id_producto = p.id_producto
            WHERE v.id_cliente = ?
            ORDER BY v.fecha DESC
        """;
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idCliente);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVentaCompleta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas por cliente: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Venta> buscarPorProducto(int idProducto) throws SQLException {
        String sql = """
            SELECT v.*, 
                   c.nombre as cliente_nombre, c.dni as cliente_dni, 
                   c.telefono as cliente_telefono, c.email as cliente_email,
                   p.nombre as producto_nombre, p.precio as producto_precio, 
                   p.stock as producto_stock
            FROM ventas v
            JOIN clientes c ON v.id_cliente = c.id_cliente
            JOIN productos p ON v.id_producto = p.id_producto
            WHERE v.id_producto = ?
            ORDER BY v.fecha DESC
        """;
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idProducto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVentaCompleta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas por producto: " + e.getMessage());
            throw e;
        }
    }
    
    public List<Venta> buscarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT v.*, 
                   c.nombre as cliente_nombre, c.dni as cliente_dni, 
                   c.telefono as cliente_telefono, c.email as cliente_email,
                   p.nombre as producto_nombre, p.precio as producto_precio, 
                   p.stock as producto_stock
            FROM ventas v
            JOIN clientes c ON v.id_cliente = c.id_cliente
            JOIN productos p ON v.id_producto = p.id_producto
            WHERE v.fecha BETWEEN ? AND ?
            ORDER BY v.fecha DESC
        """;
        
        List<Venta> ventas = new ArrayList<>();
        
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVentaCompleta(rs));
                }
            }
            
            return ventas;
            
        } catch (SQLException e) {
            System.err.println("Error al buscar ventas por fechas: " + e.getMessage());
            throw e;
        }
    }
    
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
            System.err.println("Error al verificar ventas del cliente: " + e.getMessage());
            throw e;
        }
    }
    
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
            System.err.println("Error al verificar ventas del producto: " + e.getMessage());
            throw e;
        }
    }
    
    private void actualizarStockProducto(Connection conn, int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id_producto = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();
        }
    }
    
    private Venta mapearVentaCompleta(ResultSet rs) throws SQLException {
        // Crear cliente
        Cliente cliente = new Cliente(
            rs.getInt("id_cliente"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_dni"),
            rs.getString("cliente_telefono"),
            rs.getString("cliente_email")
        );
        
        // Crear producto
        Producto producto = new Producto(
            rs.getInt("id_producto"),
            rs.getString("producto_nombre"),
            rs.getBigDecimal("producto_precio"),
            rs.getInt("producto_stock")
        );
        
        // Crear venta
        Venta venta = new Venta(
            rs.getInt("id_venta"),
            rs.getDate("fecha").toLocalDate(),
            cliente,
            producto,
            rs.getInt("cantidad"),
            rs.getBigDecimal("precio_unitario"),
            rs.getBigDecimal("total")
        );
        
        return venta;
    }
}