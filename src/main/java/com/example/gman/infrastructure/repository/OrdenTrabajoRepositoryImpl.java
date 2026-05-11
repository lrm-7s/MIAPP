package com.example.gman.infrastructure.repository;

import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.domain.repository.OrdenTrabajoRepository;
import com.example.gman.infrastructure.database.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajoRepositoryImpl implements OrdenTrabajoRepository {

    // NOTA: DatabaseHelper usa métodos estáticos — no se inyecta por constructor.
    // Se mantiene coherente con CatalogoRepositoryImpl y LocalizacionRepositoryImpl.

    // ── SQL base con JOINs ───────────────────────────────────────────────
    // CORRECCIÓN: recibido_por_id ahora JOIN a empleados (no usuarios)
    //             aceptada_por_id también JOIN a empleados
    private static final String SQL_SELECT = """
            SELECT
                ot.id, ot.numero_ot, ot.fecha_solicitud, ot.fecha_requerida,
                ot.descripcion, ot.notas_tecnico, ot.oficio, ot.fecha_vencimiento,
                ot.estado_id, ot.tipo_ot_id, ot.prioridad_id,
                ot.estado_equipo_id, ot.codigo_falla_id,
                ot.equipo_id, ot.localizacion_id,
                ot.recibido_por_id, ot.creado_por_id, ot.aceptada_por_id,
                ot.instruccion_id,
                ot.desc_causa, ot.accion_realizada, ot.prevencion,
                ot.duracion_dias,
                ot.fecha_respuesta, ot.fecha_inicio, ot.fecha_termino, ot.fecha_entrega,
                ot.respuesta_est, ot.respuesta_real,
                ot.inicio_est, ot.inicio_real,
                ot.termino_est, ot.termino_real,
                ce.nombre     AS estado_nombre,
                ct.nombre     AS tipo_ot_nombre,
                cp.nombre     AS prioridad_nombre,
                cse.nombre    AS estado_equipo_nombre,
                cf.nombre     AS codigo_falla_nombre,
                l.descripcion AS localizacion_nombre,
                eq.nombre     AS equipo_nombre,
                uc.nombre     AS creado_por_nombre,
                er.nombre     AS recibido_por_nombre
            FROM orden_trabajo ot
            LEFT JOIN catalogo      ce  ON ot.estado_id        = ce.id
            LEFT JOIN catalogo      ct  ON ot.tipo_ot_id       = ct.id
            LEFT JOIN catalogo      cp  ON ot.prioridad_id     = cp.id
            LEFT JOIN catalogo      cse ON ot.estado_equipo_id = cse.id
            LEFT JOIN catalogo      cf  ON ot.codigo_falla_id  = cf.id
            LEFT JOIN localizaciones l  ON ot.localizacion_id  = l.id
            LEFT JOIN equipos        eq ON ot.equipo_id        = eq.id
            LEFT JOIN usuarios       uc ON ot.creado_por_id    = uc.id
            LEFT JOIN empleados      er ON ot.recibido_por_id  = er.numero_empleado
            """;

    // ════════════════════════════════════════════════════════════════
    //  CONSULTAS
    // ════════════════════════════════════════════════════════════════

    @Override
    public List<OrdenTrabajo> getAll() throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = SQL_SELECT + " ORDER BY ot.id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    @Override
    public List<OrdenTrabajo> getByEstado(int estadoId) throws SQLException {
        List<OrdenTrabajo> lista = new ArrayList<>();
        String sql = SQL_SELECT + " WHERE ot.estado_id = ? ORDER BY ot.id DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, estadoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public OrdenTrabajo getById(int id) throws SQLException {
        String sql = SQL_SELECT + " WHERE ot.id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ════════════════════════════════════════════════════════════════
    //  CREAR
    // ════════════════════════════════════════════════════════════════

    @Override
    public void crear(OrdenTrabajo ot) throws SQLException {
        String sql = """
                INSERT INTO orden_trabajo
                  (numero_ot, fecha_solicitud, estado_id, tipo_ot_id, prioridad_id,
                   fecha_requerida, descripcion, equipo_id, localizacion_id,
                   estado_equipo_id, recibido_por_id, notas_tecnico, creado_por_id,
                   oficio, fecha_vencimiento, instruccion_id)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1,  ot.getNumeroOt());
            ps.setString(2,  ot.getFechaSolicitud());
            setIntOrNull(ps, 3,  ot.getEstadoId());
            setIntOrNull(ps, 4,  ot.getTipoOtId());
            setIntOrNull(ps, 5,  ot.getPrioridadId());
            ps.setString(6,  ot.getFechaRequerida());
            ps.setString(7,  ot.getDescripcion());
            setIntOrNull(ps, 8,  ot.getEquipoId());
            setIntOrNull(ps, 9,  ot.getLocalizacionId());
            setIntOrNull(ps, 10, ot.getEstadoEquipoId());
            setIntOrNull(ps, 11, ot.getRecibidoPorId());
            ps.setString(12, ot.getNotasTecnico());
            setIntOrNull(ps, 13, ot.getCreadoPorId());
            ps.setString(14, ot.getOficio());
            ps.setString(15, ot.getFechaVencimiento());
            setIntOrNull(ps, 16, ot.getInstruccionId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ot.setId(keys.getInt(1));
            }
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ACTUALIZAR
    // ════════════════════════════════════════════════════════════════

    @Override
    public void actualizar(OrdenTrabajo ot) throws SQLException {
        String sql = """
                UPDATE orden_trabajo SET
                  estado_id=?, tipo_ot_id=?, prioridad_id=?,
                  fecha_requerida=?, descripcion=?,
                  equipo_id=?, localizacion_id=?,
                  estado_equipo_id=?, recibido_por_id=?,
                  notas_tecnico=?, oficio=?, fecha_vencimiento=?
                WHERE id=?
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setIntOrNull(ps, 1,  ot.getEstadoId());
            setIntOrNull(ps, 2,  ot.getTipoOtId());
            setIntOrNull(ps, 3,  ot.getPrioridadId());
            ps.setString(4,  ot.getFechaRequerida());
            ps.setString(5,  ot.getDescripcion());
            setIntOrNull(ps, 6,  ot.getEquipoId());
            setIntOrNull(ps, 7,  ot.getLocalizacionId());
            setIntOrNull(ps, 8,  ot.getEstadoEquipoId());
            setIntOrNull(ps, 9,  ot.getRecibidoPorId());
            ps.setString(10, ot.getNotasTecnico());
            ps.setString(11, ot.getOficio());
            ps.setString(12, ot.getFechaVencimiento());
            ps.setInt(13,    ot.getId());
            ps.executeUpdate();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  CERRAR
    // ════════════════════════════════════════════════════════════════

    @Override
    public void cerrar(OrdenTrabajo ot) throws SQLException {
        String sql = """
                UPDATE orden_trabajo SET
                  estado_id=?,
                  fecha_respuesta=?, fecha_inicio=?, fecha_termino=?, fecha_entrega=?,
                  respuesta_est=?, respuesta_real=?,
                  inicio_est=?, inicio_real=?,
                  termino_est=?, termino_real=?,
                  codigo_falla_id=?, desc_causa=?,
                  accion_realizada=?, prevencion=?,
                  duracion_dias=?, aceptada_por_id=?
                WHERE id=?
                """;
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setIntOrNull(ps, 1,  ot.getEstadoId());
            ps.setString(2,  ot.getFechaRespuesta());
            ps.setString(3,  ot.getFechaInicio());
            ps.setString(4,  ot.getFechaTermino());
            ps.setString(5,  ot.getFechaEntrega());
            ps.setString(6,  ot.getRespuestaEst());
            ps.setString(7,  ot.getRespuestaReal());
            ps.setString(8,  ot.getInicioEst());
            ps.setString(9,  ot.getInicioReal());
            ps.setString(10, ot.getTerminoEst());
            ps.setString(11, ot.getTerminoReal());
            setIntOrNull(ps, 12, ot.getCodigoFallaId());
            ps.setString(13, ot.getDescCausa());
            ps.setString(14, ot.getAccionRealizada());
            ps.setString(15, ot.getPrevencion());
            ps.setInt(16,    ot.getDuracionDias());
            setIntOrNull(ps, 17, ot.getAceptadaPorId());
            ps.setInt(18,    ot.getId());
            ps.executeUpdate();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ════════════════════════════════════════════════════════════════

    @Override
    public void eliminar(int id) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM orden_trabajo WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  EMPLEADOS ASIGNADOS
    // ════════════════════════════════════════════════════════════════

    @Override
    public void asignarEmpleados(int otId, List<Integer> numerosEmpleado) throws SQLException {
        try (Connection conn = DatabaseHelper.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM ot_empleados WHERE ot_id = ?")) {
                ps.setInt(1, otId);
                ps.executeUpdate();
            }
            if (numerosEmpleado == null || numerosEmpleado.isEmpty()) return;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO ot_empleados (ot_id, empleado_numero) VALUES (?,?)")) {
                for (int num : numerosEmpleado) {
                    ps.setInt(1, otId);
                    ps.setInt(2, num);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    @Override
    public List<Integer> getEmpleadosDeOt(int otId) throws SQLException {
        List<Integer> lista = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT empleado_numero FROM ot_empleados WHERE ot_id = ?")) {
            ps.setInt(1, otId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getInt("empleado_numero"));
            }
        }
        return lista;
    }

    // ════════════════════════════════════════════════════════════════
    //  RESOLVER USUARIO ID POR USERNAME
    //  Necesario porque Usuario.java usa username como PK (no tiene int id)
    // ════════════════════════════════════════════════════════════════

    @Override
    public int resolverUsuarioId(String username) throws SQLException {
        if (username == null || username.isBlank()) return 0;
        String sql = "SELECT id FROM usuarios WHERE username = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        return 0;
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    private void setIntOrNull(PreparedStatement ps, int idx, int value) throws SQLException {
        if (value == 0) ps.setNull(idx, Types.INTEGER);
        else            ps.setInt(idx, value);
    }

    private OrdenTrabajo mapRow(ResultSet rs) throws SQLException {
        OrdenTrabajo ot = new OrdenTrabajo();
        ot.setId(              rs.getInt("id"));
        ot.setNumeroOt(        rs.getString("numero_ot"));
        ot.setFechaSolicitud(  rs.getString("fecha_solicitud"));
        ot.setFechaRequerida(  rs.getString("fecha_requerida"));
        ot.setDescripcion(     rs.getString("descripcion"));
        ot.setNotasTecnico(    rs.getString("notas_tecnico"));
        ot.setOficio(          rs.getString("oficio"));
        ot.setFechaVencimiento(rs.getString("fecha_vencimiento"));

        ot.setEstadoId(        rs.getInt("estado_id"));
        ot.setTipoOtId(        rs.getInt("tipo_ot_id"));
        ot.setPrioridadId(     rs.getInt("prioridad_id"));
        ot.setEstadoEquipoId(  rs.getInt("estado_equipo_id"));
        ot.setCodigoFallaId(   rs.getInt("codigo_falla_id"));
        ot.setEquipoId(        rs.getInt("equipo_id"));
        ot.setLocalizacionId(  rs.getInt("localizacion_id"));
        ot.setRecibidoPorId(   rs.getInt("recibido_por_id"));
        ot.setCreadoPorId(     rs.getInt("creado_por_id"));
        ot.setAceptadaPorId(   rs.getInt("aceptada_por_id"));
        ot.setInstruccionId(   rs.getInt("instruccion_id"));

        ot.setDescCausa(       rs.getString("desc_causa"));
        ot.setAccionRealizada( rs.getString("accion_realizada"));
        ot.setPrevencion(      rs.getString("prevencion"));
        ot.setDuracionDias(    rs.getInt("duracion_dias"));
        ot.setFechaRespuesta(  rs.getString("fecha_respuesta"));
        ot.setFechaInicio(     rs.getString("fecha_inicio"));
        ot.setFechaTermino(    rs.getString("fecha_termino"));
        ot.setFechaEntrega(    rs.getString("fecha_entrega"));
        ot.setRespuestaEst(    rs.getString("respuesta_est"));
        ot.setRespuestaReal(   rs.getString("respuesta_real"));
        ot.setInicioEst(       rs.getString("inicio_est"));
        ot.setInicioReal(      rs.getString("inicio_real"));
        ot.setTerminoEst(      rs.getString("termino_est"));
        ot.setTerminoReal(     rs.getString("termino_real"));

        ot.setEstadoNombre(       rs.getString("estado_nombre"));
        ot.setTipoOtNombre(       rs.getString("tipo_ot_nombre"));
        ot.setPrioridadNombre(    rs.getString("prioridad_nombre"));
        ot.setEstadoEquipoNombre( rs.getString("estado_equipo_nombre"));
        ot.setCodigoFallaNombre(  rs.getString("codigo_falla_nombre"));
        ot.setLocalizacionNombre( rs.getString("localizacion_nombre"));
        ot.setEquipoNombre(       rs.getString("equipo_nombre"));
        ot.setCreadoPorNombre(    rs.getString("creado_por_nombre"));
        ot.setRecibidoPorNombre(  rs.getString("recibido_por_nombre"));
        return ot;
    }
}