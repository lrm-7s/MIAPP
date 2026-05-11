package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.PermisoModulo;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import com.example.gman.domain.repository.UsuarioRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    // ─── Busca usuario por username + carga sus permisos ─────────────
    @Override
    public Usuario findByUsername(String username) throws Exception {
        String sql = "SELECT username, nombre, rol FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getString("username"),
                        rs.getString("nombre"),
                        Rol.parse(rs.getString("rol"))
                );
                cargarPermisos(usuario, conn);
                return usuario;
            }
        }
        return null;
    }

    // ─── Verifica contraseña (soporta BCrypt y texto plano legacy) ───
    @Override
    public boolean checkPassword(String username, String plainPassword) throws Exception {
        String sql = "SELECT password FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String stored = rs.getString("password");
                // Si está hasheado con BCrypt, usar BCrypt.checkpw
                if (stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$"))) {
                    return BCrypt.checkpw(plainPassword, stored);
                } else {
                    // Fallback legacy: comparación directa (texto plano)
                    return plainPassword.equals(stored);
                }
            }
        }
        return false;
    }

    // ─── Registra nuevo usuario (contraseña siempre hasheada) ────────
    @Override
    public void addUser(Usuario usuario, String plainPassword) throws Exception {
        String sql = "INSERT INTO usuarios(username, nombre, password, rol) VALUES (?,?,?,?)";
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNombre());
            stmt.setString(3, hashed);  // ← siempre guarda el hash
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
                Usuario u = new Usuario(
                        rs.getString("username"),
                        rs.getString("nombre"),
                        Rol.parse(rs.getString("rol"))
                );
                cargarPermisos(u, conn);
                lista.add(u);
            }
        }
        return lista;
    }

    // ─── Actualizar usuario ──────────────────────────────────────────
    @Override
    public void updateUser(String username, String nombre,
                           String password, Rol rol) throws Exception {
        String sql = (password != null && !password.isBlank())
                ? "UPDATE usuarios SET nombre=?, password=?, rol=? WHERE username=?"
                : "UPDATE usuarios SET nombre=?, rol=? WHERE username=?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (password != null && !password.isBlank()) {
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                stmt.setString(1, nombre);
                stmt.setString(2, hashed);  // ← también hashea al actualizar
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

    // ─── Guardar permisos de un usuario ──────────────────────────────
    public void guardarPermisos(String username,
                                List<PermisoModulo> permisos) throws Exception {
        String sql = """
            INSERT INTO usuario_permisos
                (username, modulo, puede_ver, puede_crear, puede_editar, puede_eliminar)
            VALUES (?,?,?,?,?,?)
            ON CONFLICT(username, modulo) DO UPDATE SET
                puede_ver      = excluded.puede_ver,
                puede_crear    = excluded.puede_crear,
                puede_editar   = excluded.puede_editar,
                puede_eliminar = excluded.puede_eliminar
            """;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (PermisoModulo p : permisos) {
                stmt.setString(1, username);
                stmt.setString(2, p.getModulo());
                stmt.setInt(3, p.isPuedeVer()      ? 1 : 0);
                stmt.setInt(4, p.isPuedeCrear()    ? 1 : 0);
                stmt.setInt(5, p.isPuedeEditar()   ? 1 : 0);
                stmt.setInt(6, p.isPuedeEliminar() ? 1 : 0);
                stmt.addBatch();
            }
            stmt.executeBatch();
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

    // ─── Carga permisos del usuario desde BD ─────────────────────────
    private void cargarPermisos(Usuario usuario, Connection conn) throws Exception {
        String sql = """
            SELECT modulo, puede_ver, puede_crear, puede_editar, puede_eliminar
            FROM usuario_permisos WHERE username = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getUsername());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PermisoModulo p = new PermisoModulo(
                        rs.getString("modulo"),
                        rs.getInt("puede_ver")      == 1,
                        rs.getInt("puede_crear")    == 1,
                        rs.getInt("puede_editar")   == 1,
                        rs.getInt("puede_eliminar") == 1
                );
                usuario.setPermiso(p);
            }
        }
    }
}