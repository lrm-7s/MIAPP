package com.example.gman.domain.model;

import javafx.beans.property.*;

/**
 * Modelo de dominio para un Empleado.
 * Usa JavaFX Properties para enlace directo con TableView/TextField.
 */
public class Empleado {

    private final IntegerProperty  numeroEmpleado  = new SimpleIntegerProperty();
    private final StringProperty   nombre          = new SimpleStringProperty();
    private final StringProperty   direccion       = new SimpleStringProperty();
    private final StringProperty   posicion        = new SimpleStringProperty();
    private final StringProperty   celular         = new SimpleStringProperty();
    private final StringProperty   departamento    = new SimpleStringProperty();
    private final StringProperty   correo          = new SimpleStringProperty();
    private final DoubleProperty   salarioPorHora  = new SimpleDoubleProperty();
    private final DoubleProperty   tiempoExtra1    = new SimpleDoubleProperty();
    private final DoubleProperty   tiempoExtra2    = new SimpleDoubleProperty();
    private final DoubleProperty   tiempoExtra3    = new SimpleDoubleProperty();

    public Empleado() {}

    // ─── Número de Empleado ──────────────────────────────────────────
    public int getNumeroEmpleado()                  { return numeroEmpleado.get(); }
    public void setNumeroEmpleado(int v)            { numeroEmpleado.set(v); }
    public IntegerProperty numeroEmpleadoProperty() { return numeroEmpleado; }

    // ─── Nombre ──────────────────────────────────────────────────────
    public String getNombre()                { return nombre.get(); }
    public void setNombre(String v)          { nombre.set(v); }
    public StringProperty nombreProperty()   { return nombre; }

    // ─── Dirección ───────────────────────────────────────────────────
    public String getDireccion()             { return direccion.get(); }
    public void setDireccion(String v)       { direccion.set(v); }
    public StringProperty direccionProperty(){ return direccion; }

    // ─── Posición ────────────────────────────────────────────────────
    public String getPosicion()              { return posicion.get(); }
    public void setPosicion(String v)        { posicion.set(v); }
    public StringProperty posicionProperty() { return posicion; }

    // ─── Celular ─────────────────────────────────────────────────────
    public String getCelular()               { return celular.get(); }
    public void setCelular(String v)         { celular.set(v); }
    public StringProperty celularProperty()  { return celular; }

    // ─── Departamento ────────────────────────────────────────────────
    public String getDepartamento()                { return departamento.get(); }
    public void setDepartamento(String v)          { departamento.set(v); }
    public StringProperty departamentoProperty()   { return departamento; }

    // ─── Correo electrónico ──────────────────────────────────────────
    public String getCorreo()                { return correo.get(); }
    public void setCorreo(String v)          { correo.set(v); }
    public StringProperty correoProperty()   { return correo; }

    // ─── Salario por hora ────────────────────────────────────────────
    public double getSalarioPorHora()                  { return salarioPorHora.get(); }
    public void setSalarioPorHora(double v)            { salarioPorHora.set(v); }
    public DoubleProperty salarioPorHoraProperty()     { return salarioPorHora; }

    // ─── Tiempo Extra 1 (tarifa/factor) ─────────────────────────────
    public double getTiempoExtra1()                { return tiempoExtra1.get(); }
    public void setTiempoExtra1(double v)          { tiempoExtra1.set(v); }
    public DoubleProperty tiempoExtra1Property()   { return tiempoExtra1; }

    // ─── Tiempo Extra 2 ──────────────────────────────────────────────
    public double getTiempoExtra2()                { return tiempoExtra2.get(); }
    public void setTiempoExtra2(double v)          { tiempoExtra2.set(v); }
    public DoubleProperty tiempoExtra2Property()   { return tiempoExtra2; }

    // ─── Tiempo Extra 3 ──────────────────────────────────────────────
    public double getTiempoExtra3()                { return tiempoExtra3.get(); }
    public void setTiempoExtra3(double v)          { tiempoExtra3.set(v); }
    public DoubleProperty tiempoExtra3Property()   { return tiempoExtra3; }

    @Override
    public String toString() {
        return nombre.get() + " (#" + numeroEmpleado.get() + ")";
    }
}