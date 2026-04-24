package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.repository.EmpleadoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    // ─── SQL constants ────────────────────────────────────────────────
    private static final String SELECT_ALL =
            "SELECT numero_empleado, nombre, direccion, posicion, celular, " +
                    "       departamento, correo, salario_por_hora, " +
                    "       tiempo_extra1, tiempo_extra2, tiempo_extra3 " +
                    "FROM empleados ORDER BY nombre";

    private static final String SELECT_BY_ID =
            "SELECT numero_empleado, nombre, direccion, posicion, celular, " +
                    "       departamento, correo, salario_por_hora, " +
                    "       tiempo_extra1, tiempo_extra2, tiempo_extra3 " +
                    "FROM empleados WHERE numero_empleado = ?";

    private static final String INSERT =
            "INSERT INTO empleados " +
                    "(nombre, direccion, posicion, celular, departamento, correo, " +
                    " salario_por_hora, tiempo_extra1, tiempo_extra2, tiempo_extra3) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE empleados SET " +
                    "nombre = ?, direccion = ?, posicion = ?, celular = ?, " +
                    "departamento = ?, correo = ?, salario_por_hora = ?, " +
                    "tiempo_extra1 = ?, tiempo_extra2 = ?, tiempo_extra3 = ? " +
                    "WHERE numero_empleado = ?";

    private static final String DELETE =
            "DELETE FROM empleados WHERE numero_empleado = ?";

    // ─── Listar todos ────────────────────────────────────────────────
    @Override
    public List<Empleado> getAllEmpleados() throws Exception {
        List<Empleado> lista = new ArrayList<>();

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // ─── Buscar por número ───────────────────────────────────────────
    @Override
    public Empleado findByNumero(int numeroEmpleado) throws Exception {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setInt(1, numeroEmpleado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ─── Insertar ────────────────────────────────────────────────────
    @Override
    public void addEmpleado(Empleado e) throws Exception {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT,
                     Statement.RETURN_GENERATED_KEYS)) {

            bindEmpleado(stmt, e, false);
            stmt.executeUpdate();

            // Recuperar el ID auto-generado
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) e.setNumeroEmpleado(keys.getInt(1));
            }
        }
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    @Override
    public void updateEmpleado(Empleado e) throws Exception {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            bindEmpleado(stmt, e, true);
            int filas = stmt.executeUpdate();
            if (filas == 0)
                throw new Exception("Empleado #" + e.getNumeroEmpleado() + " no encontrado.");
        }
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    @Override
    public void deleteEmpleado(int numeroEmpleado) throws Exception {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setInt(1, numeroEmpleado);
            int filas = stmt.executeUpdate();
            if (filas == 0)
                throw new Exception("Empleado #" + numeroEmpleado + " no encontrado.");
        }
    }

    // ─── Helpers privados ─────────────────────────────────────────────

    /** Mapea un ResultSet a un objeto Empleado */
    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setNumeroEmpleado(rs.getInt("numero_empleado"));
        e.setNombre(rs.getString("nombre"));
        e.setDireccion(rs.getString("direccion"));
        e.setPosicion(rs.getString("posicion"));
        e.setCelular(rs.getString("celular"));
        e.setDepartamento(rs.getString("departamento"));
        e.setCorreo(rs.getString("correo"));
        e.setSalarioPorHora(rs.getDouble("salario_por_hora"));
        e.setTiempoExtra1(rs.getDouble("tiempo_extra1"));
        e.setTiempoExtra2(rs.getDouble("tiempo_extra2"));
        e.setTiempoExtra3(rs.getDouble("tiempo_extra3"));
        return e;
    }

    /**
     * Vincula los parámetros del PreparedStatement.
     * @param incluirPK  true para UPDATE (el número de empleado va al final), false para INSERT
     */
    private void bindEmpleado(PreparedStatement stmt, Empleado e,
                              boolean incluirPK) throws SQLException {
        stmt.setString(1, e.getNombre());
        stmt.setString(2, e.getDireccion());
        stmt.setString(3, e.getPosicion());
        stmt.setString(4, e.getCelular());
        stmt.setString(5, e.getDepartamento());
        stmt.setString(6, e.getCorreo());
        stmt.setDouble(7, e.getSalarioPorHora());
        stmt.setDouble(8, e.getTiempoExtra1());
        stmt.setDouble(9, e.getTiempoExtra2());
        stmt.setDouble(10, e.getTiempoExtra3());
        if (incluirPK) stmt.setInt(11, e.getNumeroEmpleado());
    }
}