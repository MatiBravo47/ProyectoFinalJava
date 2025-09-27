package com.sistemaventas.modelo;

import java.util.Objects;

/**
 * Clase modelo que representa un Cliente
 * Contiene toda la información de un cliente del sistema
 */

public class Cliente {
    
    public int idCliente;
    private String nombre;
    private String telefono;
    private String email;

    public Cliente() {
    }

    public Cliente(String nombre, String telefono, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public Cliente(int idCliente, String nombre, String telefono, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.idCliente;
        hash = 59 * hash + Objects.hashCode(this.nombre);
        hash = 59 * hash + Objects.hashCode(this.telefono);
        hash = 59 * hash + Objects.hashCode(this.email);
        return hash;
    }

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
        if (!Objects.equals(this.telefono, other.telefono)) {
            return false;
        }
        return Objects.equals(this.email, other.email);
    }
    
    
}
