package com.example.gman.domain.model;

/**
 * Modelo de dominio para la tabla catalogo.
 * Representa cualquier valor parametrizable del sistema
 * (departamentos, tipos de equipo, prioridades, etc.).
 */
public class Catalogo {

    private int    id;
    private String tipo;
    private String codigo;
    private String nombre;
    private String descripcion;
    // Constructor vacío
    public Catalogo() {}

    // Constructor completo
    public Catalogo(int id, String tipo, String codigo, String nombre, String descripcion) {
        this.id          = id;
        this.tipo        = tipo;
        this.codigo      = codigo;
        this.nombre      = nombre;
        this.descripcion = descripcion;
    }

    // ── Getters & Setters ────────────────────────────────────────────

    public int    getId()          { return id; }
    public void   setId(int id)    { this.id = id; }

    public String getTipo()              { return tipo; }
    public void   setTipo(String tipo)   { this.tipo = tipo; }

    public String getCodigo()                { return codigo; }
    public void   setCodigo(String codigo)   { this.codigo = codigo; }

    public String getNombre()                { return nombre; }
    public void   setNombre(String nombre)   { this.nombre = nombre; }

    public String getDescripcion()                   { return descripcion; }
    public void   setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Representación usada por ComboBox cuando se muestra el objeto directamente.
     */
    @Override
    public String toString() {
        return nombre != null ? nombre : codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Catalogo)) return false;
        return id == ((Catalogo) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}