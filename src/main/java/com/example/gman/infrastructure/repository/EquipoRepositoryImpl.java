package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.repository.EquipoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite de EquipoRepository.
 *
 * Correcciones respecto a la versión anterior:
 *  1. SQL usa columnas correctas: area_id, localizacion_id, criticidad_id, tipo_id
 *  2. setParams usa setInt (no setString) para las 4 FKs
 *  3. findAll hace LEFT JOIN para resolver nombres (areaNombre, localizacionNombre, etc.)
 *  4. mapRow lee los campos _id como int y los _nombre como String
 *  5. Se agrega findById para cargar el formulario de edición
 */
public class EquipoRepositoryImpl implements EquipoRepository {



    // ── SQL base con JOIN (para findAll y findById) ───────────────────────
    private static final String SQL_SELECT = """
            SELECT
                e.id,
                e.codigo,
                e.nombre,
                e.capacidad,
                e.marca,
                e.modelo,
                e.serie,
                e.area_id,
                e.localizacion_id,
                e.centro_costos,
                e.criticidad_id,
                e.tipo_id,
                ca.nombre   AS area_nombre,
                l.descripcion AS localizacion_nombre,
                cc.nombre   AS criticidad_nombre,
                ct.nombre   AS tipo_nombre
            FROM equipos e
            LEFT JOIN catalogo     ca ON e.area_id         = ca.id
            LEFT JOIN localizaciones l ON e.localizacion_id = l.id
            LEFT JOIN catalogo     cc ON e.criticidad_id   = cc.id
            LEFT JOIN catalogo     ct ON e.tipo_id         = ct.id
            """;

    // ─────────────────────────────────────────────────────────────────────
    // SAVE (insert o update)
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public Equipo save(Equipo equipo) {
        return equipo.getId() == 0 ? insert(equipo) : update(equipo);
    }

    private Equipo insert(Equipo equipo) {
        String sql = """
                INSERT INTO equipos
                  (codigo, nombre, capacidad, marca, modelo, serie,
                   area_id, localizacion_id, centro_costos, criticidad_id, tipo_id)
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
                  codigo=?, nombre=?, capacidad=?, marca=?, modelo=?, serie=?,
                  area_id=?, localizacion_id=?, centro_costos=?, criticidad_id=?, tipo_id=?
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

    // ─────────────────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────────────────
    // FIND ALL — con JOIN para nombres resueltos
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public List<Equipo> findAll() {
        List<Equipo> lista = new ArrayList<>();
        String sql = SQL_SELECT + " ORDER BY e.id";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            while (rs.next()) lista.add(mapRow(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar equipos: " + e.getMessage(), e);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────────────────
    // FIND BY ID — para cargar el formulario de edición
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public Equipo findById(int id) {
        String sql = SQL_SELECT + " WHERE e.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar equipo por id: " + e.getMessage(), e);
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────
    // EXISTS CODIGO
    // ─────────────────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Asigna los 11 parámetros del INSERT/UPDATE.
     * Las 4 FKs usan setInt; si el valor es 0 se guarda NULL.
     */
    private void setParams(PreparedStatement stmt, Equipo e) throws SQLException {
        stmt.setString(1, e.getCodigo());
        stmt.setString(2, e.getNombre());
        stmt.setString(3, e.getCapacidad());
        stmt.setString(4, e.getMarca());
        stmt.setString(5, e.getModelo());
        stmt.setString(6, e.getSerie());
        setIntOrNull(stmt, 7,  e.getAreaId());
        setIntOrNull(stmt, 8,  e.getLocalizacionId());
        stmt.setString(9, e.getCentroCostos());
        setIntOrNull(stmt, 10, e.getCriticidadId());
        setIntOrNull(stmt, 11, e.getTipoId());
    }

    /** Guarda NULL cuando el FK es 0 (no seleccionado). */
    private void setIntOrNull(PreparedStatement stmt, int idx, int value) throws SQLException {
        if (value == 0) stmt.setNull(idx, Types.INTEGER);
        else            stmt.setInt(idx, value);
    }

    /**
     * Mapea un ResultSet a Equipo.
     * Lee IDs enteros para las FKs y Strings para los nombres resueltos.
     */
    private Equipo mapRow(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombre(rs.getString("nombre"));
        e.setCapacidad(rs.getString("capacidad"));
        e.setMarca(rs.getString("marca"));
        e.setModelo(rs.getString("modelo"));
        e.setSerie(rs.getString("serie"));
        e.setAreaId(rs.getInt("area_id"));
        e.setLocalizacionId(rs.getInt("localizacion_id"));
        e.setCentroCostos(rs.getString("centro_costos"));
        e.setCriticidadId(rs.getInt("criticidad_id"));
        e.setTipoId(rs.getInt("tipo_id"));

        // Nombres resueltos (solo display)
        e.setAreaNombre(rs.getString("area_nombre"));
        e.setLocalizacionNombre(rs.getString("localizacion_nombre"));
        e.setCriticidadNombre(rs.getString("criticidad_nombre"));
        e.setTipoNombre(rs.getString("tipo_nombre"));
        return e;
    }
}