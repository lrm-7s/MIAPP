package com.example.gman.application.service;

import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import com.example.gman.domain.repository.UsuarioRepository;

import java.util.List;

public class AuthService {

    private final UsuarioRepository usuarioRepo;

    public AuthService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    // ─── Login ───────────────────────────────────────────────────────
    // CORREGIDO: ya no silencia la excepción con RuntimeException.
    // El caller (LoginViewModel) la captura y muestra el mensaje real al usuario.
    public boolean login(String username, String password) throws Exception {
        return usuarioRepo.checkPassword(username, password);
    }

    // ─── Obtiene usuario completo con rol ────────────────────────────
    // CORREGIDO: propaga la excepción en vez de retornar null silenciosamente.
    public Usuario getUsuario(String username) throws Exception {
        return usuarioRepo.findByUsername(username);
    }

    // ─── Registro con rol y validaciones ────────────────────────────
    public void register(String username, String nombre,
                         String password, Rol rol) throws Exception {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("El usuario no puede estar vacío.");

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");

        if (password == null || password.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");

        if (rol == null)
            throw new IllegalArgumentException("Debe asignar un rol al usuario.");

        Usuario existente = usuarioRepo.findByUsername(username);
        if (existente != null)
            throw new IllegalArgumentException("El usuario '" + username + "' ya existe.");

        Usuario nuevo = new Usuario(username, nombre, rol);
        usuarioRepo.addUser(nuevo, password);
    }

    // ─── Lista todos los usuarios ────────────────────────────────────
    public List<Usuario> getAllUsuarios() throws Exception {
        return usuarioRepo.getAllUsuarios();
    }

    // ─── Actualizar usuario existente ────────────────────────────────
    /**
     * @param password Nueva contraseña en texto plano, o {@code null} para no cambiarla.
     */
    public void updateUser(String username, String nombre,
                           String password, Rol rol) throws Exception {

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");

        if (rol == null)
            throw new IllegalArgumentException("Debe asignar un rol al usuario.");

        usuarioRepo.updateUser(username, nombre, password, rol);
    }

    // ─── Eliminar usuario ────────────────────────────────────────────
    public void deleteUser(String username) throws Exception {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username inválido.");

        usuarioRepo.deleteUser(username);
    }

    // ─── Verifica permiso de un usuario sobre un módulo ─────────────
    // CORREGIDO: este método no debería usarse para verificar la sesión activa.
    // Para eso usa SessionManager directamente. Este método queda solo para
    // consultas administrativas sobre otros usuarios (ej: GestionUsuarios).
    public boolean tienePermiso(String username, String modulo) throws Exception {
        Usuario u = usuarioRepo.findByUsername(username);
        if (u == null) return false;
        return u.tienePermiso(modulo);
    }
}