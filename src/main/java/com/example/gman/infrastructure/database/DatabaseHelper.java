package com.example.gman.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DB_URL = buildDbUrl();
    private static String buildDbUrl() {
        // Coloca la BD junto al ejecutable, en una carpeta "data"
        String home = System.getProperty("user.home");
        java.io.File dir = new java.io.File(home, "GMAN_data");
        dir.mkdirs(); // Crea la carpeta si no existe
        return "jdbc:sqlite:" + new java.io.File(dir, "database.db").getAbsolutePath();
    }

    // ✅ Corrección: ejecutar el pragma en cada conexión
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
    public static void initDatabase() {

        String hashedPassword =
                org.mindrot.jbcrypt.BCrypt.hashpw(
                        "admin123",
                        org.mindrot.jbcrypt.BCrypt.gensalt()
                );
        // ── Habilitar claves foráneas en SQLite ──────────────────────
        String sqlForeignKeys = "PRAGMA foreign_keys = ON;";

        // ── CATÁLOGO ─────────────────────────────────────────────────
        // Tabla central de valores parametrizables.
        // tipo diferencia cada sub-catálogo:
        //   DEPARTAMENTO, FALLA, TIPO_EQUIPO, AREA, CRITICIDAD,
        //   TIPO_OT, PRIORIDAD, ESTADO_OT, ESTADO_EQUIPO,
        //   POSICION, TIPO_INSTRUCCION, MISCELANEO
        String sqlCatalogo = """
            CREATE TABLE IF NOT EXISTS catalogo (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo        TEXT    NOT NULL,
                codigo      TEXT    NOT NULL UNIQUE,
                nombre      TEXT    NOT NULL,
                descripcion TEXT
            );
            """;

        // ── PROVEEDORES ───────────────────────────────────────────────
        String sqlProveedores = """
            CREATE TABLE IF NOT EXISTS proveedores (
                id             INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo         TEXT    NOT NULL UNIQUE,
                nombre         TEXT    NOT NULL,
                representante  TEXT,
                puesto         TEXT,
                direccion      TEXT,
                provincia      TEXT,
                pais           TEXT,
                telefono       TEXT,
                servicios      TEXT
            );
            """;

        // ── LOCALIZACIONES ────────────────────────────────────────────
        // departamento_id → catalogo(id) tipo='DEPARTAMENTO'
        String sqlLocalizaciones = """
            CREATE TABLE IF NOT EXISTS localizaciones (
                id                  INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_localizacion TEXT    NOT NULL UNIQUE,
                descripcion         TEXT    NOT NULL,
                departamento_id     INTEGER,
                notas               TEXT,
                FOREIGN KEY (departamento_id) REFERENCES catalogo(id)
            );
            """;

        // ── EQUIPOS ───────────────────────────────────────────────────
        // area_id       → catalogo(id) tipo='AREA'
        // criticidad_id → catalogo(id) tipo='CRITICIDAD'
        // tipo_id       → catalogo(id) tipo='TIPO_EQUIPO'
        // localizacion_id → localizaciones(id)  (planta/ubicación física)
        String sqlEquipos = """
            CREATE TABLE IF NOT EXISTS equipos (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo          TEXT    NOT NULL UNIQUE,
                nombre          TEXT    NOT NULL,
                capacidad       TEXT,
                marca           TEXT,
                modelo          TEXT,
                serie           TEXT,
                area_id         INTEGER,
                localizacion_id INTEGER,
                centro_costos   TEXT,
                criticidad_id   INTEGER,
                tipo_id         INTEGER,
                FOREIGN KEY (area_id)         REFERENCES catalogo(id),
                FOREIGN KEY (localizacion_id) REFERENCES localizaciones(id),
                FOREIGN KEY (criticidad_id)   REFERENCES catalogo(id),
                FOREIGN KEY (tipo_id)         REFERENCES catalogo(id)
            );
            """;

        // ── EMPLEADOS ─────────────────────────────────────────────────
        // posicion_id     → catalogo(id) tipo='POSICION'
        // departamento_id → catalogo(id) tipo='DEPARTAMENTO'
        // localizacion_id → localizaciones(id)
        String sqlEmpleados = """
            CREATE TABLE IF NOT EXISTS empleados (
                numero_empleado  INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre           TEXT    NOT NULL,
                direccion        TEXT,
                posicion_id      INTEGER NOT NULL,
                celular          TEXT,
                correo           TEXT,
                departamento_id  INTEGER NOT NULL,
                localizacion_id  INTEGER,
                salario_por_hora REAL,
                tiempo_extra1    REAL,
                tiempo_extra2    REAL,
                tiempo_extra3    REAL,
                FOREIGN KEY (posicion_id)     REFERENCES catalogo(id),
                FOREIGN KEY (departamento_id) REFERENCES catalogo(id),
                FOREIGN KEY (localizacion_id) REFERENCES localizaciones(id)
            );
            """;

        // ── USUARIOS ──────────────────────────────────────────────────
        // rol         → etiqueta descriptiva (texto libre, sin efecto en permisos)
        // empleado_id → empleados(numero_empleado) — opcional, vincula usuario a empleado
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                username    TEXT    NOT NULL UNIQUE,
                nombre      TEXT,
                password    TEXT    NOT NULL,
                rol         TEXT    NOT NULL DEFAULT 'CONSULTOR',
                empleado_id INTEGER,
                FOREIGN KEY (empleado_id) REFERENCES empleados(numero_empleado)
            );
            """;

        // ── PERMISOS POR USUARIO Y MÓDULO ─────────────────────────────
        // El administrador asigna permisos granulares: ver/crear/editar/eliminar
        // por cada módulo del sistema.
        String sqlPermisos = """
            CREATE TABLE IF NOT EXISTS usuario_permisos (
                username        TEXT    NOT NULL,
                modulo          TEXT    NOT NULL,
                puede_ver       INTEGER NOT NULL DEFAULT 0,
                puede_crear     INTEGER NOT NULL DEFAULT 0,
                puede_editar    INTEGER NOT NULL DEFAULT 0,
                puede_eliminar  INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY (username, modulo),
                FOREIGN KEY (username) REFERENCES usuarios(username)
                    ON DELETE CASCADE
            );
            """;

        // ── ORDEN DE TRABAJO ──────────────────────────────────────────
        // Todos los campos categóricos apuntan a catalogo(id).
        // Las personas apuntan a usuarios(id).
        // instruccion_id → instrucciones(id)  (plan de mantenimiento preventivo)
        String sqlOrdenTrabajo = """
            CREATE TABLE IF NOT EXISTS orden_trabajo (
                id               INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_ot        TEXT    NOT NULL UNIQUE,
                fecha_solicitud  TEXT    NOT NULL,
                estado_id        INTEGER NOT NULL,
                tipo_ot_id       INTEGER NOT NULL,
                prioridad_id     INTEGER,
                fecha_requerida  TEXT,
                descripcion      TEXT,
                equipo_id        INTEGER,
                localizacion_id  INTEGER,
                estado_equipo_id INTEGER,
                creado_por_id    INTEGER,
                recibido_por_id  INTEGER,
                notas_tecnico    TEXT,
                accion_realizada TEXT,
                codigo_falla_id  INTEGER,
                desc_causa       TEXT,
                prevencion       TEXT,
                fecha_inicio     TEXT,
                fecha_termino    TEXT,
                fecha_entrega    TEXT,
                fecha_respuesta  TEXT,
                duracion_dias    INTEGER,
                aceptada_por_id  INTEGER,
                instruccion_id   INTEGER,
                fecha_vencimiento TEXT,
                oficio           TEXT,
                respuesta_est    TEXT,
                respuesta_real   TEXT,
                inicio_est       TEXT,
                inicio_real      TEXT,
                termino_est      TEXT,
                termino_real     TEXT,
                FOREIGN KEY (estado_id)        REFERENCES catalogo(id),
                FOREIGN KEY (tipo_ot_id)       REFERENCES catalogo(id),
                FOREIGN KEY (prioridad_id)     REFERENCES catalogo(id),
                FOREIGN KEY (equipo_id)        REFERENCES equipos(id),
                FOREIGN KEY (localizacion_id)  REFERENCES localizaciones(id),
                FOREIGN KEY (estado_equipo_id) REFERENCES catalogo(id),
                FOREIGN KEY (creado_por_id)    REFERENCES usuarios(id),
                FOREIGN KEY (recibido_por_id)  REFERENCES empleados(numero_empleado),
                FOREIGN KEY (aceptada_por_id)  REFERENCES empleados(numero_empleado),
                FOREIGN KEY (codigo_falla_id)  REFERENCES catalogo(id),
                FOREIGN KEY (instruccion_id)   REFERENCES instrucciones(id)
            );
            """;

        // ── OT ↔ EMPLEADOS (tabla intermedia) ────────────────────────
        String sqlOtEmpleados = """
            CREATE TABLE IF NOT EXISTS ot_empleados (
                ot_id           INTEGER NOT NULL,
                empleado_numero INTEGER NOT NULL,
                PRIMARY KEY (ot_id, empleado_numero),
                FOREIGN KEY (ot_id)           REFERENCES orden_trabajo(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (empleado_numero) REFERENCES empleados(numero_empleado)
            );
            """;

        // ── INVENTARIO ────────────────────────────────────────────────
        String sqlInventario = """
            CREATE TABLE IF NOT EXISTS inventario (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo          TEXT    NOT NULL UNIQUE,
                descripcion     TEXT    NOT NULL,
                cantidad        INTEGER NOT NULL DEFAULT 0,
                precio_unitario REAL,
                proveedor_id    INTEGER,
                FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
            );
            """;

        // ── OT ↔ INVENTARIO (tabla intermedia) ───────────────────────
        String sqlOtInventario = """
            CREATE TABLE IF NOT EXISTS ot_inventario (
                ot_id        INTEGER NOT NULL,
                inventario_id INTEGER NOT NULL,
                cantidad_usada INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY (ot_id, inventario_id),
                FOREIGN KEY (ot_id)         REFERENCES orden_trabajo(id)
                    ON DELETE CASCADE,
                FOREIGN KEY (inventario_id) REFERENCES inventario(id)
            );
            """;

        // ── INSTRUCCIONES (Plan de Mantenimiento) ─────────────────────
        // tipo_id   → catalogo(id) tipo='TIPO_INSTRUCCION'
        // equipo_id → equipos(id)
        String sqlInstrucciones = """
            CREATE TABLE IF NOT EXISTS instrucciones (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo          TEXT    NOT NULL UNIQUE,
                descripcion     TEXT    NOT NULL,
                horas_estimadas REAL,
                procedimiento   TEXT,
                tipo_id         INTEGER,
                equipo_id       INTEGER,
                FOREIGN KEY (tipo_id)   REFERENCES catalogo(id),
                FOREIGN KEY (equipo_id) REFERENCES equipos(id)
            );
            """;

        // ── TAREAS DE INSTRUCCIÓN ─────────────────────────────────────
        String sqlInstruccionTareas = """
            CREATE TABLE IF NOT EXISTS instruccion_tareas (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                instruccion_id  INTEGER NOT NULL,
                descripcion_tarea TEXT  NOT NULL,
                orden           INTEGER,
                FOREIGN KEY (instruccion_id) REFERENCES instrucciones(id)
                    ON DELETE CASCADE
            );
            """;

        // ── REPORTES ──────────────────────────────────────────────────
        String sqlReportes = """
            CREATE TABLE IF NOT EXISTS reportes (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                grupo       TEXT NOT NULL,
                nombre      TEXT NOT NULL,
                query_ref   TEXT,
                descripcion TEXT
            );
            """;

        // ══════════════════════════════════════════════════════════════
        // SEEDS — datos iniciales
        // ══════════════════════════════════════════════════════════════

        String sqlCatalogoSeed = """
            INSERT OR IGNORE INTO catalogo (tipo, codigo, nombre, descripcion) VALUES
                -- Departamentos
                ('DEPARTAMENTO',     'DEP-01', 'Mantenimiento',      'Dpto. de mantenimiento'),
                ('DEPARTAMENTO',     'DEP-02', 'Producción',         'Dpto. de producción'),
                ('DEPARTAMENTO',     'DEP-03', 'Administración',     'Dpto. administrativo'),
                -- Áreas
                ('AREA',             'ARE-01', 'Área Norte',         'Zona norte de la planta'),
                ('AREA',             'ARE-02', 'Área Sur',           'Zona sur de la planta'),
                -- Criticidad
                ('CRITICIDAD',       'CRI-01', 'Alta',               'Equipo crítico para producción'),
                ('CRITICIDAD',       'CRI-02', 'Media',              'Equipo de criticidad media'),
                ('CRITICIDAD',       'CRI-03', 'Baja',               'Equipo no crítico'),
                -- Tipo de equipo
                ('TIPO_EQUIPO',      'TEQ-01', 'Compresor',          'Equipos compresores'),
                ('TIPO_EQUIPO',      'TEQ-02', 'Bomba',              'Equipos de bombeo'),
                ('TIPO_EQUIPO',      'TEQ-03', 'Motor',              'Motores eléctricos'),
                -- Tipo OT
                ('TIPO_OT',          'TOT-01', 'Correctivo',         'Mantenimiento correctivo'),
                ('TIPO_OT',          'TOT-02', 'Preventivo',         'Mantenimiento preventivo'),
                ('TIPO_OT',          'TOT-03', 'Predictivo',         'Mantenimiento predictivo'),
                -- Estado OT
                ('ESTADO_OT',        'EST-01', 'Abierta',            'OT recién creada'),
                ('ESTADO_OT',        'EST-02', 'En Proceso',         'OT en ejecución'),
                ('ESTADO_OT',        'EST-03', 'Cerrada',            'OT finalizada'),
                ('ESTADO_OT',        'EST-04', 'Cancelada',          'OT cancelada'),
                -- Estado del equipo al momento de la OT
                ('ESTADO_EQUIPO',    'SEQ-01', 'Operativo',          'Equipo funcionando'),
                ('ESTADO_EQUIPO',    'SEQ-02', 'Fuera de Servicio',  'Equipo detenido'),
                ('ESTADO_EQUIPO',    'SEQ-03', 'En Mantenimiento',   'Equipo en reparación'),
                -- Prioridad OT
                ('PRIORIDAD',        'PRI-01', 'Alta',               'Atención inmediata'),
                ('PRIORIDAD',        'PRI-02', 'Media',              'Atención en 24-48 h'),
                ('PRIORIDAD',        'PRI-03', 'Baja',               'Puede esperar'),
                -- Código de falla
                ('FALLA',            'FAL-01', 'Falla Eléctrica',    'Problemas eléctricos'),
                ('FALLA',            'FAL-02', 'Falla Mecánica',     'Problemas mecánicos'),
                ('FALLA',            'FAL-03', 'Falla Hidráulica',   'Problemas hidráulicos'),
                -- Posición de empleado
                ('POSICION',         'POS-01', 'Técnico',            'Técnico de mantenimiento'),
                ('POSICION',         'POS-02', 'Supervisor',         'Supervisor de área'),
                ('POSICION',         'POS-03', 'Ingeniero',          'Ingeniero de planta'),
                -- Tipo de instrucción (plan de mantenimiento)
                ('TIPO_INSTRUCCION', 'TIN-01', 'Inspección',         'Revisión visual y funcional'),
                ('TIPO_INSTRUCCION', 'TIN-02', 'Lubricación',        'Lubricación de componentes'),
                ('TIPO_INSTRUCCION', 'TIN-03', 'Calibración',        'Calibración de instrumentos'),
                -- Misceláneos
                ('MISCELANEO',       'MIS-01', 'Valor1',             'Misceláneo genérico 1'),
                ('MISCELANEO',       'MIS-02', 'Valor2',             'Misceláneo genérico 2');
            """;

        String sqlLocalizacionSeed = """
            INSERT OR IGNORE INTO localizaciones
                (numero_localizacion, descripcion, departamento_id, notas)
            SELECT 'LOC-01', 'Planta Principal',    id, 'Planta principal de operaciones'
              FROM catalogo WHERE codigo = 'DEP-01'
            UNION ALL
            SELECT 'LOC-02', 'Almacén Central',     id, 'Almacén de materia prima'
              FROM catalogo WHERE codigo = 'DEP-02'
            UNION ALL
            SELECT 'LOC-03', 'Área de Compresores', id, 'Zona de equipos de compresión'
              FROM catalogo WHERE codigo = 'DEP-01';
            """;

        String sqlAdminDefault = """
    INSERT OR IGNORE INTO usuarios (username, nombre, password, rol)
    VALUES ('admin', 'Administrador', '""" + hashedPassword + """
    ', 'ADMIN');
    """;

        String sqlAdminPermisos = """
            INSERT OR IGNORE INTO usuario_permisos
                (username, modulo, puede_ver, puede_crear, puede_editar, puede_eliminar)
            VALUES
                ('admin', 'EQUIPOS',             1, 1, 1, 1),
                ('admin', 'MANO_OBRA',            1, 1, 1, 1),
                ('admin', 'ORDEN_TRABAJO',        1, 1, 1, 1),
                ('admin', 'CATALOGO',             1, 1, 1, 1),
                ('admin', 'PLAN_MANTENIMIENTO',   1, 1, 1, 1),
                ('admin', 'HISTORICO',            1, 1, 1, 1),
                ('admin', 'LOCALIZACIONES',       1, 1, 1, 1),
                ('admin', 'REPORTES',             1, 1, 1, 1),
                ('admin', 'INVENTARIO',           1, 1, 1, 1),
                ('admin', 'GESTION_USUARIOS',     1, 1, 1, 1);
            """;

        String sqlReportesSeed = """
            INSERT OR IGNORE INTO reportes (grupo, nombre, query_ref, descripcion) VALUES
                ('Equipos',         'Listado de Equipos',            'RPT_EQUIPOS_LISTA',    'Todos los equipos registrados'),
                ('Equipos',         'Equipos por Área',              'RPT_EQUIPOS_AREA',     'Equipos agrupados por área'),
                ('Órdenes',         'OT Abiertas',                   'RPT_OT_ABIERTAS',      'Órdenes de trabajo en estado abierto'),
                ('Órdenes',         'OT por Período',                'RPT_OT_PERIODO',       'Órdenes filtradas por rango de fechas'),
                ('Órdenes',         'Historial de OT por Equipo',    'RPT_OT_EQUIPO',        'OT agrupadas por equipo'),
                ('Mantenimiento',   'Plan de Mantenimiento',         'RPT_PLAN_MP',          'Instrucciones y tareas programadas'),
                ('Mantenimiento',   'Cumplimiento de Preventivos',   'RPT_MP_CUMPLIMIENTO',  'Porcentaje de ejecución de PM'),
                ('Mano de Obra',    'Listado de Empleados',          'RPT_EMPLEADOS',        'Todos los empleados activos'),
                ('Mano de Obra',    'Horas por Empleado',            'RPT_HORAS_EMP',        'Horas trabajadas por empleado'),
                ('Inventario',      'Stock Actual',                  'RPT_INV_STOCK',        'Cantidad actual de cada ítem');
            """;

        // ══════════════════════════════════════════════════════════════
        // EJECUCIÓN
        // ══════════════════════════════════════════════════════════════
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlForeignKeys);

            // Tablas base (sin FK entre sí)
            stmt.execute(sqlCatalogo);
            stmt.execute(sqlProveedores);

            // Tablas que dependen de catalogo
            stmt.execute(sqlLocalizaciones);
            stmt.execute(sqlEmpleados);

            // Tablas que dependen de localizaciones / empleados
            stmt.execute(sqlEquipos);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlPermisos);

            // Tablas que dependen de equipos / usuarios
            stmt.execute(sqlInstrucciones);
            stmt.execute(sqlOrdenTrabajo);

            // Tablas intermedias y dependientes de OT
            stmt.execute(sqlOtEmpleados);
            stmt.execute(sqlInventario);
            stmt.execute(sqlOtInventario);
            stmt.execute(sqlInstruccionTareas);
            stmt.execute(sqlReportes);

            // Seeds (orden importa por las FK en localizacionSeed)
            stmt.execute(sqlCatalogoSeed);
            stmt.execute(sqlLocalizacionSeed);
            stmt.execute(sqlAdminDefault);
            stmt.execute(sqlAdminPermisos);
            stmt.execute(sqlReportesSeed);
            System.out.println("Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}