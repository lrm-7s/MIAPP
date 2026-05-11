package com.example.gman.domain.repository;

import com.example.gman.domain.model.Localizacion;

import java.util.List;

/**
 * Contrato de acceso a datos para el módulo Localizaciones.
 */
public interface LocalizacionesRepository {

    List<Localizacion> findAll();
    List<Localizacion> findByDepartamento(int departamentoId);
    Localizacion       findById(int id);
    Localizacion       findByNumero(String numeroLocalizacion);

    void save(Localizacion localizacion);
    void update(Localizacion localizacion);
    void delete(int id);
}