package com.sistemaventas.excepcion;

/**
 * Excepción para errores de validación de datos
 * Se lanza cuando los datos ingresados no cumplen con las reglas de validación
 * 
 * @author Matt_
 * @version 1.0
 * @since 2025-01-01
 */
public class ValidacionException extends SistemaVentasException {
    
    private String campo;
    private Object valorInvalido;
    private String restriccion;
    
    /**
     * Constructor básico
     * @param mensaje Descripción del error de validación
     */
    public ValidacionException(String mensaje) {
        super("VAL-001", "VALIDACION", mensaje);
    }
    
    /**
     * Constructor con campo específico
     * @param campo Nombre del campo que falló la validación
     * @param mensaje Descripción del error
     */
    public ValidacionException(String campo, String mensaje) {
        super("VAL-002", "VALIDACION", mensaje);
        this.campo = campo;
    }
    
    /**
     * Constructor con campo y valor inválido
     * @param campo Nombre del campo
     * @param mensaje Descripción del error
     * @param valorInvalido Valor que causó el error
     */
    public ValidacionException(String campo, String mensaje, Object valorInvalido) {
        super("VAL-003", "VALIDACION", mensaje);
        this.campo = campo;
        this.valorInvalido = valorInvalido;
    }
    
    /**
     * Constructor con causa
     * @param mensaje Descripción del error
     * @param causa Excepción original
     */
    public ValidacionException(String mensaje, Throwable causa) {
        super("VAL-004", "VALIDACION", mensaje);
        initCause(causa);
    }
    
    /**
     * Obtiene el campo que falló la validación
     * @return Nombre del campo o null
     */
    public String getCampo() {
        return campo;
    }
    
    /**
     * Obtiene el valor que causó el error
     * @return Valor inválido o null
     */
    public Object getValorInvalido() {
        return valorInvalido;
    }
    
    /**
     * Obtiene la restricción que se violó
     * @return Descripción de la restricción o null
     */
    public String getRestriccion() {
        return restriccion;
    }
    
    /**
     * Establece la restricción violada
     * @param restriccion Descripción de la restricción
     */
    public void setRestriccion(String restriccion) {
        this.restriccion = restriccion;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (campo != null) {
            sb.append(" | Campo: ").append(campo);
        }
        if (valorInvalido != null) {
            sb.append(" | Valor: ").append(valorInvalido);
        }
        if (restriccion != null) {
            sb.append(" | Restricción: ").append(restriccion);
        }
        return sb.toString();
    }
    
    // ===== MÉTODOS ESTÁTICOS PARA CREAR EXCEPCIONES COMUNES =====
    
    /**
     * Crea excepción de campo obligatorio
     * @param campo Nombre del campo obligatorio
     * @return ValidacionException configurada
     */
    public static ValidacionException campoObligatorio(String campo) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' es obligatorio y no puede estar vacío", campo));
    }
    
    /**
     * Crea excepción de campo nulo
     * @param campo Nombre del campo que no puede ser nulo
     * @return ValidacionException configurada
     */
    public static ValidacionException campoNulo(String campo) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no puede ser nulo", campo));
    }
    
    /**
     * Crea excepción de longitud inválida
     * @param campo Nombre del campo
     * @param longitudActual Longitud actual del valor
     * @param longitudMaxima Longitud máxima permitida
     * @return ValidacionException configurada
     */
    public static ValidacionException longitudInvalida(String campo, int longitudActual, int longitudMaxima) {
        ValidacionException ex = new ValidacionException(campo, 
            String.format("El campo '%s' excede la longitud máxima. Actual: %d, Máximo: %d", 
                campo, longitudActual, longitudMaxima));
        ex.setRestriccion(String.format("Max: %d caracteres", longitudMaxima));
        return ex;
    }
    
    /**
     * Crea excepción de longitud mínima
     * @param campo Nombre del campo
     * @param longitudActual Longitud actual del valor
     * @param longitudMinima Longitud mínima requerida
     * @return ValidacionException configurada
     */
    public static ValidacionException longitudMinima(String campo, int longitudActual, int longitudMinima) {
        ValidacionException ex = new ValidacionException(campo, 
            String.format("El campo '%s' no cumple con la longitud mínima. Actual: %d, Mínimo: %d", 
                campo, longitudActual, longitudMinima));
        ex.setRestriccion(String.format("Mín: %d caracteres", longitudMinima));
        return ex;
    }
    
    /**
     * Crea excepción de valor negativo
     * @param campo Nombre del campo
     * @param valor Valor negativo recibido
     * @return ValidacionException configurada
     */
    public static ValidacionException valorNegativo(String campo, Object valor) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no puede ser negativo. Valor: %s", campo, valor), valor);
    }
    
    /**
     * Crea excepción de valor cero
     * @param campo Nombre del campo
     * @return ValidacionException configurada
     */
    public static ValidacionException valorCero(String campo) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' debe ser mayor a cero", campo));
    }
    
    /**
     * Crea excepción de rango inválido
     * @param campo Nombre del campo
     * @param valor Valor recibido
     * @param min Valor mínimo permitido
     * @param max Valor máximo permitido
     * @return ValidacionException configurada
     */
    public static ValidacionException rangoInvalido(String campo, Object valor, Object min, Object max) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' está fuera del rango permitido. Valor: %s, Rango: [%s - %s]", 
                campo, valor, min, max), valor);
    }
    
    /**
     * Crea excepción de formato inválido
     * @param campo Nombre del campo
     * @param formatoEsperado Formato esperado
     * @return ValidacionException configurada
     */
    public static ValidacionException formatoInvalido(String campo, String formatoEsperado) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no tiene el formato correcto. Formato esperado: %s", 
                campo, formatoEsperado));
    }
    
    /**
     * Crea excepción de formato inválido con valor
     * @param campo Nombre del campo
     * @param formatoEsperado Formato esperado
     * @param valorInvalido Valor que no cumple el formato
     * @return ValidacionException configurada
     */
    public static ValidacionException formatoInvalido(String campo, String formatoEsperado, Object valorInvalido) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no tiene el formato correcto. Formato esperado: %s. Valor recibido: %s", 
                campo, formatoEsperado, valorInvalido), valorInvalido);
    }
    
    /**
     * Crea excepción de fecha inválida
     * @param campo Nombre del campo
     * @param razon Razón por la cual la fecha es inválida
     * @return ValidacionException configurada
     */
    public static ValidacionException fechaInvalida(String campo, String razon) {
        return new ValidacionException(campo, 
            String.format("La fecha en el campo '%s' no es válida: %s", campo, razon));
    }
    
    /**
     * Crea excepción de fecha futura
     * @param campo Nombre del campo
     * @return ValidacionException configurada
     */
    public static ValidacionException fechaFutura(String campo) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no puede ser una fecha futura", campo));
    }
    
    /**
     * Crea excepción de fecha antigua
     * @param campo Nombre del campo
     * @param limiteAnterior Fecha límite anterior
     * @return ValidacionException configurada
     */
    public static ValidacionException fechaAntigua(String campo, String limiteAnterior) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' no puede ser anterior a %s", campo, limiteAnterior));
    }
    
    /**
     * Crea excepción de valor inválido genérico
     * @param campo Nombre del campo
     * @param razon Razón del error
     * @return ValidacionException configurada
     */
    public static ValidacionException valorInvalido(String campo, String razon) {
        return new ValidacionException(campo, 
            String.format("El campo '%s' tiene un valor inválido: %s", campo, razon));
    }
}