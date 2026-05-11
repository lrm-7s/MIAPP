package com.example.gman.domain.model;

public enum Rol {
    ADMIN,
    SUPERVISOR,
    TECNICO,
    CONSULTOR;
    public String getDisplayName() {
        return switch (this) {
            case ADMIN     -> "Administrador";
            case SUPERVISOR -> "Supervisor";
            case TECNICO   -> "Técnico";
            case CONSULTOR -> "Consultor";
        };
    }
    /** Parseo seguro desde String */
    public static Rol parse(String valor) {
        try {
            return Rol.valueOf(valor.toUpperCase());
        } catch (Exception e) {
            return CONSULTOR;
        }
    }
}