package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.domain.repository.OrdenTrabajoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoRepositoryImpl implements OrdenTrabajoRepository {

    // ─── Obtener todas ───────────────────────────────────────────────
    @Override
    public List<OrdenTrabajo> getAll() throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT * FROM orden_trabajo ORDER BY id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // ─── Obtener por estado ──────────────────────────────────────────
    @Override
    public List<OrdenTrabajo> getByEstado(String estado) throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = "SELECT * FROM orden_trabajo WHERE estado = ? ORDER BY id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // ─── Obtener por ID ──────────────────────────────────────────────
    @Override
    public OrdenTrabajo getById(int id) throws SQLException {
        String sql = "SELECT * FROM orden_trabajo WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ─── Crear ───────────────────────────────────────────────────────
    @Override
    public void crear(OrdenTrabajo ot) throws SQLException {
        String sql = """
            INSERT INTO orden_trabajo
            (numero_ot, fecha_solicitud, estado, tipo_ot, prioridad,
             fecha_requerida, descripcion, equipo_id, localizacion,
             estado_equipo, recibido_por, notas_tecnico, creado_por)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,  ot.getNumeroOt());
            ps.setString(2,  ot.getFechaSolicitud());
            ps.setString(3,  ot.getEstado());
            ps.setString(4,  ot.getTipoOt());
            ps.setString(5,  ot.getPrioridad());
            ps.setString(6,  ot.getFechaRequerida());
            ps.setString(7,  ot.getDescripcion());
            ps.setInt(8,     ot.getEquipoId());
            ps.setString(9,  ot.getLocalizacion());
            ps.setString(10, ot.getEstadoEquipo());
            ps.setString(11, ot.getRecibidoPor());
            ps.setString(12, ot.getNotasTecnico());
            ps.setString(13, ot.getCreadoPor());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ot.setId(keys.getInt(1));
            }
        }
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    @Override
    public void actualizar(OrdenTrabajo ot) throws SQLException {
        String sql = """
            UPDATE orden_trabajo SET
                estado = ?, tipo_ot = ?, prioridad = ?,
                fecha_requerida = ?, descripcion = ?,
                equipo_id = ?, localizacion = ?,
                estado_equipo = ?, recibido_por = ?, notas_tecnico = ?
            WHERE id = ?
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  ot.getEstado());
            ps.setString(2,  ot.getTipoOt());
            ps.setString(3,  ot.getPrioridad());
            ps.setString(4,  ot.getFechaRequerida());
            ps.setString(5,  ot.getDescripcion());
            ps.setInt(6,     ot.getEquipoId());
            ps.setString(7,  ot.getLocalizacion());
            ps.setString(8,  ot.getEstadoEquipo());
            ps.setString(9,  ot.getRecibidoPor());
            ps.setString(10, ot.getNotasTecnico());
            ps.setInt(11,    ot.getId());
            ps.executeUpdate();
        }
    }

    // ─── Cerrar OT (guarda datos de cierre) ──────────────────────────
    @Override
    public void cerrar(OrdenTrabajo ot) throws SQLException {
        String sql = """
            UPDATE orden_trabajo SET
                estado = 'CERRADA',
                fecha_respuesta = ?, fecha_inicio = ?, fecha_termino = ?,
                fecha_entrega = ?, codigo_falla = ?, desc_causa = ?,
                accion_realizada = ?, prevencion = ?,
                duracion_dias = ?, aceptada_por = ?
            WHERE id = ?
            """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,  ot.getFechaRespuesta());
            ps.setString(2,  ot.getFechaInicio());
            ps.setString(3,  ot.getFechaTermino());
            ps.setString(4,  ot.getFechaEntrega());
            ps.setString(5,  ot.getCodigoFalla());
            ps.setString(6,  ot.getDescCausa());
            ps.setString(7,  ot.getAccionRealizada());
            ps.setString(8,  ot.getPrevencion());
            ps.setInt(9,     ot.getDuracionDias());
            ps.setString(10, ot.getAceptadaPor());
            ps.setInt(11,    ot.getId());
            ps.executeUpdate();
        }
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    @Override
    public void eliminar(int id) throws SQLException {
        // Primero elimina relaciones
        try (Connection conn = DatabaseHelper.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ot_empleados WHERE ot_id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM orden_trabajo WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }

    // ─── Asignar empleados ───────────────────────────────────────────
    @Override
    public void asignarEmpleados(int otId,
                                 List<Integer> numerosEmpleado) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Limpia asignaciones anteriores
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ot_empleados WHERE ot_id = ?")) {
                ps.setInt(1, otId);
                ps.executeUpdate();
            }
            // Inserta nuevas
            String ins = "INSERT OR IGNORE INTO ot_empleados (ot_id, empleado_numero) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(ins)) {
                for (int num : numerosEmpleado) {
                    ps.setInt(1, otId);
                    ps.setInt(2, num);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    // ─── Obtener empleados de una OT ─────────────────────────────────
    @Override
    public List<Integer> getEmpleadosDeOt(int otId) throws SQLException {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT empleado_numero FROM ot_empleados WHERE ot_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, otId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getInt("empleado_numero"));
            }
        }
        return lista;
    }

    // ─── Mapeo ResultSet → OrdenTrabajo ──────────────────────────────
    private OrdenTrabajo mapear(ResultSet rs) throws SQLException {
        OrdenTrabajo ot = new OrdenTrabajo();
        ot.setId(             rs.getInt("id"));
        ot.setNumeroOt(       rs.getString("numero_ot"));
        ot.setFechaSolicitud( rs.getString("fecha_solicitud"));
        ot.setEstado(         rs.getString("estado"));
        ot.setTipoOt(         rs.getString("tipo_ot"));
        ot.setPrioridad(      rs.getString("prioridad"));
        ot.setFechaRequerida( rs.getString("fecha_requerida"));
        ot.setDescripcion(    rs.getString("descripcion"));
        ot.setEquipoId(       rs.getInt("equipo_id"));
        ot.setLocalizacion(   rs.getString("localizacion"));
        ot.setEstadoEquipo(   rs.getString("estado_equipo"));
        ot.setRecibidoPor(    rs.getString("recibido_por"));
        ot.setNotasTecnico(   rs.getString("notas_tecnico"));
        ot.setFechaRespuesta( rs.getString("fecha_respuesta"));
        ot.setFechaInicio(    rs.getString("fecha_inicio"));
        ot.setFechaTermino(   rs.getString("fecha_termino"));
        ot.setFechaEntrega(   rs.getString("fecha_entrega"));
        ot.setCodigoFalla(    rs.getString("codigo_falla"));
        ot.setDescCausa(      rs.getString("desc_causa"));
        ot.setAccionRealizada(rs.getString("accion_realizada"));
        ot.setPrevencion(     rs.getString("prevencion"));
        ot.setDuracionDias(   rs.getInt("duracion_dias"));
        ot.setAceptadaPor(    rs.getString("aceptada_por"));
        ot.setCreadoPor(      rs.getString("creado_por"));
        return ot;
    }
}