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

    // ─── Inyección ───────────────────────────────────────────────────
    // CORREGIDO: inicializarComboBox() se llama solo cuando AMBAS dependencias
    // están listas, sin importar el orden en que se inyecten.

    public void setViewModel(UsuariosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarSiListo();
    }

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;
        inicializarSiListo();
    }

    // Se ejecuta solo cuando viewModel Y coordinator están asignados.
    private void inicializarSiListo() {
        if (viewModel != null && coordinator != null) {
            inicializarComboBox();
        }
    }

    // ─── Inicialización ──────────────────────────────────────────────
    private void inicializarComboBox() {
        rolComboBox.setItems(viewModel.getRolesDisponibles());
        rolComboBox.setConverter(new javafx.util.StringConverter<Rol>() {
            @Override public String toString(Rol rol) {
                return rol != null ? rol.getDisplayName() : "";
            }
            @Override public Rol fromString(String s) { return null; }
        });

        // Seleccionar CONSULTOR por defecto, nunca ADMIN
        rolComboBox.getItems().stream()
                .filter(r -> r == Rol.CONSULTOR)
                .findFirst()
                .ifPresent(rolComboBox::setValue);
    }

    // ─── Registrar ───────────────────────────────────────────────────
    @FXML
    private void registrar() {
        String username  = usernameField.getText().trim();
        String nombre    = nombreField.getText().trim();
        String password  = passwordField.getText();
        String confirmar = confirmarPasswordField.getText();
        Rol    rol       = rolComboBox.getValue();

        // ── Validaciones básicas ──────────────────────────────────────
        if (username.isEmpty()) { mostrarError("El username es obligatorio."); return; }
        if (nombre.isEmpty())   { mostrarError("El nombre es obligatorio.");   return; }
        if (password.isEmpty()) { mostrarError("La contraseña es obligatoria."); return; }
        if (rol == null)        { mostrarError("Selecciona un rol."); return; }

        // ── No permitir crear otro ADMIN desde aquí ───────────────────
        if (Rol.ADMIN.equals(rol)) {
            mostrarError("No se puede registrar otro Administrador.");
            return;
        }

        try {
            viewModel.registrar(username, nombre, password, confirmar, rol);
            mostrarExito("Usuario '" + username + "' registrado. "
                    + "Recuerda asignarle permisos desde Gestión de Usuarios.");
            limpiarFormulario();
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // ─── Cancelar ────────────────────────────────────────────────────
    @FXML
    private void cancelar() {
        coordinator.openGestionUsuarios();
    }

    // ─── Helpers UI ──────────────────────────────────────────────────
    private void mostrarError(String mensaje) {
        mensajeLabel.setText("⚠ " + mensaje);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        if (!mensajeLabel.getStyleClass().contains("mensaje-error"))
            mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
    }

    private void mostrarExito(String mensaje) {
        mensajeLabel.setText("✔ " + mensaje);
        mensajeLabel.getStyleClass().removeAll("mensaje-error");
        if (!mensajeLabel.getStyleClass().contains("mensaje-exito"))
            mensajeLabel.getStyleClass().add("mensaje-exito");
        mensajeLabel.setVisible(true);
    }

    private void limpiarFormulario() {
        usernameField.clear();
        nombreField.clear();
        passwordField.clear();
        confirmarPasswordField.clear();
        rolComboBox.getItems().stream()
                .filter(r -> r == Rol.CONSULTOR)
                .findFirst()
                .ifPresent(rolComboBox::setValue);
    }
}