package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Localizacion;
import com.example.gman.presentation.viewmodel.LocalizacionesViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

public class LocalizacionesController {

    // ── Tabla y filtros ──────────────────────────────────────────────
    @FXML private Button                      btnAgregar;
    @FXML private TextField                   txtBuscar;
    @FXML private ComboBox<Catalogo>          cmbFiltroDept;
    @FXML private TableView<Localizacion>     tablaLocalizaciones;
    @FXML private TableColumn<Localizacion, String> colNumero;
    @FXML private TableColumn<Localizacion, String> colDescripcion;
    @FXML private TableColumn<Localizacion, String> colDepartamento;
    @FXML private TableColumn<Localizacion, String> colNotas;
    @FXML private TableColumn<Localizacion, Void>   colAcciones;

    // ── Formulario overlay ───────────────────────────────────────────
    @FXML private StackPane          overlayForm;
    @FXML private Label              lblFormTitulo;
    @FXML private TextField          txtNumero;
    @FXML private TextField          txtDescripcion;
    @FXML private ComboBox<Catalogo> cmbDepartamento;
    @FXML private TextArea           txtNotas;
    @FXML private Label              lblError;

    // ── Confirmar eliminación ────────────────────────────────────────
    @FXML private StackPane overlayConfirmar;
    @FXML private Label     lblConfirmarMsg;

    // ── Dependencias y estado ────────────────────────────────────────
    private LocalizacionesViewModel viewModel;
    private SessionManager          session;

    private List<Localizacion>  todasLasLocs;   // caché para filtrado en memoria
    private Localizacion        editando;        // null = modo agregar
    private Localizacion        aEliminar;

