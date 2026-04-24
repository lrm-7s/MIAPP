package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.EmpleadoService;
import com.example.gman.domain.model.Empleado;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class EmpleadosViewModel {

    private final EmpleadoService service;

    private final ObservableList<Empleado> empleados =
            FXCollections.observableArrayList();

    public EmpleadosViewModel(EmpleadoService service) {
        this.service = service;
    }

    // ─── Lista observable para la tabla ─────────────────────────────
    public ObservableList<Empleado> getEmpleados() {
        return empleados;
    }

    // ─── Cargar desde BD ─────────────────────────────────────────────
    public void cargarEmpleados() throws Exception {
        empleados.clear();
        List<Empleado> lista = service.getAllEmpleados();
        empleados.addAll(lista);
    }

    // ─── Crear ───────────────────────────────────────────────────────
    public void crearEmpleado(Empleado empleado) throws Exception {
        service.crearEmpleado(empleado);
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    public void actualizarEmpleado(Empleado empleado) throws Exception {
        service.actualizarEmpleado(empleado);
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    public void eliminarEmpleado(int numeroEmpleado) throws Exception {
        service.eliminarEmpleado(numeroEmpleado);
    }

    // ─── Filtrar por texto ───────────────────────────────────────────
    public ObservableList<Empleado> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return empleados;

        String lower = texto.toLowerCase();
        ObservableList<Empleado> filtrados = FXCollections.observableArrayList();

        for (Empleado e : empleados) {
            boolean coincide =
                    e.getNombre()      .toLowerCase().contains(lower) ||
                            e.getDepartamento().toLowerCase().contains(lower) ||
                            e.getPosicion()    .toLowerCase().contains(lower) ||
                            String.valueOf(e.getNumeroEmpleado()).contains(lower);
            if (coincide) filtrados.add(e);
        }
        return filtrados;
    }
}