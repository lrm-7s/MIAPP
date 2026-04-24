package com.example.gman.domain.repository;

import com.example.gman.domain.model.Equipo;
import java.util.List;

public interface EquipoRepository {
    Equipo save(Equipo equipo);
    void delete(int id);
    List<Equipo> findAll();
    boolean existsCodigo(String codigo, int excludeId);
}