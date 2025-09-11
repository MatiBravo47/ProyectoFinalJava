package com.sistemaventas.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Producto {
    
    private int idProducto;
    private String nombre;
    private BigDecimal precio; // Usamos BigDecimal para mayor precisión con dinero
    private int stock;
    
    public Producto() {
    }
    

    public Producto(String nombre, BigDecimal precio, int stock) {
        this.nombre = nombre;
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }
    
    public Producto(int idProducto, String nombre, BigDecimal precio, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }
    
    // Getters y Setters con validaciones
    
    public int getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }
    

    public boolean hayStock(int cantidad) {
        return this.stock >= cantidad;
    }
    

    public void reducirStock(int cantidad) {
        if (!hayStock(cantidad)) {
            throw new IllegalArgumentException(
                "No hay suficiente stock. Disponible: " + this.stock + 
                ", Solicitado: " + cantidad);
        }
        this.stock -= cantidad;
    }
    

    public BigDecimal getValorInventario() {
        return precio.multiply(BigDecimal.valueOf(stock));
    }
    
    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=$%.2f, stock=%d}", 
                           idProducto, nombre, precio, stock);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Producto producto = (Producto) obj;
        return idProducto == producto.idProducto;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(idProducto);
    }
}
