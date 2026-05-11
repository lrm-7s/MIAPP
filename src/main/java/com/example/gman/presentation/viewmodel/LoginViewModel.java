package com.example.gman.presentation.viewmodel;

import com.example.gman.application.service.AuthService;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {

    private final StringProperty loginMessage = new SimpleStringProperty();
    private final AppCoordinator coordinator;
    private final AuthService authService;

    public LoginViewModel(AppCoordinator coordinator, AuthService authService) {
        this.coordinator = coordinator;
        this.authService = authService;
    }

    public StringProperty loginMessageProperty() { return loginMessage; }
    public void setLoginMessage(String msg) { loginMessage.set(msg); }

    // CORREGIDO: ahora propaga la excepción al controller, que la muestra
    // con un mensaje claro al usuario en vez de silenciarla como "contraseña incorrecta".
    public boolean login(String username, String password) throws Exception {
        boolean credencialesOk = authService.login(username, password);
        if (!credencialesOk) {
            setLoginMessage("Usuario o contraseña incorrectos");
            return false;
        }

        // CORREGIDO: getUsuario() también propaga Exception ahora.
        // Si falla aquí, el controller mostrará el error real (ej: BD caída).
        Usuario user = authService.getUsuario(username);
        if (user == null) {
            setLoginMessage("Error interno: usuario no encontrado. Contacte al administrador.");
            return false;
        }

        coordinator.getSessionManager().setCurrentUser(user);
        return true;
    }
}