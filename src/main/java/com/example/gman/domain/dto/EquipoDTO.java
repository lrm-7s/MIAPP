package com.example.gman.domain.dto;

import com.example.gman.domain.model.Equipo;


public class EquipoDTO {
    public int id;
    public String codigo;
    public String nombre;
    public String capacidad;
    public String marca;
    public String modelo;
    public String serie;
    public String area;
    public String planta;
    public String centroCostos;
    public String criticidad;
    public String tipo;

    public static EquipoDTO fromModel(Equipo e) {
        EquipoDTO dto = new EquipoDTO();
        dto.id           = e.getId();
        dto.codigo       = e.getCodigo();
        dto.nombre       = e.getNombre();
        dto.capacidad    = e.getCapacidad();
        dto.marca        = e.getMarca();
        dto.modelo       = e.getModelo();
        dto.serie        = e.getSerie();
        dto.area         = e.getArea();
        dto.planta       = e.getPlanta();
        dto.centroCostos = e.getCentroCostos();
        dto.criticidad   = e.getCriticidad();
        dto.tipo         = e.getTipo();
        return dto;
    }

    public Equipo toModel() {
        Equipo e = new Equipo();
        e.setId(id);
        e.setCodigo(codigo);
        e.setNombre(nombre);
        e.setCapacidad(capacidad);
        e.setMarca(marca);
        e.setModelo(modelo);
        e.setSerie(serie);
        e.setArea(area);
        e.setPlanta(planta);
        e.setCentroCostos(centroCostos);
        e.setCriticidad(criticidad);
        e.setTipo(tipo);
        return e;
    }



}