package com.example.gman.domain.model;

public enum Rol {

    ADMIN(
            "Administrador",
            new String[]{
                    // Módulos completos
                    "EQUIPOS",            "EQUIPOS_EDITAR",       "EQUIPOS_ELIMINAR",
                    "CATALOGO",           "CATALOGO_EDITAR",      "CATALOGO_ELIMINAR",
                    "ORDEN_TRABAJO",      "ORDEN_TRABAJO_EDITAR", "ORDEN_TRABAJO_ELIMINAR",
                    "PLAN_MANTENIMIENTO", "PLAN_MANTENIMIENTO_EDITAR", "PLAN_MANTENIMIENTO_ELIMINAR",
                    "HISTORICO",          "HISTORICO_EDITAR",     "HISTORICO_ELIMINAR",
                    "LOCALIZACIONES",     "LOCALIZACIONES_EDITAR","LOCALIZACIONES_ELIMINAR",
                    "REPORTES",           "REPORTES_EDITAR",      "REPORTES_ELIMINAR",
                    "MANO_OBRA",          "MANO_OBRA_EDITAR",     "MANO_OBRA_ELIMINAR",
                    // Exclusivos del ADMIN
                    "GESTION_USUARIOS",   "GESTION_USUARIOS_EDITAR",
                    "GESTION_EMPLEADOS",  "GESTION_EMPLEADOS_EDITAR"
            }
    ),

    SUPERVISOR(
            "Supervisor",
            new String[]{
                    // Ver + Editar (sin eliminar, sin usuarios/empleados)
                    "EQUIPOS",            "EQUIPOS_EDITAR",
                    "CATALOGO",           "CATALOGO_EDITAR",
                    "ORDEN_TRABAJO",      "ORDEN_TRABAJO_EDITAR",
                    "PLAN_MANTENIMIENTO", "PLAN_MANTENIMIENTO_EDITAR",
                    "HISTORICO",          "HISTORICO_EDITAR",
                    "LOCALIZACIONES",     "LOCALIZACIONES_EDITAR",
                    "REPORTES",           "REPORTES_EDITAR",
                    "MANO_OBRA",          "MANO_OBRA_EDITAR"
            }
    ),

    CONSULTOR(
            "Consultor",
            new String[]{
                    // Solo ver (lectura)
                    "EQUIPOS",
                    "CATALOGO",
                    "ORDEN_TRABAJO",
                    "PLAN_MANTENIMIENTO",
                    "HISTORICO",
                    "LOCALIZACIONES",
                    "REPORTES",
                    "MANO_OBRA"
            }
    );

    // ─────────────────────────────────────────────────────────────────
    private final String   displayName;
    private final String[] permisos;

    Rol(String displayName, String[] permisos) {
        this.displayName = displayName;
        this.permisos    = permisos;
    }

    public String getDisplayName() { return displayName; }
    public String[] getPermisos()  { return permisos; }

    /** Verifica acceso a un módulo o acción específica */
    public boolean tienePermiso(String permiso) {
        for (String p : permisos)
            if (p.equalsIgnoreCase(permiso)) return true;
        return false;
    }

    // ─── Helpers semánticos ──────────────────────────────────────────

    /** ¿Puede ver el módulo? */
    public boolean puedeVer(String modulo) {
        return tienePermiso(modulo);
    }

    /** ¿Puede crear o editar en el módulo? */
    public boolean puedeEditar(String modulo) {
        return tienePermiso(modulo + "_EDITAR");
    }

    /** ¿Puede eliminar en el módulo? */
    public boolean puedeEliminar(String modulo) {
        return tienePermiso(modulo + "_ELIMINAR");
    }
}