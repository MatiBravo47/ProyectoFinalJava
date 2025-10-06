package com.sistemaventas.modelo;

import java.util.Objects;

/**
 * Clase modelo que representa un Cliente en el sistema de gestión de ventas.
 * <p>
 * Esta clase encapsula toda la información relacionada con un cliente,
 * incluyendo datos de identificación personal y contacto. Implementa
 * validaciones para asegurar la integridad de los datos del cliente.
 * </p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li>Identificador único autoincremental</li>
 *   <li>Validación de DNI (8 dígitos numéricos)</li>
 *   <li>Validación de teléfono (10 dígitos numéricos)</li>
 *   <li>Validación de email (formato estándar)</li>
 *   <li>Implementación de equals() y hashCode() para comparaciones</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see java.util.Objects
 */
public class Cliente {
    
    /**
     * Identificador único del cliente en la base de datos.
     * <p>
     * Este campo es autoincremental y se asigna automáticamente
     * cuando se persiste el cliente por primera vez.
     * </p>
     */
    public int idCliente;
    
    /**
     * Nombre completo del cliente.
     * <p>
     * Campo obligatorio que debe contener el nombre y apellido
     * del cliente. No puede ser nulo ni vacío.
     * </p>
     */
    private String nombre;
    
    /**
     * Documento Nacional de Identidad del cliente.
     * <p>
     * Debe contener exactamente 8 dígitos numéricos.
     * Este campo es único en el sistema y obligatorio.
     * </p>
     */
    private String dni;
    
    /**
     * Número de teléfono del cliente.
     * <p>
     * Debe contener exactamente 10 dígitos numéricos.
     * Campo obligatorio para contacto con el cliente.
     * </p>
     */
    private String telefono;
    
    /**
     * Dirección de correo electrónico del cliente.
     * <p>
     * Debe tener un formato válido de email (usuario@dominio.com).
     * Este campo es único en el sistema y obligatorio.
     * </p>
     */
    private String email;

    /**
     * Constructor por defecto.
     * <p>
     * Crea una instancia vacía de Cliente. Los campos
     * deben ser establecidos mediante los métodos setter
     * correspondientes.
     * </p>
     */
    public Cliente() {
    }

    /**
     * Constructor para crear un nuevo cliente.
     * <p>
     * Este constructor se utiliza cuando se crea un cliente
     * que aún no ha sido persistido en la base de datos.
     * El idCliente será asignado automáticamente por la BD.
     * </p>
     * 
     * @param nombre    Nombre completo del cliente (no puede ser nulo)
     * @param dni       DNI del cliente, debe tener 8 dígitos (no puede ser nulo)
     * @param telefono  Teléfono del cliente, debe tener 10 dígitos (no puede ser nulo)
     * @param email     Email del cliente con formato válido (no puede ser nulo)
     * 
     * @throws IllegalArgumentException si algún parámetro es nulo o inválido
     */
    public Cliente(String nombre, String dni, String telefono, String email) {
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    /**
     * Constructor completo para un cliente existente.
     * <p>
     * Este constructor se utiliza cuando se recupera un cliente
     * de la base de datos y se necesita crear la instancia
     * con todos sus datos, incluyendo el ID.
     * </p>
     * 
     * @param idCliente Identificador único del cliente en la BD
     * @param nombre    Nombre completo del cliente
     * @param dni       DNI del cliente (8 dígitos)
     * @param telefono  Teléfono del cliente (10 dígitos)
     * @param email     Email del cliente con formato válido
     * 
     * @throws IllegalArgumentException si algún parámetro es nulo o inválido
     */
    public Cliente(int idCliente, String nombre, String dni, String telefono, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    /**
     * Obtiene el identificador único del cliente.
     * 
     * @return el ID del cliente en la base de datos
     */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el identificador único del cliente.
     * <p>
     * Este método se utiliza principalmente cuando se recupera
     * un cliente de la base de datos.
     * </p>
     * 
     * @param idCliente el ID único del cliente
     */
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    /**
     * Obtiene el nombre completo del cliente.
     * 
     * @return el nombre del cliente, puede ser null si no se ha establecido
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre completo del cliente.
     * 
     * @param nombre el nombre del cliente (no debe ser nulo ni vacío)
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el DNI del cliente.
     * 
     * @return el DNI del cliente, puede ser null si no se ha establecido
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del cliente.
     * <p>
     * El DNI debe contener exactamente 8 dígitos numéricos.
     * </p>
     * 
     * @param dni el DNI del cliente (debe tener 8 dígitos)
     * @throws IllegalArgumentException si el DNI no tiene 8 dígitos o es nulo
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el número de teléfono del cliente.
     * 
     * @return el teléfono del cliente, puede ser null si no se ha establecido
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del cliente.
     * <p>
     * El teléfono debe contener exactamente 10 dígitos numéricos.
     * </p>
     * 
     * @param telefono el teléfono del cliente (debe tener 10 dígitos)
     * @throws IllegalArgumentException si el teléfono no tiene 10 dígitos o es nulo
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene la dirección de email del cliente.
     * 
     * @return el email del cliente, puede ser null si no se ha establecido
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece la dirección de email del cliente.
     * <p>
     * El email debe tener un formato válido (usuario@dominio.com).
     * </p>
     * 
     * @param email el email del cliente (debe tener formato válido)
     * @throws IllegalArgumentException si el email no tiene formato válido o es nulo
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Calcula el código hash para este cliente.
     * <p>
     * El código hash se basa en todos los campos del cliente:
     * idCliente, nombre, dni, telefono y email. Esto asegura que
     * dos clientes con los mismos datos tengan el mismo hash.
     * </p>
     * 
     * @return el código hash calculado para este cliente
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.idCliente;
        hash = 59 * hash + Objects.hashCode(this.nombre);
        hash = 59 * hash + Objects.hashCode(this.dni);
        hash = 59 * hash + Objects.hashCode(this.telefono);
        hash = 59 * hash + Objects.hashCode(this.email);
        return hash;
    }

    /**
     * Compara este cliente con otro objeto para determinar si son iguales.
     * <p>
     * Dos clientes se consideran iguales si tienen:
     * <ul>
     *   <li>El mismo ID de cliente</li>
     *   <li>El mismo nombre</li>
     *   <li>El mismo DNI</li>
     *   <li>El mismo teléfono</li>
     *   <li>El mismo email</li>
     * </ul>
     * </p>
     * 
     * @param obj el objeto a comparar con este cliente
     * @return true si los objetos son iguales, false en caso contrario
     * @see #hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cliente other = (Cliente) obj;
        if (this.idCliente != other.idCliente) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.dni, other.dni)) {
            return false;
        }
        if (!Objects.equals(this.telefono, other.telefono)) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }
    
    
}
