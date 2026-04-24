package com.example.gman.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración global de la aplicación.
 * Lee valores desde config.properties y expone constantes tipadas.
 * Uso: AppConfig.init() al arrancar, luego AppConfig.API_BASE_URL o AppConfig.get("clave")
 */
public class AppConfig {

    // ── Constantes tipadas (se llenan en init()) ──────────────────────────
    public static String APP_NAME;
    public static String APP_VERSION;

    public static String API_BASE_URL;
    public static String EQUIPOS_ENDPOINT;
    public static String MANOOBRA_ENDPOINT;
    public static String ORDENES_ENDPOINT;
    public static String REPORTES_ENDPOINT;

    public static int    TIMEOUT_SECONDS;
    public static int    MAX_RETRIES;
    public static String DEFAULT_LANGUAGE;

    // ── Estado interno ────────────────────────────────────────────────────
    private static final Properties props = new Properties();
    private static boolean initialized = false;

    // Constructor privado: clase utilitaria, no instanciar
    private AppConfig() {}

    // ── API pública ───────────────────────────────────────────────────────

    /**
     * Inicializa la configuración desde config.properties.
     * Los campos estáticos se rellenan aquí, leyendo el archivo primero
     * y usando los valores hardcodeados solo como fallback.
     * Debe llamarse UNA VEZ desde MainApp.start().
     */
    public static void init() {
        if (initialized) return;
        loadProperties();

        APP_NAME    = get("app.name",    "GMAN - Sistema de Gestión de Mantenimiento");
        APP_VERSION = get("app.version", "1.0");

        API_BASE_URL       = get("api.base_url",       "https://api.gman.local/v1");
        EQUIPOS_ENDPOINT   = get("api.equipos",        API_BASE_URL + "/equipos");
        MANOOBRA_ENDPOINT  = get("api.manoobra",       API_BASE_URL + "/manoobra");
        ORDENES_ENDPOINT   = get("api.ordenes",        API_BASE_URL + "/ordenestrabajo");
        REPORTES_ENDPOINT  = get("api.reportes",       API_BASE_URL + "/reportes");

        TIMEOUT_SECONDS  = getInt("network.timeout_seconds", 30);
        MAX_RETRIES      = getInt("network.max_retries",      3);
        DEFAULT_LANGUAGE = get("app.language",               "es");

        initialized = true;
    }

    /** Devuelve un valor del archivo .properties, o defaultValue si no existe. */
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /** Devuelve un valor int del archivo .properties, o defaultValue si no existe / no es número. */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ── Privados ──────────────────────────────────────────────────────────

    private static void loadProperties() {
        try (InputStream is = AppConfig.class
                .getResourceAsStream("/com/example/gman/config.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println("[AppConfig] config.properties no encontrado, usando valores por defecto.");
            }
        } catch (Exception e) {
            System.err.println("[AppConfig] Error cargando config.properties: " + e.getMessage());
        }
    }
}