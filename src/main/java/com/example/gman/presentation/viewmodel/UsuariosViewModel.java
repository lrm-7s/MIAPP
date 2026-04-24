package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.AuthService;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class UsuariosViewModel {

    private final AuthService authService;

    private final ObservableList<Usuario> usuarios =
            FXCollections.observableArrayList();

    public UsuariosViewModel(AuthService authService) {
        this.authService = authService;
    }

    // ─── Roles para el ComboBox ──────────────────────────────────────
    public ObservableList<Rol> getRolesDisponibles() {
        return FXCollections.observableArrayList(Rol.values());
    }

    // ─── Lista observable para la tabla ─────────────────────────────
    public ObservableList<Usuario> getUsuarios() {
        return usuarios;
    }

    // ─── Registrar nuevo usuario ─────────────────────────────────────
    public void registrar(String username, String nombre,
                          String password, String confirmar,
                          Rol rol) throws Exception {

        if (!password.equals(confirmar))
            throw new IllegalArgumentException("Las contraseñas no coinciden.");

        authService.register(username, nombre, password, rol);
    }

    // ─── Cargar usuarios desde BD ────────────────────────────────────
    public void cargarUsuarios() throws Exception {
        usuarios.clear();
        List<Usuario> lista = authService.getAllUsuarios();
        usuarios.addAll(lista);
    }

    // ─── Actualizar usuario existente ────────────────────────────────
    /**
     * @param username  Identificador (no cambia)
     * @param nombre    Nuevo nombre completo
     * @param password  Nueva contraseña en texto plano, o {@code null} para no cambiarla
     * @param rol       Nuevo rol
     */
    public void actualizarUsuario(String username, String nombre,
                                  String password, Rol rol) throws Exception {
        authService.updateUser(username, nombre, password, rol);
    }

    // ─── Eliminar usuario ────────────────────────────────────────────
    public void eliminarUsuario(String username) throws Exception {
        authService.deleteUser(username);
    }

    // ─── Filtrar por texto ───────────────────────────────────────────
    public ObservableList<Usuario> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return usuarios;

        String lower = texto.toLowerCase();
        ObservableList<Usuario> filtrados = FXCollections.observableArrayList();

        for (Usuario u : usuarios) {
            if (u.getUsername().toLowerCase().contains(lower) ||
                    u.getNombre().toLowerCase().contains(lower)   ||
                    u.getRol().getDisplayName().toLowerCase().contains(lower)) {
                filtrados.add(u);
            }
        }
        return filtrados;
    }
}