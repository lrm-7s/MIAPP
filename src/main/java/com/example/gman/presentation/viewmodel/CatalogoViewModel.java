package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.domain.model.Catalogo;

import java.util.List;

/**
 * ViewModel del módulo Catálogos.
 * Actúa como intermediario entre el Controller y el Service,
 * validando reglas de presentación antes de delegar al servicio.
 */
public class CatalogoViewModel {

    private final CatalogoService service;

    public CatalogoViewModel(CatalogoService service) {
        this.service = service;
    }

    // ════════════════════════════════════════════════════════════════
    //  CATÁLOGO GENÉRICO (tabla catalogo)
    // ════════════════════════════════════════════════════════════════

    /**
     * Retorna todos los registros de un tipo específico.
     * Ej: listarPorTipo("DEPARTAMENTO")
     */
    public List<Catalogo> listarPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank())
            throw new IllegalArgumentException("El tipo de catálogo no puede estar vacío.");
        return service.listarPorTipo(tipo);
    }

    /**
     * Retorna todos los registros de todos los tipos.
     * Útil para poblar combos en otros módulos.
     */
    public List<Catalogo> listarTodos() {
        return service.listarTodos();
    }

    /**
     * Retorna registros de un tipo para usarlos en un ComboBox.
     * Equivalente a listarPorTipo pero semánticamente claro.
     */
    public List<Catalogo> obtenerParaCombo(String tipo) {
        return listarPorTipo(tipo);
    }

    /**
     * Crea un nuevo registro en el catálogo.
     */
    public void crear(String tipo, String codigo, String nombre, String descripcion) {
        validarCamposObligatorios(codigo, nombre);
        Catalogo c = new Catalogo();
        c.setTipo(tipo.toUpperCase().trim());
        c.setCodigo(codigo.toUpperCase().trim());
        c.setNombre(nombre.trim());
        c.setDescripcion(descripcion);
        service.crear(c);
    }

    /**
     * Actualiza un registro existente.
     */
    public void actualizar(Catalogo catalogo) {
        if (catalogo.getId() <= 0)
            throw new IllegalArgumentException("ID de registro inválido.");
        validarCamposObligatorios(catalogo.getCodigo(), catalogo.getNombre());
        catalogo.setCodigo(catalogo.getCodigo().toUpperCase().trim());
        catalogo.setNombre(catalogo.getNombre().trim());
        service.actualizar(catalogo);
    }

    /**
     * Elimina un registro por su ID.
     * Lanza excepción si el servicio detecta que está en uso (FK constraint).
     */
    public void eliminar(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido.");
        service.eliminar(id);
    }

    // ════════════════════════════════════════════════════════════════
    //  PROVEEDORES — mapeados a Catalogo para reutilizar la vista
    // ════════════════════════════════════════════════════════════════

    /**
     * Devuelve los proveedores como lista de Catalogo (codigo=codigo, nombre=nombre).
     * Permite reutilizar la TableView genérica sin un FXML extra.
     * Para gestión completa de proveedores se usaría ProveedorController dedicado.
     */
    public List<Catalogo> listarProveedoresComoItems() {
        return service.listarProveedoresComoItems();
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIONES COMUNES
    // ════════════════════════════════════════════════════════════════

    private void validarCamposObligatorios(String codigo, String nombre) {
        if (codigo == null || codigo.isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");
    }
}