package com.example.gman.domain.model;

/**
 * Permisos de un usuario sobre un módulo específico.
 * El ADMIN ignora esto — siempre tiene acceso total.
 */
public class PermisoModulo {

    private String  modulo;
    private boolean puedeVer;
    private boolean puedeCrear;
    private boolean puedeEditar;
    private boolean puedeEliminar;

    public PermisoModulo(String modulo) {
        this.modulo = modulo;
    }

    public PermisoModulo(String modulo, boolean puedeVer, boolean puedeCrear,
                         boolean puedeEditar, boolean puedeEliminar) {
        this.modulo        = modulo;
        this.puedeVer      = puedeVer;
        this.puedeCrear    = puedeCrear;
        this.puedeEditar   = puedeEditar;
        this.puedeEliminar = puedeEliminar;
    }

    public String  getModulo()        { return modulo;        }
    public boolean isPuedeVer()       { return puedeVer;      }
    public boolean isPuedeCrear()     { return puedeCrear;    }
    public boolean isPuedeEditar()    { return puedeEditar;   }
    public boolean isPuedeEliminar()  { return puedeEliminar; }

    public void setPuedeVer(boolean v)      { this.puedeVer      = v; }
    public void setPuedeCrear(boolean v)    { this.puedeCrear    = v; }
    public void setPuedeEditar(boolean v)   { this.puedeEditar   = v; }
    public void setPuedeEliminar(boolean v) { this.puedeEliminar = v; }

    /** Lista de todos los módulos del sistema */
    public static String[] MODULOS = {
            "EQUIPOS",
            "MANO_OBRA",
            "ORDEN_TRABAJO",
            "CATALOGO",
            "PLAN_MANTENIMIENTO",
            "HISTORICO",
            "LOCALIZACIONES",
            "REPORTES",
            "GESTION_USUARIOS"
    };
}