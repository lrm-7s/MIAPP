package com.example.gman.domain.model;

/**
 * Modelo de dominio para la tabla localizaciones.
 * Representa una ubicación física dentro de la planta.
 */
public class Localizacion {

    private int      id;
    private String   numeroLocalizacion;
    private String   descripcion;
    private int      departamentoId;       // FK → catalogo(id) tipo='DEPARTAMENTO'
    private String   departamentoNombre;   // campo de lectura (JOIN), no se persiste
    private String   notas;

    // Constructor vacío
    public Localizacion() {}

    // Constructor completo
    public Localizacion(int id, String numeroLocalizacion, String descripcion,
                        int departamentoId, String departamentoNombre, String notas) {
        this.id                  = id;
        this.numeroLocalizacion  = numeroLocalizacion;
        this.descripcion         = descripcion;
        this.departamentoId      = departamentoId;
        this.departamentoNombre  = departamentoNombre;
        this.notas               = notas;
    }

    // ── Getters & Setters ────────────────────────────────────────────

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getNumeroLocalizacion()                          { return numeroLocalizacion; }
    public void   setNumeroLocalizacion(String numeroLocalizacion) { this.numeroLocalizacion = numeroLocalizacion; }

    public String getDescripcion()                   { return descripcion; }
    public void   setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int  getDepartamentoId()              { return departamentoId; }
    public void setDepartamentoId(int id)        { this.departamentoId = id; }

    public String getDepartamentoNombre()                      { return departamentoNombre; }
    public void   setDepartamentoNombre(String deptNombre)     { this.departamentoNombre = deptNombre; }

    public String getNotas()             { return notas; }
    public void   setNotas(String notas) { this.notas = notas; }

    /** Usado por ComboBox cuando el objeto se muestra directamente. */
    @Override
    public String toString() {
        return numeroLocalizacion != null
                ? numeroLocalizacion + " — " + descripcion
                : descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localizacion)) return false;
        return id == ((Localizacion) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}