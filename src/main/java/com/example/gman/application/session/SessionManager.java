package com.example.gman.application.session;

import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;

public class SessionManager {

    private Usuario currentUser;

    // ─── Gestión de sesión ──────────────────────────────────────────
    public void setCurrentUser(Usuario user) { this.currentUser = user; }
    public Usuario getCurrentUser()          { return currentUser; }
    public boolean isLoggedIn()              { return currentUser != null; }
    public void clearSession()               { currentUser = null; }

    // ─── Acceso al rol ───────────────────────────────────────────────
    public Rol getRol() {
        return currentUser != null ? currentUser.getRol() : null;
    }

    public String getRolDisplayName() {
        if (currentUser == null) return "";
        Rol rol = currentUser.getRol();
        return rol != null ? rol.getDisplayName() : "";
    }

    // ─── Info de sesión ──────────────────────────────────────────────
    public String getNombreUsuario() { return currentUser != null ? currentUser.getNombre()   : ""; }
    public String getUsername()      { return currentUser != null ? currentUser.getUsername() : ""; }

    // ─── Control de permisos ────────────────────────────────────────

    /** Acceso básico al módulo (ver) */
    public boolean tienePermiso(String modulo) {
        if (currentUser == null) return false;
        return currentUser.tienePermiso(modulo);
    }

    /** ¿Puede ver el módulo? */
    public boolean puedeVer(String modulo) {
        if (currentUser == null) return false;
        return getRol().puedeVer(modulo);
    }

    /** ¿Puede crear o editar en el módulo? */
    public boolean puedeEditar(String modulo) {
        if (currentUser == null) return false;
        return getRol().puedeEditar(modulo);
    }

    /** ¿Puede eliminar en el módulo? */
    public boolean puedeEliminar(String modulo) {
        if (currentUser == null) return false;
        return getRol().puedeEliminar(modulo);
    }

    /** ¿Es ADMIN? */
    public boolean esAdmin() {

        if (currentUser == null) return false;
        return Rol.ADMIN.equals(currentUser.getRol());
    }

    /** ¿Es SUPERVISOR o superior? */
    public boolean esSupervisorOSuperior() {
        if (currentUser == null) return false;
        Rol rol = currentUser.getRol();
        return Rol.ADMIN.equals(rol) || Rol.SUPERVISOR.equals(rol);
    }
}