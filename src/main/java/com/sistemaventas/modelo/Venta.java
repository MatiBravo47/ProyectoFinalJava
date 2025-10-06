package com.sistemaventas.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase modelo que representa una Venta en el sistema de gestión de ventas.
 * <p>
 * Esta clase encapsula toda la información relacionada con una transacción
 * de venta, incluyendo datos del cliente, producto, cantidades y totales.
 * Utiliza BigDecimal para manejo preciso de valores monetarios.
 * </p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li>Identificador único autoincremental</li>
 *   <li>Fecha de la venta con validación</li>
 *   <li>Referencias a cliente y producto</li>
 *   <li>Cálculo automático de totales</li>
 *   <li>Campos auxiliares para visualización</li>
 * </ul>
 * 
 * <p><strong>Campos de visualización:</strong></p>
 * <p>
 * Los campos nombreCliente y nombreProducto son auxiliares y no se
 * persisten en la base de datos. Se utilizan únicamente para mostrar
 * información legible en las interfaces de usuario.
 * </p>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see java.math.BigDecimal
 * @see java.time.LocalDate
 */
public class Venta {
    
    /**
     * Identificador único de la venta en la base de datos.
     * <p>
     * Este campo es autoincremental y se asigna automáticamente
     * cuando se persiste la venta por primera vez.
     * </p>
     */
    private int idVenta;
    
    /**
     * Fecha en que se realizó la venta.
     * <p>
     * Campo obligatorio que registra cuándo ocurrió la transacción.
     * No puede ser nulo.
     * </p>
     */
    private LocalDate fecha;
    
    /**
     * Identificador del cliente que realizó la compra.
     * <p>
     * Referencia al cliente en la tabla clientes.
     * Debe ser un ID válido mayor a cero.
     * </p>
     */
    private int idCliente;
    
    /**
     * Identificador del producto vendido.
     * <p>
     * Referencia al producto en la tabla productos.
     * Debe ser un ID válido mayor a cero.
     * </p>
     */
    private int idProducto;
    
    /**
     * Cantidad de productos vendidos.
     * <p>
     * Debe ser un número entero positivo mayor a cero.
     * Se utiliza para calcular el total de la venta.
     * </p>
     */
    private int cantidad;
    
    /**
     * Precio unitario del producto al momento de la venta.
     * <p>
     * Utiliza BigDecimal para precisión en cálculos monetarios.
     * Debe ser mayor a cero. Se multiplica por la cantidad
     * para obtener el total.
     * </p>
     */
    private BigDecimal precioUnitario;
    
    /**
     * Total calculado de la venta.
     * <p>
     * Representa el monto total de la transacción.
     * Se calcula como precioUnitario × cantidad.
     * Debe ser mayor a cero.
     * </p>
     */
    private BigDecimal total;
    
    /**
     * Nombre del cliente (campo auxiliar para visualización).
     * <p>
     * Este campo no se persiste en la base de datos.
     * Se utiliza únicamente para mostrar información
     * legible en las interfaces de usuario.
     * </p>
     */
    private String nombreCliente;
    
    /**
     * Nombre del producto (campo auxiliar para visualización).
     * <p>
     * Este campo no se persiste en la base de datos.
     * Se utiliza únicamente para mostrar información
     * legible en las interfaces de usuario.
     * </p>
     */
    private String nombreProducto;
    
    /**
     * Constructor por defecto.
     * <p>
     * Crea una instancia vacía de Venta. Los campos
     * deben ser establecidos mediante los métodos setter
     * correspondientes.
     * </p>
     */
    public Venta() {
    }
    
    /**
     * Constructor para crear una nueva venta.
     * <p>
     * Este constructor se utiliza cuando se crea una venta
     * que aún no ha sido persistida en la base de datos.
     * El idVenta será asignado automáticamente por la BD.
     * </p>
     * 
     * @param fecha         Fecha de la venta (no puede ser nula)
     * @param idCliente     ID del cliente (debe ser mayor a cero)
     * @param idProducto    ID del producto (debe ser mayor a cero)
     * @param cantidad      Cantidad vendida (debe ser mayor a cero)
     * @param precioUnitario Precio unitario (debe ser mayor a cero)
     * @param total         Total de la venta (debe ser mayor a cero)
     * 
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Venta(LocalDate fecha, int idCliente, int idProducto, int cantidad, 
                 BigDecimal precioUnitario, BigDecimal total) {
        this.fecha = fecha;
        this.idCliente = idCliente;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }
    
    /**
     * Constructor completo para una venta existente.
     * <p>
     * Este constructor se utiliza cuando se recupera una venta
     * de la base de datos y se necesita crear la instancia
     * con todos sus datos, incluyendo el ID.
     * </p>
     * 
     * @param idVenta       Identificador único de la venta en la BD
     * @param fecha         Fecha de la venta
     * @param idCliente     ID del cliente
     * @param idProducto    ID del producto
     * @param cantidad      Cantidad vendida
     * @param precioUnitario Precio unitario
     * @param total         Total de la venta
     * 
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
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
    
    /**
     * Obtiene el identificador único de la venta.
     * 
     * @return el ID de la venta en la base de datos
     */
    public int getIdVenta() {
        return idVenta;
    }
    
