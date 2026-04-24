package com.example.gman.domain.model;

public class Usuario {
    private String username;
    private String nombre;
    private Rol rol;      // ← nuevo campo
    // ─── Constructor mínimo (compatibilidad con código existente) ───
    public Usuario(String username) {
        this.username = username;
        this.nombre   = username;
        this.rol      = Rol.CONSULTOR; // rol por defecto si no se especifica
    }

    // ─── Constructor completo ───────────────────────────────────────
    public Usuario(String username, String nombre, Rol rol) {
        this.username = username;
        this.nombre   = nombre;
        this.rol      = rol;
    }
    // ─── Getters ────────────────────────────────────────────────────
    public String getUsername() { return username; }
    public String getNombre()   { return nombre;   }
    public Rol    getRol()      { return rol;       }

    // ─── Setters ────────────────────────────────────────────────────
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRol(Rol rol)          { this.rol = rol;       }

    // ─── Utilidad ──────────────────────────────────────────────────
    /** Verifica si este usuario tiene permiso sobre un módulo */
    public boolean tienePermiso(String modulo) {
        if (rol == null) return false;
        return rol.tienePermiso(modulo);
    }

    @Override
    public String toString() {
        return nombre + " [" + (rol != null ? rol.getDisplayName() : "Sin rol") + "]";
    }
}
