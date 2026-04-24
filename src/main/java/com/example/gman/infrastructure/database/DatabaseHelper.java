package com.example.gman.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private static void initDatabase() {

        String sqlEquipos = """
            CREATE TABLE IF NOT EXISTS equipos (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo        TEXT NOT NULL UNIQUE,
                nombre        TEXT NOT NULL,
                capacidad     TEXT,
                marca         TEXT,
                modelo        TEXT,
                serie         TEXT,
                area          TEXT,
                planta        TEXT,
                centro_costos TEXT,
                criticidad    TEXT,
                tipo          TEXT
            );  
            """;
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                nombre   TEXT,
                password TEXT NOT NULL,
                rol      TEXT NOT NULL DEFAULT 'CONSULTOR'
            );
            """;

        String sqlEmpleados = """
            CREATE TABLE IF NOT EXISTS empleados (
                numero_empleado  INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre           TEXT NOT NULL,
                direccion        TEXT,
                posicion         TEXT NOT NULL,
                celular          TEXT,
                departamento     TEXT NOT NULL,
                correo           TEXT,
                salario_por_hora REAL,
                tiempo_extra1    REAL,
                tiempo_extra2    REAL,
                tiempo_extra3    REAL
            );
            """;

        String sqlCatalogo = """
            CREATE TABLE IF NOT EXISTS catalogo (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo        TEXT NOT NULL,
                codigo      TEXT NOT NULL UNIQUE,
                nombre      TEXT NOT NULL,
                descripcion TEXT
            );
            """;

        // ── LOCALIZACIONES ────────────────────────────────────────────
        String sqlLocalizaciones = """
            CREATE TABLE IF NOT EXISTS localizaciones (
                id                   INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_localizacion  TEXT NOT NULL UNIQUE,
                descripcion          TEXT NOT NULL,
                departamento         TEXT,
                notas                TEXT
            );
            """;

        // ── OT ────────────────────────────────────────────────────────
        String sqlOrdenTrabajo = """
            CREATE TABLE IF NOT EXISTS orden_trabajo (
                id               INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_ot        TEXT NOT NULL UNIQUE,
                fecha_solicitud  TEXT NOT NULL,
                estado           TEXT NOT NULL DEFAULT 'ABIERTA',
                tipo_ot          TEXT NOT NULL,
                prioridad        TEXT,
                fecha_requerida  TEXT,
                descripcion      TEXT,
                equipo_id        INTEGER,
                localizacion     TEXT,
                estado_equipo    TEXT,
                recibido_por     TEXT,
                notas_tecnico    TEXT,
                fecha_respuesta  TEXT,
                fecha_inicio     TEXT,
                fecha_termino    TEXT,
                fecha_entrega    TEXT,
                codigo_falla     TEXT,
                desc_causa       TEXT,
                accion_realizada TEXT,
                prevencion       TEXT,
                duracion_dias    INTEGER,
                aceptada_por     TEXT,
                creado_por       TEXT,
                num_tarea           TEXT,
                codigo_instruccion  TEXT,
                fecha_vencimiento   TEXT,
                oficio              TEXT,
                respuesta_est       TEXT,
                respuesta_real      TEXT,
                inicio_est          TEXT,
                inicio_real         TEXT,
                termino_est         TEXT,
                termino_real        TEXT,
                FOREIGN KEY (equipo_id) REFERENCES equipos(id)
            );
            """;

        String sqlOtEmpleados = """
            CREATE TABLE IF NOT EXISTS ot_empleados (
                ot_id           INTEGER NOT NULL,
                empleado_numero INTEGER NOT NULL,
                PRIMARY KEY (ot_id, empleado_numero),
                FOREIGN KEY (ot_id)           REFERENCES orden_trabajo(id),
                FOREIGN KEY (empleado_numero) REFERENCES empleados(numero_empleado)
            );
            """;

        // ── Seeds ─────────────────────────────────────────────────────
        String sqlAdminDefault = """
            INSERT OR IGNORE INTO usuarios (username, nombre, password, rol)
            VALUES ('admin', 'Administrador', 'admin123', 'ADMIN');
            """;

        String sqlCatalogoSeed = """
            INSERT OR IGNORE INTO catalogo (tipo, codigo, nombre, descripcion) VALUES
                ('DEPARTAMENTO', 'DEP-01', 'Mantenimiento',    'Dpto. de mantenimiento'),
                ('DEPARTAMENTO', 'DEP-02', 'Producción',       'Dpto. de producción'),
                ('FALLA',        'FAL-01', 'Falla Eléctrica',  'Problemas eléctricos'),
                ('FALLA',        'FAL-02', 'Falla Mecánica',   'Problemas mecánicos'),
                ('TIPO_EQUIPO',  'TEQ-01', 'Compresor',        'Equipos compresores'),
                ('TIPO_EQUIPO',  'TEQ-02', 'Bomba',            'Equipos de bombeo'),
                ('PROVEEDOR',    'PRV-01', 'Proveedor General','Proveedor por defecto'),
                ('MISCELANEO',   'MIS-01', 'Alta',             'Prioridad alta'),
                ('MISCELANEO',   'MIS-02', 'Media',            'Prioridad media'),
                ('MISCELANEO',   'MIS-03', 'Baja',             'Prioridad baja');
            """;

        String sqlLocalizacionSeed = """
            INSERT OR IGNORE INTO localizaciones
                (numero_localizacion, descripcion, departamento, notas) VALUES
                ('LOC-01', 'Planta Principal',    'Mantenimiento', 'Planta principal de operaciones'),
                ('LOC-02', 'Almacén Central',     'Producción',    'Almacén de materia prima'),
                ('LOC-03', 'Área de Compresores', 'Mantenimiento', 'Zona de equipos de compresión');
            """;

        String sqlMigracion = """
            ALTER TABLE usuarios ADD COLUMN rol TEXT NOT NULL DEFAULT 'CONSULTOR';
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // ── Tablas ────────────────────────────────────────────────
            stmt.execute(sqlEquipos);
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlEmpleados);
            stmt.execute(sqlCatalogo);
            stmt.execute(sqlLocalizaciones);   // ← NUEVO
            stmt.execute(sqlOrdenTrabajo);
            stmt.execute(sqlOtEmpleados);

            // ── Migración silenciosa ──────────────────────────────────
            try {
                stmt.execute(sqlMigracion);
                System.out.println("Migración: columna 'rol' agregada.");
            } catch (SQLException ignored) { /* columna ya existe */ }

            // ── Seeds ─────────────────────────────────────────────────
            stmt.execute(sqlAdminDefault);
            stmt.execute(sqlCatalogoSeed);
            stmt.execute(sqlLocalizacionSeed); // ← NUEVO

            System.out.println("Base de datos inicializada correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}