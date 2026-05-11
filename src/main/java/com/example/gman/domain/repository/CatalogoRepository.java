package com.example.gman.domain.repository;

import com.example.gman.domain.model.Catalogo;

import java.util.List;

/**
 * Contrato de acceso a datos para el módulo Catálogos.
 */
public interface CatalogoRepository {

    List<Catalogo> findAll();
    List<Catalogo> findByTipo(String tipo);
    Catalogo       findById(int id);
    Catalogo       findByCodigo(String codigo);

    /** Proveedores mapeados a Catalogo para reutilizar la TableView. */
    List<Catalogo> findProveedoresComoItems();

    void save(Catalogo catalogo);
    void update(Catalogo catalogo);
    void delete(int id);
}