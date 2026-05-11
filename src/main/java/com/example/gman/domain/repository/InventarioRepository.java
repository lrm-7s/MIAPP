package com.example.gman.domain.repository;

import com.example.gman.domain.model.Inventario;
import java.util.List;

public interface InventarioRepository {
    List<Inventario> findAll();
    Inventario       findById(int id);
    void             save(Inventario item);
    void             update(Inventario item);
    void             delete(int id);
    boolean          existsCodigo(String codigo, int excludeId);
}