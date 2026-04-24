package com.example.gman.presentation.viewmodel;
import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {

    private final SessionManager sessionManager;
    private final StringProperty loginMessage = new SimpleStringProperty();
    public LoginViewModel(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public StringProperty loginMessageProperty() {
        return loginMessage;
    }

    public boolean login(String username) {
        if(username == null || username.isBlank()) {
            loginMessage.set("El usuario no puede estar vacío");
            return false;
        }
        Usuario user = new Usuario(username, username);
        sessionManager.login(user);
        loginMessage.set("Login exitoso");
        return true;
    }
}