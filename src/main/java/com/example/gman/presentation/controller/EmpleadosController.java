package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.Localizacion;
import com.example.gman.presentation.viewmodel.EmpleadosViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

public class EmpleadosController {

    // ── Tabla y búsqueda ─────────────────────────────────────────────
    @FXML private Button                     btnNuevo;
    @FXML private TextField                  busquedaField;
    @FXML private TableView<Empleado>        empleadosTable;
    @FXML private TableColumn<Empleado, Integer> colNumero;
    @FXML private TableColumn<Empleado, String>  colNombre;
    @FXML private TableColumn<Empleado, String>  colPosicion;
    @FXML private TableColumn<Empleado, String>  colDepartamento;
    @FXML private TableColumn<Empleado, String>  colCorreo;
    @FXML private TableColumn<Empleado, Void>    colAcciones;
    @FXML private Label                      mensajeLabel;

    // ── Overlay formulario ───────────────────────────────────────────
    @FXML private StackPane          overlayPane;
    @FXML private Label              dialogoTitulo;
    @FXML private TextField          fNumero;
    @FXML private TextField          fNombre;
    @FXML private TextField          fDireccion;
    @FXML private ComboBox<Catalogo>     cmbPosicion;
    @FXML private ComboBox<Catalogo>     cmbDepartamento;
    @FXML private ComboBox<Localizacion> cmbLocalizacion;
    @FXML private TextField          fCelular;
    @FXML private TextField          fCorreo;
    @FXML private TextField          fSalario;
    @FXML private TextField          fExtra1;
    @FXML private TextField          fExtra2;
    @FXML private TextField          fExtra3;
    @FXML private Label              lblError;

    // ── Overlay confirmar eliminación ────────────────────────────────
    @FXML private StackPane overlayConfirmar;
    @FXML private Label     lblConfirmarMsg;

    // ── Dependencias y estado ────────────────────────────────────────
    private EmpleadosViewModel viewModel;
    private AppCoordinator     coordinator;
    private SessionManager     sessionManager;
    private boolean            modoEdicion = false;
    private Empleado           empleadoEditando;
    private Empleado           empleadoAEliminar;

    // ════════════════════════════════════════════════════════════════
    //  INYECCIÓN
    // ════════════════════════════════════════════════════════════════

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator  = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(EmpleadosViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarTabla();
        cargarCombos();
        cargarDatos();
        aplicarPermisos();
    }

    // ════════════════════════════════════════════════════════════════
    //  PERMISOS
    // ════════════════════════════════════════════════════════════════

    private void aplicarPermisos() {
        if (sessionManager == null) return;

        boolean puedeCrear    = sessionManager.tienePermiso("MANO_OBRA");
        boolean puedeEditar   = sessionManager.tienePermiso("MANO_OBRA");
        boolean puedeEliminar = sessionManager.tienePermiso("MANO_OBRA");

        btnNuevo.setVisible(puedeCrear);
        btnNuevo.setManaged(puedeCrear);
        colAcciones.setCellFactory(crearCeldaAcciones(puedeEditar, puedeEliminar));
    }

    // ════════════════════════════════════════════════════════════════
    //  INICIALIZACIÓN DE TABLA
    // ════════════════════════════════════════════════════════════════

    private void inicializarTabla() {
        colNumero.setCellValueFactory(d ->
                d.getValue().numeroEmpleadoProperty().asObject());
        colNombre.setCellValueFactory(d ->
                d.getValue().nombreProperty());
        colPosicion.setCellValueFactory(d ->
                d.getValue().posicionNombreProperty());
        colDepartamento.setCellValueFactory(d ->
                d.getValue().departamentoNombreProperty());
        colCorreo.setCellValueFactory(d ->
                d.getValue().correoProperty());

        colAcciones.setCellFactory(crearCeldaAcciones(true, true));
        empleadosTable.setItems(viewModel.getEmpleados());
    }

    // ════════════════════════════════════════════════════════════════
    //  COMBOS
    // ════════════════════════════════════════════════════════════════

