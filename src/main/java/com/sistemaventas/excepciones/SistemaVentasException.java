package com.sistemaventas.excepcion;

/**
 * Excepción base para el sistema de ventas
 * Todas las excepciones personalizadas del sistema heredan de esta clase
 * 
 * @author Matt_
 * @version 1.0
 * @since 2025-01-01
 */
public class SistemaVentasException extends Exception {
    
    private String codigoError;
    private String categoria;
    
    /**
     * Constructor básico con mensaje
     * @param mensaje Descripción del error
     */
    public SistemaVentasException(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa
     * @param mensaje Descripción del error
     * @param causa Excepción que causó este error
     */
    public SistemaVentasException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
    
    /**
     * Constructor con código de error y mensaje
     * @param codigoError Código único del error (ej: CLI-001)
     * @param mensaje Descripción del error
     */
    public SistemaVentasException(String codigoError, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
    }
    
    /**
     * Constructor con código de error, mensaje y causa
     * @param codigoError Código único del error
     * @param mensaje Descripción del error
     * @param causa Excepción que causó este error
     */
    public SistemaVentasException(String codigoError, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = codigoError;
    }
    
    /**
     * Constructor completo con código, categoría y mensaje
     * @param codigoError Código único del error
     * @param categoria Categoría del error (CLIENTE, PRODUCTO, VENTA, etc.)
     * @param mensaje Descripción del error
     */
    public SistemaVentasException(String codigoError, String categoria, String mensaje) {
        super(mensaje);
        this.codigoError = codigoError;
        this.categoria = categoria;
    }
    
    /**
     * Obtiene el código de error
     * @return Código del error o null si no fue asignado
     */
    public String getCodigoError() {
        return codigoError;
    }
    
    /**
     * Obtiene la categoría del error
     * @return Categoría del error o null si no fue asignada
     */
    public String getCategoria() {
        return categoria;
    }
    
    /**
     * Representación en string del error con código y categoría
     * @return String formateado con toda la información
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (codigoError != null) {
            sb.append("[").append(codigoError).append("]");
        }
        if (categoria != null) {
            sb.append(" [").append(categoria).append("]");
        }
        if (sb.length() > 0) {
            sb.append(" ");
        }
        sb.append(getMessage());
        return sb.toString();
    }
    
    /**
     * Obtiene un mensaje amigable para mostrar al usuario
     * @return Mensaje simplificado para el usuario final
     */
    public String getMensajeUsuario() {
        return getMessage();
    }
    
    /**
     * Obtiene un mensaje técnico detallado para logs
     * @return Mensaje técnico completo con código y categoría
     */
    public String getMensajeTecnico() {
        return toString();
    }
}