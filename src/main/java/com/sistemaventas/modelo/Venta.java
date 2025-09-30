package com.sistemaventas.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase modelo que representa una Venta
 * @author Matt_
 */
public class Venta {
    
    private int idVenta;
    private LocalDate fecha;
    private int idCliente;
    private int idProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    
    // Campos adicionales para mostrar en vistas (no se guardan en BD)
    private String nombreCliente;
    private String nombreProducto;
    
    // Constructor vacío
    public Venta() {
    }
    
    // Constructor sin ID (para crear nueva venta)
    public Venta(LocalDate fecha, int idCliente, int idProducto, int cantidad, 
                 BigDecimal precioUnitario, BigDecimal total) {
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }
    
    // Constructor completo (para cargar desde BD)
    public Venta(int idVenta, LocalDate fecha, int idCliente, int idProducto, 
                 int cantidad, BigDecimal precioUnitario, BigDecimal total) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
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
    
    public int getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(int idCliente) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        this.idCliente = idCliente;
    }
    
    public int getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        this.idProducto = idProducto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total debe ser mayor a cero");
        }
        this.total = total;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    /**
     * Calcula el total basado en cantidad y precio unitario
     */
    public BigDecimal calcularTotal() {
        if (precioUnitario != null && cantidad > 0) {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return String.format("Venta{id=%d, fecha=%s, cliente=%d, producto=%d, cantidad=%d, total=$%.2f}", 
                           idVenta, fecha, idCliente, idProducto, cantidad, total);
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