    private void cargarCombos() {
        // Posiciones
        ObservableList<Catalogo> posiciones = FXCollections.observableArrayList(
                viewModel.listarPosiciones());
        cmbPosicion.setItems(posiciones);

        // Departamentos
        ObservableList<Catalogo> departamentos = FXCollections.observableArrayList(
                viewModel.listarDepartamentos());
        cmbDepartamento.setItems(departamentos);

        // Localizaciones — agregar opción vacía al inicio
        ObservableList<Localizacion> locs = FXCollections.observableArrayList();
        Localizacion sinLoc = new Localizacion();
        sinLoc.setId(0);
        sinLoc.setDescripcion("— Sin localización —");
        locs.add(sinLoc);
        locs.addAll(viewModel.listarLocalizaciones());
        cmbLocalizacion.setItems(locs);
        cmbLocalizacion.getSelectionModel().selectFirst();
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ════════════════════════════════════════════════════════════════

    private void cargarDatos() {
        try {
            viewModel.cargarEmpleados();
            ocultarMensaje();
        } catch (Exception e) {
            mostrarError("Error al cargar empleados: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  FILTRO
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void filtrarEmpleados() {
        empleadosTable.setItems(viewModel.filtrar(busquedaField.getText()));
    }

    @FXML
    private void limpiarBusqueda() {
        busquedaField.clear();
        empleadosTable.setItems(viewModel.getEmpleados());
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO — ABRIR
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void abrirNuevo() {
        modoEdicion      = false;
        empleadoEditando = null;
        dialogoTitulo.setText("Nuevo Empleado");
        limpiarFormulario();
        overlayPane.setVisible(true);
    }

    private void abrirEdicion(Empleado e) {
        modoEdicion      = true;
        empleadoEditando = e;
        dialogoTitulo.setText("Editar Empleado");
        poblarFormulario(e);
        overlayPane.setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO — GUARDAR
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void guardar() {
        try {
            Empleado e = leerFormulario();
            if (modoEdicion) {
                e.setNumeroEmpleado(empleadoEditando.getNumeroEmpleado());
                viewModel.actualizarEmpleado(e);
                mostrarExito("Empleado actualizado correctamente.");
            } else {
                viewModel.crearEmpleado(e);
                mostrarExito("Empleado creado correctamente.");
            }
            cerrarDialogo();
            cargarDatos();
        } catch (IllegalArgumentException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void cerrarDialogo() {
        overlayPane.setVisible(false);
        limpiarFormulario();
        ocultarMensaje();
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINACIÓN
    // ════════════════════════════════════════════════════════════════

    private void pedirConfirmacion(Empleado e) {
        empleadoAEliminar = e;
        lblConfirmarMsg.setText(
                "¿Seguro que desea eliminar a «" + e.getNombre()
                        + "» (#" + e.getNumeroEmpleado() + ")?\n"
                        + "Esta acción no se puede deshacer.");
        overlayConfirmar.setVisible(true);
    }

    @FXML
    private void confirmarEliminar() {
        if (empleadoAEliminar == null) return;
        try {
            viewModel.eliminarEmpleado(empleadoAEliminar.getNumeroEmpleado());
            cerrarConfirmar();
            cargarDatos();
            mostrarExito("Empleado eliminado correctamente.");
        } catch (Exception ex) {
            cerrarConfirmar();
            mostrarError("Error al eliminar: " + ex.getMessage());
        }
    }

    @FXML
    private void cerrarConfirmar() {
        empleadoAEliminar = null;
        overlayConfirmar.setVisible(false);
    }

    // ════════════════════════════════════════════════════════════════
    //  CELDA DE ACCIONES
    // ════════════════════════════════════════════════════════════════

    private Callback<TableColumn<Empleado, Void>, TableCell<Empleado, Void>>
    crearCeldaAcciones(boolean puedeEditar, boolean puedeEliminar) {
        return col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏ Editar");
            private final Button btnEliminar = new Button("🗑 Eliminar");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-eliminar");
                btnEditar.setOnAction(e ->
                        abrirEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e ->
                        pedirConfirmacion(getTableView().getItems().get(getIndex())));
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

    // ════════════════════════════════════════════════════════════════
    //  HELPERS DE FORMULARIO
    // ════════════════════════════════════════════════════════════════

    private void limpiarFormulario() {
        fNumero.clear();
        fNombre.clear();
        fDireccion.clear();
        fCelular.clear();
        fCorreo.clear();
        fSalario.clear();
        fExtra1.clear();
        fExtra2.clear();
        fExtra3.clear();
        cmbPosicion.getSelectionModel().clearSelection();
        cmbDepartamento.getSelectionModel().clearSelection();
        cmbLocalizacion.getSelectionModel().selectFirst();
        lblError.setText("");
    }

    private void poblarFormulario(Empleado e) {
        fNumero.setText(String.valueOf(e.getNumeroEmpleado()));
        fNombre.setText(e.getNombre());
        fDireccion.setText(e.getDireccion() != null ? e.getDireccion() : "");
        fCelular.setText(e.getCelular() != null ? e.getCelular() : "");
        fCorreo.setText(e.getCorreo() != null ? e.getCorreo() : "");
        fSalario.setText(e.getSalarioPorHora() > 0 ? String.valueOf(e.getSalarioPorHora()) : "");
        fExtra1.setText(e.getTiempoExtra1() > 0 ? String.valueOf(e.getTiempoExtra1()) : "");
        fExtra2.setText(e.getTiempoExtra2() > 0 ? String.valueOf(e.getTiempoExtra2()) : "");
        fExtra3.setText(e.getTiempoExtra3() > 0 ? String.valueOf(e.getTiempoExtra3()) : "");

        // Seleccionar posición
        cmbPosicion.getItems().stream()
                .filter(c -> c.getId() == e.getPosicionId())
                .findFirst()
                .ifPresent(c -> cmbPosicion.getSelectionModel().select(c));

        // Seleccionar departamento
        cmbDepartamento.getItems().stream()
                .filter(c -> c.getId() == e.getDepartamentoId())
                .findFirst()
                .ifPresent(c -> cmbDepartamento.getSelectionModel().select(c));

        // Seleccionar localización
        cmbLocalizacion.getItems().stream()
                .filter(l -> l.getId() == e.getLocalizacionId())
                .findFirst()
                .ifPresentOrElse(
                        l -> cmbLocalizacion.getSelectionModel().select(l),
                        () -> cmbLocalizacion.getSelectionModel().selectFirst());

        lblError.setText("");
    }

    private Empleado leerFormulario() {
        Empleado e = new Empleado();

        String nombre = fNombre.getText().trim();
        if (nombre.isEmpty())
            throw new IllegalArgumentException("El nombre es obligatorio.");

        Catalogo posicion = cmbPosicion.getValue();
        if (posicion == null)
            throw new IllegalArgumentException("Debe seleccionar una posición.");

        Catalogo departamento = cmbDepartamento.getValue();
        if (departamento == null)
            throw new IllegalArgumentException("Debe seleccionar un departamento.");

        e.setNombre(nombre);
        e.setDireccion(fDireccion.getText().trim());
        e.setPosicionId(posicion.getId());
        e.setCelular(fCelular.getText().trim());
        e.setCorreo(fCorreo.getText().trim());
        e.setDepartamentoId(departamento.getId());

        Localizacion loc = cmbLocalizacion.getValue();
        e.setLocalizacionId(loc != null && loc.getId() > 0 ? loc.getId() : 0);

        e.setSalarioPorHora(parseDouble(fSalario, "Salario por hora"));
        e.setTiempoExtra1(parseDouble(fExtra1, "Factor extra 1"));
        e.setTiempoExtra2(parseDouble(fExtra2, "Factor extra 2"));
        e.setTiempoExtra3(parseDouble(fExtra3, "Factor extra 3"));

        return e;
    }

    private double parseDouble(TextField field, String nombreCampo) {
        String texto = field.getText().trim();
        if (texto.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("«" + nombreCampo + "» debe ser un número válido.");
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS DE UI
    // ════════════════════════════════════════════════════════════════

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