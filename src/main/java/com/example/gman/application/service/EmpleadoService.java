package com.example.gman.application.service;

import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.repository.EmpleadoRepository;
import com.example.gman.infrastructure.repository.EmpleadoRepositoryImpl;

import java.util.List;

/**
 * Servicio de negocio para el módulo Empleados.
 */
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService() {
        this.repository = new EmpleadoRepositoryImpl();
    }

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    // ════════════════════════════════════════════════════════════════
    //  LECTURA
    // ════════════════════════════════════════════════════════════════

    public List<Empleado> listarTodos() {
        return repository.findAll();
    }

    public List<Empleado> listarPorDepartamento(int departamentoId) {
        return repository.findByDepartamento(departamentoId);
    }

    public Empleado buscarPorId(int numeroEmpleado) {
        return repository.findById(numeroEmpleado);
    }

    // ════════════════════════════════════════════════════════════════
    //  ESCRITURA
    // ════════════════════════════════════════════════════════════════

    public void crear(Empleado empleado) {
        validar(empleado);
        repository.save(empleado);
    }

    public void actualizar(Empleado empleado) {
        if (empleado.getNumeroEmpleado() <= 0)
            throw new IllegalArgumentException("Número de empleado inválido.");
        validar(empleado);
        repository.update(empleado);
    }

    public void eliminar(int numeroEmpleado) {
        if (numeroEmpleado <= 0)
            throw new IllegalArgumentException("Número de empleado inválido.");
        repository.delete(numeroEmpleado);
    }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIONES
    // ════════════════════════════════════════════════════════════════

    private void validar(Empleado e) {
        if (e.getNombre() == null || e.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del empleado es obligatorio.");
        if (e.getPosicionId() <= 0)
            throw new IllegalArgumentException("Debe seleccionar una posición.");
        if (e.getDepartamentoId() <= 0)
            throw new IllegalArgumentException("Debe seleccionar un departamento.");
    }
}