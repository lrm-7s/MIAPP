package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.service.EmpleadoService;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.Localizacion;
import com.example.gman.application.service.LocalizacionesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel del módulo Empleados.
 *
 * CAMBIOS respecto a la versión anterior:
 *  - posicion / departamento ahora son IDs (Catalogo), no texto libre
 *  - Se exponen listas de Catalogo y Localizacion para poblar combos
 *  - Se elimina dependencia de EmpleadoFormController (formulario integrado)
 */
public class EmpleadosViewModel {

    private final EmpleadoService     empleadoService;
    private final CatalogoService     catalogoService;
    private final LocalizacionesService localizacionesService;

    // Lista observable para binding con TableView
    private final ObservableList<Empleado> empleados =
            FXCollections.observableArrayList();

    // Caché para filtrado en memoria
    private List<Empleado> todosLosEmpleados;

    public EmpleadosViewModel() {
        this.empleadoService      = new EmpleadoService();
        this.catalogoService      = new CatalogoService();
        this.localizacionesService = new LocalizacionesService();
    }

    // ════════════════════════════════════════════════════════════════
    //  DATOS PARA LA TABLA
    // ════════════════════════════════════════════════════════════════

    public ObservableList<Empleado> getEmpleados() {
        return empleados;
    }

    public void cargarEmpleados() {
        todosLosEmpleados = empleadoService.listarTodos();
        empleados.setAll(todosLosEmpleados);
    }

    /**
     * Filtra en memoria por nombre, posición o departamento.
     */
    public ObservableList<Empleado> filtrar(String texto) {
        if (texto == null || texto.isBlank()) {
            return FXCollections.observableArrayList(todosLosEmpleados);
        }
        String t = texto.toLowerCase();
        List<Empleado> filtrados = todosLosEmpleados.stream()
                .filter(e ->
                        (e.getNombre()             != null && e.getNombre().toLowerCase().contains(t)) ||
                                (e.getPosicionNombre()     != null && e.getPosicionNombre().toLowerCase().contains(t)) ||
                                (e.getDepartamentoNombre() != null && e.getDepartamentoNombre().toLowerCase().contains(t)) ||
                                (e.getCorreo()             != null && e.getCorreo().toLowerCase().contains(t)))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(filtrados);
    }

    // ════════════════════════════════════════════════════════════════
    //  DATOS PARA COMBOS (formulario)
    // ════════════════════════════════════════════════════════════════

    public List<Catalogo> listarPosiciones() {
        return catalogoService.listarPorTipo("POSICION");
    }

    public List<Catalogo> listarDepartamentos() {
        return catalogoService.listarPorTipo("DEPARTAMENTO");
    }

    public List<Localizacion> listarLocalizaciones() {
        return localizacionesService.listarTodas();
    }

    // ════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════

    public void crearEmpleado(Empleado e) {
        empleadoService.crear(e);
    }

    public void actualizarEmpleado(Empleado e) {
        empleadoService.actualizar(e);
    }

    public void eliminarEmpleado(int numeroEmpleado) {
        empleadoService.eliminar(numeroEmpleado);
    }
}