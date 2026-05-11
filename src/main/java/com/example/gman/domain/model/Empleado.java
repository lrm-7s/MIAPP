package com.example.gman.domain.model;

import javafx.beans.property.*;

/**
 * Modelo de dominio para la tabla empleados.
 * Usa JavaFX Properties para binding directo con TableView.
 *
 * CAMBIOS respecto a la versión anterior:
 *  - posicion     (String) → posicionId (int) FK a catalogo
 *  - departamento (String) → departamentoId (int) FK a catalogo
 *  - Se agregan posicionNombre / departamentoNombre para mostrar en tabla (JOIN)
 *  - Se agrega localizacionId FK a localizaciones
 */
public class Empleado {

    // ── Identificador ────────────────────────────────────────────────
    private final IntegerProperty numeroEmpleado = new SimpleIntegerProperty();

    // ── Datos personales ─────────────────────────────────────────────
    private final StringProperty nombre    = new SimpleStringProperty();
    private final StringProperty direccion = new SimpleStringProperty();
    private final StringProperty celular   = new SimpleStringProperty();
    private final StringProperty correo    = new SimpleStringProperty();

    // ── FK → catalogo (posicion) ─────────────────────────────────────
    private int    posicionId;
    private String posicionNombre;   // campo de lectura (JOIN), no se persiste

    // ── FK → catalogo (departamento) ────────────────────────────────
    private int    departamentoId;
    private String departamentoNombre; // campo de lectura (JOIN)

    // ── FK → localizaciones ──────────────────────────────────────────
    private int    localizacionId;
    private String localizacionDesc;   // campo de lectura (JOIN)

    // ── Datos económicos ─────────────────────────────────────────────
    private final DoubleProperty salarioPorHora = new SimpleDoubleProperty();
    private final DoubleProperty tiempoExtra1   = new SimpleDoubleProperty();
    private final DoubleProperty tiempoExtra2   = new SimpleDoubleProperty();
    private final DoubleProperty tiempoExtra3   = new SimpleDoubleProperty();

    // ════════════════════════════════════════════════════════════════
    //  PROPERTIES (para TableView binding)
    // ════════════════════════════════════════════════════════════════

    public IntegerProperty numeroEmpleadoProperty() { return numeroEmpleado; }
    public StringProperty  nombreProperty()          { return nombre; }
    public StringProperty  direccionProperty()       { return direccion; }
    public StringProperty  celularProperty()         { return celular; }
    public StringProperty  correoProperty()          { return correo; }
    public DoubleProperty  salarioPorHoraProperty()  { return salarioPorHora; }
    public DoubleProperty  tiempoExtra1Property()    { return tiempoExtra1; }
    public DoubleProperty  tiempoExtra2Property()    { return tiempoExtra2; }
    public DoubleProperty  tiempoExtra3Property()    { return tiempoExtra3; }

    // Properties de solo lectura para la tabla (nombres del JOIN)
    public StringProperty posicionNombreProperty() {
        return new SimpleStringProperty(posicionNombre != null ? posicionNombre : "—");
    }
    public StringProperty departamentoNombreProperty() {
        return new SimpleStringProperty(departamentoNombre != null ? departamentoNombre : "—");
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS & SETTERS
    // ════════════════════════════════════════════════════════════════

    public int    getNumeroEmpleado()              { return numeroEmpleado.get(); }
    public void   setNumeroEmpleado(int v)         { numeroEmpleado.set(v); }

    public String getNombre()                      { return nombre.get(); }
    public void   setNombre(String v)              { nombre.set(v); }

    public String getDireccion()                   { return direccion.get(); }
    public void   setDireccion(String v)           { direccion.set(v); }

    public String getCelular()                     { return celular.get(); }
    public void   setCelular(String v)             { celular.set(v); }

    public String getCorreo()                      { return correo.get(); }
    public void   setCorreo(String v)              { correo.set(v); }

    // Posición
    public int    getPosicionId()                  { return posicionId; }
    public void   setPosicionId(int v)             { posicionId = v; }
    public String getPosicionNombre()              { return posicionNombre; }
    public void   setPosicionNombre(String v)      { posicionNombre = v; }

    // Departamento
    public int    getDepartamentoId()              { return departamentoId; }
    public void   setDepartamentoId(int v)         { departamentoId = v; }
    public String getDepartamentoNombre()          { return departamentoNombre; }
    public void   setDepartamentoNombre(String v)  { departamentoNombre = v; }

    // Localización
    public int    getLocalizacionId()              { return localizacionId; }
    public void   setLocalizacionId(int v)         { localizacionId = v; }
    public String getLocalizacionDesc()            { return localizacionDesc; }
    public void   setLocalizacionDesc(String v)    { localizacionDesc = v; }

    // Económicos
    public double getSalarioPorHora()              { return salarioPorHora.get(); }
    public void   setSalarioPorHora(double v)      { salarioPorHora.set(v); }

    public double getTiempoExtra1()                { return tiempoExtra1.get(); }
    public void   setTiempoExtra1(double v)        { tiempoExtra1.set(v); }

    public double getTiempoExtra2()                { return tiempoExtra2.get(); }
    public void   setTiempoExtra2(double v)        { tiempoExtra2.set(v); }

    public double getTiempoExtra3()                { return tiempoExtra3.get(); }
    public void   setTiempoExtra3(double v)        { tiempoExtra3.set(v); }

    @Override
    public String toString() {
        return getNumeroEmpleado() + " — " + getNombre();
    }
}