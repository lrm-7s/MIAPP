package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import com.example.gman.domain.repository.UsuarioRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    // ─── Busca usuario por username ──────────────────────────────────
    @Override
    public Usuario findByUsername(String username) throws Exception {
        String sql = "SELECT username, nombre, rol FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getString("username"),
                        rs.getString("nombre"),
                        parseRol(rs.getString("rol"))
                );
            }
        }
        return null;
    }

    // ─── Verifica contraseña ─────────────────────────────────────────
    @Override
    public boolean checkPassword(String username, String plainPassword) throws Exception {
        String sql = "SELECT password FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return plainPassword.equals(rs.getString("password"));
            }
        }
        return false;
    }

    // ─── Registra nuevo usuario ──────────────────────────────────────
    @Override
    public void addUser(Usuario usuario, String plainPassword) throws Exception {
        String sql = "INSERT INTO usuarios(username, nombre, password, rol) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, plainPassword);
            stmt.setString(4, usuario.getRol().name());
            stmt.executeUpdate();
        }
    }

    // ─── Lista todos los usuarios ────────────────────────────────────
    @Override
    public List<Usuario> getAllUsuarios() throws Exception {
        String sql = "SELECT username, nombre, rol FROM usuarios ORDER BY nombre";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getString("username"),
                        rs.getString("nombre"),
                        parseRol(rs.getString("rol"))
                ));
            }
        }
        return lista;
    }

    // ─── Actualizar usuario ──────────────────────────────────────────
    @Override
    public void updateUser(String username, String nombre,
                           String password, Rol rol) throws Exception {

        // Si se proporciona nueva contraseña, también se actualiza
        String sql = (password != null && !password.isBlank())
                ? "UPDATE usuarios SET nombre = ?, password = ?, rol = ? WHERE username = ?"
                : "UPDATE usuarios SET nombre = ?, rol = ? WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (password != null && !password.isBlank()) {
                stmt.setString(1, nombre);
                stmt.setString(2, password);
                stmt.setString(3, rol.name());
                stmt.setString(4, username);
            } else {
                stmt.setString(1, nombre);
                stmt.setString(2, rol.name());
                stmt.setString(3, username);
            }

            int filas = stmt.executeUpdate();
            if (filas == 0)
                throw new Exception("No se encontró el usuario '" + username + "'.");
        }
    }

    // ─── Eliminar usuario ────────────────────────────────────────────
    @Override
    public void deleteUser(String username) throws Exception {
        String sql = "DELETE FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int filas = stmt.executeUpdate();
            if (filas == 0)
                throw new Exception("No se encontró el usuario '" + username + "'.");
        }
    }

    // ─── Helper interno ──────────────────────────────────────────────
    private Rol parseRol(String rolStr) {
        try {
            return Rol.valueOf(rolStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Rol desconocido: " + rolStr + " → TECNICO");
            return Rol.ADMIN;
        }
    }
}