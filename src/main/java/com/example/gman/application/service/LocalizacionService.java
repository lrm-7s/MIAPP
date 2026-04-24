package com.example.gman.application.service;

import com.example.gman.domain.model.Localizacion;
import com.example.gman.domain.repository.LocalizacionRepository;

import java.util.List;

public class LocalizacionService {

    private final LocalizacionRepository repository;

    public LocalizacionService(LocalizacionRepository repository) {
        this.repository = repository;
    }

    // ── Obtener todos ────────────────────────────────────────────────
    public List<Localizacion> getAll() {
        return repository.findAll();
    }

    // ── Guardar (crear o actualizar) ─────────────────────────────────
    public void guardar(Localizacion loc) {
        String error = validar(loc);
        if (!error.isEmpty()) throw new IllegalArgumentException(error);

        if (repository.existsNumero(loc.getNumeroLocalizacion(), loc.getId())) {
            throw new IllegalArgumentException(
                    "El número de localización ya existe, usa uno diferente.");
        }
        repository.save(loc);
    }

    // ── Eliminar ─────────────────────────────────────────────────────
    public void eliminar(int id) {
        repository.delete(id);
    }

    // ── Validación ───────────────────────────────────────────────────
    public String validar(Localizacion loc) {
        if (loc.getNumeroLocalizacion() == null || loc.getNumeroLocalizacion().isBlank())
            return "El número de localización es obligatorio.";
        if (loc.getDescripcion() == null || loc.getDescripcion().isBlank())
            return "La descripción es obligatoria.";
        return "";
    }
}