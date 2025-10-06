package com.sistemaventas.excepcion;

/**
 * Excepciones específicas para operaciones con Cliente
 * Maneja todos los errores relacionados con la entidad Cliente
 * 
 * @author Matt_
 * @version 1.0
 * @since 2025-01-01
 */
public class ClienteException extends SistemaVentasException {
    
    private Integer idCliente;
    
    /**
     * Constructor básico
     * @param mensaje Descripción del error
     */
    public ClienteException(String mensaje) {
        super("CLI-001", "CLIENTE", mensaje);
    }
    
    /**
     * Constructor con código de error específico
     * @param codigoError Código único del error
     * @param mensaje Descripción del error
     */
    public ClienteException(String codigoError, String mensaje) {
        super(codigoError, "CLIENTE", mensaje);
    }
    
    /**
     * Constructor con causa
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public ClienteException(String mensaje, Throwable causa) {
        super("CLI-002", "CLIENTE", mensaje);
        initCause(causa);
    }
    
    /**
     * Constructor con ID de cliente
     * @param codigoError Código del error
     * @param mensaje Descripción del error
     * @param idCliente ID del cliente relacionado
     */
    public ClienteException(String codigoError, String mensaje, Integer idCliente) {
        super(codigoError, "CLIENTE", mensaje);
        this.idCliente = idCliente;
    }
    
    /**
     * Obtiene el ID del cliente relacionado con el error
     * @return ID del cliente o null
     */
    public Integer getIdCliente() {
        return idCliente;
    }
    
    // ===== EXCEPCIONES ESPECÍFICAS =====
    
    /**
     * Se lanza cuando no se encuentra un cliente
     */
    public static class ClienteNoEncontradoException extends ClienteException {
        
        /**
         * Constructor por ID
         * @param id ID del cliente no encontrado
         */
        public ClienteNoEncontradoException(int id) {
            super("CLI-404", 
                String.format("No se encontró el cliente con ID: %d. Verifique que el ID sea correcto.", id), 
                id);
        }
        
        /**
         * Constructor por email
         * @param email Email del cliente no encontrado
         */
        public ClienteNoEncontradoException(String email) {
            super("CLI-404", 
                String.format("No se encontró el cliente con el email: %s", email));
        }
    }
    
    /**
     * Se lanza cuando se intenta crear un cliente con datos duplicados
     */
    public static class ClienteDuplicadoException extends ClienteException {
        
        private String campoDuplicado;
        
        /**
         * Constructor por email duplicado
         * @param email Email que ya existe
         */
        public ClienteDuplicadoException(String email) {
            super("CLI-409", 
                String.format("Ya existe un cliente registrado con el email: '%s'. " +
                    "Por favor, utilice un email diferente.", email));
            this.campoDuplicado = "email";
        }
        
        /**
         * Constructor genérico por campo duplicado
         * @param campo Nombre del campo duplicado
         * @param valor Valor duplicado
         */
        public ClienteDuplicadoException(String campo, String valor) {
            super("CLI-409", 
                String.format("Ya existe un cliente con el %s: '%s'", campo, valor));
            this.campoDuplicado = campo;
        }
        
        /**
         * Obtiene el campo que está duplicado
         * @return Nombre del campo
         */
        public String getCampoDuplicado() {
            return campoDuplicado;
        }
    }
    
    /**
     * Se lanza cuando se intenta eliminar un cliente que tiene ventas asociadas
     */
    public static class ClienteConVentasException extends ClienteException {
        
        private int cantidadVentas;
        
        /**
         * Constructor
         * @param idCliente ID del cliente
         * @param cantidadVentas Cantidad de ventas asociadas
         */
        public ClienteConVentasException(int idCliente, int cantidadVentas) {
            super("CLI-409", 
                String.format("No se puede eliminar el cliente (ID: %d) porque tiene %d venta(s) asociada(s). " +
                    "Primero debe eliminar o reasignar las ventas.", idCliente, cantidadVentas), 
                idCliente);
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
            return String.format("Este cliente tiene %d venta(s) registrada(s) y no puede ser eliminado. " +
                "Las ventas asociadas deben ser eliminadas primero.", cantidadVentas);
        }
    }
    
