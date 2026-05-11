package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.model.Localizacion;
import com.example.gman.presentation.viewmodel.EquiposViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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
    @FXML private TableColumn<Equipo, String> colLocalizacion;   // antes: colPlanta
    @FXML private TableColumn<Equipo, String> colCentro;
    @FXML private TableColumn<Equipo, String> colCriticidad;
    @FXML private TableColumn<Equipo, String> colTipo;
    @FXML private TableColumn<Equipo, Void>   colAcciones;

    // ── Paneles ───────────────────────────────────────────────────────
    @FXML private VBox  panelLista;
    @FXML private VBox  panelFormulario;
    @FXML private Label lblTituloForm;

    // ── Campos del formulario ─────────────────────────────────────────
    @FXML private TextField tfCodigo, tfNombre, tfCapacidad;
    @FXML private TextField tfMarca, tfModelo, tfSerie, tfCentro;

    // CORREGIDO: combos tipados con Catalogo y Localizacion (no String)
    @FXML private ComboBox<Catalogo>     cbArea;
    @FXML private ComboBox<Localizacion> cbLocalizacion;   // antes: cbPlanta
    @FXML private ComboBox<Catalogo>     cbCriticidad;
    @FXML private ComboBox<Catalogo>     cbTipo;

    // ── Botones ───────────────────────────────────────────────────────
    @FXML private Button btnAgregar;
    @FXML private Button btnGuardar;
    @FXML private Button btnRetroceder;

    private Equipo           equipoEnEdicion = null;
    private EquiposViewModel viewModel;
    private SessionManager   sessionManager;

    // ── Inyección de dependencias ─────────────────────────────────────

    public void setCoordinator(AppCoordinator coordinator) {
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(EquiposViewModel vm) {
        this.viewModel = vm;

        tableEquipos.setItems(viewModel.getEquipos());

        // CORREGIDO: combos reciben List<Catalogo> / List<Localizacion>
        cbArea.setItems(viewModel.getAreas());
        cbLocalizacion.setItems(viewModel.getLocalizaciones());
        cbCriticidad.setItems(viewModel.getCriticidades());
        cbTipo.setItems(viewModel.getTiposEquipo());

        // Convertidores para que el ComboBox muestre el nombre (no el toString del objeto)
        cbArea.setConverter(catalogoConverter());
        cbCriticidad.setConverter(catalogoConverter());
        cbTipo.setConverter(catalogoConverter());
        cbLocalizacion.setConverter(localizacionConverter());

        actualizarTotal();
        aplicarPermisos();
    }

    // ── Inicialización ────────────────────────────────────────────────

    @FXML
    public void initialize() {
        colId.setVisible(false);

        // CORREGIDO: columnas de tabla usan los campos *Nombre (display)
        colCodigo.setCellValueFactory(       d -> d.getValue().codigoProperty());
        colNombre.setCellValueFactory(       d -> d.getValue().nombreProperty());
        colCapacidad.setCellValueFactory(    d -> d.getValue().capacidadProperty());
        colMarca.setCellValueFactory(        d -> d.getValue().marcaProperty());
        colModelo.setCellValueFactory(       d -> d.getValue().modeloProperty());
        colSerie.setCellValueFactory(        d -> d.getValue().serieProperty());
        colArea.setCellValueFactory(         d -> d.getValue().areaNombreProperty());
        colLocalizacion.setCellValueFactory( d -> d.getValue().localizacionNombreProperty());
        colCentro.setCellValueFactory(       d -> d.getValue().centroCostosProperty());
        colCriticidad.setCellValueFactory(   d -> d.getValue().criticidadNombreProperty());
        colTipo.setCellValueFactory(         d -> d.getValue().tipoNombreProperty());

        colNumero.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        btnAgregar.setOnAction(   e -> abrirFormularioNuevo());
        btnGuardar.setOnAction(   e -> guardarEquipo());
        btnRetroceder.setOnAction(e -> mostrarLista());
    }

    // ── Permisos ──────────────────────────────────────────────────────

    private void aplicarPermisos() {
        if (sessionManager == null) return;
        boolean puedeEditar   = sessionManager.puedeEditar("EQUIPOS");
        boolean puedeEliminar = sessionManager.puedeEliminar("EQUIPOS");
        btnAgregar.setVisible(puedeEditar);
        btnAgregar.setManaged(puedeEditar);
        configurarColumnaAcciones(puedeEditar, puedeEliminar);
    }

    private void configurarColumnaAcciones(boolean puedeEditar, boolean puedeEliminar) {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.setTooltip(new Tooltip("Editar este equipo"));
                btnEliminar.setTooltip(new Tooltip("Eliminar este equipo"));
                btnEditar.setOnAction(e -> abrirFormularioEdicion(
                        getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> confirmarYEliminar(
                        getTableView().getItems().get(getIndex())));
                if (puedeEditar)   box.getChildren().add(btnEditar);
                if (puedeEliminar) box.getChildren().add(btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
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
                        : viewModel.filtrar(texto));
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

    // ── Guardar ───────────────────────────────────────────────────────

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
            equipo.setCentroCostos(tfCentro.getText().trim());

            // CORREGIDO: se extrae el ID del objeto seleccionado en el combo
            Catalogo selArea = cbArea.getValue();
            equipo.setAreaId(selArea != null ? selArea.getId() : 0);

            Localizacion selLoc = cbLocalizacion.getValue();
            equipo.setLocalizacionId(selLoc != null ? selLoc.getId() : 0);

            Catalogo selCrit = cbCriticidad.getValue();
            equipo.setCriticidadId(selCrit != null ? selCrit.getId() : 0);

            Catalogo selTipo = cbTipo.getValue();
            equipo.setTipoId(selTipo != null ? selTipo.getId() : 0);

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

    // ── Helpers formulario ────────────────────────────────────────────

    /**
     * Carga los datos del equipo en el formulario al editar.
     * CORREGIDO: busca el objeto Catalogo/Localizacion por ID para pre-seleccionar
     * el combo correctamente (no por String).
     */
    private void llenarFormulario(Equipo e) {
        tfCodigo.setText(   e.getCodigo());
        tfNombre.setText(   e.getNombre());
        tfCapacidad.setText(e.getCapacidad());
        tfMarca.setText(    e.getMarca());
        tfModelo.setText(   e.getModelo());
        tfSerie.setText(    e.getSerie());
        tfCentro.setText(   e.getCentroCostos());

        // Seleccionar el item del combo que coincida con el FK almacenado
        cbArea.getItems().stream()
                .filter(c -> c.getId() == e.getAreaId())
                .findFirst().ifPresent(cbArea::setValue);

        cbLocalizacion.getItems().stream()
                .filter(l -> l.getId() == e.getLocalizacionId())
                .findFirst().ifPresent(cbLocalizacion::setValue);

        cbCriticidad.getItems().stream()
                .filter(c -> c.getId() == e.getCriticidadId())
                .findFirst().ifPresent(cbCriticidad::setValue);

        cbTipo.getItems().stream()
                .filter(c -> c.getId() == e.getTipoId())
                .findFirst().ifPresent(cbTipo::setValue);
    }

    private void limpiarFormulario() {
        tfCodigo.clear();   tfNombre.clear();   tfCapacidad.clear();
        tfMarca.clear();    tfModelo.clear();    tfSerie.clear();
        tfCentro.clear();
        cbArea.setValue(null);
        cbLocalizacion.setValue(null);
        cbCriticidad.setValue(null);
        cbTipo.setValue(null);
    }

    // ── Convertidores para ComboBox ───────────────────────────────────

    /** Muestra catalogo.getNombre() en el ComboBox y en la celda seleccionada. */
    private StringConverter<Catalogo> catalogoConverter() {
        return new StringConverter<>() {
            @Override public String toString(Catalogo c) {
                return c == null ? "" : c.getNombre();
            }
            @Override public Catalogo fromString(String s) { return null; }
        };
    }

    /** Muestra localizacion.getDescripcion() en el ComboBox. */
    private StringConverter<Localizacion> localizacionConverter() {
        return new StringConverter<>() {
            @Override public String toString(Localizacion l) {
                return l == null ? "" : l.getNumeroLocalizacion() + " — " + l.getDescripcion();
            }
            @Override public Localizacion fromString(String s) { return null; }
        };
    }

    // ── Alertas ───────────────────────────────────────────────────────

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