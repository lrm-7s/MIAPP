package com.example.gman.domain.model;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String username;
    private String nombre;
    private Rol    rol; // solo etiqueta descriptiva

    // ─── Permisos individuales por módulo ────────────────────────────
    private final Map<String, PermisoModulo> permisos = new HashMap<>();

    // ─── Constructores ───────────────────────────────────────────────
    public Usuario(String username) {
        this.username = username;
        this.nombre   = username;
        this.rol      = Rol.CONSULTOR;
    }

    public Usuario(String username, String nombre, Rol rol) {
        this.username = username;
        this.nombre   = nombre;
        this.rol      = rol;
    }

    // ─── Getters / Setters ───────────────────────────────────────────
    public String getUsername() { return username; }
    public String getNombre()   { return nombre;   }
    public Rol    getRol()      { return rol;       }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setRol(Rol rol)          { this.rol    = rol;    }

    // ─── Gestión de permisos ─────────────────────────────────────────

    /** Carga un permiso de módulo (llamado al hacer login) */
    public void setPermiso(PermisoModulo permiso) {
        permisos.put(permiso.getModulo(), permiso);
    }

    /** Obtiene el permiso de un módulo, o uno vacío si no existe */
    public PermisoModulo getPermiso(String modulo) {
        return permisos.getOrDefault(modulo, new PermisoModulo(modulo));
    }

    // ─── Verificaciones rápidas ──────────────────────────────────────

    /** ADMIN siempre puede todo */
    public boolean esAdmin() {
        return Rol.ADMIN.equals(rol);
    }

    public boolean puedeVer(String modulo) {
        if (esAdmin()) return true;
        return getPermiso(modulo).isPuedeVer();
    }

    public boolean puedeCrear(String modulo) {
        if (esAdmin()) return true;
        return getPermiso(modulo).isPuedeCrear();
    }

    public boolean puedeEditar(String modulo) {
        if (esAdmin()) return true;
        return getPermiso(modulo).isPuedeEditar();
    }

    public boolean puedeEliminar(String modulo) {
        if (esAdmin()) return true;
        return getPermiso(modulo).isPuedeEliminar();
    }

    /** Compatibilidad con código existente */
    public boolean tienePermiso(String modulo) {
        return puedeVer(modulo);
    }

    @Override
    public String toString() {
        return nombre + " [" + (rol != null ? rol.getDisplayName() : "Sin rol") + "]";
    }
}