    // ════════════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        viewModel = new LocalizacionesViewModel();
        configurarTabla();
        cargarCombos();
        cargarTabla();
    }

    public void setSession(SessionManager session) {
        this.session = session;

        // ── FIX: usar permisos granulares ────────────────────────────
        boolean puedeCrear = session.puedeCrear("LOCALIZACIONES");
        btnAgregar.setVisible(puedeCrear);
        btnAgregar.setManaged(puedeCrear);
        tablaLocalizaciones.refresh();
    }

    // ════════════════════════════════════════════════════════════════
    //  CONFIGURACIÓN DE TABLA
    // ════════════════════════════════════════════════════════════════

    private void configurarTabla() {
        colNumero.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNumeroLocalizacion()));
        colDescripcion.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDescripcion()));
        colDepartamento.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDepartamentoNombre() != null
                                ? c.getValue().getDepartamentoNombre() : "—"));
        colNotas.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getNotas() != null ? c.getValue().getNotas() : ""));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏ Editar");
            private final Button btnEliminar = new Button("🗑 Eliminar");
            private final HBox   box         = new HBox(6, btnEditar, btnEliminar);

            {
                box.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-eliminar");

                btnEditar.setOnAction(e ->
                        abrirFormEditar(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e ->
                        pedirConfirmacion(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // ── FIX: permisos granulares ──────────────────────────
                    boolean puedeEditar   = session != null && session.puedeEditar("LOCALIZACIONES");
                    boolean puedeEliminar = session != null && session.puedeEliminar("LOCALIZACIONES");
                    btnEditar.setVisible(puedeEditar);
                    btnEditar.setManaged(puedeEditar);
                    btnEliminar.setVisible(puedeEliminar);
                    btnEliminar.setManaged(puedeEliminar);
                    setGraphic(box.getChildren().isEmpty() ? null : box);
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ════════════════════════════════════════════════════════════════

    private void cargarCombos() {
        List<Catalogo> departamentos = viewModel.listarDepartamentos();

        // Combo filtro (con opción "Todos")
        ObservableList<Catalogo> itemsFiltro = FXCollections.observableArrayList();
        Catalogo todos = new Catalogo(0, "", "", "Todos", null);
        itemsFiltro.add(todos);
        itemsFiltro.addAll(departamentos);
        cmbFiltroDept.setItems(itemsFiltro);
        cmbFiltroDept.getSelectionModel().selectFirst();

        // Combo formulario
        ObservableList<Catalogo> itemsForm = FXCollections.observableArrayList();
        Catalogo sinDept = new Catalogo(0, "", "", "— Sin departamento —", null);
        itemsForm.add(sinDept);
        itemsForm.addAll(departamentos);
        cmbDepartamento.setItems(itemsForm);
        cmbDepartamento.getSelectionModel().selectFirst();
    }

    private void cargarTabla() {
        todasLasLocs = viewModel.listarTodas();
        tablaLocalizaciones.setItems(FXCollections.observableArrayList(todasLasLocs));
    }

    // ════════════════════════════════════════════════════════════════
    //  FILTROS
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void filtrar() {
        String   texto = txtBuscar.getText();
        Catalogo dept  = cmbFiltroDept.getValue();
        // Si "Todos" está seleccionado (id=0), pasar null para no filtrar por dept
        Catalogo deptFiltro = (dept != null && dept.getId() > 0) ? dept : null;
        List<Localizacion> filtradas = viewModel.filtrar(todasLasLocs, texto, deptFiltro);
        tablaLocalizaciones.setItems(FXCollections.observableArrayList(filtradas));
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbFiltroDept.getSelectionModel().selectFirst();
        tablaLocalizaciones.setItems(FXCollections.observableArrayList(todasLasLocs));
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO AGREGAR / EDITAR
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void abrirFormAgregar() {
        editando = null;
        lblFormTitulo.setText("Agregar Localización");
        limpiarForm();
        mostrarOverlayForm(true);
    }

    private void abrirFormEditar(Localizacion loc) {
        editando = loc;
        lblFormTitulo.setText("Editar Localización");

        txtNumero.setText(loc.getNumeroLocalizacion());
        txtDescripcion.setText(loc.getDescripcion());
        txtNotas.setText(loc.getNotas() != null ? loc.getNotas() : "");

        // Seleccionar departamento en el combo
        cmbDepartamento.getItems().stream()
                .filter(c -> c.getId() == loc.getDepartamentoId())
                .findFirst()
                .ifPresentOrElse(
                        c -> cmbDepartamento.getSelectionModel().select(c),
                        () -> cmbDepartamento.getSelectionModel().selectFirst());

        lblError.setText("");
        mostrarOverlayForm(true);
    }

    @FXML
    private void guardarRegistro() {
        String numero      = txtNumero.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Catalogo dept      = cmbDepartamento.getValue();
        String notas       = txtNotas.getText().trim();

        if (numero.isEmpty() || descripcion.isEmpty()) {
            lblError.setText("N° Localización y Descripción son obligatorios.");
            return;
        }

        try {
            if (editando == null) {
                viewModel.crear(numero, descripcion, dept, notas);
            } else {
                viewModel.actualizar(editando, numero, descripcion, dept, notas);
            }
            cerrarForm();
            cargarTabla();
            cargarCombos(); // refrescar por si cambió algo
        } catch (Exception e) {
            lblError.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarForm() {
        mostrarOverlayForm(false);
        limpiarForm();
    }

    private void limpiarForm() {
        txtNumero.clear();
        txtDescripcion.clear();
        txtNotas.clear();
        cmbDepartamento.getSelectionModel().selectFirst();
        lblError.setText("");
        editando = null;
    }

    private void mostrarOverlayForm(boolean visible) {
        overlayForm.setVisible(visible);
        overlayForm.setManaged(visible);
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ════════════════════════════════════════════════════════════════

    private void pedirConfirmacion(Localizacion loc) {
        aEliminar = loc;
        lblConfirmarMsg.setText(
                "¿Seguro que desea eliminar «" + loc.getNumeroLocalizacion()
                        + " — " + loc.getDescripcion() + "»?\n"
                        + "Esta acción no se puede deshacer.");
        overlayConfirmar.setVisible(true);
        overlayConfirmar.setManaged(true);
    }

    @FXML
    private void confirmarEliminar() {
        if (aEliminar == null) return;
        try {
            viewModel.eliminar(aEliminar.getId());
            cerrarConfirmar();
            cargarTabla();
        } catch (Exception e) {
            cerrarConfirmar();
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo eliminar:\n" + e.getMessage(),
                    ButtonType.OK).showAndWait();
        }
    }

    @FXML
    private void cerrarConfirmar() {
        aEliminar = null;
        overlayConfirmar.setVisible(false);
        overlayConfirmar.setManaged(false);
    }
}