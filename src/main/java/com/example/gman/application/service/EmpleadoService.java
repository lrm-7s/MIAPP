package com.example.gman.application.service;

import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.repository.EmpleadoRepository;

import java.util.List;

/**
 * Servicio de lógica de negocio para el módulo de Empleados.
 * Valida los datos antes de delegar al repositorio.
 */
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    // ─── Consultas ───────────────────────────────────────────────────

    public List<Empleado> getAllEmpleados() {
        try {
            return repository.getAllEmpleados();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener empleados", e);
        }
    }

    public Empleado findByNumero(int numeroEmpleado) throws Exception {
        return repository.findByNumero(numeroEmpleado);
    }

    // ─── Crear ───────────────────────────────────────────────────────

    public void crearEmpleado(Empleado empleado) throws Exception {
        validar(empleado);
        repository.addEmpleado(empleado);
    }

    // ─── Actualizar ──────────────────────────────────────────────────

    public void actualizarEmpleado(Empleado empleado) throws Exception {
        validar(empleado);
        repository.updateEmpleado(empleado);
    }

    // ─── Eliminar ────────────────────────────────────────────────────

    public void eliminarEmpleado(int numeroEmpleado) throws Exception {
        repository.deleteEmpleado(numeroEmpleado);
    }

    // ─── Validación ──────────────────────────────────────────────────

    private void validar(Empleado e) {
        if (e.getNombre() == null || e.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        if (e.getPosicion() == null || e.getPosicion().isBlank())
            throw new IllegalArgumentException("La posición es obligatoria.");

        if (e.getDepartamento() == null || e.getDepartamento().isBlank())
            throw new IllegalArgumentException("El departamento es obligatorio.");

        if (e.getCorreo() != null && !e.getCorreo().isBlank()
                && !e.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))
            throw new IllegalArgumentException("El correo electrónico no tiene formato válido.");

        if (e.getSalarioPorHora() < 0)
            throw new IllegalArgumentException("El salario por hora no puede ser negativo.");

        if (e.getTiempoExtra1() < 0 || e.getTiempoExtra2() < 0 || e.getTiempoExtra3() < 0)
            throw new IllegalArgumentException("Los factores de tiempo extra no pueden ser negativos.");
    }
}