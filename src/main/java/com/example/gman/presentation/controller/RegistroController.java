package com.example.gman.presentation.controller;

import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Rol;
import com.example.gman.presentation.viewmodel.UsuariosViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistroController {

    // ─── Campos del formulario ───────────────────────────────────────
    @FXML private TextField     usernameField;
    @FXML private TextField     nombreField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmarPasswordField;
    @FXML private ComboBox<Rol> rolComboBox;
    @FXML private Label         mensajeLabel;

    // ─── Dependencias ────────────────────────────────────────────────
    private UsuariosViewModel viewModel;
    private AppCoordinator    coordinator;

    // ─── Inyección de dependencias ───────────────────────────────────
    public void setViewModel(UsuariosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarComboBox();
    }

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // ─── Inicialización ──────────────────────────────────────────────
    private void inicializarComboBox() {
        // Carga los roles y muestra su nombre legible
        rolComboBox.setItems(viewModel.getRolesDisponibles());
        rolComboBox.setConverter(new javafx.util.StringConverter<Rol>() {
            @Override public String toString(Rol rol) {
                return rol != null ? rol.getDisplayName() : "";
            }
            @Override public Rol fromString(String s) { return null; }
        });
        rolComboBox.getSelectionModel().selectFirst(); // ADMIN por defecto
    }

    // ─── Acción: Registrar ───────────────────────────────────────────
    @FXML
    private void registrar() {
        String username  = usernameField.getText().trim();
        String nombre    = nombreField.getText().trim();
        String password  = passwordField.getText();
        String confirmar = confirmarPasswordField.getText();
        Rol    rol       = rolComboBox.getValue();

        try {
            viewModel.registrar(username, nombre, password, confirmar, rol);
            mostrarExito("Usuario '" + username + "' registrado correctamente.");
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // ─── Acción: Cancelar ────────────────────────────────────────────
    @FXML
    private void cancelar() {
        coordinator.openGestionUsuarios();
    }

    // ─── Helpers de UI ───────────────────────────────────────────────
    private void mostrarError(String mensaje) {
        mensajeLabel.setText("⚠ " + mensaje);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
    }

    private void mostrarExito(String mensaje) {
        mensajeLabel.setText("✓ " + mensaje);
        mensajeLabel.getStyleClass().removeAll("mensaje-error");
        mensajeLabel.getStyleClass().add("mensaje-exito");
        mensajeLabel.setVisible(true);
    }

    private void limpiarFormulario() {
        usernameField.clear();
        nombreField.clear();
        passwordField.clear();
        confirmarPasswordField.clear();
        rolComboBox.getSelectionModel().selectFirst();
    }
}