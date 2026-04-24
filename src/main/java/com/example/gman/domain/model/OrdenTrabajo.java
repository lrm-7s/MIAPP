package com.example.gman.domain.model;

import javafx.beans.property.*;
import java.util.ArrayList;
import java.util.List;

public class OrdenTrabajo {

    private final IntegerProperty id              = new SimpleIntegerProperty();
    private final StringProperty  numeroOt        = new SimpleStringProperty();
    private final StringProperty  fechaSolicitud  = new SimpleStringProperty();
    private final StringProperty  estado          = new SimpleStringProperty("ABIERTA");
    private final StringProperty  tipoOt          = new SimpleStringProperty();
    private final StringProperty  prioridad       = new SimpleStringProperty();
    private final StringProperty  fechaRequerida  = new SimpleStringProperty();
    private final StringProperty  descripcion     = new SimpleStringProperty();
    // Nuevos campos
    private final StringProperty  numTarea          = new SimpleStringProperty();
    private final StringProperty  codigoInstruccion = new SimpleStringProperty();
    private final StringProperty  fechaVencimiento  = new SimpleStringProperty();
    private final StringProperty  oficio            = new SimpleStringProperty();

    // Cierre — columnas Estimado/Actual
    private final StringProperty  respuestaEst  = new SimpleStringProperty();
    private final StringProperty  respuestaReal = new SimpleStringProperty();
    private final StringProperty  inicioEst     = new SimpleStringProperty();
    private final StringProperty  inicioReal    = new SimpleStringProperty();
    private final StringProperty  terminoEst    = new SimpleStringProperty();
    private final StringProperty  terminoReal   = new SimpleStringProperty();

// Con sus getters/setters/properties siguiendo el mismo patrón

    // ─── Equipo ──────────────────────────────────────────────────────
    private final IntegerProperty equipoId        = new SimpleIntegerProperty();
    private final StringProperty  localizacion    = new SimpleStringProperty();
    private final StringProperty  estadoEquipo    = new SimpleStringProperty();
    private final StringProperty  recibidoPor     = new SimpleStringProperty();
    private final StringProperty  notasTecnico    = new SimpleStringProperty();

    // ─── Cierre ──────────────────────────────────────────────────────
    private final StringProperty  fechaRespuesta  = new SimpleStringProperty();
    private final StringProperty  fechaInicio     = new SimpleStringProperty();
    private final StringProperty  fechaTermino    = new SimpleStringProperty();
    private final StringProperty  fechaEntrega    = new SimpleStringProperty();
    private final StringProperty  codigoFalla     = new SimpleStringProperty();
    private final StringProperty  descCausa       = new SimpleStringProperty();
    private final StringProperty  accionRealizada = new SimpleStringProperty();
    private final StringProperty  prevencion      = new SimpleStringProperty();
    private final IntegerProperty duracionDias    = new SimpleIntegerProperty();
    private final StringProperty  aceptadaPor     = new SimpleStringProperty();
    private final StringProperty  creadoPor       = new SimpleStringProperty();

    // ─── Empleados asignados (relación N:M) ──────────────────────────
    private List<Empleado> empleados = new ArrayList<>();

    public OrdenTrabajo() {}

    // ─── Getters / Setters / Properties ─────────────────────────────

    public int getId()                       { return id.get(); }
    public void setId(int v)                 { id.set(v); }
    public IntegerProperty idProperty()      { return id; }

    public String getNumeroOt()                    { return numeroOt.get(); }
    public void setNumeroOt(String v)              { numeroOt.set(v); }
    public StringProperty numeroOtProperty()       { return numeroOt; }

    public String getFechaSolicitud()              { return fechaSolicitud.get(); }
    public void setFechaSolicitud(String v)        { fechaSolicitud.set(v); }
    public StringProperty fechaSolicitudProperty() { return fechaSolicitud; }

    public String getEstado()                      { return estado.get(); }
    public void setEstado(String v)                { estado.set(v); }
    public StringProperty estadoProperty()         { return estado; }

    public String getTipoOt()                      { return tipoOt.get(); }
    public void setTipoOt(String v)                { tipoOt.set(v); }
    public StringProperty tipoOtProperty()         { return tipoOt; }

