package com.example.gman.domain.repository;

import com.example.gman.domain.model.Empleado;

import java.util.List;

/**
 * Contrato de acceso a datos para el módulo Empleados.
 */
public interface EmpleadoRepository {

    List<Empleado> findAll();
    List<Empleado> findByDepartamento(int departamentoId);
    Empleado       findById(int numeroEmpleado);

    void save(Empleado empleado);
    void update(Empleado empleado);
    void delete(int numeroEmpleado);
}