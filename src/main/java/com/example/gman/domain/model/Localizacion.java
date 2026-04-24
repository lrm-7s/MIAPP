package com.example.gman.domain.model;

import javafx.beans.property.*;

public class Localizacion {

    private final IntegerProperty id                  = new SimpleIntegerProperty();
    private final StringProperty  numeroLocalizacion  = new SimpleStringProperty();
    private final StringProperty  descripcion         = new SimpleStringProperty();
    private final StringProperty  departamento        = new SimpleStringProperty();
    private final StringProperty  notas               = new SimpleStringProperty();

    public Localizacion() {}

    public Localizacion(String numeroLocalizacion, String descripcion,
                        String departamento, String notas) {
        this.numeroLocalizacion.set(numeroLocalizacion);
        this.descripcion.set(descripcion);
        this.departamento.set(departamento);
        this.notas.set(notas);
    }

    // ── ID ───────────────────────────────────────────────────────────
    public int getId()                   { return id.get(); }
    public void setId(int v)             { id.set(v); }
    public IntegerProperty idProperty()  { return id; }

    // ── Número de localización ───────────────────────────────────────
    public String getNumeroLocalizacion()               { return numeroLocalizacion.get(); }
    public void setNumeroLocalizacion(String v)         { numeroLocalizacion.set(v); }
    public StringProperty numeroLocalizacionProperty()  { return numeroLocalizacion; }

    // ── Descripción ──────────────────────────────────────────────────
    public String getDescripcion()              { return descripcion.get(); }
    public void setDescripcion(String v)        { descripcion.set(v); }
    public StringProperty descripcionProperty() { return descripcion; }

    // ── Departamento ─────────────────────────────────────────────────
    public String getDepartamento()              { return departamento.get(); }
    public void setDepartamento(String v)        { departamento.set(v); }
    public StringProperty departamentoProperty() { return departamento; }

    // ── Notas ────────────────────────────────────────────────────────
    public String getNotas()              { return notas.get(); }
    public void setNotas(String v)        { notas.set(v); }
    public StringProperty notasProperty() { return notas; }

    @Override
    public String toString() {
        return numeroLocalizacion.get() + " - " + descripcion.get();
    }
}