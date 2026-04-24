package com.example.gman.domain.repository;

import com.example.gman.domain.model.Empleado;

import java.util.List;

public interface EmpleadoRepository {

    /** Devuelve todos los empleados ordenados por nombre. */
    List<Empleado> getAllEmpleados() throws Exception;

    /** Busca un empleado por su número de empleado (PK). */
    Empleado findByNumero(int numeroEmpleado) throws Exception;

    /** Inserta un nuevo empleado. El número de empleado es asignado por la BD. */
    void addEmpleado(Empleado empleado) throws Exception;

    /** Actualiza todos los campos de un empleado existente. */
    void updateEmpleado(Empleado empleado) throws Exception;

    /** Elimina el empleado con el número indicado. */
    void deleteEmpleado(int numeroEmpleado) throws Exception;
}