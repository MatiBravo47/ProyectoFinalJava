package com.sistemaventas.excepcion;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Excepciones específicas para operaciones con Venta
 * Maneja todos los errores relacionados con la entidad Venta
 * 
 * @author Matt_
 * @version 1.0
 * @since 2025-01-01
 */
public class VentaException extends SistemaVentasException {
    
    private Integer idVenta;
    
    /**
     * Constructor básico
     * @param mensaje Descripción del error
     */
    public VentaException(String mensaje) {
        super("VTA-001", "VENTA", mensaje);
    }
    
    /**
     * Constructor con código de error específico
     * @param codigoError Código único del error
     * @param mensaje Descripción del error
     */
    public VentaException(String codigoError, String mensaje) {
        super(codigoError, "VENTA", mensaje);
    }
    
    /**
     * Constructor con causa
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public VentaException(String mensaje, Throwable causa) {
        super("VTA-002", "VENTA", mensaje);
        initCause(causa);
    }
    
    /**
     * Constructor con ID de venta
     * @param codigoError Código del error
     * @param mensaje Descripción del error
     * @param idVenta ID de la venta relacionada
     */
    public VentaException(String codigoError, String mensaje, Integer idVenta) {
        super(codigoError, "VENTA", mensaje);
        this.idVenta = idVenta;
    }
    
    /**
     * Obtiene el ID de la venta relacionada con el error
     * @return ID de la venta o null
     */
    public Integer getIdVenta() {
        return idVenta;
    }
    
    // ===== EXCEPCIONES ESPECÍFICAS =====
    
    /**
     * Se lanza cuando no se encuentra una venta
     */
    public static class VentaNoEncontradaException extends VentaException {
        
        /**
         * Constructor
         * @param id ID de la venta no encontrada
         */
        public VentaNoEncontradaException(int id) {
            super("VTA-404", 
                String.format("No se encontró la venta con ID: %d. Verifique que el ID sea correcto.", id), 
                id);
        }
    }
    
    /**
     * Se lanza cuando la fecha de la venta es inválida
     */
    public static class FechaVentaInvalidaException extends VentaException {
        
        private LocalDate fechaInvalida;
        
        /**
         * Constructor con razón
         * @param razon Razón por la que la fecha es inválida
         */
        public FechaVentaInvalidaException(String razon) {
            super("VTA-VAL-001", 
                String.format("Fecha de venta inválida: %s", razon));
        }
        
        /**
         * Constructor con fecha y razón
         * @param fecha Fecha inválida
         * @param razon Razón del error
         */
        public FechaVentaInvalidaException(LocalDate fecha, String razon) {
            super("VTA-VAL-001", 
                String.format("La fecha %s no es válida: %s", fecha, razon));
            this.fechaInvalida = fecha;
        }
        
        /**
         * Crea excepción de fecha futura
         * @param fecha Fecha futura
         * @return Nueva excepción configurada
         */
        public static FechaVentaInvalidaException fechaFutura(LocalDate fecha) {
            return new FechaVentaInvalidaException(fecha, 
                "No se pueden registrar ventas con fecha futura. Use la fecha actual o anterior.");
        }
        
        /**
         * Crea excepción de fecha antigua
         * @param fecha Fecha antigua
         * @param fechaLimite Fecha límite anterior permitida
         * @return Nueva excepción configurada
         */
        public static FechaVentaInvalidaException fechaAntigua(LocalDate fecha, LocalDate fechaLimite) {
            return new FechaVentaInvalidaException(fecha, 
                String.format("La fecha es demasiado antigua. No se permiten ventas anteriores a %s", fechaLimite));
        }
        
        /**
         * Obtiene la fecha inválida
         * @return Fecha que causó el error
         */
        public LocalDate getFechaInvalida() {
            return fechaInvalida;
        }
    }
    
    /**
     * Se lanza cuando la cantidad es inválida
     */
    public static class CantidadInvalidaException extends VentaException {
        
        private int cantidad;
        
        /**
         * Constructor con razón
         * @param razon Razón del error
         */
        public CantidadInvalidaException(String razon) {
            super("VTA-VAL-002", String.format("Cantidad inválida: %s", razon));
        }
        
        /**
         * Constructor con cantidad y razón
         * @param cantidad Cantidad inválida
         * @param razon Razón del error
         */
        public CantidadInvalidaException(int cantidad, String razon) {
            super("VTA-VAL-002", 
                String.format("La cantidad %d es inválida: %s", cantidad, razon));
            this.cantidad = cantidad;
        }
        
        /**
         * Crea excepción de cantidad cero
         * @return Nueva excepción configurada
         */
        public static CantidadInvalidaException cantidadCero() {
            return new CantidadInvalidaException("La cantidad debe ser mayor a cero.");
        }
        
        /**
         * Crea excepción de cantidad negativa
         * @param cantidad Cantidad negativa
         * @return Nueva excepción configurada
         */
        public static CantidadInvalidaException cantidadNegativa(int cantidad) {
            return new CantidadInvalidaException(cantidad, "La cantidad no puede ser negativa.");
        }
        
        /**
         * Crea excepción de cantidad excesiva
         * @param cantidad Cantidad excesiva
         * @param cantidadMaxima Cantidad máxima permitida
         * @return Nueva excepción configurada
         */
        public static CantidadInvalidaException cantidadExcesiva(int cantidad, int cantidadMaxima) {
            return new CantidadInvalidaException(cantidad, 
                String.format("La cantidad excede el máximo permitido de %d unidades.", cantidadMaxima));
        }
        
        /**
         * Obtiene la cantidad inválida
         * @return Cantidad que causó el error
         */
        public int getCantidad() {
            return cantidad;
        }
    }
    
