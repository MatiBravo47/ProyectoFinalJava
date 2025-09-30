package com.sistemaventas.controlador;

import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.modelo.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para manejar la lógica de negocio de Producto
 * Actúa como intermediario entre la Vista y el DAO
 * @author Matt_
 */
public class ProductoController {
    
    private ProductoDAO productoDAO;
    
    public ProductoController() {
        this.productoDAO = new ProductoDAO();
    }
    
    /**
     * Guarda un nuevo producto
     * @param producto Producto a guardar
     * @return true si se guardó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public boolean guardarProducto(Producto producto) throws SQLException {
        // Validaciones de negocio
        validarProducto(producto);
        
        // Verificar nombre único
        List<Producto> productosExistentes = productoDAO.buscarPorNombre(producto.getNombre());
        if (productosExistentes != null && !productosExistentes.isEmpty()) {
            // Verificar si hay coincidencia exacta
            for (Producto p : productosExistentes) {
                if (p.getNombre().equalsIgnoreCase(producto.getNombre())) {
                    throw new IllegalArgumentException("Ya existe un producto con ese nombre");
                }
            }
        }
        
        return productoDAO.guardar(producto);
    }
    
    /**
     * Actualiza un producto existente
     * @param producto Producto a actualizar
     * @return true si se actualizó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public boolean actualizarProducto(Producto producto) throws SQLException {
        // Validaciones de negocio
        validarProducto(producto);
        
        // Verificar que el producto existe
        Producto existente = productoDAO.buscarPorId(producto.getIdProducto());
        if (existente == null) {
            throw new IllegalArgumentException("El producto no existe en la base de datos");
        }
        
        // Verificar nombre único si cambió
        List<Producto> productosConNombre = productoDAO.buscarPorNombre(producto.getNombre());
        for (Producto p : productosConNombre) {
            if (p.getIdProducto() != producto.getIdProducto()) {
                throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
            }
        }
        
        return productoDAO.actualizar(producto);
    }
    
    /**
     * Elimina un producto por ID
     * @param id ID del producto a eliminar
     * @return true si se eliminó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si el producto tiene ventas asociadas
     */
    public boolean eliminarProducto(int id) throws SQLException {
        // Verificar que el producto existe
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        
        // TODO: Verificar si el producto tiene ventas asociadas
        // Esta verificación se implementará cuando tengamos VentaDAO completo
        // if (tieneVentasAsociadas(id)) {
        //     throw new IllegalArgumentException("No se puede eliminar el producto porque tiene ventas asociadas");
        // }
        
        return productoDAO.eliminar(id);
    }
    
    /**
     * Busca un producto por ID
     * @param id ID del producto
     * @return Producto encontrado o null si no existe
     * @throws SQLException si hay error en la base de datos
     */
    public Producto buscarProductoPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        
        return productoDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todos los productos
     * @return Lista de todos los productos
     * @throws SQLException si hay error en la base de datos
     */
    public List<Producto> obtenerTodosLosProductos() throws SQLException {
        return productoDAO.obtenerTodos();
    }
    
    /**
     * Busca productos por nombre
     * @param nombre Nombre a buscar (puede ser parcial)
     * @return Lista de productos que coinciden
     * @throws SQLException si hay error en la base de datos
     */
    public List<Producto> buscarProductosPorNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            return obtenerTodosLosProductos();
        }
        
        return productoDAO.buscarPorNombre(nombre.trim());
    }
    
    /**
     * Actualiza el stock de un producto
     * @param id ID del producto
     * @param nuevoStock Nuevo valor de stock
     * @return true si se actualizó exitosamente
     * @throws SQLException si hay error en la base de datos
     */
    public boolean actualizarStock(int id, int nuevoStock) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        // Verificar que el producto existe
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        
        return productoDAO.actualizarStock(id, nuevoStock);
    }
    
    /**
     * Reduce el stock de un producto (usado en ventas)
     * @param id ID del producto
     * @param cantidad Cantidad a reducir
     * @return true si se redujo exitosamente
     * @throws SQLException si hay error en la base de datos
     */
    public boolean reducirStock(int id, int cantidad) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        // Verificar que el producto existe y tiene stock suficiente
        Producto producto = productoDAO.buscarPorId(id);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe");
        }
        
        if (producto.getStock() < cantidad) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                    producto.getStock(), cantidad)
            );
        }
        
        int nuevoStock = producto.getStock() - cantidad;
        return productoDAO.actualizarStock(id, nuevoStock);
    }
    
    /**
     * Valida que un producto tenga los datos mínimos requeridos
     * @param producto Producto a validar
     * @throws IllegalArgumentException si los datos son inválidos
     */
    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        
        // Validar nombre obligatorio
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        
        // Validar longitud del nombre
        if (producto.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre no puede exceder los 100 caracteres");
        }
        
        // Validar precio
        if (producto.getPrecio() == null || producto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        
        // Validar que el precio no sea excesivamente alto (validación de negocio)
        if (producto.getPrecio().compareTo(new BigDecimal("999999.99")) > 0) {
            throw new IllegalArgumentException("El precio no puede exceder $999,999.99");
        }
        
        // Validar stock
        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        // Validar que el stock no sea excesivamente alto (validación de negocio)
        if (producto.getStock() > 999999) {
            throw new IllegalArgumentException("El stock no puede exceder 999,999 unidades");
        }
    }
}