package com.example.gman.presentation.controller;

import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.presentation.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblMessage;

    private LoginViewModel viewModel;
    private AppCoordinator coordinator;

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;

        // ← Usa el AuthService del coordinator, no crea uno nuevo
        this.viewModel = new LoginViewModel(
                coordinator,
                coordinator.getAuthService()  // ← este es el fix
        );

        lblMessage.textProperty().bind(this.viewModel.loginMessageProperty());
    }

    @FXML
    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            viewModel.setLoginMessage("Ingrese usuario y contraseña");
            return;
        }

        try {
            if (viewModel.login(user, pass)) {
                coordinator.onLoginSuccess();
            } else {
                viewModel.setLoginMessage("Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            viewModel.setLoginMessage("Error al conectar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}