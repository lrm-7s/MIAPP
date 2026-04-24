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

    public boolean login(String username, String password) {
        if (authService.login(username, password)) {
            Usuario user = authService.getUsuario(username);
            coordinator.getSessionManager().setCurrentUser(user);
            return true;
        }
        setLoginMessage("Usuario o contraseña incorrectos");
        return false;
    }
}
