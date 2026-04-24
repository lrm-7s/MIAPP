package com.example.gman.domain.repository;

import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;

import java.util.List;

public interface UsuarioRepository {

    Usuario      findByUsername(String username)                          throws Exception;
    boolean      checkPassword(String username, String plainPassword)     throws Exception;
    void         addUser(Usuario usuario, String plainPassword)           throws Exception;
    List<Usuario> getAllUsuarios()                                         throws Exception;

    // ── Nuevos métodos ───────────────────────────────────────────────
    /**
     * Actualiza nombre, rol y, opcionalmente, la contraseña de un usuario.
     *
     * @param username  Clave primaria (no cambia)
     * @param nombre    Nuevo nombre completo
     * @param password  Nueva contraseña en texto plano, o {@code null} para no modificarla
     * @param rol       Nuevo rol
     */
    void updateUser(String username, String nombre, String password, Rol rol) throws Exception;

    /**
     * Elimina permanentemente el usuario con el username indicado.
     */
    void deleteUser(String username) throws Exception;
}