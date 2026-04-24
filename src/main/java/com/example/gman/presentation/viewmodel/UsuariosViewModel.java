package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.AuthService;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class UsuariosViewModel {

    private final AuthService authService;

    // Lista observable para la tabla de usuarios
    private final ObservableList<Usuario> usuarios =
            FXCollections.observableArrayList();

    public UsuariosViewModel(AuthService authService) {
        this.authService = authService;
    }

    // ─── Roles disponibles para el ComboBox ─────────────────────────
    public ObservableList<Rol> getRolesDisponibles() {
        return FXCollections.observableArrayList(Rol.values());
    }

    // ─── Lista de usuarios para la tabla ────────────────────────────
    public ObservableList<Usuario> getUsuarios() {
        return usuarios;
    }

    // ─── Registrar nuevo usuario ─────────────────────────────────────
    /**
     * Llama al AuthService para registrar.
     * Lanza excepción con mensaje legible si algo falla.
     */
    public void registrar(String username, String nombre,
                          String password, String confirmar,
                          Rol rol) throws Exception {

        // Validación de contraseñas coincidentes (regla de UI)
        if (!password.equals(confirmar)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        // El resto de validaciones las maneja AuthService
        authService.register(username, nombre, password, rol);
    }

    // ─── Cargar todos los usuarios desde la BD ───────────────────────
    public void cargarUsuarios() throws Exception {
        usuarios.clear();
        List<Usuario> lista = authService.getAllUsuarios();
        usuarios.addAll(lista);
    }

    // ─── Filtrar usuarios por texto de búsqueda ──────────────────────
    public ObservableList<Usuario> filtrar(String texto) {
        if (texto == null || texto.isBlank()) {
            return usuarios;
        }
        String lower = texto.toLowerCase();
        ObservableList<Usuario> filtrados =
                FXCollections.observableArrayList();
        for (Usuario u : usuarios) {
            if (u.getUsername().toLowerCase().contains(lower) ||
                    u.getNombre().toLowerCase().contains(lower) ||
                    u.getRol().getDisplayName().toLowerCase().contains(lower)) {
                filtrados.add(u);
            }
        }
        return filtrados;
    }
}