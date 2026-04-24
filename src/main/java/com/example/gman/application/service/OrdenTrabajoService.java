package com.example.gman.application.service;

import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.domain.repository.OrdenTrabajoRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenTrabajoService {

    private final OrdenTrabajoRepository repo;
    private final EmpleadoService        empleadoService;
    private final EquipoService          equipoService;
    private final CatalogoService        catalogoService;

    public OrdenTrabajoService(OrdenTrabajoRepository repo,
                               EmpleadoService empleadoService,
                               EquipoService equipoService,
                               CatalogoService catalogoService) {
        this.repo            = repo;
        this.empleadoService = empleadoService;
        this.equipoService   = equipoService;
        this.catalogoService = catalogoService;
    }

    // ─── Consultas ───────────────────────────────────────────────────
    public List<OrdenTrabajo> getAll()                   throws SQLException { return repo.getAll(); }
    public List<OrdenTrabajo> getByEstado(String estado) throws SQLException { return repo.getByEstado(estado); }
    public OrdenTrabajo       getById(int id)            throws SQLException { return repo.getById(id); }

    // ─── Crear ───────────────────────────────────────────────────────
    public void crear(OrdenTrabajo ot, List<Integer> empleadosNums,
                      String creadoPor) throws SQLException {
        validar(ot);
        ot.setCreadoPor(creadoPor);
        if (ot.getFechaSolicitud() == null || ot.getFechaSolicitud().isBlank())
            ot.setFechaSolicitud(LocalDate.now().toString());
        repo.crear(ot);
        if (!empleadosNums.isEmpty())
            repo.asignarEmpleados(ot.getId(), empleadosNums);
    }

    // ─── Actualizar ──────────────────────────────────────────────────
    public void actualizar(OrdenTrabajo ot,
                           List<Integer> empleadosNums) throws SQLException {
        validar(ot);
        repo.actualizar(ot);
        repo.asignarEmpleados(ot.getId(), empleadosNums);
    }

    // ─── Cerrar ──────────────────────────────────────────────────────
    public void cerrar(OrdenTrabajo ot) throws SQLException {
        if (ot.getId() == 0) throw new IllegalArgumentException("OT sin ID.");
        repo.cerrar(ot);
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    public void eliminar(int id) throws SQLException { repo.eliminar(id); }

    // ─── Empleados de una OT ─────────────────────────────────────────
    public List<Empleado> getEmpleadosDeOt(int otId) throws SQLException {
        List<Integer> nums = repo.getEmpleadosDeOt(otId);
        return empleadoService.getAllEmpleados().stream()
                .filter(e -> nums.contains(e.getNumeroEmpleado()))
                .collect(Collectors.toList());
    }

    // ─── Prioridades desde catálogo (MISCELANEO) ─────────────────────
    public List<String> getPrioridades() throws SQLException {
        return catalogoService.getByTipo("MISCELANEO").stream()
                .map(c -> c.getNombre())
                .collect(Collectors.toList());
    }

    // ─── Validación ──────────────────────────────────────────────────
    private void validar(OrdenTrabajo ot) {
        if (ot.getNumeroOt() == null || ot.getNumeroOt().isBlank())
            throw new IllegalArgumentException("El número de OT es obligatorio.");
        if (ot.getTipoOt() == null || ot.getTipoOt().isBlank())
            throw new IllegalArgumentException("El tipo de OT es obligatorio.");
        if (ot.getDescripcion() == null || ot.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria.");
    }
}