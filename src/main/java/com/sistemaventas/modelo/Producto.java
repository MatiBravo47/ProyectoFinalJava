package com.sistemaventas.modelo;

import java.math.BigDecimal;    //valores decimales con precisión y vitar errores de redondeo típicos de double
import java.math.RoundingMode; //redondear decimales


public class Producto {
    
    private int idProducto;
    private String nombre;
    private BigDecimal precio;
    private int stock;
    
    //Constructor vacio
    public Producto() {
    }
    
    //Constructor sin idProducto
    public Producto(String nombre, BigDecimal precio, int stock) {
        this.nombre = nombre;
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }
    
    /*
    BigDecimal tiene un método llamado setScale(int scale, RoundingMode mode).

        scale → la cantidad de decimales.

        RoundingMode → forma de redondea si hay más decimales de los que pediste.
    
    HALF_UP?

    Es un modo de redondeo muy usado en dinero:

    Mira el tercer decimal (el que sobra).

    Si ese decimal es 5 o más, redondea hacia arriba.

    Si es menor a 5, redondea hacia abajo.
    */
    
    //Constructor con idProducto
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
    
    //No permite nombres vacíos o nulos.
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    //El precio debe ser mayor a 0.
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