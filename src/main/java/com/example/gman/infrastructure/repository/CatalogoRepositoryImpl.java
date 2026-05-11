package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.repository.CatalogoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite del repositorio de Catálogos.
 */
public class CatalogoRepositoryImpl implements CatalogoRepository {

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    @Override
    public List<Catalogo> findAll() {
        String sql = "SELECT id, tipo, codigo, nombre, descripcion FROM catalogo ORDER BY tipo, nombre";
        return ejecutarLista(sql);
    }

    @Override
    public List<Catalogo> findByTipo(String tipo) {
        String sql = "SELECT id, tipo, codigo, nombre, descripcion " +
                "FROM catalogo WHERE tipo = ? ORDER BY nombre";
        List<Catalogo> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Catalogo findById(int id) {
        String sql = "SELECT id, tipo, codigo, nombre, descripcion FROM catalogo WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Catalogo findByCodigo(String codigo) {
        String sql = "SELECT id, tipo, codigo, nombre, descripcion FROM catalogo WHERE codigo = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Devuelve proveedores de la tabla proveedores mapeados a Catalogo.
     * codigo → codigo, nombre → nombre, descripcion → servicios
     */
    @Override
    public List<Catalogo> findProveedoresComoItems() {
        String sql = "SELECT id, codigo, nombre, servicios FROM proveedores ORDER BY nombre";
        List<Catalogo> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Catalogo c = new Catalogo();
                c.setId(rs.getInt("id"));
                c.setTipo("__PROVEEDORES__");
                c.setCodigo(rs.getString("codigo"));
                c.setNombre(rs.getString("nombre"));
                c.setDescripcion(rs.getString("servicios"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════
    //  ESCRITURA
    // ════════════════════════════════════════════════════════════════

    @Override
    public void save(Catalogo c) {
        String sql = "INSERT INTO catalogo (tipo, codigo, nombre, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTipo());
            ps.setString(2, c.getCodigo());
            ps.setString(3, c.getNombre());
            ps.setString(4, c.getDescripcion());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar catálogo: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Catalogo c) {
        String sql = "UPDATE catalogo SET tipo = ?, codigo = ?, nombre = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTipo());
            ps.setString(2, c.getCodigo());
            ps.setString(3, c.getNombre());
            ps.setString(4, c.getDescripcion());
            ps.setInt(5, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar catálogo: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM catalogo WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            // SQLite lanza "FOREIGN KEY constraint failed" si está en uso
            throw new RuntimeException("No se puede eliminar: el registro está en uso por otros módulos.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════════════

    private List<Catalogo> ejecutarLista(String sql) {
        List<Catalogo> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Catalogo mapear(ResultSet rs) throws SQLException {
        Catalogo c = new Catalogo();
        c.setId(rs.getInt("id"));
        c.setTipo(rs.getString("tipo"));
        c.setCodigo(rs.getString("codigo"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        return c;
    }
}