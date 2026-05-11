package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Localizacion;
import com.example.gman.domain.repository.LocalizacionesRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite del repositorio de Localizaciones.
 * Todos los SELECTs hacen JOIN con catalogo para traer el nombre del departamento.
 */
public class LocalizacionesRepositoryImpl implements LocalizacionesRepository {

    // SQL base con JOIN para traer departamento_nombre
    private static final String SELECT_BASE =
            "SELECT l.id, l.numero_localizacion, l.descripcion, " +
                    "       l.departamento_id, c.nombre AS departamento_nombre, l.notas " +
                    "FROM localizaciones l " +
                    "LEFT JOIN catalogo c ON c.id = l.departamento_id ";

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    @Override
    public List<Localizacion> findAll() {
        String sql = SELECT_BASE + "ORDER BY l.numero_localizacion";
        List<Localizacion> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Localizacion> findByDepartamento(int departamentoId) {
        String sql = SELECT_BASE + "WHERE l.departamento_id = ? ORDER BY l.numero_localizacion";
        List<Localizacion> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departamentoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Localizacion findById(int id) {
        String sql = SELECT_BASE + "WHERE l.id = ?";
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
    public Localizacion findByNumero(String numeroLocalizacion) {
        String sql = SELECT_BASE + "WHERE l.numero_localizacion = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroLocalizacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ════════════════════════════════════════════════════════════════
    //  ESCRITURA
    // ════════════════════════════════════════════════════════════════

    @Override
    public void save(Localizacion l) {
        String sql = "INSERT INTO localizaciones (numero_localizacion, descripcion, departamento_id, notas) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getNumeroLocalizacion());
            ps.setString(2, l.getDescripcion());
            setIntOrNull(ps, 3, l.getDepartamentoId());
            ps.setString(4, l.getNotas());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar localización: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Localizacion l) {
        String sql = "UPDATE localizaciones SET numero_localizacion = ?, descripcion = ?, " +
                "departamento_id = ?, notas = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, l.getNumeroLocalizacion());
            ps.setString(2, l.getDescripcion());
            setIntOrNull(ps, 3, l.getDepartamentoId());
            ps.setString(4, l.getNotas());
            ps.setInt(5, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar localización: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM localizaciones WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se puede eliminar: la localización está en uso por equipos o empleados.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════════════

    private Localizacion mapear(ResultSet rs) throws SQLException {
        Localizacion l = new Localizacion();
        l.setId(rs.getInt("id"));
        l.setNumeroLocalizacion(rs.getString("numero_localizacion"));
        l.setDescripcion(rs.getString("descripcion"));
        l.setDepartamentoId(rs.getInt("departamento_id"));
        l.setDepartamentoNombre(rs.getString("departamento_nombre"));
        l.setNotas(rs.getString("notas"));
        return l;
    }

    /** Inserta un int como NULL si el valor es 0 (no seleccionado). */
    private void setIntOrNull(PreparedStatement ps, int index, int value) throws SQLException {
        if (value > 0) ps.setInt(index, value);
        else           ps.setNull(index, Types.INTEGER);
    }
}