package com.sistemaventas.vista.tables;

import com.sistemaventas.dao.VentaDAO;
import com.sistemaventas.modelo.Venta;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de tabla para mostrar ventas en JTable
 * @author Matt_
 */
public class VentaTableModel extends AbstractTableModel {
    
    private String[] columns = {"ID", "Fecha", "Cliente", "Producto", "Cantidad", "Precio Unit.", "Total"};
    private List<Venta> ventas = new ArrayList<>();
    private VentaDAO ventaDAO = new VentaDAO();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public VentaTableModel() {
        // No cargar datos automáticamente en el constructor
    }
    
    /**
     * Carga todas las ventas desde la base de datos
     */
    public void cargarDatos() {
        try {
            System.out.println("Cargando ventas desde la base de datos...");
            List<Venta> nuevasVentas = ventaDAO.obtenerTodas();
            
            // Limpiar la lista actual
            ventas.clear();
            
            // Agregar los nuevos datos
            ventas.addAll(nuevasVentas);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // En caso de error, mantener una lista vacía
            ventas.clear();
            fireTableDataChanged();
            
            // Re-lanzar como RuntimeException para que la vista pueda manejarlo
            throw new RuntimeException("Error al cargar datos de ventas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca ventas por cliente
     */
    public void buscarPorCliente(int idCliente) {
        try {
            List<Venta> ventasEncontradas = ventaDAO.buscarPorCliente(idCliente);
            
            // Limpiar la lista actual
            ventas.clear();
            
            // Agregar los resultados
            ventas.addAll(ventasEncontradas);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca ventas por producto
     */
    public void buscarPorProducto(int idProducto) {
        try {
            List<Venta> ventasEncontradas = ventaDAO.buscarPorProducto(idProducto);
            
            // Limpiar la lista actual
            ventas.clear();
            
            // Agregar los resultados
            ventas.addAll(ventasEncontradas);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca ventas por rango de fechas
     */
    public void buscarPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<Venta> ventasEncontradas = ventaDAO.buscarPorFechas(fechaInicio, fechaFin);
            
            // Limpiar la lista actual
            ventas.clear();
            
            // Agregar los resultados
            ventas.addAll(ventasEncontradas);
            
            // Notificar que los datos cambiaron
            fireTableDataChanged();
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error en la búsqueda: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getRowCount() { 
        return ventas.size(); 
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
        if (row < 0 || row >= ventas.size()) {
            return null;
        }
        
        Venta venta = ventas.get(row);
        
        switch (col) {
            case 0: return venta.getIdVenta();
            case 1: return venta.getFecha().format(dateFormatter);
            case 2: return venta.getNombreCliente() != null ? venta.getNombreCliente() : "ID: " + venta.getIdCliente();
            case 3: return venta.getNombreProducto() != null ? venta.getNombreProducto() : "ID: " + venta.getIdProducto();
            case 4: return venta.getCantidad();
            case 5: return String.format("$%.2f", venta.getPrecioUnitario());
            case 6: return String.format("$%.2f", venta.getTotal());
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
            case 4: return Integer.class;
            case 1:
            case 2:
            case 3:
            case 5:
            case 6: return String.class;
            default: return Object.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false; // Las celdas no son editables directamente
    }
    
    /**
     * Obtiene la venta en la fila especificada
     */
    public Venta getVentaAt(int row) {
        if (row >= 0 && row < ventas.size()) {
            return ventas.get(row);
        }
        return null;
    }
    
    /**
     * Agrega una nueva venta a la base de datos y actualiza la tabla
     */
    public boolean agregarVenta(Venta venta) {
        try {
            System.out.println("Agregando nueva venta");
            
            boolean guardado = ventaDAO.guardar(venta);
            
            if (guardado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo agregar la venta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Actualiza una venta existente en la base de datos
     */
    public boolean actualizarVenta(Venta venta) {
        try {
            boolean actualizado = ventaDAO.actualizar(venta);
            
            if (actualizado) {
                // Recargar todos los datos para asegurar consistencia
                cargarDatos();
                return true;
            } else {
                return false;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("No se pudo actualizar la venta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina una venta de la base de datos
     */
    public boolean eliminarVenta(int row) {
        if (row >= 0 && row < ventas.size()) {
            Venta venta = ventas.get(row);
            
            try {
                boolean eliminado = ventaDAO.eliminar(venta.getIdVenta());
                
                if (eliminado) {
                    // Recargar todos los datos para asegurar consistencia
                    cargarDatos();
                    return true;
                } else {
                    return false;
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("No se pudo eliminar la venta: " + e.getMessage(), e);
            }
        }
        
        return false;
    }
    
    /**
     * Calcula el total de todas las ventas mostradas
     */
    public BigDecimal calcularTotalVentas() {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Venta v : ventas) {
            total = total.add(v.getTotal());
        }
        
        return total;
    }
}