package com.sistemaventas.controlador;

import com.sistemaventas.dao.VentaDAO;
import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Venta;
import com.sistemaventas.modelo.Producto;
import com.sistemaventas.modelo.Cliente;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para manejar la lógica de negocio de Venta
 * @author Matt_
 */
public class VentaController {
    
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    
    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
    }
    
    /**
     * Registra una nueva venta y actualiza el stock
     * @param venta Venta a registrar
     * @return true si se guardó exitosamente
     * @throws SQLException si hay error en la base de datos
     * @throws IllegalArgumentException si los datos son inválidos
     */
    public boolean registrarVenta(Venta venta) throws SQLException {
        // Validaciones de negocio
        validarVenta(venta);
        
        // Verificar que el cliente existe
        Cliente cliente = clienteDAO.buscarPorId(venta.getIdCliente());
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente seleccionado no existe");
        }
        
        // Verificar que el producto existe y tiene stock suficiente
        Producto producto = productoDAO.buscarPorId(venta.getIdProducto());
        if (producto == null) {
            throw new IllegalArgumentException("El producto seleccionado no existe");
        }
        
        if (producto.getStock() < venta.getCantidad()) {
            throw new IllegalArgumentException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    producto.getStock(), venta.getCantidad())
            );
        }
        
        // Verificar que el precio unitario coincida con el precio actual del producto
        // (o permitir un precio diferente si es una política de negocio)
        venta.setPrecioUnitario(producto.getPrecio());
        
        // Calcular el total
        venta.setTotal(venta.calcularTotal());
        
        // Guardar la venta
        boolean ventaGuardada = ventaDAO.guardar(venta);
        
        if (ventaGuardada) {
            // Reducir el stock del producto
            int nuevoStock = producto.getStock() - venta.getCantidad();
            productoDAO.actualizarStock(producto.getIdProducto(), nuevoStock);
        }
        
        return ventaGuardada;
    }
    
    /**
     * Actualiza una venta existente
     * NOTA: Esta operación debe manejarse con cuidado ya que afecta el stock
     * @param venta Venta a actualizar
     * @return true si se actualizó exitosamente
     * @throws SQLException si hay error en la base de datos
     */
    public boolean actualizarVenta(Venta venta) throws SQLException {
        validarVenta(venta);
        
        // Verificar que la venta existe
        Venta ventaExistente = ventaDAO.buscarPorId(venta.getIdVenta());
        if (ventaExistente == null) {
            throw new IllegalArgumentException("La venta no existe en la base de datos");
        }
        
        // Si cambió la cantidad, ajustar el stock
        if (ventaExistente.getCantidad() != venta.getCantidad() ||
            ventaExistente.getIdProducto() != venta.getIdProducto()) {
            
            // Devolver el stock del producto original
            Producto productoOriginal = productoDAO.buscarPorId(ventaExistente.getIdProducto());
            int stockDevuelto = productoOriginal.getStock() + ventaExistente.getCantidad();
            productoDAO.actualizarStock(productoOriginal.getIdProducto(), stockDevuelto);
            
            // Reducir el stock del nuevo producto (o del mismo con nueva cantidad)
            Producto productoNuevo = productoDAO.buscarPorId(venta.getIdProducto());
            if (productoNuevo.getStock() < venta.getCantidad()) {
                // Revertir el stock anterior
                productoDAO.actualizarStock(productoOriginal.getIdProducto(), productoOriginal.getStock());
                throw new IllegalArgumentException("Stock insuficiente para la actualización");
            }
            
            int nuevoStock = productoNuevo.getStock() - venta.getCantidad();
            productoDAO.actualizarStock(productoNuevo.getIdProducto(), nuevoStock);
        }
        
        // Recalcular el total
        venta.setTotal(venta.calcularTotal());
        
        return ventaDAO.actualizar(venta);
    }
    
    /**
     * Elimina una venta y restaura el stock
     * @param id ID de la venta a eliminar
     * @return true si se eliminó exitosamente
     * @throws SQLException si hay error en la base de datos
     */
    public boolean eliminarVenta(int id) throws SQLException {
        // Verificar que la venta existe
        Venta venta = ventaDAO.buscarPorId(id);
        if (venta == null) {
            throw new IllegalArgumentException("La venta no existe");
        }
        
        // Devolver el stock del producto
        Producto producto = productoDAO.buscarPorId(venta.getIdProducto());
        if (producto != null) {
            int stockRestaurado = producto.getStock() + venta.getCantidad();
            productoDAO.actualizarStock(producto.getIdProducto(), stockRestaurado);
        }
        
        return ventaDAO.eliminar(id);
    }
    
    /**
     * Busca una venta por ID
     */
    public Venta buscarVentaPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la venta debe ser mayor a cero");
        }
        
        return ventaDAO.buscarPorId(id);
    }
    
    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodasLasVentas() throws SQLException {
        return ventaDAO.obtenerTodas();
    }
    
    /**
     * Busca ventas por cliente
     */
    public List<Venta> buscarVentasPorCliente(int idCliente) throws SQLException {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        
        return ventaDAO.buscarPorCliente(idCliente);
    }
    
    /**
     * Busca ventas por producto
     */
    public List<Venta> buscarVentasPorProducto(int idProducto) throws SQLException {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        
        return ventaDAO.buscarPorProducto(idProducto);
    }
    
    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> buscarVentasPorFechas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        return ventaDAO.buscarPorFechas(fechaInicio, fechaFin);
    }
    
    /**
     * Calcula el total de ventas en un periodo
     */
    public BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<Venta> ventas = buscarVentasPorFechas(fechaInicio, fechaFin);
        
        BigDecimal total = BigDecimal.ZERO;
        for (Venta venta : ventas) {
            total = total.add(venta.getTotal());
        }
        
        return total;
    }
    
    /**
     * Verifica si un cliente tiene ventas
     */
    public boolean clienteTieneVentas(int idCliente) throws SQLException {
        return ventaDAO.clienteTieneVentas(idCliente);
    }
    
    /**
     * Verifica si un producto tiene ventas
     */
    public boolean productoTieneVentas(int idProducto) throws SQLException {
        return ventaDAO.productoTieneVentas(idProducto);
    }
    
    /**
     * Valida que una venta tenga los datos mínimos requeridos
     */
    private void validarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        
        // Validar fecha
        if (venta.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        
        // No permitir ventas futuras
        if (venta.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden registrar ventas con fecha futura");
        }
        
        // No permitir ventas muy antiguas (por ejemplo, más de 1 año)
        if (venta.getFecha().isBefore(LocalDate.now().minusYears(1))) {
            throw new IllegalArgumentException("No se pueden registrar ventas con más de 1 año de antigüedad");
        }
        
        // Validar cliente
        if (venta.getIdCliente() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un cliente válido");
        }
        
        // Validar producto
        if (venta.getIdProducto() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido");
        }
        
        // Validar cantidad
        if (venta.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        if (venta.getCantidad() > 9999) {
            throw new IllegalArgumentException("La cantidad no puede exceder 9,999 unidades");
        }
        
        // Validar precio unitario
        if (venta.getPrecioUnitario() == null || venta.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
    }
}