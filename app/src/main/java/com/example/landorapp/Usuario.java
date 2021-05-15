package com.example.landorapp;

import java.io.Serializable;

public class Usuario implements Serializable {
    private boolean isManager;
    private String nombreCompleto;
    private String username;
    private String telefono;
    private String email;

    public Usuario(boolean isManager, String nombreCompleto, String username, String telefono, String email) {
        this.isManager = isManager;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.telefono=telefono;

        this.email=email;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }
}
