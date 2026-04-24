package com.example.gman.domain.repository;

import com.example.gman.domain.model.OrdenTrabajo;
import java.sql.SQLException;
import java.util.List;

public interface OrdenTrabajoRepository {
    List<OrdenTrabajo> getAll()                        throws SQLException;
    List<OrdenTrabajo> getByEstado(String estado)      throws SQLException;
    OrdenTrabajo       getById(int id)                 throws SQLException;
    void               crear(OrdenTrabajo ot)          throws SQLException;
    void               actualizar(OrdenTrabajo ot)     throws SQLException;
    void               cerrar(OrdenTrabajo ot)         throws SQLException;
    void               eliminar(int id)                throws SQLException;
    void               asignarEmpleados(int otId, List<Integer> numerosEmpleado) throws SQLException;
    List<Integer>      getEmpleadosDeOt(int otId)      throws SQLException;
}