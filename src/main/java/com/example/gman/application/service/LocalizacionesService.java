package com.example.gman.application.service;

import com.example.gman.domain.model.Localizacion;
import com.example.gman.domain.repository.LocalizacionesRepository;
import com.example.gman.infrastructure.repository.LocalizacionesRepositoryImpl;

import java.util.List;

/**
 * Servicio de negocio para el módulo Localizaciones.
 */
public class LocalizacionesService {

    private final LocalizacionesRepository repository;

    public LocalizacionesService() {
        this.repository = new LocalizacionesRepositoryImpl();
    }

    public LocalizacionesService(LocalizacionesRepository repository) {
        this.repository = repository;
    }

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    public List<Localizacion> listarTodas() {
        return repository.findAll();
    }

    public List<Localizacion> listarPorDepartamento(int departamentoId) {
        return repository.findByDepartamento(departamentoId);
    }

    public Localizacion buscarPorId(int id) {
        return repository.findById(id);
    }

    public Localizacion buscarPorNumero(String numero) {
        return repository.findByNumero(numero);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESCRITURA
    // ════════════════════════════════════════════════════════════════

    public void crear(Localizacion loc) {
        validar(loc);
        verificarNumeroUnico(loc.getNumeroLocalizacion(), -1);
        repository.save(loc);
    }

    public void actualizar(Localizacion loc) {
        if (loc.getId() <= 0)
            throw new IllegalArgumentException("ID de localización inválido.");
        validar(loc);
        verificarNumeroUnico(loc.getNumeroLocalizacion(), loc.getId());
        repository.update(loc);
    }

    public void eliminar(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido.");
        repository.delete(id);
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIONES
    // ════════════════════════════════════════════════════════════════

    private void validar(Localizacion loc) {
        if (loc.getNumeroLocalizacion() == null || loc.getNumeroLocalizacion().isBlank())
            throw new IllegalArgumentException("El número de localización es obligatorio.");
        if (loc.getDescripcion() == null || loc.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria.");
    }

    private void verificarNumeroUnico(String numero, int excludeId) {
        Localizacion existente = repository.findByNumero(numero);
        if (existente != null && existente.getId() != excludeId)
            throw new IllegalStateException(
                    "Ya existe una localización con el número «" + numero + "».");
    }
}