package com.example.gman.domain.repository;

import com.example.gman.domain.model.Localizacion;

import java.util.List;

public interface LocalizacionRepository {
    List<Localizacion> findAll();
    Localizacion save(Localizacion localizacion);
    void delete(int id);
    boolean existsNumero(String numero, int excludeId);
}