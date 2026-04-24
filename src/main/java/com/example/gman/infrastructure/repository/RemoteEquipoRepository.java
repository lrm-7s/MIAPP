package com.example.gman.infrastructure.repository;

import com.example.gman.domain.dto.EquipoDTO;
import com.example.gman.domain.model.Equipo;
import com.example.gman.infrastructure.network.ApiClient;

import java.util.List;
import java.util.stream.Collectors;

public class RemoteEquipoRepository {

    private final ApiClient apiClient;

    public RemoteEquipoRepository(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<Equipo> findAll() throws Exception {
        return apiClient.getEquipos().stream()
                .map(EquipoDTO::toModel)
                .collect(Collectors.toList());
    }

    public Equipo save(Equipo equipo) throws Exception {
        EquipoDTO dto = EquipoDTO.fromModel(equipo);
        EquipoDTO saved = apiClient.saveEquipo(dto);
        return saved.toModel();
    }

    public void delete(int id) throws Exception {
        apiClient.deleteEquipo(id);
    }
}