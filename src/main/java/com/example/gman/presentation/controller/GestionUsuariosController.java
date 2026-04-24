package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import com.example.gman.presentation.viewmodel.UsuariosViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class GestionUsuariosController {

    // ─── Tabla ───────────────────────────────────────────────────────
    @FXML private TableView<Usuario>            usuariosTable;
    @FXML private TableColumn<Usuario, String>  colUsername;
    @FXML private TableColumn<Usuario, String>  colNombre;
    @FXML private TableColumn<Usuario, String>  colRol;
    @FXML private TableColumn<Usuario, Void>    colAcciones;
    @FXML private TextField                     busquedaField;
    @FXML private Label                         mensajeLabel;

    // ─── Botón registrar ─────────────────────────────────────────────
    @FXML private Button btnRegistrar;

    // ─── Overlay ─────────────────────────────────────────────────────
    @FXML private StackPane overlayPane;
    @FXML private Label     dialogoTitulo;
    @FXML private TextField     editUsername;
    @FXML private TextField     editNombre;
    @FXML private PasswordField editPassword;
    @FXML private ComboBox<Rol> editRolCombo;

    // ─── Dependencias ────────────────────────────────────────────────
    private UsuariosViewModel viewModel;
    private AppCoordinator    coordinator;
    private SessionManager    sessionManager; // ← NUEVO

    // ─── Inyección ───────────────────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(UsuariosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarTabla();
        cargarDatos();
        aplicarPermisos();
    }

    // ─── Permisos ────────────────────────────────────────────────────
    private void aplicarPermisos() {
        if (sessionManager == null) return;

        // Solo ADMIN puede editar y eliminar usuarios
        boolean puedeEditar   = sessionManager.puedeEditar("GESTION_USUARIOS");
        boolean puedeEliminar = sessionManager.puedeEliminar("GESTION_USUARIOS");

        // Ocultar botón "Registrar nuevo usuario"
        if (btnRegistrar != null) {
            btnRegistrar.setVisible(puedeEditar);
            btnRegistrar.setManaged(puedeEditar);
        }

        // Reconfigurar columna acciones
        colAcciones.setCellFactory(crearCeldaAcciones(puedeEditar, puedeEliminar));
    }

    // ─── Inicialización ──────────────────────────────────────────────
    private void inicializarTabla() {
        colUsername.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getUsername()));
        colNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getNombre()));
        colRol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getRol() != null
                                ? d.getValue().getRol().getDisplayName()
                                : "Sin rol"));

        colAcciones.setCellFactory(crearCeldaAcciones(true, true));
        usuariosTable.setItems(viewModel.getUsuarios());
        editRolCombo.setItems(viewModel.getRolesDisponibles());
    }

    private void cargarDatos() {
        try {
            viewModel.cargarUsuarios();
            ocultarMensaje();
        } catch (Exception e) {
            mostrarError("Error al cargar usuarios: " + e.getMessage());
        }
    }

    // ─── Filtro ──────────────────────────────────────────────────────
    @FXML
    private void filtrarUsuarios() {
        usuariosTable.setItems(viewModel.filtrar(busquedaField.getText()));
    }

    // ─── Registro ────────────────────────────────────────────────────
    @FXML
    private void abrirRegistro() {
        if (sessionManager != null && !sessionManager.esAdmin()) {
            mostrarError("Solo el administrador puede registrar usuarios.");
            return;
        }
        coordinator.openRegistro();
    }

    // ─── Edición ─────────────────────────────────────────────────────
    private void abrirDialogoEdicion(Usuario usuario) {
        if (sessionManager != null && !sessionManager.puedeEditar("GESTION_USUARIOS")) {
            mostrarError("No tienes permiso para editar usuarios.");
            return;
        }
        editUsername.setText(usuario.getUsername());
        editNombre.setText(usuario.getNombre());
        editPassword.clear();
        editRolCombo.setValue(usuario.getRol());
        overlayPane.setVisible(true);
    }

    @FXML
    private void cerrarDialogo() {
        overlayPane.setVisible(false);
        ocultarMensaje();
    }

    @FXML
    private void guardarEdicion() {
        if (sessionManager != null && !sessionManager.puedeEditar("GESTION_USUARIOS")) {
            mostrarError("No tienes permiso para guardar cambios.");
            return;
        }
        String username = editUsername.getText().trim();
        String nombre   = editNombre.getText().trim();
        String password = editPassword.getText();
        Rol    rol      = editRolCombo.getValue();

        if (nombre.isEmpty())  { mostrarError("El nombre no puede estar vacío."); return; }
        if (rol == null)       { mostrarError("Debe seleccionar un rol."); return; }

        try {
            viewModel.actualizarUsuario(username, nombre,
                    password.isEmpty() ? null : password, rol);
            cerrarDialogo();
            cargarDatos();
            mostrarExito("Usuario '" + username + "' actualizado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    // ─── Eliminación ─────────────────────────────────────────────────
    private void confirmarEliminacion(Usuario usuario) {
        if (sessionManager != null && !sessionManager.puedeEliminar("GESTION_USUARIOS")) {
            mostrarError("No tienes permiso para eliminar usuarios.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar usuario?");
        alert.setContentText(
                "¿Está seguro de que desea eliminar al usuario '"
                        + usuario.getNombre() + "' (" + usuario.getUsername() + ")?\n"
                        + "Esta acción no se puede deshacer."
        );
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    viewModel.eliminarUsuario(usuario.getUsername());
                    cargarDatos();
                    mostrarExito("Usuario eliminado correctamente.");
                } catch (Exception e) {
                    mostrarError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    // ─── Celda de acciones con permisos ──────────────────────────────
    private Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>>
    crearCeldaAcciones(boolean puedeEditar, boolean puedeEliminar) {
        return col -> new TableCell<>() {

            private final Button btnEditar   = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.setTooltip(new Tooltip("Editar usuario"));
                btnEliminar.setTooltip(new Tooltip("Eliminar usuario"));
                btnEditar.getStyleClass().add("btn-accion-editar");
                btnEliminar.getStyleClass().add("btn-accion-eliminar");

                btnEditar.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    abrirDialogoEdicion(u);
                });
                btnEliminar.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    confirmarEliminacion(u);
                });

                if (puedeEditar)   box.getChildren().add(btnEditar);
                if (puedeEliminar) box.getChildren().add(btnEliminar);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || box.getChildren().isEmpty() ? null : box);
            }
        };
    }

    // ─── Helpers de UI ───────────────────────────────────────────────
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

    private void ocultarMensaje() { mensajeLabel.setVisible(false); }
}