    /**
     * Establece el identificador único de la venta.
     * <p>
     * Este método se utiliza principalmente cuando se recupera
     * una venta de la base de datos.
     * </p>
     * 
     * @param idVenta el ID único de la venta
     */
    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }
    
    /**
     * Obtiene la fecha de la venta.
     * 
     * @return la fecha de la venta, puede ser null si no se ha establecido
     */
    public LocalDate getFecha() {
        return fecha;
    }
    
    /**
     * Establece la fecha de la venta.
     * <p>
     * La fecha no puede ser nula ya que es un campo obligatorio
     * para registrar cuándo ocurrió la transacción.
     * </p>
     * 
     * @param fecha la fecha de la venta (no puede ser nula)
     * @throws IllegalArgumentException si la fecha es nula
     */
    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        this.fecha = fecha;
    }
    
    /**
     * Obtiene el identificador del cliente.
     * 
     * @return el ID del cliente que realizó la compra
     */
    public int getIdCliente() {
        return idCliente;
    }
    
    /**
     * Establece el identificador del cliente.
     * <p>
     * El ID del cliente debe ser mayor a cero ya que representa
     * una referencia válida a un cliente existente.
     * </p>
     * 
     * @param idCliente el ID del cliente (debe ser mayor a cero)
     * @throws IllegalArgumentException si el ID del cliente es menor o igual a cero
     */
    public void setIdCliente(int idCliente) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a cero");
        }
        this.idCliente = idCliente;
    }
    
    /**
     * Obtiene el identificador del producto.
     * 
     * @return el ID del producto vendido
     */
    public int getIdProducto() {
        return idProducto;
    }
    
    /**
     * Establece el identificador del producto.
     * <p>
     * El ID del producto debe ser mayor a cero ya que representa
     * una referencia válida a un producto existente.
     * </p>
     * 
     * @param idProducto el ID del producto (debe ser mayor a cero)
     * @throws IllegalArgumentException si el ID del producto es menor o igual a cero
     */
    public void setIdProducto(int idProducto) {
        if (idProducto <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser mayor a cero");
        }
        this.idProducto = idProducto;
    }
    
    /**
     * Obtiene la cantidad vendida.
     * 
     * @return la cantidad de productos vendidos
     */
    public int getCantidad() {
        return cantidad;
    }
    
    /**
     * Establece la cantidad vendida.
     * <p>
     * La cantidad debe ser mayor a cero ya que representa
     * una transacción válida de venta.
     * </p>
     * 
     * @param cantidad la cantidad vendida (debe ser mayor a cero)
     * @throws IllegalArgumentException si la cantidad es menor o igual a cero
     */
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
    }
    
    /**
     * Obtiene el precio unitario del producto.
     * 
     * @return el precio unitario al momento de la venta
     */
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    /**
     * Establece el precio unitario del producto.
     * <p>
     * El precio unitario debe ser mayor a cero ya que representa
     * el valor de cada unidad del producto vendido.
     * </p>
     * 
     * @param precioUnitario el precio unitario (debe ser mayor a cero)
     * @throws IllegalArgumentException si el precio unitario es nulo o menor o igual a cero
     */
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
        this.precioUnitario = precioUnitario;
    }
    
    /**
     * Obtiene el total de la venta.
     * 
     * @return el monto total de la transacción
     */
    public BigDecimal getTotal() {
        return total;
    }
    
    /**
     * Establece el total de la venta.
     * <p>
     * El total debe ser mayor a cero ya que representa
     * el monto total de la transacción.
     * </p>
     * 
     * @param total el total de la venta (debe ser mayor a cero)
     * @throws IllegalArgumentException si el total es nulo o menor o igual a cero
     */
    public void setTotal(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total debe ser mayor a cero");
        }
        this.total = total;
    }
    
    /**
     * Obtiene el nombre del cliente (campo auxiliar).
     * 
     * @return el nombre del cliente para visualización
     */
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    /**
     * Establece el nombre del cliente (campo auxiliar).
     * <p>
     * Este campo se utiliza únicamente para visualización
     * y no se persiste en la base de datos.
     * </p>
     * 
     * @param nombreCliente el nombre del cliente para mostrar
     */
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    /**
     * Obtiene el nombre del producto (campo auxiliar).
     * 
     * @return el nombre del producto para visualización
     */
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    /**
     * Establece el nombre del producto (campo auxiliar).
     * <p>
     * Este campo se utiliza únicamente para visualización
     * y no se persiste en la base de datos.
     * </p>
     * 
     * @param nombreProducto el nombre del producto para mostrar
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    /**
     * Calcula el total de la venta basado en cantidad y precio unitario.
     * <p>
     * Este método multiplica el precio unitario por la cantidad vendida
     * para obtener el total de la transacción. Si alguno de los valores
     * no está disponible, retorna cero.
     * </p>
     * 
     * @return el total calculado (precioUnitario × cantidad) o BigDecimal.ZERO si no hay datos válidos
     */
    public BigDecimal calcularTotal() {
        if (precioUnitario != null && cantidad > 0) {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Devuelve una representación en cadena de la venta.
     * <p>
     * Incluye todos los campos principales de la venta en un formato
     * legible para debugging y logging.
     * </p>
     * 
     * @return una cadena que representa la venta
     */
    @Override
    public String toString() {
        return String.format("Venta{id=%d, fecha=%s, cliente=%d, producto=%d, cantidad=%d, total=$%.2f}", 
                           idVenta, fecha, idCliente, idProducto, cantidad, total);
    }
    
    /**
     * Compara esta venta con otro objeto para determinar si son iguales.
     * <p>
     * Dos ventas se consideran iguales si tienen el mismo ID.
     * Esto es consistente con el hecho de que el ID es único en la BD.
     * </p>
     * 
     * @param obj el objeto a comparar con esta venta
     * @return true si los objetos son iguales, false en caso contrario
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Venta venta = (Venta) obj;
        return idVenta == venta.idVenta;
    }
    
    /**
     * Calcula el código hash para esta venta.
     * <p>
     * El código hash se basa únicamente en el ID de la venta,
     * ya que es el campo que determina la igualdad.
     * </p>
     * 
     * @return el código hash calculado para esta venta
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        return Objects.hash(idVenta);
    }
}