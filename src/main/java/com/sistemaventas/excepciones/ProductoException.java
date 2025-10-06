package com.sistemaventas.excepcion;

import java.math.BigDecimal;

/**
 * Excepciones específicas para operaciones con Producto
 * Maneja todos los errores relacionados con la entidad Producto
 * 
 * @author Matt_
 * @version 1.0
 * @since 2025-01-01
 */
public class ProductoException extends SistemaVentasException {
    
    private Integer idProducto;
    
    /**
     * Constructor básico
     * @param mensaje Descripción del error
     */
    public ProductoException(String mensaje) {
        super("PROD-001", "PRODUCTO", mensaje);
    }
    
    /**
     * Constructor con código de error específico
     * @param codigoError Código único del error
     * @param mensaje Descripción del error
     */
    public ProductoException(String codigoError, String mensaje) {
        super(codigoError, "PRODUCTO", mensaje);
    }
    
    /**
     * Constructor con causa
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public ProductoException(String mensaje, Throwable causa) {
        super("PROD-002", "PRODUCTO", mensaje);
        initCause(causa);
    }
    
    /**
     * Constructor con ID de producto
     * @param codigoError Código del error
     * @param mensaje Descripción del error
     * @param idProducto ID del producto relacionado
     */
    public ProductoException(String codigoError, String mensaje, Integer idProducto) {
        super(codigoError, "PRODUCTO", mensaje);
        this.idProducto = idProducto;
    }
    
    /**
     * Obtiene el ID del producto relacionado con el error
     * @return ID del producto o null
     */
    public Integer getIdProducto() {
        return idProducto;
    }
    
    // ===== EXCEPCIONES ESPECÍFICAS =====
    
    /**
     * Se lanza cuando no se encuentra un producto
     */
    public static class ProductoNoEncontradoException extends ProductoException {
        
        /**
         * Constructor por ID
         * @param id ID del producto no encontrado
         */
        public ProductoNoEncontradoException(int id) {
            super("PROD-404", 
                String.format("No se encontró el producto con ID: %d. Verifique que el ID sea correcto.", id), 
                id);
        }
        
        /**
         * Constructor por nombre
         * @param nombre Nombre del producto no encontrado
         */
        public ProductoNoEncontradoException(String nombre) {
            super("PROD-404", 
                String.format("No se encontró el producto: %s", nombre));
        }
    }
    
    /**
     * Se lanza cuando se intenta crear un producto con nombre duplicado
     */
    public static class ProductoDuplicadoException extends ProductoException {
        
        private String nombreDuplicado;
        
        /**
         * Constructor
         * @param nombre Nombre que ya existe
         */
        public ProductoDuplicadoException(String nombre) {
            super("PROD-409", 
                String.format("Ya existe un producto registrado con el nombre: '%s'. " +
                    "Por favor, utilice un nombre diferente o actualice el producto existente.", nombre));
            this.nombreDuplicado = nombre;
        }
        
        /**
         * Obtiene el nombre duplicado
         * @return Nombre del producto duplicado
         */
        public String getNombreDuplicado() {
            return nombreDuplicado;
        }
        
        @Override
        public String getMensajeUsuario() {
            return String.format("Ya existe un producto con el nombre '%s'. " +
                "Elija un nombre diferente.", nombreDuplicado);
        }
    }
    
    /**
     * Se lanza cuando se intenta eliminar un producto que tiene ventas asociadas
     */
    public static class ProductoConVentasException extends ProductoException {
        
        private int cantidadVentas;
        
        /**
         * Constructor
         * @param idProducto ID del producto
         * @param cantidadVentas Cantidad de ventas asociadas
         */
        public ProductoConVentasException(int idProducto, int cantidadVentas) {
            super("PROD-409", 
                String.format("No se puede eliminar el producto (ID: %d) porque tiene %d venta(s) asociada(s). " +
                    "Las ventas deben ser eliminadas primero.", idProducto, cantidadVentas), 
                idProducto);
            this.cantidadVentas = cantidadVentas;
        }
        
