package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.repository.EmpleadoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite del repositorio de Empleados.
 * Todos los SELECTs hacen JOIN con catalogo y localizaciones
 * para traer nombres descriptivos listos para la vista.
 */
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private static final String SELECT_BASE = """
            SELECT e.numero_empleado,
                   e.nombre,
                   e.direccion,
                   e.celular,
                   e.correo,
                   e.posicion_id,
                   cp.nombre   AS posicion_nombre,
                   e.departamento_id,
                   cd.nombre   AS departamento_nombre,
                   e.localizacion_id,
                   l.descripcion AS localizacion_desc,
                   e.salario_por_hora,
                   e.tiempo_extra1,
                   e.tiempo_extra2,
                   e.tiempo_extra3
            FROM empleados e
            LEFT JOIN catalogo   cp ON cp.id = e.posicion_id
            LEFT JOIN catalogo   cd ON cd.id = e.departamento_id
            LEFT JOIN localizaciones l ON l.id = e.localizacion_id
            """;

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    @Override
    public List<Empleado> findAll() {
        String sql = SELECT_BASE + "ORDER BY e.nombre";
        List<Empleado> lista = new ArrayList<>();
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
    public List<Empleado> findByDepartamento(int departamentoId) {
        String sql = SELECT_BASE + "WHERE e.departamento_id = ? ORDER BY e.nombre";
        List<Empleado> lista = new ArrayList<>();
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
    public Empleado findById(int numeroEmpleado) {
        String sql = SELECT_BASE + "WHERE e.numero_empleado = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroEmpleado);
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
    public void save(Empleado e) {
        String sql = """
                INSERT INTO empleados
                    (nombre, direccion, posicion_id, celular, correo,
                     departamento_id, localizacion_id,
                     salario_por_hora, tiempo_extra1, tiempo_extra2, tiempo_extra3)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDireccion());
            setIntOrNull(ps, 3, e.getPosicionId());
            ps.setString(4, e.getCelular());
            ps.setString(5, e.getCorreo());
            setIntOrNull(ps, 6, e.getDepartamentoId());
            setIntOrNull(ps, 7, e.getLocalizacionId());
            ps.setDouble(8,  e.getSalarioPorHora());
            ps.setDouble(9,  e.getTiempoExtra1());
            ps.setDouble(10, e.getTiempoExtra2());
            ps.setDouble(11, e.getTiempoExtra3());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al guardar empleado: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void update(Empleado e) {
        String sql = """
                UPDATE empleados SET
                    nombre          = ?,
                    direccion       = ?,
                    posicion_id     = ?,
                    celular         = ?,
                    correo          = ?,
                    departamento_id = ?,
                    localizacion_id = ?,
                    salario_por_hora= ?,
                    tiempo_extra1   = ?,
                    tiempo_extra2   = ?,
                    tiempo_extra3   = ?
                WHERE numero_empleado = ?
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDireccion());
            setIntOrNull(ps, 3, e.getPosicionId());
            ps.setString(4, e.getCelular());
            ps.setString(5, e.getCorreo());
            setIntOrNull(ps, 6, e.getDepartamentoId());
            setIntOrNull(ps, 7, e.getLocalizacionId());
            ps.setDouble(8,  e.getSalarioPorHora());
            ps.setDouble(9,  e.getTiempoExtra1());
            ps.setDouble(10, e.getTiempoExtra2());
            ps.setDouble(11, e.getTiempoExtra3());
            ps.setInt(12, e.getNumeroEmpleado());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar empleado: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(int numeroEmpleado) {
        String sql = "DELETE FROM empleados WHERE numero_empleado = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, numeroEmpleado);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se puede eliminar: el empleado está vinculado a órdenes de trabajo u otros registros.", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════════════

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setNumeroEmpleado(rs.getInt("numero_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setDireccion(rs.getString("direccion"));
        e.setCelular(rs.getString("celular"));
        e.setCorreo(rs.getString("correo"));
        e.setPosicionId(rs.getInt("posicion_id"));
        e.setPosicionNombre(rs.getString("posicion_nombre"));
        e.setDepartamentoId(rs.getInt("departamento_id"));
        e.setDepartamentoNombre(rs.getString("departamento_nombre"));
        e.setLocalizacionId(rs.getInt("localizacion_id"));
        e.setLocalizacionDesc(rs.getString("localizacion_desc"));
        e.setSalarioPorHora(rs.getDouble("salario_por_hora"));
        e.setTiempoExtra1(rs.getDouble("tiempo_extra1"));
        e.setTiempoExtra2(rs.getDouble("tiempo_extra2"));
        e.setTiempoExtra3(rs.getDouble("tiempo_extra3"));
        return e;
    }

    private void setIntOrNull(PreparedStatement ps, int index, int value) throws SQLException {
        if (value > 0) ps.setInt(index, value);
        else           ps.setNull(index, Types.INTEGER);
    }
}