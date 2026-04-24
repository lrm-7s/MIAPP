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
    public boolean login(String username, String password) {
        try {
            return usuarioRepo.checkPassword(username, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ─── Obtiene usuario completo con rol ────────────────────────────
    public Usuario getUsuario(String username) {
        try {
            return usuarioRepo.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─── Registro con rol y validaciones ────────────────────────────
    public void register(String username, String nombre,
                         String password, Rol rol) throws Exception {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("El usuario no puede estar vacío.");

        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");

        if (password == null || password.length() < 4)
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
    public boolean tienePermiso(String username, String modulo) {
        Usuario u = getUsuario(username);
        if (u == null) return false;
        return u.tienePermiso(modulo);
    }
}