package com.example.gman.domain.repository;

import com.example.gman.domain.model.Equipo;
import java.util.List;

public interface EquipoRepository {
    Equipo save(Equipo equipo);
    void delete(int id);
    List<Equipo> findAll();          // incluye JOIN para nombres resueltos
    Equipo findById(int id);         // nuevo: necesario para cargar formulario edición
    boolean existsCodigo(String codigo, int excludeId);
}