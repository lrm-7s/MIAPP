package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.*;
import com.example.gman.domain.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class OrdenTrabajoViewModel {

    private final OrdenTrabajoService    service;
    private final EmpleadoService        empleadoService;
    private final EquipoService          equipoService;
    private final CatalogoService        catalogoService;
    private final LocalizacionesService  localizacionesService;

    private final ObservableList<OrdenTrabajo> ordenes       = FXCollections.observableArrayList();
    private final ObservableList<Empleado>     empleados     = FXCollections.observableArrayList();
    private final ObservableList<Equipo>       equipos       = FXCollections.observableArrayList();
    private final ObservableList<Catalogo>     estadosOt     = FXCollections.observableArrayList();
    private final ObservableList<Catalogo>     tiposOt       = FXCollections.observableArrayList();
    private final ObservableList<Catalogo>     prioridades   = FXCollections.observableArrayList();
    private final ObservableList<Catalogo>     estadosEquipo = FXCollections.observableArrayList();
    private final ObservableList<Catalogo>     codigosFalla  = FXCollections.observableArrayList();
    private final ObservableList<Localizacion> localizaciones= FXCollections.observableArrayList();

    public OrdenTrabajoViewModel(OrdenTrabajoService service,
                                 EmpleadoService empleadoService,
                                 EquipoService equipoService,
                                 CatalogoService catalogoService,
                                 LocalizacionesService localizacionesService) {
        this.service               = service;
        this.empleadoService       = empleadoService;
        this.equipoService         = equipoService;
        this.catalogoService       = catalogoService;
        this.localizacionesService = localizacionesService;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGA
    // ════════════════════════════════════════════════════════════════

    public void cargarTodo() throws SQLException {
        cargarOrdenes();
        cargarEmpleados();
        cargarEquipos();
        cargarCombos();
    }

    public void cargarOrdenes() throws SQLException {
        ordenes.setAll(service.getAll());
    }

    // CORRECCIÓN: getAllEmpleados() → listarTodos()
    public void cargarEmpleados() {
        empleados.setAll(empleadoService.listarTodos());
    }

    // CORRECCIÓN: nombre del método consistente con otros servicios
    public void cargarEquipos() {
        try {
            equipos.setAll(equipoService.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarCombos() {
        estadosOt.setAll(service.getEstadosOt());
        tiposOt.setAll(service.getTiposOt());
        prioridades.setAll(service.getPrioridades());
        estadosEquipo.setAll(service.getEstadosEquipo());
        codigosFalla.setAll(service.getCodigosFalla());
        localizaciones.setAll(localizacionesService.listarTodas());
    }

    // ════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════

    public void crear(OrdenTrabajo ot, List<Integer> empleadosNums,
                      String creadoPorUsername) throws SQLException {
        service.crear(ot, empleadosNums, creadoPorUsername);
        cargarOrdenes();
    }

    public void actualizar(OrdenTrabajo ot, List<Integer> empleadosNums) throws SQLException {
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

    // ════════════════════════════════════════════════════════════════
    //  EMPLEADOS DE UNA OT
    // ════════════════════════════════════════════════════════════════

    public List<Empleado> getEmpleadosDeOt(int otId) throws SQLException {
        return service.getEmpleadosDeOt(otId);
    }

    // ════════════════════════════════════════════════════════════════
    //  FILTROS
    // ════════════════════════════════════════════════════════════════

    public ObservableList<OrdenTrabajo> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return ordenes;
        String lower = texto.toLowerCase();
        ObservableList<OrdenTrabajo> filtrados = FXCollections.observableArrayList();
        for (OrdenTrabajo ot : ordenes) {
            if (contiene(ot.getNumeroOt(),        lower) ||
                    contiene(ot.getDescripcion(),      lower) ||
                    contiene(ot.getEstadoNombre(),     lower) ||
                    contiene(ot.getTipoOtNombre(),     lower) ||
                    contiene(ot.getPrioridadNombre(),  lower) ||
                    contiene(ot.getEquipoNombre(),     lower)) {
                filtrados.add(ot);
            }
        }
        return filtrados;
    }

    private boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    // ════════════════════════════════════════════════════════════════
    //  OBSERVABLES
    // ════════════════════════════════════════════════════════════════

    public ObservableList<OrdenTrabajo> getOrdenes()        { return ordenes;        }
    public ObservableList<Empleado>     getEmpleados()      { return empleados;      }
    public ObservableList<Equipo>       getEquipos()        { return equipos;        }
    public ObservableList<Catalogo>     getEstadosOt()      { return estadosOt;      }
    public ObservableList<Catalogo>     getTiposOt()        { return tiposOt;        }
    public ObservableList<Catalogo>     getPrioridades()    { return prioridades;    }
    public ObservableList<Catalogo>     getEstadosEquipo()  { return estadosEquipo;  }
    public ObservableList<Catalogo>     getCodigosFalla()   { return codigosFalla;   }
    public ObservableList<Localizacion> getLocalizaciones() { return localizaciones; }
}