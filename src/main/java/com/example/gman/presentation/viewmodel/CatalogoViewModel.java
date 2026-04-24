package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.domain.model.Catalogo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.List;

public class CatalogoViewModel {

    private final CatalogoService service;

    // Lista completa y lista filtrada por tipo activo
    private final ObservableList<Catalogo> items =
            FXCollections.observableArrayList();

    private String tipoActivo = null; // null = todos

    public CatalogoViewModel(CatalogoService service) {
        this.service = service;
    }

    // ─── Lista observable para la tabla ─────────────────────────────
    public ObservableList<Catalogo> getItems() {
        return items;
    }

    // ─── Cargar todos ────────────────────────────────────────────────
    public void cargarTodos() throws SQLException {
        items.clear();
        items.addAll(service.getAll());
        tipoActivo = null;
    }

    // ─── Cargar por tipo (card seleccionada) ─────────────────────────
    public void cargarPorTipo(String tipo) throws SQLException {
        items.clear();
        List<Catalogo> lista = service.getByTipo(tipo);
        items.addAll(lista);
        tipoActivo = tipo;
    }

    // ─── Crear ───────────────────────────────────────────────────────
    public void crear(Catalogo c) throws SQLException {
        service.crear(c);
        recargar();
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    public void actualizar(Catalogo c) throws SQLException {
        service.actualizar(c);
        recargar();
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    public void eliminar(int id) throws SQLException {
        service.eliminar(id);
        recargar();
    }

    // ─── Filtrar por texto ───────────────────────────────────────────
    public ObservableList<Catalogo> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return items;

        String lower = texto.toLowerCase();
        ObservableList<Catalogo> filtrados = FXCollections.observableArrayList();

        for (Catalogo c : items) {
            boolean coincide =
                    c.getNombre().toLowerCase().contains(lower) ||
                            c.getCodigo().toLowerCase().contains(lower) ||
                            c.getTipo().toLowerCase().contains(lower);
            if (coincide) filtrados.add(c);
        }
        return filtrados;
    }

    // ─── Tipo activo ─────────────────────────────────────────────────
    public String getTipoActivo() { return tipoActivo; }

    // ─── Recarga según contexto actual ──────────────────────────────
    private void recargar() throws SQLException {
        if (tipoActivo != null) {
            cargarPorTipo(tipoActivo);
        } else {
            cargarTodos();
        }
    }
}