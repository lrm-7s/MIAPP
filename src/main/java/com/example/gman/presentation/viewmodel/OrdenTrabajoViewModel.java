package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.service.EmpleadoService;
import com.example.gman.application.service.EquipoService;
import com.example.gman.application.service.OrdenTrabajoService;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.model.OrdenTrabajo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class OrdenTrabajoViewModel {

    private final OrdenTrabajoService service;
    private final EmpleadoService     empleadoService;
    private final EquipoService       equipoService;
    private final CatalogoService     catalogoService;

    private final ObservableList<OrdenTrabajo> ordenes    = FXCollections.observableArrayList();
    private final ObservableList<Empleado>     empleados  = FXCollections.observableArrayList();
    private final ObservableList<Equipo>       equipos    = FXCollections.observableArrayList();
    private final ObservableList<String>       prioridades = FXCollections.observableArrayList();

    // Tipos fijos
    private final ObservableList<String> tiposOt = FXCollections.observableArrayList(
            "CORRECTIVA", "PREVENTIVA", "PREDICTIVA");
    private final ObservableList<String> estados = FXCollections.observableArrayList(
            "ABIERTA", "EN_PROCESO", "CERRADA", "CANCELADA");

    public OrdenTrabajoViewModel(OrdenTrabajoService service,
                                 EmpleadoService empleadoService,
                                 EquipoService equipoService,
                                 CatalogoService catalogoService) {
        this.service         = service;
        this.empleadoService = empleadoService;
        this.equipoService   = equipoService;
        this.catalogoService = catalogoService;
    }

    // ─── Carga inicial ───────────────────────────────────────────────
    public void cargarTodo() throws Exception {
        cargarOrdenes();
        cargarEmpleados();
        cargarEquipos();
        cargarPrioridades();
    }

    public void cargarOrdenes() throws SQLException {
        ordenes.clear();
        ordenes.addAll(service.getAll());
    }

    public void cargarEmpleados() throws Exception {
        empleados.clear();
        empleados.addAll(empleadoService.getAllEmpleados());
    }

    public void cargarEquipos() throws Exception {
        equipos.clear();
        equipos.addAll(equipoService.obtenerEquipos());
    }

    public void cargarPrioridades() throws SQLException {
        prioridades.clear();
        prioridades.addAll(service.getPrioridades());
    }

    // ─── CRUD ────────────────────────────────────────────────────────
    public void crear(OrdenTrabajo ot, List<Integer> empleadosNums,
                      String creadoPor) throws SQLException {
        service.crear(ot, empleadosNums, creadoPor);
        cargarOrdenes();
    }

    public void actualizar(OrdenTrabajo ot,
                           List<Integer> empleadosNums) throws SQLException {
        service.actualizar(ot, empleadosNums);
        cargarOrdenes();
    }

    public void cerrar(OrdenTrabajo ot) throws SQLException {
        service.cerrar(ot);
        cargarOrdenes();
    }

    public void eliminar(int id) throws SQLException {
        service.eliminar(id);
        cargarOrdenes();
    }

    // ─── Empleados de una OT ─────────────────────────────────────────
    public List<Empleado> getEmpleadosDeOt(int otId) throws SQLException {
        return service.getEmpleadosDeOt(otId);
    }

    // ─── Filtros ─────────────────────────────────────────────────────
    public ObservableList<OrdenTrabajo> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return ordenes;
        String lower = texto.toLowerCase();
        ObservableList<OrdenTrabajo> filtrados = FXCollections.observableArrayList();
        for (OrdenTrabajo ot : ordenes) {
            boolean coincide =
                    ot.getNumeroOt().toLowerCase().contains(lower) ||
                            (ot.getDescripcion() != null && ot.getDescripcion().toLowerCase().contains(lower)) ||
                            (ot.getEstado() != null && ot.getEstado().toLowerCase().contains(lower)) ||
                            (ot.getTipoOt() != null && ot.getTipoOt().toLowerCase().contains(lower));
            if (coincide) filtrados.add(ot);
        }
        return filtrados;
    }

    // ─── Observables para la UI ──────────────────────────────────────
    public ObservableList<OrdenTrabajo> getOrdenes()    { return ordenes;    }
    public ObservableList<Empleado>     getEmpleados()  { return empleados;  }
    public ObservableList<Equipo>       getEquipos()    { return equipos;    }
    public ObservableList<String>       getPrioridades(){ return prioridades;}
    public ObservableList<String>       getTiposOt()    { return tiposOt;    }
    public ObservableList<String>       getEstados()    { return estados;    }
}