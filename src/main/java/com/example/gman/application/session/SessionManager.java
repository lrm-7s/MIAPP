package com.example.gman.application.session;

import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;

public class SessionManager {

    private Usuario currentUser;

    // ─── Sesión ──────────────────────────────────────────────────────
    public void setCurrentUser(Usuario user) { this.currentUser = user;    }
    public Usuario getCurrentUser()          { return currentUser;         }
    public boolean isLoggedIn()              { return currentUser != null; }
    public void clearSession()               { currentUser = null;         }

    // ─── Info ────────────────────────────────────────────────────────
    public String getNombreUsuario() { return currentUser != null ? currentUser.getNombre()   : ""; }
    public String getUsername()      { return currentUser != null ? currentUser.getUsername() : ""; }
    public Rol    getRol()           { return currentUser != null ? currentUser.getRol()    : null; }

    public String getRolDisplayName() {
        if (currentUser == null) return "";
        Rol rol = currentUser.getRol();
        return rol != null ? rol.getDisplayName() : "";
    }
    // ─── Permisos — delegan al usuario ───────────────────────────────
    public boolean esAdmin() {
        return currentUser != null && currentUser.esAdmin();
    }
    public boolean puedeVer(String modulo) {
        return currentUser != null && currentUser.puedeVer(modulo);
    }

    public boolean puedeCrear(String modulo) {
        return currentUser != null && currentUser.puedeCrear(modulo);
    }

    public boolean puedeEditar(String modulo) {
        return currentUser != null && currentUser.puedeEditar(modulo);
    }

    public boolean puedeEliminar(String modulo) {
        return currentUser != null && currentUser.puedeEliminar(modulo);
    }

    public boolean esSupervisorOSuperior() {
        if (currentUser == null) return false;
        Rol rol = currentUser.getRol();
        return Rol.ADMIN.equals(rol) || Rol.SUPERVISOR.equals(rol);
    }
}