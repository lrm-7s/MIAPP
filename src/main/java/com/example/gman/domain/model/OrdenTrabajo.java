package com.example.gman.domain.model;

import javafx.beans.property.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de dominio para orden_trabajo.
 *
 * Correcciones respecto a la versión anterior:
 *  - estado, tipoOt, prioridad, estadoEquipo, codigoFalla → IntegerProperty (FKs)
 *  - localizacion (String) → localizacionId (int FK)
 *  - recibidoPor, creadoPor, aceptadaPor (String) → *Id (int FK → usuarios)
 *  - Se agregan instruccionId, fechaVencimiento, oficio
 *  - Se agregan campos *Nombre (display) para TableView sin persistir
 *  - Campos est/real del cierre separados correctamente
 */
public class OrdenTrabajo {

    // ── PK ────────────────────────────────────────────────────────────────
    private final IntegerProperty id               = new SimpleIntegerProperty();

    // ── Texto libre ───────────────────────────────────────────────────────
    private final StringProperty  numeroOt         = new SimpleStringProperty();
    private final StringProperty  fechaSolicitud   = new SimpleStringProperty();
    private final StringProperty  fechaRequerida   = new SimpleStringProperty();
    private final StringProperty  descripcion      = new SimpleStringProperty();
    private final StringProperty  notasTecnico     = new SimpleStringProperty();
    private final StringProperty  oficio           = new SimpleStringProperty();
    private final StringProperty  fechaVencimiento = new SimpleStringProperty();

    // ── FKs → catalogo ───────────────────────────────────────────────────
    private final IntegerProperty estadoId         = new SimpleIntegerProperty();
    private final IntegerProperty tipoOtId         = new SimpleIntegerProperty();
    private final IntegerProperty prioridadId      = new SimpleIntegerProperty();
    private final IntegerProperty estadoEquipoId   = new SimpleIntegerProperty();
    private final IntegerProperty codigoFallaId    = new SimpleIntegerProperty();

    // ── FKs → otras tablas ───────────────────────────────────────────────
    private final IntegerProperty equipoId         = new SimpleIntegerProperty();
    private final IntegerProperty localizacionId   = new SimpleIntegerProperty();
    private final IntegerProperty recibidoPorId    = new SimpleIntegerProperty();
    private final IntegerProperty creadoPorId      = new SimpleIntegerProperty();
    private final IntegerProperty aceptadaPorId    = new SimpleIntegerProperty();
    private final IntegerProperty instruccionId    = new SimpleIntegerProperty();

    // ── Cierre — texto ───────────────────────────────────────────────────
    private final StringProperty  descCausa        = new SimpleStringProperty();
    private final StringProperty  accionRealizada  = new SimpleStringProperty();
    private final StringProperty  prevencion       = new SimpleStringProperty();
    private final IntegerProperty duracionDias     = new SimpleIntegerProperty();

    // ── Fechas cierre ────────────────────────────────────────────────────
    private final StringProperty  fechaRespuesta   = new SimpleStringProperty();
    private final StringProperty  fechaInicio      = new SimpleStringProperty();
    private final StringProperty  fechaTermino     = new SimpleStringProperty();
    private final StringProperty  fechaEntrega     = new SimpleStringProperty();
    private final StringProperty  respuestaEst     = new SimpleStringProperty();
    private final StringProperty  respuestaReal    = new SimpleStringProperty();
    private final StringProperty  inicioEst        = new SimpleStringProperty();
    private final StringProperty  inicioReal       = new SimpleStringProperty();
    private final StringProperty  terminoEst       = new SimpleStringProperty();
    private final StringProperty  terminoReal      = new SimpleStringProperty();

    // ── Display (solo UI, no se persisten) ───────────────────────────────
    private final StringProperty  estadoNombre       = new SimpleStringProperty();
    private final StringProperty  tipoOtNombre       = new SimpleStringProperty();
    private final StringProperty  prioridadNombre    = new SimpleStringProperty();
    private final StringProperty  estadoEquipoNombre = new SimpleStringProperty();
    private final StringProperty  codigoFallaNombre  = new SimpleStringProperty();
    private final StringProperty  localizacionNombre = new SimpleStringProperty();
    private final StringProperty  equipoNombre       = new SimpleStringProperty();
    private final StringProperty  creadoPorNombre    = new SimpleStringProperty();
    private final StringProperty  recibidoPorNombre  = new SimpleStringProperty();

    // ── Relación N:M ─────────────────────────────────────────────────────
    private List<Empleado> empleados = new ArrayList<>();

    public OrdenTrabajo() {}

    // ════ id ═════════════════════════════════════════════════════════
    public int getId()                       { return id.get(); }
    public void setId(int v)                 { id.set(v); }
    public IntegerProperty idProperty()      { return id; }

    // ════ numeroOt ═══════════════════════════════════════════════════
    public String getNumeroOt()                    { return numeroOt.get(); }
    public void setNumeroOt(String v)              { numeroOt.set(v); }
    public StringProperty numeroOtProperty()       { return numeroOt; }

