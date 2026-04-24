package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Equipo;
import com.example.gman.presentation.viewmodel.EquiposViewModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EquiposController {

    // ── Búsqueda ──────────────────────────────────────────────────────
    @FXML private TextField busquedaField;
    @FXML private Label     lblTotalEquipos;

    // ── Tabla ─────────────────────────────────────────────────────────
    @FXML private TableView<Equipo>           tableEquipos;
    @FXML private TableColumn<Equipo, Void>   colNumero;
    @FXML private TableColumn<Equipo, Number> colId;
    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colCapacidad;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colModelo;
    @FXML private TableColumn<Equipo, String> colSerie;
    @FXML private TableColumn<Equipo, String> colArea;
    @FXML private TableColumn<Equipo, String> colPlanta;
    @FXML private TableColumn<Equipo, String> colCentro;
    @FXML private TableColumn<Equipo, String> colCriticidad;
    @FXML private TableColumn<Equipo, String> colTipo;
    @FXML private TableColumn<Equipo, Void>   colAcciones;

    // ── Paneles ───────────────────────────────────────────────────────
    @FXML private VBox  panelLista;
    @FXML private VBox  panelFormulario;
    @FXML private Label lblTituloForm;

    // ── Campos del formulario ─────────────────────────────────────────
    @FXML private TextField        tfCodigo, tfNombre, tfCapacidad;
    @FXML private TextField        tfMarca, tfModelo, tfSerie, tfCentro;
    @FXML private ComboBox<String> cbArea, cbPlanta, cbCriticidad, cbTipo;

    // ── Botones ───────────────────────────────────────────────────────
    @FXML private Button btnAgregar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRetroceder;

    private Equipo           equipoEnEdicion = null;
    private EquiposViewModel viewModel;
    private SessionManager   sessionManager;  // ← NUEVO

    // ── Inyección de dependencias ─────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(EquiposViewModel vm) {
        this.viewModel = vm;
        tableEquipos.setItems(viewModel.getEquipos());
        cbArea.setItems(viewModel.getAreas());
        cbPlanta.setItems(viewModel.getPlanta());
        cbCriticidad.setItems(viewModel.getCriticidad());
        cbTipo.setItems(viewModel.getTipo());
        actualizarTotal();

        configurarComboEditable(cbArea,       viewModel.getAreas());
        configurarComboEditable(cbPlanta,     viewModel.getPlanta());
        configurarComboEditable(cbCriticidad, viewModel.getCriticidad());
        configurarComboEditable(cbTipo,       viewModel.getTipo());

        // ── Aplicar permisos tras tener sessionManager ────────────────
        aplicarPermisos();
    }

    private void aplicarPermisos() {
        if (sessionManager == null) return;

        boolean puedeEditar   = sessionManager.puedeEditar("EQUIPOS");
        boolean puedeEliminar = sessionManager.puedeEliminar("EQUIPOS");

        // Botón "Agregar" solo visible si puede editar
        btnAgregar.setVisible(puedeEditar);
        btnAgregar.setManaged(puedeEditar);

        // Columna acciones se reconfigura según permisos
        configurarColumnaAcciones(puedeEditar, puedeEliminar);
    }

    private void configurarComboEditable(ComboBox<String> combo,
                                         ObservableList<String> lista) {
        combo.setEditable(true);
        combo.setOnAction(e -> {
            String valor = combo.getEditor().getText();
            if (valor != null && !valor.trim().isEmpty()
                    && !lista.contains(valor)) {
                lista.add(valor);
                combo.setValue(valor);
            }
        });
    }

    // ── Inicialización ────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Columnas
        colId.setVisible(false);
        colCodigo.setCellValueFactory(    d -> d.getValue().codigoProperty());
        colNombre.setCellValueFactory(    d -> d.getValue().nombreProperty());
        colCapacidad.setCellValueFactory( d -> d.getValue().capacidadProperty());
        colMarca.setCellValueFactory(     d -> d.getValue().marcaProperty());
        colModelo.setCellValueFactory(    d -> d.getValue().modeloProperty());
        colSerie.setCellValueFactory(     d -> d.getValue().serieProperty());
        colArea.setCellValueFactory(      d -> d.getValue().areaProperty());
        colPlanta.setCellValueFactory(    d -> d.getValue().plantaProperty());
        colCentro.setCellValueFactory(    d -> d.getValue().centroCostosProperty());
        colCriticidad.setCellValueFactory(d -> d.getValue().criticidadProperty());
        colTipo.setCellValueFactory(      d -> d.getValue().tipoProperty());

        colNumero.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        // Botones
        btnAgregar.setOnAction(   e -> abrirFormularioNuevo());
        btnGuardar.setOnAction(   e -> guardarEquipo());
        btnRetroceder.setOnAction(e -> mostrarLista());
    }

    // ── Columna acciones con permisos ─────────────────────────────────
    private void configurarColumnaAcciones(boolean puedeEditar,
                                           boolean puedeEliminar) {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.setTooltip(new Tooltip("Editar este equipo"));
                btnEliminar.setTooltip(new Tooltip("Eliminar este equipo"));

                btnEditar.setOnAction(e -> {
                    Equipo equipo = getTableView().getItems().get(getIndex());
                    abrirFormularioEdicion(equipo);
                });
                btnEliminar.setOnAction(e -> {
                    Equipo equipo = getTableView().getItems().get(getIndex());
                    confirmarYEliminar(equipo);
                });

                // Solo agrega los botones que el rol permite
                if (puedeEditar)   box.getChildren().add(btnEditar);
                if (puedeEliminar) box.getChildren().add(btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                // Si no hay ningún botón visible, oculta la celda
                setGraphic(empty || box.getChildren().isEmpty() ? null : box);
            }
        });
    }

    // ── Búsqueda ──────────────────────────────────────────────────────
    @FXML
    private void filtrarEquipos() {
        String texto = busquedaField.getText();
        tableEquipos.setItems(
                (texto == null || texto.isBlank())
                        ? viewModel.getEquipos()
                        : viewModel.filtrar(texto)
        );
    }

    // ── Navegación ────────────────────────────────────────────────────
    private void mostrarLista() {
        limpiarFormulario();
        if (busquedaField != null) busquedaField.clear();
        tableEquipos.setItems(viewModel.getEquipos());
        tableEquipos.getSelectionModel().clearSelection();
        panelFormulario.setVisible(false);
        panelFormulario.setManaged(false);
        panelLista.setVisible(true);
        panelLista.setManaged(true);
    }

    private void abrirFormularioNuevo() {
        // Doble verificación por seguridad
        if (sessionManager != null && !sessionManager.puedeEditar("EQUIPOS")) {
            mostrarAlerta("No tienes permiso para agregar equipos.");
            return;
        }
        equipoEnEdicion = null;
        limpiarFormulario();
        lblTituloForm.setText("Nuevo Equipo");
        panelLista.setVisible(false);
        panelLista.setManaged(false);
        panelFormulario.setVisible(true);
        panelFormulario.setManaged(true);
    }

    private void abrirFormularioEdicion(Equipo equipo) {
        if (sessionManager != null && !sessionManager.puedeEditar("EQUIPOS")) {
            mostrarAlerta("No tienes permiso para editar equipos.");
            return;
        }
        equipoEnEdicion = equipo;
        llenarFormulario(equipo);
        lblTituloForm.setText("Editar Equipo");
        panelLista.setVisible(false);
        panelLista.setManaged(false);
        panelFormulario.setVisible(true);
        panelFormulario.setManaged(true);
    }

    // ── Acciones ──────────────────────────────────────────────────────
    private void guardarEquipo() {
        if (sessionManager != null && !sessionManager.puedeEditar("EQUIPOS")) {
            mostrarAlerta("No tienes permiso para guardar equipos.");
            return;
        }
        try {
            Equipo equipo = new Equipo();
            if (equipoEnEdicion != null) equipo.setId(equipoEnEdicion.getId());

            equipo.setCodigo(      tfCodigo.getText().trim());
            equipo.setNombre(      tfNombre.getText().trim());
            equipo.setCapacidad(   tfCapacidad.getText().trim());
            equipo.setMarca(       tfMarca.getText().trim());
            equipo.setModelo(      tfModelo.getText().trim());
            equipo.setSerie(       tfSerie.getText().trim());
            equipo.setArea(        cbArea.getValue());
            equipo.setPlanta(      cbPlanta.getValue());
            equipo.setCentroCostos(tfCentro.getText().trim());
            equipo.setCriticidad(  cbCriticidad.getValue());
            equipo.setTipo(        cbTipo.getValue());

            String error = viewModel.validarEquipo(equipo);
            if (!error.isEmpty()) { mostrarAlerta(error); return; }

            if (viewModel.existeCodigo(equipo.getCodigo(), equipo.getId())) {
                mostrarAlerta("El código ya existe, usa uno diferente.");
                return;
            }

            viewModel.guardarEquipo(equipo);
            mostrarInfo("Equipo guardado correctamente.");
            actualizarTotal();
            mostrarLista();

        } catch (Exception ex) {
            mostrarAlerta("Error al guardar equipo: " + ex.getMessage());
        }
    }

    private void confirmarYEliminar(Equipo equipo) {
        if (sessionManager != null && !sessionManager.puedeEliminar("EQUIPOS")) {
            mostrarAlerta("No tienes permiso para eliminar equipos.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Eliminar equipo");
        confirm.setContentText("¿Desea eliminar el equipo: " + equipo.getNombre() + "?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            viewModel.eliminarEquipo(equipo);
            actualizarTotal();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private void llenarFormulario(Equipo e) {
        tfCodigo.setText(     e.getCodigo());
        tfNombre.setText(     e.getNombre());
        tfCapacidad.setText(  e.getCapacidad());
        tfMarca.setText(      e.getMarca());
        tfModelo.setText(     e.getModelo());
        tfSerie.setText(      e.getSerie());
        cbArea.setValue(      e.getArea());
        cbPlanta.setValue(    e.getPlanta());
        tfCentro.setText(     e.getCentroCostos());
        cbCriticidad.setValue(e.getCriticidad());
        cbTipo.setValue(      e.getTipo());
    }

    private void limpiarFormulario() {
        tfCodigo.clear(); tfNombre.clear();    tfCapacidad.clear();
        tfMarca.clear();  tfModelo.clear();    tfSerie.clear();
        tfCentro.clear();
        cbArea.setValue(null);       cbPlanta.setValue(null);
        cbCriticidad.setValue(null); cbTipo.setValue(null);
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Atención");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void actualizarTotal() {
        if (viewModel != null && viewModel.getEquipos() != null)
            lblTotalEquipos.setText("Total de equipos: " + viewModel.getEquipos().size());
    }
}