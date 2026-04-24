package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Localizacion;
import com.example.gman.domain.repository.LocalizacionRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocalizacionRepositoryImpl implements LocalizacionRepository {

    @Override
    public List<Localizacion> findAll() {
        List<Localizacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM localizaciones ORDER BY numero_localizacion";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) lista.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar localizaciones: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Localizacion save(Localizacion loc) {
        return loc.getId() == 0 ? insert(loc) : update(loc);
    }

    private Localizacion insert(Localizacion loc) {
        String sql = """
            INSERT INTO localizaciones (numero_localizacion, descripcion, departamento, notas)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParams(ps, loc);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) loc.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar localización: " + e.getMessage(), e);
        }
        return loc;
    }

    private Localizacion update(Localizacion loc) {
        String sql = """
            UPDATE localizaciones
            SET numero_localizacion=?, descripcion=?, departamento=?, notas=?
            WHERE id=?
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, loc);
            ps.setInt(5, loc.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar localización: " + e.getMessage(), e);
        }
        return loc;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM localizaciones WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar localización: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsNumero(String numero, int excludeId) {
        String sql = "SELECT COUNT(*) FROM localizaciones WHERE numero_localizacion=? AND id!=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, numero);
            ps.setInt(2, excludeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar número: " + e.getMessage(), e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────
    private void setParams(PreparedStatement ps, Localizacion loc) throws SQLException {
        ps.setString(1, loc.getNumeroLocalizacion());
        ps.setString(2, loc.getDescripcion());
        ps.setString(3, loc.getDepartamento());
        ps.setString(4, loc.getNotas());
    }

    private Localizacion mapRow(ResultSet rs) throws SQLException {
        Localizacion loc = new Localizacion();
        loc.setId(rs.getInt("id"));
        loc.setNumeroLocalizacion(rs.getString("numero_localizacion"));
        loc.setDescripcion(rs.getString("descripcion"));
        loc.setDepartamento(rs.getString("departamento"));
        loc.setNotas(rs.getString("notas"));
        return loc;
    }
}