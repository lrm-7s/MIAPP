package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OrdenTrabajoController {

    // ─── Tabla principal ─────────────────────────────────────────────
    @FXML private TableView<OrdenTrabajo>            tableOT;
    @FXML private TableColumn<OrdenTrabajo, String>  colNumero;
    @FXML private TableColumn<OrdenTrabajo, String>  colDescripcion;
    @FXML private TableColumn<OrdenTrabajo, String>  colEstado;
    @FXML private TableColumn<OrdenTrabajo, String>  colPrioridad;
    @FXML private TableColumn<OrdenTrabajo, String>  colFecha;
    @FXML private TableColumn<OrdenTrabajo, Void>    colAcciones;

    // ─── Botones del header ──────────────────────────────────────────
    @FXML private Button btnNuevaOT;
    @FXML private Button btnVerEditar;
    @FXML private Button btnCerrarOT;

    // ─── Búsqueda ────────────────────────────────────────────────────
    @FXML private TextField busquedaField;
    @FXML private Label     mensajeLabel;

    // ─── Dependencias ────────────────────────────────────────────────
    private OrdenTrabajoViewModel viewModel;
    private AppCoordinator        coordinator;
    private SessionManager        sessionManager;
    private OrdenTrabajo          otSeleccionada;

    // ─── Inyección ───────────────────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(OrdenTrabajoViewModel vm) {
        this.viewModel = vm;
        try {
            viewModel.cargarTodo();
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
        inicializarTabla();
        aplicarPermisos();
    }

    // ─── Permisos ────────────────────────────────────────────────────
    private void aplicarPermisos() {
        if (sessionManager == null) return;

        boolean puedeEditar   = sessionManager.puedeEditar("ORDEN_TRABAJO");
        boolean puedeEliminar = sessionManager.puedeEliminar("ORDEN_TRABAJO");

        // "Nueva OT" y "Cerrar OT" solo si puede editar
        btnNuevaOT.setVisible(puedeEditar);
        btnNuevaOT.setManaged(puedeEditar);
        btnCerrarOT.setVisible(puedeEditar);
        btnCerrarOT.setManaged(puedeEditar);

        // "Ver/Editar" visible para todos (CONSULTOR solo puede ver)
        configurarColumnaAcciones(puedeEditar, puedeEliminar);
    }

    // ─── Inicialización de tabla ─────────────────────────────────────
    private void inicializarTabla() {
        colNumero.setCellValueFactory(     d -> d.getValue().numeroOtProperty());
        colDescripcion.setCellValueFactory(d -> d.getValue().descripcionProperty());
        colEstado.setCellValueFactory(     d -> d.getValue().estadoProperty());
        colPrioridad.setCellValueFactory(  d -> d.getValue().prioridadProperty());
        colFecha.setCellValueFactory(      d -> d.getValue().fechaSolicitudProperty());
        tableOT.setItems(viewModel.getOrdenes());

        // Selección de fila
        tableOT.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> otSeleccionada = nuevo);
    }

    private void configurarColumnaAcciones(boolean puedeEditar,
                                           boolean puedeEliminar) {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.setTooltip(new Tooltip("Ver / Editar OT"));
                btnEliminar.setTooltip(new Tooltip("Eliminar OT"));
                btnEditar.getStyleClass().add("btn-accion-editar");
                btnEliminar.getStyleClass().add("btn-accion-eliminar");

                btnEditar.setOnAction(e -> {
                    OrdenTrabajo ot = getTableView().getItems().get(getIndex());
                    abrirFormulario(ot);
                });
                btnEliminar.setOnAction(e -> {
                    OrdenTrabajo ot = getTableView().getItems().get(getIndex());
                    confirmarEliminar(ot);
                });

                // El botón ver/editar siempre aparece
                box.getChildren().add(btnEditar);
                if (puedeEliminar) box.getChildren().add(btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ─── Filtro ──────────────────────────────────────────────────────
    @FXML
    private void filtrarOrdenes() {
        tableOT.setItems(viewModel.filtrar(busquedaField.getText()));
    }

    // ─── Botón: Nueva OT ─────────────────────────────────────────────
    @FXML
    private void nuevaOT() {
        if (!sessionManager.puedeEditar("ORDEN_TRABAJO")) {
            mostrarError("No tienes permiso para crear órdenes de trabajo.");
            return;
        }
        abrirFormulario(null);
    }

    // ─── Botón: Ver/Editar OT seleccionada ───────────────────────────
    @FXML
    private void verEditarOT() {
        if (otSeleccionada == null) {
            mostrarError("Selecciona una OT de la tabla primero.");
            return;
        }
        abrirFormulario(otSeleccionada);
    }

    // ─── Botón: Cerrar OT seleccionada ───────────────────────────────
    @FXML
    private void cerrarOT() {
        if (!sessionManager.puedeEditar("ORDEN_TRABAJO")) {
            mostrarError("No tienes permiso para cerrar órdenes.");
            return;
        }
        if (otSeleccionada == null) {
            mostrarError("Selecciona una OT de la tabla primero.");
            return;
        }
        if ("CERRADA".equals(otSeleccionada.getEstado())) {
            mostrarError("Esta OT ya está cerrada.");
            return;
        }
        abrirCierre(otSeleccionada);
    }

    // ─── Abrir formulario (nueva o edición) ──────────────────────────
    private void abrirFormulario(OrdenTrabajo ot) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/OrdenTrabajoForm.fxml"));
            VBox root = loader.load();

            OrdenTrabajoFormController ctrl = loader.getController();
            ctrl.setViewModel(viewModel);
            ctrl.setSessionManager(sessionManager);
            ctrl.cargar(ot); // null = nuevo, objeto = edición

            Stage stage = new Stage();
            stage.setTitle(ot == null ? "Nueva Orden de Trabajo" : "Editar OT");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            viewModel.cargarOrdenes(); // refresca tabla al cerrar
        } catch (Exception e) {
            mostrarError("Error al abrir formulario: " + e.getMessage());
        }
    }

    // ─── Abrir cierre ────────────────────────────────────────────────
    private void abrirCierre(OrdenTrabajo ot) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/OrdenTrabajoCierre.fxml"));
            VBox root = loader.load();

            OrdenTrabajoCierreController ctrl = loader.getController();
            ctrl.setViewModel(viewModel);
            ctrl.setSessionManager(sessionManager);
            ctrl.cargar(ot);

            Stage stage = new Stage();
            stage.setTitle("Cierre de OT - " + ot.getNumeroOt());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            viewModel.cargarOrdenes();
        } catch (Exception e) {
            mostrarError("Error al abrir cierre: " + e.getMessage());
        }
    }

    // ─── Eliminar ────────────────────────────────────────────────────
    private void confirmarEliminar(OrdenTrabajo ot) {
        if (!sessionManager.puedeEliminar("ORDEN_TRABAJO")) {
            mostrarError("No tienes permiso para eliminar órdenes.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar OT");
        alert.setHeaderText("¿Eliminar la OT " + ot.getNumeroOt() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    viewModel.eliminar(ot.getId());
                    mostrarExito("OT eliminada correctamente.");
                } catch (Exception e) {
                    mostrarError("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    // ─── Helpers UI ──────────────────────────────────────────────────
    private void mostrarError(String msg) {
        if (mensajeLabel == null) return;
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        if (!mensajeLabel.getStyleClass().contains("mensaje-error"))
            mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
    }

    private void mostrarExito(String msg) {
        if (mensajeLabel == null) return;
        mensajeLabel.setText("✔ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-error");
        if (!mensajeLabel.getStyleClass().contains("mensaje-exito"))
            mensajeLabel.getStyleClass().add("mensaje-exito");
        mensajeLabel.setVisible(true);
    }
}