        /**
         * Obtiene la cantidad de ventas asociadas
         * @return Número de ventas
         */
        public int getCantidadVentas() {
            return cantidadVentas;
        }
        
        @Override
        public String getMensajeUsuario() {
            return String.format("Este producto tiene %d venta(s) registrada(s) y no puede ser eliminado. " +
                "Elimine las ventas asociadas primero.", cantidadVentas);
        }
    }
    
    /**
     * Se lanza cuando no hay stock suficiente
     */
    public static class StockInsuficienteException extends ProductoException {
        
        private int stockDisponible;
        private int stockSolicitado;
        private String nombreProducto;
        
        /**
         * Constructor
         * @param idProducto ID del producto
         * @param nombreProducto Nombre del producto
         * @param stockDisponible Stock actual disponible
         * @param stockSolicitado Stock solicitado
         */
        public StockInsuficienteException(int idProducto, String nombreProducto, 
                                         int stockDisponible, int stockSolicitado) {
            super("PROD-STOCK-001", 
                String.format("Stock insuficiente para el producto '%s' (ID: %d). " +
                    "Stock disponible: %d unidades, Solicitado: %d unidades. " +
                    "Faltan %d unidades.", 
                    nombreProducto, idProducto, stockDisponible, stockSolicitado, 
                    (stockSolicitado - stockDisponible)), 
                idProducto);
            this.stockDisponible = stockDisponible;
            this.stockSolicitado = stockSolicitado;
            this.nombreProducto = nombreProducto;
        }
        
        /**
         * Obtiene el stock disponible
         * @return Stock actual
         */
        public int getStockDisponible() {
            return stockDisponible;
        }
        
        /**
         * Obtiene el stock solicitado
         * @return Stock que se intentó usar
         */
        public int getStockSolicitado() {
            return stockSolicitado;
        }
        
        /**
         * Obtiene el nombre del producto
         * @return Nombre del producto
         */
        public String getNombreProducto() {
            return nombreProducto;
        }
        
        @Override
        public String getMensajeUsuario() {
            return String.format("No hay suficiente stock del producto '%s'. " +
                "Disponible: %d, Necesita: %d. Faltan %d unidades.", 
                nombreProducto, stockDisponible, stockSolicitado, 
                (stockSolicitado - stockDisponible));
        }
    }
    
    /**
     * Se lanza cuando el stock es negativo
     */
    public static class StockNegativoException extends ProductoException {
        
        /**
         * Constructor básico
         */
        public StockNegativoException() {
            super("PROD-STOCK-002", 
                "El stock no puede ser negativo. Ingrese un valor mayor o igual a cero.");
        }
        
        /**
         * Constructor con valor
         * @param stockInvalido Valor de stock negativo recibido
         */
        public StockNegativoException(int stockInvalido) {
            super("PROD-STOCK-002", 
                String.format("El stock no puede ser negativo. Valor ingresado: %d", stockInvalido));
        }
    }
    
    /**
     * Se lanza cuando el stock excede el máximo permitido
     */
    public static class StockExcesivoException extends ProductoException {
        
        private int stockMaximo;
        
        /**
         * Constructor
         * @param stockIngresado Stock que se intentó ingresar
         * @param stockMaximo Stock máximo permitido
         */
        public StockExcesivoException(int stockIngresado, int stockMaximo) {
            super("PROD-STOCK-003", 
                String.format("El stock ingresado (%d) excede el máximo permitido (%d unidades).", 
                    stockIngresado, stockMaximo));
            this.stockMaximo = stockMaximo;
        }
        
        /**
         * Obtiene el stock máximo permitido
         * @return Stock máximo
         */
        public int getStockMaximo() {
            return stockMaximo;
        }
    }
    
    /**
     * Se lanza cuando el precio es inválido
     */
    public static class PrecioInvalidoException extends ProductoException {
        
        private BigDecimal precioInvalido;
        
