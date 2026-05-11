package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.service.EquipoService;
import com.example.gman.application.service.LocalizacionesService;

import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.model.Localizacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel para el módulo de Equipos.
 *
 * Cambios respecto a la versión anterior:
 *  - Recibe CatalogoService y LocalizacionesService por constructor.
 *  - Los combos de Area, Criticidad y Tipo cargan desde catalogo
 *    filtrando por tipo ('AREA', 'CRITICIDAD', 'TIPO_EQUIPO').
 *  - El combo de Localización (antes "Planta") carga desde localizaciones.
 *  - Los combos retornan List<Catalogo> / List<Localizacion> en lugar de
 *    List<String>, para que el formulario pueda obtener el ID al guardar.
 *  - Se agrega filtrar() que busca también en nombres resueltos.
 */
public class EquiposViewModel {

    private final EquipoService        equipoService;
    private final CatalogoService      catalogoService;
    private final LocalizacionesService localizacionesService;

    private final ObservableList<Equipo> equipos = FXCollections.observableArrayList();

    public EquiposViewModel(EquipoService equipoService,
                            CatalogoService catalogoService,
                            LocalizacionesService localizacionesService) {
        this.equipoService        = equipoService;
        this.catalogoService      = catalogoService;
        this.localizacionesService = localizacionesService;
        cargarEquipos();
    }

    // ── Lista principal ───────────────────────────────────────────────────

    public ObservableList<Equipo> getEquipos() { return equipos; }

    public void cargarEquipos() {
        equipos.setAll(equipoService.obtenerEquipos());
    }

    // ── Búsqueda en tiempo real ───────────────────────────────────────────

    public ObservableList<Equipo> filtrar(String texto) {
        if (texto == null || texto.isBlank()) return equipos;
        String lower = texto.toLowerCase();
        ObservableList<Equipo> filtrados = FXCollections.observableArrayList();
        for (Equipo e : equipos) {
        }
        return filtrados;
    }

    private boolean coincide(String valor, String texto) {
        return valor != null && valor.toLowerCase().contains(texto);
    }

    // ── Validación ────────────────────────────────────────────────────────

    public String validarEquipo(Equipo e) {
        if (e.getCodigo() == null || e.getCodigo().isBlank())
            return "El código es obligatorio.";
        if (e.getNombre() == null || e.getNombre().isBlank())
            return "El nombre es obligatorio.";
        if (e.getAreaId() == 0)
            return "Debe seleccionar un área.";
        if (e.getCriticidadId() == 0)
            return "Debe seleccionar la criticidad.";
        if (e.getTipoId() == 0)
            return "Debe seleccionar el tipo de equipo.";
        return "";
    }

    public boolean existeCodigo(String codigo, int id) {
        return equipoService.existeCodigo(codigo, id);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────
    public void guardarEquipo(Equipo e) {
        equipoService.guardarEquipo(e);
        cargarEquipos();
    }
    public void eliminarEquipo(Equipo e) {
        equipoService.eliminarEquipo(e.getId());
        cargarEquipos();
    }

    public Equipo obtenerPorId(int id) {
        return equipoService.obtenerEquipoPorId(id);
    }

    // ── Combos desde catálogo ─────────────────────────────────────────────

    /**
     * Retorna items de catalogo tipo='AREA' para el ComboBox de área.
     * El controller usa item.getId() al guardar y item.getNombre() como display.
     */
    public ObservableList<Catalogo> getAreas() {
        return FXCollections.observableArrayList(
                catalogoService.listarPorTipo("AREA"));
    }

    /**
     * Retorna items de catalogo tipo='CRITICIDAD'.
     */
    public ObservableList<Catalogo> getCriticidades() {
        return FXCollections.observableArrayList(
                catalogoService.listarPorTipo("CRITICIDAD"));
    }

    /**
     * Retorna items de catalogo tipo='TIPO_EQUIPO'.
     */
    public ObservableList<Catalogo> getTiposEquipo() {
        return FXCollections.observableArrayList(
                catalogoService.listarPorTipo("TIPO_EQUIPO"));
    }

    /**
     * Retorna localizaciones para el ComboBox (antes "Planta").
     * El controller usa item.getId() al guardar y item.getDescripcion() como display.
     */
    public ObservableList<Localizacion> getLocalizaciones() {
        return FXCollections.observableArrayList(
                localizacionesService.listarTodas());
    }
}