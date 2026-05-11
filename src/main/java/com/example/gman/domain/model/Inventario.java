package com.example.gman.domain.model;

import javafx.beans.property.*;

public class Inventario {

    private final IntegerProperty id              = new SimpleIntegerProperty();
    private final StringProperty  codigo          = new SimpleStringProperty();
    private final StringProperty  descripcion     = new SimpleStringProperty();
    private final IntegerProperty cantidad        = new SimpleIntegerProperty();
    private final DoubleProperty  precioUnitario  = new SimpleDoubleProperty();

    // FK → proveedores
    private int    proveedorId;
    private String proveedorNombre; // JOIN, solo lectura

    // ── Properties ───────────────────────────────────────────────────
    public IntegerProperty idProperty()             { return id; }
    public StringProperty  codigoProperty()         { return codigo; }
    public StringProperty  descripcionProperty()    { return descripcion; }
    public IntegerProperty cantidadProperty()       { return cantidad; }
    public DoubleProperty  precioUnitarioProperty() { return precioUnitario; }

    public StringProperty proveedorNombreProperty() {
        return new SimpleStringProperty(proveedorNombre != null ? proveedorNombre : "—");
    }

    // ── Getters & Setters ────────────────────────────────────────────
    public int    getId()                     { return id.get(); }
    public void   setId(int v)                { id.set(v); }

    public String getCodigo()                 { return codigo.get(); }
    public void   setCodigo(String v)         { codigo.set(v); }

    public String getDescripcion()            { return descripcion.get(); }
    public void   setDescripcion(String v)    { descripcion.set(v); }

    public int    getCantidad()               { return cantidad.get(); }
    public void   setCantidad(int v)          { cantidad.set(v); }

    public double getPrecioUnitario()         { return precioUnitario.get(); }
    public void   setPrecioUnitario(double v) { precioUnitario.set(v); }

    public int    getProveedorId()            { return proveedorId; }
    public void   setProveedorId(int v)       { proveedorId = v; }

    public String getProveedorNombre()        { return proveedorNombre; }
    public void   setProveedorNombre(String v){ proveedorNombre = v; }

    @Override
    public String toString() { return getCodigo() + " — " + getDescripcion(); }
}