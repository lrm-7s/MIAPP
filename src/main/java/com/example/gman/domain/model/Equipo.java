package com.example.gman.domain.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modelo de dominio para la tabla equipos.
 *
 * Cambios respecto a la versión anterior:
 *  - area       (String) → areaId       (int FK → catalogo)
 *  - planta     (String) → localizacionId (int FK → localizaciones)  ← renombrado
 *  - criticidad (String) → criticidadId (int FK → catalogo)
 *  - tipo       (String) → tipoId       (int FK → catalogo)
 *
 * Se agregan campos "display" (solo lectura) para mostrar nombres
 * resueltos en la TableView sin alterar las FKs.
 */
public class Equipo {

    // ── Clave primaria ────────────────────────────────────────────────────
    private final IntegerProperty id             = new SimpleIntegerProperty();

    // ── Campos de texto libre ─────────────────────────────────────────────
    private final StringProperty  codigo         = new SimpleStringProperty();
    private final StringProperty  nombre         = new SimpleStringProperty();
    private final StringProperty  capacidad      = new SimpleStringProperty();
    private final StringProperty  marca          = new SimpleStringProperty();
    private final StringProperty  modelo         = new SimpleStringProperty();
    private final StringProperty  serie          = new SimpleStringProperty();
    private final StringProperty  centroCostos   = new SimpleStringProperty();

    // ── FKs (se persisten en BD) ──────────────────────────────────────────
    private final IntegerProperty areaId         = new SimpleIntegerProperty();
    private final IntegerProperty localizacionId = new SimpleIntegerProperty(); // antes: planta
    private final IntegerProperty criticidadId   = new SimpleIntegerProperty();
    private final IntegerProperty tipoId         = new SimpleIntegerProperty();

    // ── Nombres resueltos (solo para display en TableView, NO se persisten) ──
    private final StringProperty  areaNombre         = new SimpleStringProperty();
    private final StringProperty  localizacionNombre = new SimpleStringProperty();
    private final StringProperty  criticidadNombre   = new SimpleStringProperty();
    private final StringProperty  tipoNombre         = new SimpleStringProperty();

    public Equipo() {}

    // ── id ────────────────────────────────────────────────────────────────
    public int getId()                    { return id.get(); }
    public void setId(int id)             { this.id.set(id); }
    public IntegerProperty idProperty()   { return id; }

    // ── codigo ────────────────────────────────────────────────────────────
    public String getCodigo()                      { return codigo.get(); }
    public void setCodigo(String v)                { codigo.set(v); }
    public StringProperty codigoProperty()         { return codigo; }

    // ── nombre ────────────────────────────────────────────────────────────
    public String getNombre()                      { return nombre.get(); }
    public void setNombre(String v)                { nombre.set(v); }
    public StringProperty nombreProperty()         { return nombre; }

    // ── capacidad ─────────────────────────────────────────────────────────
    public String getCapacidad()                   { return capacidad.get(); }
    public void setCapacidad(String v)             { capacidad.set(v); }
    public StringProperty capacidadProperty()      { return capacidad; }

    // ── marca ─────────────────────────────────────────────────────────────
    public String getMarca()                       { return marca.get(); }
    public void setMarca(String v)                 { marca.set(v); }
    public StringProperty marcaProperty()          { return marca; }

    // ── modelo ────────────────────────────────────────────────────────────
    public String getModelo()                      { return modelo.get(); }
    public void setModelo(String v)                { modelo.set(v); }
    public StringProperty modeloProperty()         { return modelo; }

    // ── serie ─────────────────────────────────────────────────────────────
    public String getSerie()                       { return serie.get(); }
    public void setSerie(String v)                 { serie.set(v); }
    public StringProperty serieProperty()          { return serie; }

    // ── centroCostos ──────────────────────────────────────────────────────
    public String getCentroCostos()                { return centroCostos.get(); }
    public void setCentroCostos(String v)          { centroCostos.set(v); }
    public StringProperty centroCostosProperty()   { return centroCostos; }

    // ── areaId (FK) ───────────────────────────────────────────────────────
    public int getAreaId()                         { return areaId.get(); }
    public void setAreaId(int v)                   { areaId.set(v); }
    public IntegerProperty areaIdProperty()        { return areaId; }

    // ── localizacionId (FK) — reemplaza a "planta" ────────────────────────
    public int getLocalizacionId()                 { return localizacionId.get(); }
    public void setLocalizacionId(int v)           { localizacionId.set(v); }
    public IntegerProperty localizacionIdProperty(){ return localizacionId; }
    // ── criticidadId (FK) ─────────────────────────────────────────────────
    public int getCriticidadId()                   { return criticidadId.get(); }
    public void setCriticidadId(int v)             { criticidadId.set(v); }
    public IntegerProperty criticidadIdProperty()  { return criticidadId; }

    // ── tipoId (FK) ───────────────────────────────────────────────────────
    public int getTipoId()                         { return tipoId.get(); }
    public void setTipoId(int v)                   { tipoId.set(v); }
    public IntegerProperty tipoIdProperty()        { return tipoId; }

    // ── Nombres resueltos (display) ───────────────────────────────────────
    public String getAreaNombre()                      { return areaNombre.get(); }
    public void setAreaNombre(String v)                { areaNombre.set(v); }
    public StringProperty areaNombreProperty()         { return areaNombre; }
    public String getLocalizacionNombre()              { return localizacionNombre.get(); }
    public void setLocalizacionNombre(String v)        { localizacionNombre.set(v); }
    public StringProperty localizacionNombreProperty() { return localizacionNombre; }
    public String getCriticidadNombre()                { return criticidadNombre.get(); }
    public void setCriticidadNombre(String v)          { criticidadNombre.set(v); }
    public StringProperty criticidadNombreProperty()   { return criticidadNombre; }
    public String getTipoNombre()                      { return tipoNombre.get(); }
    public void setTipoNombre(String v)                { tipoNombre.set(v); }
    public StringProperty tipoNombreProperty()         { return tipoNombre; }

    @Override
    public String toString() {
        return nombre.get() + " (" + codigo.get() + ")";
    }
}