    /**
     * Se lanza cuando el total calculado no coincide
     */
    public static class TotalInvalidoException extends VentaException {
        
        private BigDecimal totalCalculado;
        private BigDecimal totalEsperado;
        
        /**
         * Constructor
         * @param totalCalculado Total que se calculó
         * @param totalEsperado Total que se esperaba
         */
        public TotalInvalidoException(BigDecimal totalCalculado, BigDecimal totalEsperado) {
            super("VTA-VAL-003", 
                String.format("El total calculado ($%.2f) no coincide con el total esperado ($%.2f). " +
                    "Verifique los datos de la venta.", totalCalculado, totalEsperado));
            this.totalCalculado = totalCalculado;
            this.totalEsperado = totalEsperado;
        }
        
        /**
         * Obtiene el total calculado
         * @return Total calculado
         */
        public BigDecimal getTotalCalculado() {
            return totalCalculado;
        }
        
        /**
         * Obtiene el total esperado
         * @return Total esperado
         */
        public BigDecimal getTotalEsperado() {
            return totalEsperado;
        }
    }
    
    /**
     * Se lanza cuando se intenta realizar una venta sin cliente
     */
    public static class ClienteRequeridoException extends VentaException {
        
        /**
         * Constructor
         */
        public ClienteRequeridoException() {
            super("VTA-VAL-004", 
                "Debe seleccionar un cliente para registrar la venta. " +
                "Las ventas no pueden realizarse sin cliente asociado.");
        }
        
        @Override
        public String getMensajeUsuario() {
            return "Por favor, seleccione un cliente antes de registrar la venta.";
        }
    }
    
    /**
     * Se lanza cuando se intenta realizar una venta sin producto
     */
    public static class ProductoRequeridoException extends VentaException {
        
        /**
         * Constructor
         */
        public ProductoRequeridoException() {
            super("VTA-VAL-005", 
                "Debe seleccionar un producto para registrar la venta. " +
                "Las ventas no pueden realizarse sin producto asociado.");
        }
        
        @Override
        public String getMensajeUsuario() {
            return "Por favor, seleccione un producto antes de registrar la venta.";
        }
    }
    
    /**
     * Se lanza cuando falla la operación de guardado
     */
    public static class VentaNoGuardadaException extends VentaException {
        
        /**
         * Constructor básico
         */
        public VentaNoGuardadaException() {
            super("VTA-500", "No se pudo registrar la venta en la base de datos. Intente nuevamente.");
        }
        
        /**
         * Constructor con causa
         * @param causa Excepción que causó el error
         */
        public VentaNoGuardadaException(Throwable causa) {
            super("VTA-500", "No se pudo registrar la venta. Error: " + causa.getMessage());
            initCause(causa);
        }
        
        /**
         * Constructor con razón adicional
         * @param razonAdicional Razón adicional del error
         */
        public VentaNoGuardadaException(String razonAdicional) {
            super("VTA-500", 
                String.format("No se pudo registrar la venta: %s", razonAdicional));
        }
    }
    
    /**
     * Se lanza cuando falla la operación de actualización
     */
    public static class VentaNoActualizadaException extends VentaException {
        
        /**
         * Constructor con ID
         * @param idVenta ID de la venta que no se pudo actualizar
         */
        public VentaNoActualizadaException(int idVenta) {
            super("VTA-500", 
                String.format("No se pudo actualizar la venta (ID: %d). Intente nuevamente.", idVenta), 
                idVenta);
        }
        
        /**
         * Constructor con ID y causa
         * @param idVenta ID de la venta
         * @param causa Excepción que causó el error
         */
        public VentaNoActualizadaException(int idVenta, Throwable causa) {
            super("VTA-500", 
                String.format("No se pudo actualizar la venta (ID: %d). Error: %s", 
                    idVenta, causa.getMessage()), 
                idVenta);
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando falla la operación de eliminación
     */
    public static class VentaNoEliminadaException extends VentaException {
        
        /**
         * Constructor con ID
         * @param idVenta ID de la venta que no se pudo eliminar
         */
        public VentaNoEliminadaException(int idVenta) {
            super("VTA-500", 
                String.format("No se pudo eliminar la venta (ID: %d). Intente nuevamente.", idVenta), 
                idVenta);
        }
        
        /**
         * Constructor con ID y causa
         * @param idVenta ID de la venta
         * @param causa Excepción que causó el error
         */
        public VentaNoEliminadaException(int idVenta, Throwable causa) {
            super("VTA-500", 
                String.format("No se pudo eliminar la venta (ID: %d). Error: %s", 
                    idVenta, causa.getMessage()), 
                idVenta);
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando hay conflictos en la actualización de stock durante una venta
     */
    public static class ConflictoStockException extends VentaException {
        
        private int idProducto;
        private String nombreProducto;
        
        /**
         * Constructor
         * @param idProducto ID del producto con conflicto
         * @param nombreProducto Nombre del producto
         * @param detalle Detalle del conflicto
         */
        public ConflictoStockException(int idProducto, String nombreProducto, String detalle) {
            super("VTA-STOCK-001", 
                String.format("Error al actualizar stock del producto '%s' (ID: %d): %s", 
                    nombreProducto, idProducto, detalle));
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
        }
        
        /**
         * Obtiene el ID del producto con conflicto
         * @return ID del producto
         */
        public int getIdProducto() {
            return idProducto;
        }
        
        /**
         * Obtiene el nombre del producto con conflicto
         * @return Nombre del producto
         */
        public String getNombreProducto() {
            return nombreProducto;
        }
    }
}