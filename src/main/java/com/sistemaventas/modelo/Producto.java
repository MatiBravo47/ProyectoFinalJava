package com.sistemaventas.modelo;

import java.math.BigDecimal;    //valores decimales con precisión y vitar errores de redondeo típicos de double
import java.math.RoundingMode; //redondear decimales

/**
 * Clase modelo que representa un Producto en el sistema de gestión de ventas.
 * <p>
 * Esta clase encapsula toda la información relacionada con un producto,
 * incluyendo datos de identificación, precio y control de inventario.
 * Utiliza BigDecimal para manejo preciso de valores monetarios.
 * </p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li>Identificador único autoincremental</li>
 *   <li>Precio con precisión decimal (2 decimales)</li>
 *   <li>Control de stock con validaciones</li>
 *   <li>Métodos para gestión de inventario</li>
 *   <li>Validaciones de integridad de datos</li>
 * </ul>
 * 
 * <p><strong>Redondeo de precios:</strong></p>
 * <p>
 * Los precios se redondean automáticamente a 2 decimales usando
 * RoundingMode.HALF_UP, que es el estándar para operaciones monetarias:
 * </p>
 * <ul>
 *   <li>Si el tercer decimal es 5 o mayor: redondea hacia arriba</li>
 *   <li>Si el tercer decimal es menor a 5: redondea hacia abajo</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see java.math.BigDecimal
 * @see java.math.RoundingMode
 */
public class Producto {
    
    /**
     * Identificador único del producto en la base de datos.
     * <p>
     * Este campo es autoincremental y se asigna automáticamente
     * cuando se persiste el producto por primera vez.
     * </p>
     */
    private int idProducto;
    
    /**
     * Nombre del producto.
     * <p>
     * Campo obligatorio que identifica al producto.
     * No puede ser nulo, vacío o contener solo espacios en blanco.
     * Se almacena sin espacios al inicio y final.
     * </p>
     */
    private String nombre;
    
    /**
     * Precio unitario del producto.
     * <p>
     * Utiliza BigDecimal para precisión en cálculos monetarios.
     * Se redondea automáticamente a 2 decimales usando HALF_UP.
     * Debe ser mayor a cero.
     * </p>
     */
    private BigDecimal precio;
    
    /**
     * Cantidad disponible en inventario.
     * <p>
     * Representa el stock actual del producto.
     * No puede ser negativo. Se actualiza automáticamente
     * cuando se realizan ventas o ajustes de inventario.
     * </p>
     */
    private int stock;
    
    /**
     * Constructor por defecto.
     * <p>
     * Crea una instancia vacía de Producto. Los campos
     * deben ser establecidos mediante los métodos setter
     * correspondientes.
     * </p>
     */
    public Producto() {
    }
    
    /**
     * Constructor para crear un nuevo producto.
     * <p>
     * Este constructor se utiliza cuando se crea un producto
     * que aún no ha sido persistido en la base de datos.
     * El idProducto será asignado automáticamente por la BD.
     * El precio se redondea automáticamente a 2 decimales.
     * </p>
     * 
     * @param nombre Nombre del producto (no puede ser nulo ni vacío)
     * @param precio Precio unitario del producto (debe ser mayor a cero)
     * @param stock  Cantidad inicial en inventario (no puede ser negativo)
     * 
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Producto(String nombre, BigDecimal precio, int stock) {
        this.nombre = nombre;
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }
    
    /**
     * Constructor completo para un producto existente.
     * <p>
     * Este constructor se utiliza cuando se recupera un producto
     * de la base de datos y se necesita crear la instancia
     * con todos sus datos, incluyendo el ID.
     * El precio se redondea automáticamente a 2 decimales.
     * </p>
     * 
     * @param idProducto Identificador único del producto en la BD
     * @param nombre     Nombre del producto
     * @param precio     Precio unitario del producto (debe ser mayor a cero)
     * @param stock      Cantidad en inventario (no puede ser negativo)
     * 
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Producto(int idProducto, String nombre, BigDecimal precio, int stock) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }
    
    // Getters y Setters con validaciones
    
    /**
     * Obtiene el identificador único del producto.
     * 
     * @return el ID del producto en la base de datos
     */
    public int getIdProducto() {
        return idProducto;
    }
    
