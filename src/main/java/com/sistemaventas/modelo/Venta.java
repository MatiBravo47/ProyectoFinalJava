package com.sistemaventas.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Clase modelo que representa una Venta en el sistema de gestión de ventas.
 * Refactorizada para usar objetos Cliente y Producto en lugar de solo IDs.
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 2.0
 */
public class Venta {
    
    private int idVenta;
    private LocalDate fecha;
    private Cliente cliente;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    
    /**
     * Constructor por defecto.
     */
    public Venta() {
        this.fecha = LocalDate.now();
    }
    
    /**
     * Constructor para crear una nueva venta (sin ID).
     * 
     * @param fecha Fecha de la venta
     * @param cliente Cliente que realiza la compra
     * @param producto Producto vendido
     * @param cantidad Cantidad vendida
     * @param precioUnitario Precio unitario al momento de la venta
     */
    public Venta(LocalDate fecha, Cliente cliente, Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        this.total = calcularTotal();
    }
    
    /**
     * Constructor completo para una venta existente (con ID).
     * 
     * @param idVenta ID de la venta
     * @param fecha Fecha de la venta
     * @param cliente Cliente que realizó la compra
     * @param producto Producto vendido
     * @param cantidad Cantidad vendida
     * @param precioUnitario Precio unitario
     * @param total Total de la venta
     */
    public Venta(int idVenta, LocalDate fecha, Cliente cliente, Producto producto, 
                 int cantidad, BigDecimal precioUnitario, BigDecimal total) {
        this.idVenta = idVenta;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }
    
    // Getters y Setters
    
    public int getIdVenta() {
        return idVenta;
    }
    
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }
    
    public String getFechaFormateada() {
        return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        this.cliente = cliente;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        this.producto = producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
        recalcularTotal();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        recalcularTotal();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total debe ser mayor a cero");
        }
        this.total = total.setScale(2, RoundingMode.HALF_UP);
    }
    
    // Métodos de utilidad para compatibilidad con código existente
    
    /**
     * Obtiene el ID del cliente (método de compatibilidad).
     * @return ID del cliente o 0 si el cliente es nulo
     */
    public int getIdCliente() {
        return cliente != null ? cliente.getIdCliente() : 0;
    }
    
    /**
     * Obtiene el ID del producto (método de compatibilidad).
     * @return ID del producto o 0 si el producto es nulo
     */
    public int getIdProducto() {
        return producto != null ? producto.getIdProducto() : 0;
    }
    
    /**
     * Obtiene el nombre del cliente.
     * @return Nombre del cliente o null si el cliente es nulo
     */
    public String getNombreCliente() {
        return cliente != null ? cliente.getNombre() : null;
    }
    
    /**
     * Obtiene el nombre del producto.
     * @return Nombre del producto o null si el producto es nulo
     */
    public String getNombreProducto() {
        return producto != null ? producto.getNombre() : null;
    }
    
    // Métodos de cálculo
    
    /**
     * Calcula el total de la venta.
     * @return Total calculado (precioUnitario × cantidad)
     */
    public BigDecimal calcularTotal() {
        if (precioUnitario != null && cantidad > 0) {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad))
                               .setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Recalcula el total cuando cambian cantidad o precio unitario.
     */
    public void recalcularTotal() {
        this.total = calcularTotal();
    }
    
    /**
     * Valida que la venta tenga todos los datos necesarios.
     * @return true si la venta es válida
     */
    public boolean esValida() {
        return fecha != null 
            && cliente != null 
            && producto != null 
            && cantidad > 0 
            && precioUnitario != null 
            && precioUnitario.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public String toString() {
        return String.format("Venta{id=%d, fecha=%s, cliente='%s', producto='%s', cantidad=%d, total=$%.2f}", 
                           idVenta, 
                           fecha, 
                           cliente != null ? cliente.getNombre() : "null",
                           producto != null ? producto.getNombre() : "null",
                           cantidad, 
                           total);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Venta venta = (Venta) obj;
        return idVenta == venta.idVenta;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idVenta);
    }
}