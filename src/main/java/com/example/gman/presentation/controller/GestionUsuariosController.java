package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.PermisoModulo;
import com.example.gman.domain.model.Rol;
import com.example.gman.domain.model.Usuario;
import com.example.gman.infrastructure.repository.UsuarioRepositoryImpl;
import com.example.gman.presentation.viewmodel.UsuariosViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuariosController {

    // ─── Tabla ───────────────────────────────────────────────────────
    @FXML private TableView<Usuario>           usuariosTable;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, Void>   colAcciones;
    @FXML private TextField                    busquedaField;
    @FXML private Label                        mensajeLabel;
    @FXML private Button                       btnRegistrar;

    // ─── Overlay edición ─────────────────────────────────────────────
    @FXML private StackPane     overlayPane;
    @FXML private Label         dialogoTitulo;
    @FXML private TextField     editUsername;
    @FXML private TextField     editNombre;
    @FXML private PasswordField editPassword;
    @FXML private ComboBox<Rol> editRolCombo;

    // ─── Checkboxes de permisos ──────────────────────────────────────
    @FXML private CheckBox chkEquiposVer,    chkEquiposCrear,    chkEquiposEditar,    chkEquiposEliminar;
    @FXML private CheckBox chkManoObraVer,   chkManoObraCrear,   chkManoObraEditar,   chkManoObraEliminar;
    @FXML private CheckBox chkOtVer,         chkOtCrear,         chkOtEditar,         chkOtEliminar;
    @FXML private CheckBox chkCatalogoVer,   chkCatalogoCrear,   chkCatalogoEditar,   chkCatalogoEliminar;
    @FXML private CheckBox chkPlanVer,       chkPlanCrear,       chkPlanEditar,       chkPlanEliminar;
    @FXML private CheckBox chkHistoricoVer,  chkHistoricoCrear,  chkHistoricoEditar,  chkHistoricoEliminar;
    @FXML private CheckBox chkLocVer,        chkLocCrear,        chkLocEditar,        chkLocEliminar;
    @FXML private CheckBox chkReportesVer,   chkReportesCrear,   chkReportesEditar,   chkReportesEliminar;
    @FXML private CheckBox chkUsuariosVer,   chkUsuariosCrear,   chkUsuariosEditar,   chkUsuariosEliminar;

    // ─── Dependencias ────────────────────────────────────────────────
    private UsuariosViewModel     viewModel;
    private AppCoordinator        coordinator;
    private SessionManager        sessionManager;
    private UsuarioRepositoryImpl usuarioRepo;

    // ─── Referencia al menú para refrescar sidebar tras editar ───────
    private MainMenuController mainMenuController;

    public void setMainMenuController(MainMenuController c) {
        this.mainMenuController = c;
    }

    // ─── Inyección ───────────────────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
        this.usuarioRepo    = new UsuarioRepositoryImpl();
    }

    public void setViewModel(UsuariosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarTabla();
        cargarDatos();
        aplicarPermisos(); // siempre al final, ya con la tabla lista
    }

    // ─── Permisos ────────────────────────────────────────────────────
    private void aplicarPermisos() {
        if (sessionManager == null) return;
        boolean puedeEditar   = sessionManager.puedeEditar("GESTION_USUARIOS");
        boolean puedeEliminar = sessionManager.puedeEliminar("GESTION_USUARIOS");

        if (btnRegistrar != null) {
            btnRegistrar.setVisible(puedeEditar);
            btnRegistrar.setManaged(puedeEditar);
        }
        // FIX Bug 2: se asigna aquí y solo aquí; eliminada la línea redundante de inicializarTabla()
        colAcciones.setCellFactory(crearCeldaAcciones(puedeEditar, puedeEliminar));
    }

    // ─── Tabla ───────────────────────────────────────────────────────
    private void inicializarTabla() {
        colUsername.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));
        colNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getNombre()));
        colRol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getRol() != null
                                ? d.getValue().getRol().getDisplayName() : "Sin rol"));

        // FIX Bug 2: eliminada colAcciones.setCellFactory(crearCeldaAcciones(true, true))
        // Los permisos reales se asignan en aplicarPermisos(), que se llama después.
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
        // FIX Bug 3: verificación consistente con el resto del sistema
        if (sessionManager != null && !sessionManager.puedeCrear("GESTION_USUARIOS")) {
            mostrarError("No tienes permiso para registrar usuarios.");
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

        boolean esAdmin = Rol.ADMIN.equals(usuario.getRol());
        setCheckboxesVisible(!esAdmin);
        if (!esAdmin) cargarCheckboxes(usuario);

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

        // FIX Bug 1 (líneas 162-163): validaciones completas
        if (nombre.isEmpty()) { mostrarError("El nombre no puede estar vacío."); return; }
        if (rol == null)      { mostrarError("Debe seleccionar un rol."); return; }

        try {
            viewModel.actualizarUsuario(username, nombre,
                    password.isEmpty() ? null : password, rol);

            // Permisos solo para no-admin
            if (!Rol.ADMIN.equals(rol)) {
                List<PermisoModulo> permisos = leerPermisosDesdUI();
                usuarioRepo.guardarPermisos(username, permisos);
            }

            cerrarDialogo();
            cargarDatos();
            mostrarExito("Usuario '" + username + "' actualizado correctamente.");

            // FIX Bug 4: si el admin editó su propio perfil, refrescar sesión y sidebar
            if (sessionManager.getUsername().equals(username)) {
                try {
                    Usuario actualizado = usuarioRepo.findByUsername(username);
                    if (actualizado != null) {
                        sessionManager.setCurrentUser(actualizado);
                        if (mainMenuController != null) {
                            mainMenuController.aplicarPermisos(sessionManager);
                        }
                    }
                } catch (Exception ignored) {}
            }

        } catch (Exception e) {
            mostrarError("Error al actualizar: " + e.getMessage());
        }
    }

    // ─── Ocultar/mostrar panel de permisos ───────────────────────────
    private void setCheckboxesVisible(boolean visible) {
        CheckBox[] todos = {
                chkEquiposVer, chkEquiposCrear, chkEquiposEditar, chkEquiposEliminar,
                chkManoObraVer, chkManoObraCrear, chkManoObraEditar, chkManoObraEliminar,
                chkOtVer, chkOtCrear, chkOtEditar, chkOtEliminar,
                chkCatalogoVer, chkCatalogoCrear, chkCatalogoEditar, chkCatalogoEliminar,
                chkPlanVer, chkPlanCrear, chkPlanEditar, chkPlanEliminar,
                chkHistoricoVer, chkHistoricoCrear, chkHistoricoEditar, chkHistoricoEliminar,
                chkLocVer, chkLocCrear, chkLocEditar, chkLocEliminar,
                chkReportesVer, chkReportesCrear, chkReportesEditar, chkReportesEliminar,
                chkUsuariosVer, chkUsuariosCrear, chkUsuariosEditar, chkUsuariosEliminar
        };
        for (CheckBox cb : todos) {
            if (cb != null) {
                cb.setVisible(visible);
                cb.setManaged(visible);
            }
        }
    }

    // ─── Checkboxes ──────────────────────────────────────────────────
    private void cargarCheckboxes(Usuario u) {
        cargarCheckbox(u, "EQUIPOS",            chkEquiposVer,   chkEquiposCrear,   chkEquiposEditar,   chkEquiposEliminar);
        cargarCheckbox(u, "MANO_OBRA",          chkManoObraVer,  chkManoObraCrear,  chkManoObraEditar,  chkManoObraEliminar);
        cargarCheckbox(u, "ORDEN_TRABAJO",      chkOtVer,        chkOtCrear,        chkOtEditar,        chkOtEliminar);
        cargarCheckbox(u, "CATALOGO",           chkCatalogoVer,  chkCatalogoCrear,  chkCatalogoEditar,  chkCatalogoEliminar);
        cargarCheckbox(u, "PLAN_MANTENIMIENTO", chkPlanVer,      chkPlanCrear,      chkPlanEditar,      chkPlanEliminar);
        cargarCheckbox(u, "HISTORICO",          chkHistoricoVer, chkHistoricoCrear, chkHistoricoEditar, chkHistoricoEliminar);
        cargarCheckbox(u, "LOCALIZACIONES",     chkLocVer,       chkLocCrear,       chkLocEditar,       chkLocEliminar);
        cargarCheckbox(u, "REPORTES",           chkReportesVer,  chkReportesCrear,  chkReportesEditar,  chkReportesEliminar);
        cargarCheckbox(u, "GESTION_USUARIOS",   chkUsuariosVer,  chkUsuariosCrear,  chkUsuariosEditar,  chkUsuariosEliminar);
    }

    private void cargarCheckbox(Usuario u, String modulo,
                                CheckBox ver, CheckBox crear,
                                CheckBox editar, CheckBox eliminar) {
        PermisoModulo p = u.getPermiso(modulo);
        ver.setSelected(p.isPuedeVer());
        crear.setSelected(p.isPuedeCrear());
        editar.setSelected(p.isPuedeEditar());
        eliminar.setSelected(p.isPuedeEliminar());
    }

    private List<PermisoModulo> leerPermisosDesdUI() {
        List<PermisoModulo> lista = new ArrayList<>();
        lista.add(leerCheckbox("EQUIPOS",            chkEquiposVer,   chkEquiposCrear,   chkEquiposEditar,   chkEquiposEliminar));
        lista.add(leerCheckbox("MANO_OBRA",          chkManoObraVer,  chkManoObraCrear,  chkManoObraEditar,  chkManoObraEliminar));
        lista.add(leerCheckbox("ORDEN_TRABAJO",      chkOtVer,        chkOtCrear,        chkOtEditar,        chkOtEliminar));
        lista.add(leerCheckbox("CATALOGO",           chkCatalogoVer,  chkCatalogoCrear,  chkCatalogoEditar,  chkCatalogoEliminar));
        lista.add(leerCheckbox("PLAN_MANTENIMIENTO", chkPlanVer,      chkPlanCrear,      chkPlanEditar,      chkPlanEliminar));
        lista.add(leerCheckbox("HISTORICO",          chkHistoricoVer, chkHistoricoCrear, chkHistoricoEditar, chkHistoricoEliminar));
        lista.add(leerCheckbox("LOCALIZACIONES",     chkLocVer,       chkLocCrear,       chkLocEditar,       chkLocEliminar));
        lista.add(leerCheckbox("REPORTES",           chkReportesVer,  chkReportesCrear,  chkReportesEditar,  chkReportesEliminar));
        lista.add(leerCheckbox("GESTION_USUARIOS",   chkUsuariosVer,  chkUsuariosCrear,  chkUsuariosEditar,  chkUsuariosEliminar));
        return lista;
    }

    private PermisoModulo leerCheckbox(String modulo,
                                       CheckBox ver, CheckBox crear,
                                       CheckBox editar, CheckBox eliminar) {
        return new PermisoModulo(modulo,
                ver.isSelected(), crear.isSelected(),
                editar.isSelected(), eliminar.isSelected());
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
        alert.setContentText("¿Desea eliminar al usuario '"
                + usuario.getNombre() + "' (" + usuario.getUsername() + ")?\n"
                + "Esta acción no se puede deshacer.");
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

    // ─── Celda acciones ──────────────────────────────────────────────
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
                btnEditar.setOnAction(e ->
                        abrirDialogoEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e ->
                        confirmarEliminacion(getTableView().getItems().get(getIndex())));
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

    private void ocultarMensaje() { mensajeLabel.setVisible(false); }
}