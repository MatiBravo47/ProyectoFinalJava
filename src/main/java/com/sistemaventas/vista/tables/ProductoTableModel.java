package com.sistemaventas.vista.tables;

import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.modelo.Producto;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla para mostrar productos en JTable
 * @author Matt_
 */
public class ProductoTableModel extends AbstractTableModel {
    
    private String[] columns = {"ID", "Nombre", "Precio", "Stock", "Valor Inventario"};
    private List<Producto> productos = new ArrayList<>();
    private ProductoDAO productoDAO = new ProductoDAO();
    
    public ProductoTableModel() {
        // No cargar datos automáticamente en el constructor
    }
    
    /**
     * Carga todos los productos desde la base de datos
     */
    public void cargarDatos() {
        try {
            System.out.println("Cargando productos desde la base de datos...");
            List<Producto> nuevosProductos = productoDAO.obtenerTodos();
            
            // Limpiar la lista actual
            productos.clear();
            
            // Agregar los nuevos datos
            productos.addAll(nuevosProductos);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // En caso de error, mantener una lista vacía
            productos.clear();
            fireTableDataChanged();
            
            // Re-lanzar como RuntimeException para que la vista pueda manejarlo
            throw new RuntimeException("Error al cargar datos de productos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca productos por nombre
     */
    public void buscarPorNombre(String nombre) {
        try {
            List<Producto> productosEncontrados;
            
            if (nombre == null || nombre.trim().isEmpty()) {
                productosEncontrados = productoDAO.obtenerTodos();
            } else {
                productosEncontrados = productoDAO.buscarPorNombre(nombre.trim());
            }
            
            // Limpiar la lista actual
            productos.clear();
            
            // Agregar los resultados de la búsqueda
            productos.addAll(productosEncontrados);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage(), e);
        }
    }
    
    /**
     * Filtra productos con stock bajo (menos de cierta cantidad)
     */
    public void filtrarStockBajo(int umbral) {
        try {
            List<Producto> todosProductos = productoDAO.obtenerTodos();
            
            // Limpiar la lista actual
            productos.clear();
            
            // Filtrar productos con stock bajo
            for (Producto p : todosProductos) {
                if (p.getStock() < umbral) {
                    productos.add(p);
                }
            }
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al filtrar stock bajo: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getRowCount() { 
        return productos.size(); 
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
        if (row < 0 || row >= productos.size()) {
            return null;
        }
        
        Producto producto = productos.get(row);
        
        switch (col) {
            case 0: return producto.getIdProducto();
            case 1: return producto.getNombre();
            case 2: return String.format("$%.2f", producto.getPrecio());
            case 3: return producto.getStock();
            case 4: return String.format("$%.2f", producto.getValorInventario());
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
            case 3: return Integer.class;
            case 1:
            case 2:
            case 4: return String.class;
            default: return Object.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false; // Las celdas no son editables directamente
    }
    
    /**
     * Obtiene el producto en la fila especificada
     */
    public Producto getProductoAt(int row) {
        if (row >= 0 && row < productos.size()) {
            return productos.get(row);
        }
        return null;
    }
    
    /**
     * Agrega un nuevo producto a la base de datos y actualiza la tabla
     */
    public boolean agregarProducto(Producto producto) {
        try {
            System.out.println("Agregando nuevo producto: " + producto.getNombre());
            
            boolean guardado = productoDAO.guardar(producto);
            
            if (guardado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo agregar el producto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza un producto existente en la base de datos
     */
    public boolean actualizarProducto(Producto producto) {
        try {
            boolean actualizado = productoDAO.actualizar(producto);
            
            if (actualizado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo actualizar el producto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina un producto de la base de datos
     */
    public boolean eliminarProducto(int row) {
        if (row >= 0 && row < productos.size()) {
            Producto producto = productos.get(row);
            
            try {
                boolean eliminado = productoDAO.eliminar(producto.getIdProducto());
                
                if (eliminado) {
                    // Recargar todos los datos para asegurar consistencia
                    cargarDatos();
                    return true;
                } else {
                    return false;
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("No se pudo eliminar el producto: " + e.getMessage(), e);
            }
        }
        
        return false;
    }
    
    /**
     * Calcula el valor total del inventario
     */
    public BigDecimal calcularValorTotalInventario() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Producto p : productos) {
            total = total.add(p.getValorInventario());
        }
        
        return total;
    }
}