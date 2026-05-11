package com.example.gman.application.service;

import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.domain.repository.OrdenTrabajoRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
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

    // ════════════════════════════════════════════════════════════════
    //  CONSULTAS
    // ════════════════════════════════════════════════════════════════

    public List<OrdenTrabajo> getAll()                  throws SQLException { return repo.getAll(); }
    public List<OrdenTrabajo> getByEstado(int estadoId) throws SQLException { return repo.getByEstado(estadoId); }
    public OrdenTrabajo       getById(int id)           throws SQLException { return repo.getById(id); }

    // ════════════════════════════════════════════════════════════════
    //  CREAR
    // ════════════════════════════════════════════════════════════════

    public void crear(OrdenTrabajo ot, List<Integer> empleadosNums,
                      String creadoPorUsername) throws SQLException {
        validar(ot);
        // El repositorio resuelve el id del usuario por su username
        if (creadoPorUsername != null) {
            int userId = repo.resolverUsuarioId(creadoPorUsername);
            ot.setCreadoPorId(userId);
        }
        if (ot.getFechaSolicitud() == null || ot.getFechaSolicitud().isBlank())
            ot.setFechaSolicitud(LocalDateTime.now().toString());
        repo.crear(ot);
        if (empleadosNums != null && !empleadosNums.isEmpty())
            repo.asignarEmpleados(ot.getId(), empleadosNums);
    }

    // ════════════════════════════════════════════════════════════════
    //  ACTUALIZAR
    // ════════════════════════════════════════════════════════════════

    public void actualizar(OrdenTrabajo ot, List<Integer> empleadosNums) throws SQLException {
        validar(ot);
        repo.actualizar(ot);
        repo.asignarEmpleados(ot.getId(), empleadosNums);
    }

    // ════════════════════════════════════════════════════════════════
    //  CERRAR
    // ════════════════════════════════════════════════════════════════

    public void cerrar(OrdenTrabajo ot) throws SQLException {
        if (ot.getId() == 0) throw new IllegalArgumentException("OT sin ID.");
        repo.cerrar(ot);
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ════════════════════════════════════════════════════════════════

    public void eliminar(int id) throws SQLException { repo.eliminar(id); }

    // ════════════════════════════════════════════════════════════════
    //  EMPLEADOS DE UNA OT
    // CORRECCIÓN: getAllEmpleados() → listarTodos()
    // ════════════════════════════════════════════════════════════════

    public List<Empleado> getEmpleadosDeOt(int otId) throws SQLException {
        List<Integer> nums = repo.getEmpleadosDeOt(otId);
        return empleadoService.listarTodos().stream()
                .filter(e -> nums.contains(e.getNumeroEmpleado()))
                .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════
    //  COMBOS DESDE CATÁLOGO
    // ════════════════════════════════════════════════════════════════

    public List<Catalogo> getEstadosOt()     { return catalogoService.listarPorTipo("ESTADO_OT");     }
    public List<Catalogo> getTiposOt()       { return catalogoService.listarPorTipo("TIPO_OT");       }
    public List<Catalogo> getPrioridades()   { return catalogoService.listarPorTipo("PRIORIDAD");     }
    public List<Catalogo> getEstadosEquipo() { return catalogoService.listarPorTipo("ESTADO_EQUIPO"); }
    public List<Catalogo> getCodigosFalla()  { return catalogoService.listarPorTipo("FALLA");         }

    // ════════════════════════════════════════════════════════════════
    //  VALIDACIÓN
    // ════════════════════════════════════════════════════════════════

    private void validar(OrdenTrabajo ot) {
        if (ot.getNumeroOt() == null || ot.getNumeroOt().isBlank())
            throw new IllegalArgumentException("El número de OT es obligatorio.");
        if (ot.getTipoOtId() == 0)
            throw new IllegalArgumentException("El tipo de OT es obligatorio.");
        if (ot.getDescripcion() == null || ot.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria.");
    }
}