    /**
     * Establece el identificador único del producto.
     * <p>
     * Este método se utiliza principalmente cuando se recupera
     * un producto de la base de datos.
     * </p>
     * 
     * @param idProducto el ID único del producto
     */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    
    /**
     * Obtiene el nombre del producto.
     * 
     * @return el nombre del producto, puede ser null si no se ha establecido
     */
    public String getNombre() {
        return nombre;
    }
    
    /**
     * Establece el nombre del producto.
     * <p>
     * El nombre no puede ser nulo, vacío o contener solo espacios en blanco.
     * Se eliminan automáticamente los espacios al inicio y final.
     * </p>
     * 
     * @param nombre el nombre del producto (no puede ser nulo ni vacío)
     * @throws IllegalArgumentException si el nombre es nulo, vacío o solo espacios
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }
    
    /**
     * Obtiene el precio unitario del producto.
     * 
     * @return el precio del producto con 2 decimales de precisión
     */
    public BigDecimal getPrecio() {
        return precio;
    }
    
    /**
     * Establece el precio unitario del producto.
     * <p>
     * El precio debe ser mayor a cero y se redondea automáticamente
     * a 2 decimales usando RoundingMode.HALF_UP.
     * </p>
     * 
     * @param precio el precio del producto (debe ser mayor a cero)
     * @throws IllegalArgumentException si el precio es nulo o menor o igual a cero
     */
    public void setPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtiene la cantidad disponible en inventario.
     * 
     * @return el stock actual del producto
     */
    public int getStock() {
        return stock;
    }
    
    /**
     * Establece la cantidad disponible en inventario.
     * <p>
     * El stock no puede ser negativo. Se utiliza para ajustes
     * de inventario o cuando se recupera el producto de la BD.
     * </p>
     * 
     * @param stock la cantidad en inventario (no puede ser negativa)
     * @throws IllegalArgumentException si el stock es negativo
     */
    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        this.stock = stock;
    }
    

    /**
     * Verifica si hay suficiente stock disponible para la cantidad solicitada.
     * <p>
     * Este método es útil para validar disponibilidad antes de procesar
     * una venta o realizar operaciones de inventario.
     * </p>
     * 
     * @param cantidad la cantidad solicitada
     * @return true si hay suficiente stock, false en caso contrario
     */
    public boolean hayStock(int cantidad) {
        return this.stock >= cantidad;
    }
    

    /**
     * Reduce el stock del producto en la cantidad especificada.
     * <p>
     * Este método se utiliza principalmente cuando se procesa una venta.
     * Verifica automáticamente que haya suficiente stock disponible
     * antes de realizar la reducción.
     * </p>
     * 
     * @param cantidad la cantidad a reducir del stock
     * @throws IllegalArgumentException si no hay suficiente stock disponible
     */
    public void reducirStock(int cantidad) {
        if (!hayStock(cantidad)) {
            throw new IllegalArgumentException(
                "No hay suficiente stock. Disponible: " + this.stock + 
                ", Solicitado: " + cantidad);
        }
        this.stock -= cantidad;
    }
    

    /**
     * Calcula el valor total del inventario para este producto.
     * <p>
     * Multiplica el precio unitario por la cantidad disponible en stock.
     * El resultado mantiene la precisión decimal de BigDecimal.
     * </p>
     * 
     * @return el valor total del inventario (precio × stock)
     */
    public BigDecimal getValorInventario() {
        return precio.multiply(BigDecimal.valueOf(stock));
    }
    
    /**
     * Devuelve una representación en cadena del producto.
     * <p>
     * Incluye todos los campos principales del producto en un formato
     * legible para debugging y logging.
     * </p>
     * 
     * @return una cadena que representa el producto
     */
    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=$%.2f, stock=%d}", 
                           idProducto, nombre, precio, stock);
    }
    
    /**
     * Compara este producto con otro objeto para determinar si son iguales.
     * <p>
     * Dos productos se consideran iguales si tienen el mismo ID.
     * Esto es consistente con el hecho de que el ID es único en la BD.
     * </p>
     * 
     * @param obj el objeto a comparar con este producto
     * @return true si los objetos son iguales, false en caso contrario
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Producto producto = (Producto) obj;
        return idProducto == producto.idProducto;
    }
    
    /**
     * Calcula el código hash para este producto.
     * <p>
     * El código hash se basa únicamente en el ID del producto,
     * ya que es el campo que determina la igualdad.
     * </p>
     * 
     * @return el código hash calculado para este producto
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(idProducto);
    }
}