        /**
         * Constructor con razón
         * @param razon Razón del error
         */
        public PrecioInvalidoException(String razon) {
            super("PROD-PRECIO-001", 
                String.format("Precio inválido: %s", razon));
        }
        
        /**
         * Constructor con precio y razón
         * @param precio Precio inválido
         * @param razon Razón del error
         */
        public PrecioInvalidoException(BigDecimal precio, String razon) {
            super("PROD-PRECIO-001", 
                String.format("El precio $%.2f es inválido: %s", precio, razon));
            this.precioInvalido = precio;
        }
        
        /**
         * Crea excepción de precio cero
         * @return Nueva excepción configurada
         */
        public static PrecioInvalidoException precioCero() {
            return new PrecioInvalidoException("El precio debe ser mayor a cero.");
        }
        
        /**
         * Crea excepción de precio negativo
         * @param precio Precio negativo
         * @return Nueva excepción configurada
         */
        public static PrecioInvalidoException precioNegativo(BigDecimal precio) {
            return new PrecioInvalidoException(precio, "El precio no puede ser negativo.");
        }
        
        /**
         * Crea excepción de precio excesivo
         * @param precio Precio excesivo
         * @param precioMaximo Precio máximo permitido
         * @return Nueva excepción configurada
         */
        public static PrecioInvalidoException precioExcesivo(BigDecimal precio, BigDecimal precioMaximo) {
            return new PrecioInvalidoException(precio, 
                String.format("El precio excede el máximo permitido de $%.2f", precioMaximo));
        }
        
        /**
         * Obtiene el precio inválido
         * @return Precio que causó el error
         */
        public BigDecimal getPrecioInvalido() {
            return precioInvalido;
        }
    }
    
    /**
     * Se lanza cuando falla la operación de guardado
     */
    public static class ProductoNoGuardadoException extends ProductoException {
        
        /**
         * Constructor básico
         */
        public ProductoNoGuardadoException() {
            super("PROD-500", "No se pudo guardar el producto en la base de datos. Intente nuevamente.");
        }
        
        /**
         * Constructor con causa
         * @param causa Excepción que causó el error
         */
        public ProductoNoGuardadoException(Throwable causa) {
            super("PROD-500", "No se pudo guardar el producto. Error: " + causa.getMessage());
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando falla la operación de actualización
     */
    public static class ProductoNoActualizadoException extends ProductoException {
        
        /**
         * Constructor con ID
         * @param idProducto ID del producto que no se pudo actualizar
         */
        public ProductoNoActualizadoException(int idProducto) {
            super("PROD-500", 
                String.format("No se pudo actualizar el producto (ID: %d). Intente nuevamente.", idProducto), 
                idProducto);
        }
        
        /**
         * Constructor con ID y causa
         * @param idProducto ID del producto
         * @param causa Excepción que causó el error
         */
        public ProductoNoActualizadoException(int idProducto, Throwable causa) {
            super("PROD-500", 
                String.format("No se pudo actualizar el producto (ID: %d). Error: %s", 
                    idProducto, causa.getMessage()), 
                idProducto);
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando falla la operación de eliminación
     */
    public static class ProductoNoEliminadoException extends ProductoException {
        
        /**
         * Constructor con ID
         * @param idProducto ID del producto que no se pudo eliminar
         */
        public ProductoNoEliminadoException(int idProducto) {
            super("PROD-500", 
                String.format("No se pudo eliminar el producto (ID: %d). Intente nuevamente.", idProducto), 
                idProducto);
        }
        
        /**
         * Constructor con ID y causa
         * @param idProducto ID del producto
         * @param causa Excepción que causó el error
         */
        public ProductoNoEliminadoException(int idProducto, Throwable causa) {
            super("PROD-500", 
                String.format("No se pudo eliminar el producto (ID: %d). Error: %s", 
                    idProducto, causa.getMessage()), 
                idProducto);
            initCause(causa);
        }
    }
}