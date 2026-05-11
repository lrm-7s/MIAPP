package com.example.gman.application.service;

import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.repository.CatalogoRepository;
import com.example.gman.infrastructure.repository.CatalogoRepositoryImpl;

import java.util.List;

/**
 * Servicio de negocio para el módulo Catálogos.
 * Centraliza toda la lógica de acceso y manipulación
 * de la tabla catalogo y proveedores.
 */
public class CatalogoService {

    private final CatalogoRepository repository;

    // Constructor por defecto — usa la implementación SQLite
    public CatalogoService() {
        this.repository = new CatalogoRepositoryImpl();
    }

    // Constructor con inyección — útil para tests
    public CatalogoService(CatalogoRepository repository) {
        this.repository = repository;
    }

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    public List<Catalogo> listarTodos() {
        return repository.findAll();
    }

    public List<Catalogo> listarPorTipo(String tipo) {
        return repository.findByTipo(tipo.toUpperCase());
    }

    public Catalogo buscarPorId(int id) {
        return repository.findById(id);
    }

    public Catalogo buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo.toUpperCase());
    }

    /**
     * Devuelve proveedores mapeados a Catalogo para reutilizar la TableView.
     */
    public List<Catalogo> listarProveedoresComoItems() {
        return repository.findProveedoresComoItems();
    }

    // ════════════════════════════════════════════════════════════════
    //  ESCRITURA
    // ════════════════════════════════════════════════════════════════

    public void crear(Catalogo catalogo) {
        verificarCodigoUnico(catalogo.getCodigo(), -1);
        repository.save(catalogo);
    }

    public void actualizar(Catalogo catalogo) {
        verificarCodigoUnico(catalogo.getCodigo(), catalogo.getId());
        repository.update(catalogo);
    }

    public void eliminar(int id) {
        // El repositorio lanzará SQLException si hay FK activa
        repository.delete(id);
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIONES
    // ════════════════════════════════════════════════════════════════

    /**
     * Verifica que el código no esté duplicado.
     * @param excludeId  ID a excluir de la búsqueda (para edición). Pasar -1 en creación.
     */
    private void verificarCodigoUnico(String codigo, int excludeId) {
        Catalogo existente = repository.findByCodigo(codigo);
        if (existente != null && existente.getId() != excludeId) {
            throw new IllegalStateException("Ya existe un registro con el código «" + codigo + "».");
        }
    }
}