    // ════ fechaSolicitud ══════════════════════════════════════════════
    public String getFechaSolicitud()              { return fechaSolicitud.get(); }
    public void setFechaSolicitud(String v)        { fechaSolicitud.set(v); }
    public StringProperty fechaSolicitudProperty() { return fechaSolicitud; }

    // ════ fechaRequerida ══════════════════════════════════════════════
    public String getFechaRequerida()              { return fechaRequerida.get(); }
    public void setFechaRequerida(String v)        { fechaRequerida.set(v); }
    public StringProperty fechaRequeridaProperty() { return fechaRequerida; }

    // ════ descripcion ═════════════════════════════════════════════════
    public String getDescripcion()                 { return descripcion.get(); }
    public void setDescripcion(String v)           { descripcion.set(v); }
    public StringProperty descripcionProperty()    { return descripcion; }

    // ════ notasTecnico ════════════════════════════════════════════════
    public String getNotasTecnico()                { return notasTecnico.get(); }
    public void setNotasTecnico(String v)          { notasTecnico.set(v); }
    public StringProperty notasTecnicoProperty()   { return notasTecnico; }

    // ════ oficio ══════════════════════════════════════════════════════
    public String getOficio()                      { return oficio.get(); }
    public void setOficio(String v)                { oficio.set(v); }
    public StringProperty oficioProperty()         { return oficio; }

    // ════ fechaVencimiento ════════════════════════════════════════════
    public String getFechaVencimiento()             { return fechaVencimiento.get(); }
    public void setFechaVencimiento(String v)       { fechaVencimiento.set(v); }
    public StringProperty fechaVencimientoProperty(){ return fechaVencimiento; }

    // ════ FKs catalogo ════════════════════════════════════════════════
    public int getEstadoId()                       { return estadoId.get(); }
    public void setEstadoId(int v)                 { estadoId.set(v); }
    public IntegerProperty estadoIdProperty()      { return estadoId; }

    public int getTipoOtId()                       { return tipoOtId.get(); }
    public void setTipoOtId(int v)                 { tipoOtId.set(v); }
    public IntegerProperty tipoOtIdProperty()      { return tipoOtId; }

    public int getPrioridadId()                    { return prioridadId.get(); }
    public void setPrioridadId(int v)              { prioridadId.set(v); }
    public IntegerProperty prioridadIdProperty()   { return prioridadId; }

    public int getEstadoEquipoId()                 { return estadoEquipoId.get(); }
    public void setEstadoEquipoId(int v)           { estadoEquipoId.set(v); }
    public IntegerProperty estadoEquipoIdProperty(){ return estadoEquipoId; }

    public int getCodigoFallaId()                  { return codigoFallaId.get(); }
    public void setCodigoFallaId(int v)            { codigoFallaId.set(v); }
    public IntegerProperty codigoFallaIdProperty() { return codigoFallaId; }

    // ════ FKs otras tablas ════════════════════════════════════════════
    public int getEquipoId()                       { return equipoId.get(); }
    public void setEquipoId(int v)                 { equipoId.set(v); }
    public IntegerProperty equipoIdProperty()      { return equipoId; }

    public int getLocalizacionId()                 { return localizacionId.get(); }
    public void setLocalizacionId(int v)           { localizacionId.set(v); }
    public IntegerProperty localizacionIdProperty(){ return localizacionId; }

    public int getRecibidoPorId()                  { return recibidoPorId.get(); }
    public void setRecibidoPorId(int v)            { recibidoPorId.set(v); }
    public IntegerProperty recibidoPorIdProperty() { return recibidoPorId; }

    public int getCreadoPorId()                    { return creadoPorId.get(); }
    public void setCreadoPorId(int v)              { creadoPorId.set(v); }
    public IntegerProperty creadoPorIdProperty()   { return creadoPorId; }

    public int getAceptadaPorId()                  { return aceptadaPorId.get(); }
    public void setAceptadaPorId(int v)            { aceptadaPorId.set(v); }
    public IntegerProperty aceptadaPorIdProperty() { return aceptadaPorId; }

    public int getInstruccionId()                  { return instruccionId.get(); }
    public void setInstruccionId(int v)            { instruccionId.set(v); }
    public IntegerProperty instruccionIdProperty() { return instruccionId; }

    // ════ Cierre ══════════════════════════════════════════════════════
    public String getDescCausa()                    { return descCausa.get(); }
    public void setDescCausa(String v)              { descCausa.set(v); }
    public StringProperty descCausaProperty()       { return descCausa; }

    public String getAccionRealizada()              { return accionRealizada.get(); }
    public void setAccionRealizada(String v)        { accionRealizada.set(v); }
    public StringProperty accionRealizadaProperty() { return accionRealizada; }

    public String getPrevencion()                   { return prevencion.get(); }
    public void setPrevencion(String v)             { prevencion.set(v); }
    public StringProperty prevencionProperty()      { return prevencion; }