    public String getPrioridad()                   { return prioridad.get(); }
    public void setPrioridad(String v)             { prioridad.set(v); }
    public StringProperty prioridadProperty()      { return prioridad; }

    public String getFechaRequerida()              { return fechaRequerida.get(); }
    public void setFechaRequerida(String v)        { fechaRequerida.set(v); }
    public StringProperty fechaRequeridaProperty() { return fechaRequerida; }

    public String getDescripcion()                 { return descripcion.get(); }
    public void setDescripcion(String v)           { descripcion.set(v); }
    public StringProperty descripcionProperty()    { return descripcion; }

    public int getEquipoId()                       { return equipoId.get(); }
    public void setEquipoId(int v)                 { equipoId.set(v); }
    public IntegerProperty equipoIdProperty()      { return equipoId; }

    public String getLocalizacion()                { return localizacion.get(); }
    public void setLocalizacion(String v)          { localizacion.set(v); }
    public StringProperty localizacionProperty()   { return localizacion; }

    public String getEstadoEquipo()                { return estadoEquipo.get(); }
    public void setEstadoEquipo(String v)          { estadoEquipo.set(v); }
    public StringProperty estadoEquipoProperty()   { return estadoEquipo; }

    public String getRecibidoPor()                 { return recibidoPor.get(); }
    public void setRecibidoPor(String v)           { recibidoPor.set(v); }
    public StringProperty recibidoPorProperty()    { return recibidoPor; }

    public String getNotasTecnico()                { return notasTecnico.get(); }
    public void setNotasTecnico(String v)          { notasTecnico.set(v); }
    public StringProperty notasTecnicoProperty()   { return notasTecnico; }

    public String getFechaRespuesta()              { return fechaRespuesta.get(); }
    public void setFechaRespuesta(String v)        { fechaRespuesta.set(v); }
    public StringProperty fechaRespuestaProperty() { return fechaRespuesta; }

    public String getFechaInicio()                 { return fechaInicio.get(); }
    public void setFechaInicio(String v)           { fechaInicio.set(v); }
    public StringProperty fechaInicioProperty()    { return fechaInicio; }

    public String getFechaTermino()                { return fechaTermino.get(); }
    public void setFechaTermino(String v)          { fechaTermino.set(v); }
    public StringProperty fechaTerminoProperty()   { return fechaTermino; }

    public String getFechaEntrega()                { return fechaEntrega.get(); }
    public void setFechaEntrega(String v)          { fechaEntrega.set(v); }
    public StringProperty fechaEntregaProperty()   { return fechaEntrega; }

    public String getCodigoFalla()                 { return codigoFalla.get(); }
    public void setCodigoFalla(String v)           { codigoFalla.set(v); }
    public StringProperty codigoFallaProperty()    { return codigoFalla; }

    public String getDescCausa()                   { return descCausa.get(); }
    public void setDescCausa(String v)             { descCausa.set(v); }
    public StringProperty descCausaProperty()      { return descCausa; }

    public String getAccionRealizada()             { return accionRealizada.get(); }
    public void setAccionRealizada(String v)       { accionRealizada.set(v); }
    public StringProperty accionRealizadaProperty(){ return accionRealizada; }

    public String getPrevencion()                  { return prevencion.get(); }
    public void setPrevencion(String v)            { prevencion.set(v); }
    public StringProperty prevencionProperty()     { return prevencion; }

    public int getDuracionDias()                   { return duracionDias.get(); }
    public void setDuracionDias(int v)             { duracionDias.set(v); }
    public IntegerProperty duracionDiasProperty()  { return duracionDias; }

    public String getAceptadaPor()                 { return aceptadaPor.get(); }
    public void setAceptadaPor(String v)           { aceptadaPor.set(v); }
    public StringProperty aceptadaPorProperty()    { return aceptadaPor; }

    public String getCreadoPor()                   { return creadoPor.get(); }
    public void setCreadoPor(String v)             { creadoPor.set(v); }
    public StringProperty creadoPorProperty()      { return creadoPor; }

    public List<Empleado> getEmpleados()           { return empleados; }
    public void setEmpleados(List<Empleado> v)     { this.empleados = v; }

    @Override
    public String toString() {
        return numeroOt.get() + " [" + estado.get() + "]";
    }
}