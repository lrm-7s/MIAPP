package com.example.gman.domain.dto;

import com.example.gman.domain.model.Equipo;

/**
 * DTO para transferencia de datos de Equipo hacia/desde la API remota.
 *
 * Cambios respecto a la versión anterior:
 *  - area       (String) → areaId       (int)
 *  - planta     (String) → localizacionId (int)
 *  - criticidad (String) → criticidadId (int)
 *  - tipo       (String) → tipoId       (int)
 */
public class EquipoDTO {

    public int    id;
    public String codigo;
    public String nombre;
    public String capacidad;
    public String marca;
    public String modelo;
    public String serie;
    public int    areaId;          // FK → catalogo (AREA)
    public int    localizacionId;  // FK → localizaciones  (antes: planta)
    public String centroCostos;
    public int    criticidadId;    // FK → catalogo (CRITICIDAD)
    public int    tipoId;          // FK → catalogo (TIPO_EQUIPO)

    // ── Model → DTO ───────────────────────────────────────────────────────
    public static EquipoDTO fromModel(Equipo e) {
        EquipoDTO dto    = new EquipoDTO();
        dto.id           = e.getId();
        dto.codigo       = e.getCodigo();
        dto.nombre       = e.getNombre();
        dto.capacidad    = e.getCapacidad();
        dto.marca        = e.getMarca();
        dto.modelo       = e.getModelo();
        dto.serie        = e.getSerie();
        dto.areaId       = e.getAreaId();
        dto.localizacionId = e.getLocalizacionId();
        dto.centroCostos = e.getCentroCostos();
        dto.criticidadId = e.getCriticidadId();
        dto.tipoId       = e.getTipoId();
        return dto;
    }

    // ── DTO → Model ───────────────────────────────────────────────────────
    public Equipo toModel() {
        Equipo e = new Equipo();
        e.setId(id);
        e.setCodigo(codigo);
        e.setNombre(nombre);
        e.setCapacidad(capacidad);
        e.setMarca(marca);
        e.setModelo(modelo);
        e.setSerie(serie);
        e.setAreaId(areaId);
        e.setLocalizacionId(localizacionId);
        e.setCentroCostos(centroCostos);
        e.setCriticidadId(criticidadId);
        e.setTipoId(tipoId);
        return e;
    }
}