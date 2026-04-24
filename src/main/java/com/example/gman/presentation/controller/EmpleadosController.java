package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Empleado;
import com.example.gman.presentation.viewmodel.EmpleadosViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class EmpleadosController {

    // ─── Tabla ───────────────────────────────────────────────────────
    @FXML private TableView<Empleado>            empleadosTable;
    @FXML private TableColumn<Empleado, Integer> colNumero;
    @FXML private TableColumn<Empleado, String>  colNombre;
    @FXML private TableColumn<Empleado, String>  colPosicion;
    @FXML private TableColumn<Empleado, String>  colDepartamento;
    @FXML private TableColumn<Empleado, String>  colCorreo;
    @FXML private TableColumn<Empleado, Void>    colAcciones;
    @FXML private TextField                      busquedaField;
    @FXML private Label                          mensajeLabel;

    // ─── Botón principal ─────────────────────────────────────────────
    @FXML private Button btnNuevo; // botón "Nuevo Empleado" en el header

    // ─── Overlay ─────────────────────────────────────────────────────
    @FXML private StackPane overlayPane;
    @FXML private Label     dialogoTitulo;

    // ─── Campos del formulario ────────────────────────────────────────
    @FXML private TextField fNumero;
    @FXML private TextField fNombre;
    @FXML private TextField fDireccion;
    @FXML private TextField fPosicion;
    @FXML private TextField fCelular;
    @FXML private TextField fDepartamento;
    @FXML private TextField fCorreo;
    @FXML private TextField fSalario;
    @FXML private TextField fExtra1;
    @FXML private TextField fExtra2;
    @FXML private TextField fExtra3;

    // ─── Dependencias ────────────────────────────────────────────────
    private EmpleadosViewModel viewModel;
    private AppCoordinator     coordinator;
    private SessionManager     sessionManager; // ← NUEVO
    private boolean            modoEdicion = false;

    // ─── Inyección ───────────────────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(EmpleadosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarTabla();
        cargarDatos();
        aplicarPermisos(); // ← se aplica después de tener sessionManager
    }

    // ─── Permisos ────────────────────────────────────────────────────
    private void aplicarPermisos() {
        if (sessionManager == null) return;

        boolean puedeEditar   = sessionManager.puedeEditar("GESTION_EMPLEADOS");
        boolean puedeEliminar = sessionManager.puedeEliminar("GESTION_EMPLEADOS");

        // Ocultar botón "Nuevo" si no puede editar
        if (btnNuevo != null) {
            btnNuevo.setVisible(puedeEditar);
            btnNuevo.setManaged(puedeEditar);
        }

        // Reconfigurar columna acciones según permisos
        colAcciones.setCellFactory(crearCeldaAcciones(puedeEditar, puedeEliminar));
    }

    // ─── Inicialización ──────────────────────────────────────────────
    private void inicializarTabla() {
        colNumero.setCellValueFactory(d ->
                d.getValue().numeroEmpleadoProperty().asObject());
        colNombre.setCellValueFactory(d ->
                d.getValue().nombreProperty());
        colPosicion.setCellValueFactory(d ->
                d.getValue().posicionProperty());
        colDepartamento.setCellValueFactory(d ->
                d.getValue().departamentoProperty());
        colCorreo.setCellValueFactory(d ->
                d.getValue().correoProperty());

        // Columna acciones por defecto sin permisos aún
        colAcciones.setCellFactory(crearCeldaAcciones(true, true));
        empleadosTable.setItems(viewModel.getEmpleados());
    }

    private void cargarDatos() {
        try {
            viewModel.cargarEmpleados();
            ocultarMensaje();
        } catch (Exception e) {
            mostrarError("Error al cargar empleados: " + e.getMessage());
        }
    }

    // ─── Filtro ──────────────────────────────────────────────────────
    @FXML
    private void filtrarEmpleados() {
        empleadosTable.setItems(viewModel.filtrar(busquedaField.getText()));
    }

    // ─── Overlay — Nuevo ─────────────────────────────────────────────
    @FXML
    private void abrirNuevo() {
        if (sessionManager != null && !sessionManager.puedeEditar("GESTION_EMPLEADOS")) {
            mostrarError("No tienes permiso para agregar empleados.");
            return;
        }
        modoEdicion = false;
        dialogoTitulo.setText("Nuevo Empleado");
        limpiarFormulario();
        fNumero.setPromptText("(auto)");
        overlayPane.setVisible(true);
    }

    // ─── Overlay — Editar ────────────────────────────────────────────
    private void abrirEdicion(Empleado e) {
        if (sessionManager != null && !sessionManager.puedeEditar("GESTION_EMPLEADOS")) {
            mostrarError("No tienes permiso para editar empleados.");
            return;
        }
        modoEdicion = true;
        dialogoTitulo.setText("Editar Empleado");
        poblarFormulario(e);
        overlayPane.setVisible(true);
    }

    @FXML
    private void cerrarDialogo() {
        overlayPane.setVisible(false);
        ocultarMensaje();
    }

    @FXML
    private void guardar() {
        if (sessionManager != null && !sessionManager.puedeEditar("GESTION_EMPLEADOS")) {
            mostrarError("No tienes permiso para guardar cambios.");
            return;
        }
        try {
            Empleado e = leerFormulario();
            if (modoEdicion) {
                viewModel.actualizarEmpleado(e);
                mostrarExito("Empleado actualizado correctamente.");
            } else {
                viewModel.crearEmpleado(e);
                mostrarExito("Empleado creado correctamente.");
            }
            cerrarDialogo();
            cargarDatos();
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error: " + ex.getMessage());
        }
    }

    // ─── Eliminación ─────────────────────────────────────────────────
    private void confirmarEliminacion(Empleado e) {
        if (sessionManager != null && !sessionManager.puedeEliminar("GESTION_EMPLEADOS")) {
            mostrarError("No tienes permiso para eliminar empleados.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar empleado?");
        alert.setContentText(
                "¿Está seguro de que desea eliminar a '" + e.getNombre()
                        + "' (#" + e.getNumeroEmpleado() + ")?\n"
                        + "Esta acción no se puede deshacer."
        );
        alert.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    viewModel.eliminarEmpleado(e.getNumeroEmpleado());
                    cargarDatos();
                    mostrarExito("Empleado eliminado correctamente.");
                } catch (Exception ex) {
                    mostrarError("Error al eliminar: " + ex.getMessage());
                }
            }
        });
    }

    // ─── Celda de acciones con permisos ──────────────────────────────
    private Callback<TableColumn<Empleado, Void>, TableCell<Empleado, Void>>
    crearCeldaAcciones(boolean puedeEditar, boolean puedeEliminar) {
        return col -> new TableCell<>() {

            private final Button btnEditar   = new Button("✏ Editar");
            private final Button btnEliminar = new Button("🗑 Eliminar");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.getStyleClass().add("btn-accion-editar");
                btnEliminar.getStyleClass().add("btn-accion-eliminar");

                btnEditar.setOnAction(e ->
                        abrirEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e ->
                        confirmarEliminacion(getTableView().getItems().get(getIndex())));

                // Solo agrega botones permitidos
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

    // ─── Helpers de formulario ────────────────────────────────────────
    private void limpiarFormulario() {
        fNumero.clear(); fNombre.clear();     fDireccion.clear();
        fPosicion.clear(); fCelular.clear(); fDepartamento.clear();
        fCorreo.clear(); fSalario.clear();
        fExtra1.clear(); fExtra2.clear();    fExtra3.clear();
    }

    private void poblarFormulario(Empleado e) {
        fNumero.setText(String.valueOf(e.getNumeroEmpleado()));
        fNombre.setText(e.getNombre());
        fDireccion.setText(e.getDireccion());
        fPosicion.setText(e.getPosicion());
        fCelular.setText(e.getCelular());
        fDepartamento.setText(e.getDepartamento());
        fCorreo.setText(e.getCorreo());
        fSalario.setText(String.valueOf(e.getSalarioPorHora()));
        fExtra1.setText(String.valueOf(e.getTiempoExtra1()));
        fExtra2.setText(String.valueOf(e.getTiempoExtra2()));
        fExtra3.setText(String.valueOf(e.getTiempoExtra3()));
    }

    private Empleado leerFormulario() {
        Empleado e = new Empleado();
        if (modoEdicion)
            e.setNumeroEmpleado(Integer.parseInt(fNumero.getText().trim()));
        e.setNombre(fNombre.getText().trim());
        e.setDireccion(fDireccion.getText().trim());
        e.setPosicion(fPosicion.getText().trim());
        e.setCelular(fCelular.getText().trim());
        e.setDepartamento(fDepartamento.getText().trim());
        e.setCorreo(fCorreo.getText().trim());
        e.setSalarioPorHora(parseDouble(fSalario, "Salario por hora"));
        e.setTiempoExtra1(parseDouble(fExtra1, "Tiempo extra 1"));
        e.setTiempoExtra2(parseDouble(fExtra2, "Tiempo extra 2"));
        e.setTiempoExtra3(parseDouble(fExtra3, "Tiempo extra 3"));
        return e;
    }

    private double parseDouble(TextField field, String nombreCampo) {
        String texto = field.getText().trim();
        if (texto.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "'" + nombreCampo + "' debe ser un número válido.");
        }
    }

    // ─── Helpers de UI ───────────────────────────────────────────────
    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        if (!mensajeLabel.getStyleClass().contains("mensaje-error"))
            mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
    }

    private void mostrarExito(String msg) {
        mensajeLabel.setText("✔ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-error");
        if (!mensajeLabel.getStyleClass().contains("mensaje-exito"))
            mensajeLabel.getStyleClass().add("mensaje-exito");
        mensajeLabel.setVisible(true);
    }

    private void ocultarMensaje() {
        mensajeLabel.setVisible(false);
    }
}