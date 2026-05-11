package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.InventarioService;
import com.example.gman.domain.model.Inventario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class InventarioViewModel {

    private final InventarioService service;
    private final ObservableList<Inventario> items = FXCollections.observableArrayList();

    public InventarioViewModel(InventarioService service) {
        this.service = service;
    }

    // ── Datos ─────────────────────────────────────────────────────────
    public ObservableList<Inventario> getItems() { return items; }

    public void cargar() {
        items.setAll(service.listarTodos());
    }

    public ObservableList<Inventario> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return items;
        String q = texto.toLowerCase();
        return items.filtered(i ->
                i.getCodigo().toLowerCase().contains(q) ||
                        i.getDescripcion().toLowerCase().contains(q));
    }

    // ── CRUD ──────────────────────────────────────────────────────────
    public void crear(Inventario item) {
        service.guardar(item);
        cargar();
    }

    public void actualizar(Inventario item) {
        service.actualizar(item);
        cargar();
    }

    public void eliminar(int id) {
        service.eliminar(id);
        cargar();
    }

    // ── Consumo por OT ───────────────────────────────────────────────
    public List<String[]> consumoPorOT(int inventarioId) {
        return service.consumoPorOT(inventarioId);
    }
}