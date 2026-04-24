package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.EquipoService;
import com.example.gman.domain.model.Equipo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EquiposViewModel {

    private final EquipoService service;
    private final ObservableList<Equipo> equipos =
            FXCollections.observableArrayList();

    public EquiposViewModel(EquipoService service) {
        this.service = service;
        cargarEquipos();
    }

    // ─── Lista completa ──────────────────────────────────────────────
    public ObservableList<Equipo> getEquipos() {
        return equipos;
    }

    public void cargarEquipos() {
        equipos.setAll(service.obtenerEquipos());
    }

    // ─── Búsqueda en tiempo real ─────────────────────────────────────
    public ObservableList<Equipo> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return equipos;

        String lower = texto.toLowerCase();
        ObservableList<Equipo> filtrados =
                FXCollections.observableArrayList();

        for (Equipo e : equipos) {
            if (coincide(e.getCodigo(),     lower) ||
                    coincide(e.getNombre(),     lower) ||
                    coincide(e.getMarca(),      lower) ||
                    coincide(e.getModelo(),     lower) ||
                    coincide(e.getSerie(),      lower) ||
                    coincide(e.getArea(),       lower) ||
                    coincide(e.getPlanta(),     lower) ||
                    coincide(e.getCriticidad(), lower) ||
                    coincide(e.getTipo(),       lower)) {
                filtrados.add(e);
            }
        }
        return filtrados;
    }

    private boolean coincide(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    // ─── Validación ──────────────────────────────────────────────────
    public String validarEquipo(Equipo e) {
        if (e.getCodigo() == null || e.getCodigo().isEmpty())
            return "El código es obligatorio.";
        if (e.getNombre() == null || e.getNombre().isEmpty())
            return "El nombre es obligatorio.";
        return "";
    }

    public boolean existeCodigo(String codigo, int id) {
        return service.existeCodigo(codigo, id);
    }

    // ─── CRUD ────────────────────────────────────────────────────────
    public void guardarEquipo(Equipo e) {
        service.guardarEquipo(e);
        cargarEquipos();
    }

    public void eliminarEquipo(Equipo e) {
        service.eliminarEquipo(e.getId());
        cargarEquipos();
    }

    // ─── ComboBox options ────────────────────────────────────────────
    public ObservableList<String> getAreas() {
        return FXCollections.observableArrayList(
                "Produccion", "Mantenimiento", "Almacenes");
    }

    public ObservableList<String> getPlanta() {
        return FXCollections.observableArrayList(
                "Planta 1", "Planta 2", "Planta 3");
    }

    public ObservableList<String> getCriticidad() {
        return FXCollections.observableArrayList(
                "Critico", "Semicritico", "No Critico");
    }

    public ObservableList<String> getTipo() {
        return FXCollections.observableArrayList(
                "Motor Electrico", "Bombas", "Intercambiadores");
    }
}