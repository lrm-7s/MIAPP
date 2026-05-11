package com.example.gman.application.service;

import com.example.gman.domain.model.Inventario;
import com.example.gman.domain.repository.InventarioRepository;
import com.example.gman.infrastructure.repository.InventarioRepositoryImpl;

import java.util.List;

public class InventarioService {

    private final InventarioRepository repo;

    public InventarioService(InventarioRepository repo) {
        this.repo = repo;
    }

    public List<Inventario> listarTodos() {
        return repo.findAll();
    }

    public Inventario obtenerPorId(int id) {
        return repo.findById(id);
    }

    public void guardar(Inventario item) {
        validar(item);
        if (repo.existsCodigo(item.getCodigo(), item.getId()))
            throw new IllegalArgumentException("Ya existe un ítem con el código «" + item.getCodigo() + "».");
        repo.save(item);
    }

    public void actualizar(Inventario item) {
        validar(item);
        if (repo.existsCodigo(item.getCodigo(), item.getId()))
            throw new IllegalArgumentException("Ya existe otro ítem con el código «" + item.getCodigo() + "».");
        repo.update(item);
    }

    public void eliminar(int id) {
        repo.delete(id);
    }

    /** Consumo de un ítem en OTs: filas [numero_ot, descripcion, cantidad_usada] */
    public List<String[]> consumoPorOT(int inventarioId) {
        if (repo instanceof InventarioRepositoryImpl impl) {
            return impl.findConsumoByOT(inventarioId);
        }
        return List.of();
    }

    // ── Validación ───────────────────────────────────────────────────
    private void validar(Inventario item) {
        if (item.getCodigo() == null || item.getCodigo().isBlank())
            throw new IllegalArgumentException("El código es obligatorio.");
        if (item.getDescripcion() == null || item.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria.");
        if (item.getCantidad() < 0)
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        if (item.getPrecioUnitario() < 0)
            throw new IllegalArgumentException("El precio no puede ser negativo.");
    }
}