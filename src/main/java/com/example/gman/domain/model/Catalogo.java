package com.example.gman.domain.model;

import javafx.beans.property.*;

/**
 * Modelo genérico para ítems de catálogo.
 * Cada sub-catálogo (Departamento, CodigoFalla, etc.) es un CatalogoItem.
 */
public class Catalogo {

    private final IntegerProperty id     = new SimpleIntegerProperty();
    private final StringProperty  tipo   = new SimpleStringProperty(); // "DEPARTAMENTO", "FALLA", etc.
    private final StringProperty  codigo = new SimpleStringProperty();
    private final StringProperty  nombre = new SimpleStringProperty();
    private final StringProperty  descripcion = new SimpleStringProperty();

    public Catalogo() {}

    public Catalogo(String tipo, String codigo, String nombre, String descripcion) {
        this.tipo.set(tipo);
        this.codigo.set(codigo);
        this.nombre.set(nombre);
        this.descripcion.set(descripcion);
    }

    // ─── ID ──────────────────────────────────────────────────────────
    public int getId()                  { return id.get(); }
    public void setId(int v)            { id.set(v); }
    public IntegerProperty idProperty() { return id; }

    // ─── Tipo ────────────────────────────────────────────────────────
    public String getTipo()              { return tipo.get(); }
    public void setTipo(String v)        { tipo.set(v); }
    public StringProperty tipoProperty() { return tipo; }

    // ─── Código ──────────────────────────────────────────────────────
    public String getCodigo()              { return codigo.get(); }
    public void setCodigo(String v)        { codigo.set(v); }
    public StringProperty codigoProperty() { return codigo; }

    // ─── Nombre ──────────────────────────────────────────────────────
    public String getNombre()              { return nombre.get(); }
    public void setNombre(String v)        { nombre.set(v); }
    public StringProperty nombreProperty() { return nombre; }

    // ─── Descripción ─────────────────────────────────────────────────
    public String getDescripcion()              { return descripcion.get(); }
    public void setDescripcion(String v)        { descripcion.set(v); }
    public StringProperty descripcionProperty() { return descripcion; }

    @Override
    public String toString() {
        return "[" + tipo.get() + "] " + codigo.get() + " - " + nombre.get();
    }
}