    public int getDuracionDias()                    { return duracionDias.get(); }
    public void setDuracionDias(int v)              { duracionDias.set(v); }
    public IntegerProperty duracionDiasProperty()   { return duracionDias; }

    public String getFechaRespuesta()               { return fechaRespuesta.get(); }
    public void setFechaRespuesta(String v)         { fechaRespuesta.set(v); }
    public StringProperty fechaRespuestaProperty()  { return fechaRespuesta; }

    public String getFechaInicio()                  { return fechaInicio.get(); }
    public void setFechaInicio(String v)            { fechaInicio.set(v); }
    public StringProperty fechaInicioProperty()     { return fechaInicio; }

    public String getFechaTermino()                 { return fechaTermino.get(); }
    public void setFechaTermino(String v)           { fechaTermino.set(v); }
    public StringProperty fechaTerminoProperty()    { return fechaTermino; }

    public String getFechaEntrega()                 { return fechaEntrega.get(); }
    public void setFechaEntrega(String v)           { fechaEntrega.set(v); }
    public StringProperty fechaEntregaProperty()    { return fechaEntrega; }

    public String getRespuestaEst()                 { return respuestaEst.get(); }
    public void setRespuestaEst(String v)           { respuestaEst.set(v); }
    public StringProperty respuestaEstProperty()    { return respuestaEst; }

    public String getRespuestaReal()                { return respuestaReal.get(); }
    public void setRespuestaReal(String v)          { respuestaReal.set(v); }
    public StringProperty respuestaRealProperty()   { return respuestaReal; }

    public String getInicioEst()                    { return inicioEst.get(); }
    public void setInicioEst(String v)              { inicioEst.set(v); }
    public StringProperty inicioEstProperty()       { return inicioEst; }

    public String getInicioReal()                   { return inicioReal.get(); }
    public void setInicioReal(String v)             { inicioReal.set(v); }
    public StringProperty inicioRealProperty()      { return inicioReal; }

    public String getTerminoEst()                   { return terminoEst.get(); }
    public void setTerminoEst(String v)             { terminoEst.set(v); }
    public StringProperty terminoEstProperty()      { return terminoEst; }

    public String getTerminoReal()                  { return terminoReal.get(); }
    public void setTerminoReal(String v)            { terminoReal.set(v); }
    public StringProperty terminoRealProperty()     { return terminoReal; }

    // ════ Display ═════════════════════════════════════════════════════
    public String getEstadoNombre()                       { return estadoNombre.get(); }
    public void setEstadoNombre(String v)                 { estadoNombre.set(v); }
    public StringProperty estadoNombreProperty()          { return estadoNombre; }

    public String getTipoOtNombre()                       { return tipoOtNombre.get(); }
    public void setTipoOtNombre(String v)                 { tipoOtNombre.set(v); }
    public StringProperty tipoOtNombreProperty()          { return tipoOtNombre; }

    public String getPrioridadNombre()                    { return prioridadNombre.get(); }
    public void setPrioridadNombre(String v)              { prioridadNombre.set(v); }
    public StringProperty prioridadNombreProperty()       { return prioridadNombre; }

    public String getEstadoEquipoNombre()                 { return estadoEquipoNombre.get(); }
    public void setEstadoEquipoNombre(String v)           { estadoEquipoNombre.set(v); }
    public StringProperty estadoEquipoNombreProperty()    { return estadoEquipoNombre; }

    public String getCodigoFallaNombre()                  { return codigoFallaNombre.get(); }
    public void setCodigoFallaNombre(String v)            { codigoFallaNombre.set(v); }
    public StringProperty codigoFallaNombreProperty()     { return codigoFallaNombre; }

    public String getLocalizacionNombre()                 { return localizacionNombre.get(); }
    public void setLocalizacionNombre(String v)           { localizacionNombre.set(v); }
    public StringProperty localizacionNombreProperty()    { return localizacionNombre; }

    public String getEquipoNombre()                       { return equipoNombre.get(); }
    public void setEquipoNombre(String v)                 { equipoNombre.set(v); }
    public StringProperty equipoNombreProperty()          { return equipoNombre; }

    public String getCreadoPorNombre()                    { return creadoPorNombre.get(); }
    public void setCreadoPorNombre(String v)              { creadoPorNombre.set(v); }
    public StringProperty creadoPorNombreProperty()       { return creadoPorNombre; }

    public String getRecibidoPorNombre()                  { return recibidoPorNombre.get(); }
    public void setRecibidoPorNombre(String v)            { recibidoPorNombre.set(v); }
    public StringProperty recibidoPorNombreProperty()     { return recibidoPorNombre; }

    public List<Empleado> getEmpleados()               { return empleados; }
    public void setEmpleados(List<Empleado> v)         { this.empleados = v; }

    @Override
    public String toString() {
        return numeroOt.get() + " [" + estadoNombre.get() + "]";
    }
}