    /**
     * Se lanza cuando el email no tiene formato válido
     */
    public static class EmailInvalidoException extends ClienteException {
        
        private String emailInvalido;
        
        /**
         * Constructor
         * @param email Email con formato inválido
         */
        public EmailInvalidoException(String email) {
            super("CLI-VAL-001", 
                String.format("El formato del email '%s' no es válido. " +
                    "El email debe contener '@' y un dominio válido (ejemplo: usuario@dominio.com)", email));
            this.emailInvalido = email;
        }
        
        /**
         * Obtiene el email inválido
         * @return Email que causó el error
         */
        public String getEmailInvalido() {
            return emailInvalido;
        }
        
        @Override
        public String getMensajeUsuario() {
            return "Por favor, ingrese un email válido con el formato correcto (ejemplo: usuario@dominio.com)";
        }
    }
    
    /**
     * Se lanza cuando el teléfono no tiene formato válido
     */
    public static class TelefonoInvalidoException extends ClienteException {
        
        /**
         * Constructor
         * @param telefono Teléfono con formato inválido
         */
        public TelefonoInvalidoException(String telefono) {
            super("CLI-VAL-002", 
                String.format("El formato del teléfono '%s' no es válido", telefono));
        }
        
        @Override
        public String getMensajeUsuario() {
            return "Por favor, ingrese un número de teléfono válido";
        }
    }
    
    /**
     * Se lanza cuando falla la operación de guardado
     */
    public static class ClienteNoGuardadoException extends ClienteException {
        
        /**
         * Constructor básico
         */
        public ClienteNoGuardadoException() {
            super("CLI-500", "No se pudo guardar el cliente en la base de datos. Intente nuevamente.");
        }
        
        /**
         * Constructor con causa
         * @param causa Excepción que causó el error
         */
        public ClienteNoGuardadoException(Throwable causa) {
            super("CLI-500", "No se pudo guardar el cliente en la base de datos. Error: " + causa.getMessage());
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando falla la operación de actualización
     */
    public static class ClienteNoActualizadoException extends ClienteException {
        
        /**
         * Constructor con ID
         * @param idCliente ID del cliente que no se pudo actualizar
         */
        public ClienteNoActualizadoException(int idCliente) {
            super("CLI-500", 
                String.format("No se pudo actualizar el cliente (ID: %d). Intente nuevamente.", idCliente), 
                idCliente);
        }
        
        /**
         * Constructor con ID y causa
         * @param idCliente ID del cliente
         * @param causa Excepción que causó el error
         */
        public ClienteNoActualizadoException(int idCliente, Throwable causa) {
            super("CLI-500", 
                String.format("No se pudo actualizar el cliente (ID: %d). Error: %s", 
                    idCliente, causa.getMessage()), 
                idCliente);
            initCause(causa);
        }
    }
    
    /**
     * Se lanza cuando falla la operación de eliminación
     */
    public static class ClienteNoEliminadoException extends ClienteException {
        
        /**
         * Constructor con ID
         * @param idCliente ID del cliente que no se pudo eliminar
         */
        public ClienteNoEliminadoException(int idCliente) {
            super("CLI-500", 
                String.format("No se pudo eliminar el cliente (ID: %d). Intente nuevamente.", idCliente), 
                idCliente);
        }
        
        /**
         * Constructor con ID y causa
         * @param idCliente ID del cliente
         * @param causa Excepción que causó el error
         */
        public ClienteNoEliminadoException(int idCliente, Throwable causa) {
            super("CLI-500", 
                String.format("No se pudo eliminar el cliente (ID: %d). Error: %s", 
                    idCliente, causa.getMessage()), 
                idCliente);
            initCause(causa);
        }
    }
}