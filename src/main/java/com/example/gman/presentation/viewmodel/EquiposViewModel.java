package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.EquipoService;
import com.example.gman.domain.model.Equipo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EquiposViewModel {

    private final EquipoService service;
    private final ObservableList<Equipo> equipos = FXCollections.observableArrayList();
    // contador automatco

    public EquiposViewModel() {
        this.service = new EquipoService();
        cargarEquipos();// cargar equipos existentes para calcular nextId
    }



    public ObservableList<Equipo> getEquipos() {
        return equipos;
    }

    public void cargarEquipos() {
        equipos.clear();
        equipos.addAll(service.obtenerEquipos());
    }

    // En EquiposViewModel
    private int nextId = 1;

    private ObservableList<Equipo> equipos = FXCollections.observableArrayList();

    public boolean guardarEquipo(EquipoDTO dto) {
        if(!validarEquipo(dto)) return false;
        Equipo equipo = mapper.dtoToModel(dto);
        Equipo equipoGuardado = service.guardarEquipo(equipo);
        equipos.add(equipoGuardado);
        return true;
    }
    public void guardarEquipo(Equipo equipo) {
        equipo.setId(nextId++);
        equipos.add(equipo);
        service.guardarEquipo(equipo);
    }
}