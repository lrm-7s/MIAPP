package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.service.LocalizacionesService;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Localizacion;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel del módulo Localizaciones.
 * Expone datos listos para la vista y delega al servicio.
 */
public class LocalizacionesViewModel {

    private final LocalizacionesService service;
    private final CatalogoService       catalogoService;

    public LocalizacionesViewModel() {
        this.service         = new LocalizacionesService();
        this.catalogoService = new CatalogoService();
    }

    // ════════════════════════════════════════════════════════════════
    //  DATOS PARA LA TABLA
    // ════════════════════════════════════════════════════════════════

    public List<Localizacion> listarTodas() {
        return service.listarTodas();
    }

    /**
     * Filtra la lista en memoria por texto (número o descripción)
     * y por departamento seleccionado.
     */
    public List<Localizacion> filtrar(List<Localizacion> todas, String texto, Catalogo dept) {
        return todas.stream()
                .filter(l -> {
                    if (texto != null && !texto.isBlank()) {
                        String t = texto.toLowerCase();
                        boolean coincide =
                                (l.getNumeroLocalizacion() != null && l.getNumeroLocalizacion().toLowerCase().contains(t)) ||
                                        (l.getDescripcion()        != null && l.getDescripcion().toLowerCase().contains(t));
                        if (!coincide) return false;
                    }
                    if (dept != null && dept.getId() > 0) {
                        return l.getDepartamentoId() == dept.getId();
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════
    //  DATOS PARA COMBOS
    // ════════════════════════════════════════════════════════════════

    /** Lista de departamentos para el ComboBox del formulario y el filtro. */
    public List<Catalogo> listarDepartamentos() {
        return catalogoService.listarPorTipo("DEPARTAMENTO");
    }

    // ════════════════════════════════════════════════════════════════
    //  CRUD
    // ════════════════════════════════════════════════════════════════

    public void crear(String numero, String descripcion, Catalogo departamento, String notas) {
        Localizacion l = new Localizacion();
        l.setNumeroLocalizacion(numero.toUpperCase().trim());
        l.setDescripcion(descripcion.trim());
        l.setDepartamentoId(departamento != null ? departamento.getId() : 0);
        l.setNotas(notas != null && !notas.isBlank() ? notas.trim() : null);
        service.crear(l);
    }

    public void actualizar(Localizacion loc, String numero, String descripcion,
                           Catalogo departamento, String notas) {
        loc.setNumeroLocalizacion(numero.toUpperCase().trim());
        loc.setDescripcion(descripcion.trim());
        loc.setDepartamentoId(departamento != null ? departamento.getId() : 0);
        loc.setNotas(notas != null && !notas.isBlank() ? notas.trim() : null);
        service.actualizar(loc);
    }

    public void eliminar(int id) {
        service.eliminar(id);
    }
}