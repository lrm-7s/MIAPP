package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.repository.EquipoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoRepositoryImpl implements EquipoRepository {
private DatabaseHelper db;
public EquipoRepositoryImpl(DatabaseHelper db){
    this.db = db;
}
    @Override
    public Equipo save(Equipo equipo) {
        if (equipo.getId() == 0) {
            return insert(equipo);
        } else {
            return update(equipo);
        }
    }

    private Equipo insert(Equipo equipo) {
        String sql = """
            INSERT INTO equipos
              (codigo, nombre, capacidad, marca, modelo, serie, area, planta, centro_costos, criticidad, tipo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParams(stmt, equipo);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) equipo.setId(keys.getInt(1));

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar equipo: " + e.getMessage(), e);
        }
        return equipo;
    }

    private Equipo update(Equipo equipo) {
        String sql = """
            UPDATE equipos SET
              codigo=?, nombre=?, capacidad=?, marca=?, modelo=?,
              serie=?, area=?, planta=?, centro_costos=?, criticidad=?, tipo=?
            WHERE id=?
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParams(stmt, equipo);
            stmt.setInt(12, equipo.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar equipo: " + e.getMessage(), e);
        }
        return equipo;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM equipos WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar equipo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Equipo> findAll() {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipos ORDER BY id";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) lista.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar equipos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public boolean existsCodigo(String codigo, int excludeId) {
        String sql = "SELECT COUNT(*) FROM equipos WHERE codigo = ? AND id != ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            stmt.setInt(2, excludeId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar código: " + e.getMessage(), e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void setParams(PreparedStatement stmt, Equipo e) throws SQLException {
        stmt.setString(1,  e.getCodigo());
        stmt.setString(2,  e.getNombre());
        stmt.setString(3,  e.getCapacidad());
        stmt.setString(4,  e.getMarca());
        stmt.setString(5,  e.getModelo());
        stmt.setString(6,  e.getSerie());
        stmt.setString(7,  e.getArea());
        stmt.setString(8,  e.getPlanta());
        stmt.setString(9,  e.getCentroCostos());
        stmt.setString(10, e.getCriticidad());
        stmt.setString(11, e.getTipo());
    }

    private Equipo mapRow(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setCapacidad(rs.getString("capacidad"));
        e.setMarca(rs.getString("marca"));
        e.setModelo(rs.getString("modelo"));
        e.setSerie(rs.getString("serie"));
        e.setArea(rs.getString("area"));
        e.setPlanta(rs.getString("planta"));
        e.setCentroCostos(rs.getString("centro_costos"));
        e.setCriticidad(rs.getString("criticidad"));
        e.setTipo(rs.getString("tipo"));
        return e;
    }
}