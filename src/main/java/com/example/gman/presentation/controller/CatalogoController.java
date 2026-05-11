package com.example.gman.presentation.controller;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.presentation.viewmodel.CatalogoViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
public class CatalogoController {

    // ── Hub ──────────────────────────────────────────────────────────
    @FXML private javafx.scene.control.ScrollPane scrollHub;

    // ── Panel CRUD ───────────────────────────────────────────────────
    @FXML private AnchorPane                      panelCrud;
    @FXML private Label                           lblSubcatalogo;
    @FXML private Button                          btnAgregar;
    @FXML private TableView<Catalogo>             tablaCatalogo;
    @FXML private TableColumn<Catalogo, String>   colCodigo;
    @FXML private TableColumn<Catalogo, String>   colNombre;
    @FXML private TableColumn<Catalogo, String>   colDescripcion;
    @FXML private TableColumn<Catalogo, Void>     colAcciones;

    // ── Formulario overlay ───────────────────────────────────────────
    @FXML private StackPane overlayForm;
    @FXML private Label     lblFormTitulo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea  txtDescripcion;
    @FXML private Label     lblError;

    // ── Diálogo confirmar eliminación ────────────────────────────────
    @FXML private StackPane overlayConfirmar;
    @FXML private Label     lblConfirmarMsg;

    // ── Dependencias ─────────────────────────────────────────────────
    private CatalogoViewModel viewModel;
    private SessionManager    session;

    // ── Estado interno ───────────────────────────────────────────────
    private String    tipoActual;       // tipo de catálogo activo (ej. "DEPARTAMENTO")
    private Catalogo  registroEditando; // null = modo agregar
    private Catalogo  registroAEliminar;
    private boolean   soloLectura;

    // ════════════════════════════════════════════════════════════════
    //  INICIALIZACIÓN
    // ════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        viewModel = new CatalogoViewModel(new CatalogoService());
        configurarTabla();
    }

    /** Llamado desde AppCoordinator después de cargar el FXML. */
    public void setSession(SessionManager session) {
        this.session = session;
        // Permisos: si no puede crear, ocultar botón Agregar
        boolean puedeCrear = session.tienePermiso("CATALOGO");
        btnAgregar.setVisible(puedeCrear);
        btnAgregar.setManaged(puedeCrear);
        soloLectura = !session.tienePermiso("CATALOGO");
    }


    // ════════════════════════════════════════════════════════════════
    //  CONFIGURACIÓN DE TABLA
    // ════════════════════════════════════════════════════════════════

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(c ->
                new SimpleStringProperty(
                        c.getValue().getDescripcion() != null ? c.getValue().getDescripcion() : "—"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏ Editar");
            private final Button btnEliminar = new Button("🗑 Eliminar");
            private final HBox   box         = new HBox(6, btnEditar, btnEliminar);

            {
                box.setAlignment(Pos.CENTER);
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-eliminar");

                btnEditar.setOnAction(e -> {
                    Catalogo c = getTableView().getItems().get(getIndex());
                    abrirFormEditar(c);
                });
                btnEliminar.setOnAction(e -> {
                    Catalogo c = getTableView().getItems().get(getIndex());
                    pedirConfirmacion(c);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Ocultar acciones según permisos
                    boolean puedeEditar   = session != null && session.tienePermiso("CATALOGO");
                    boolean puedeEliminar = session != null && session.tienePermiso("CATALOGO");   btnEditar.setVisible(puedeEditar);
                    btnEditar.setManaged(puedeEditar);
                    btnEliminar.setVisible(puedeEliminar);
                    btnEliminar.setManaged(puedeEliminar);
                    setGraphic(box);
                }
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  NAVEGACIÓN HUB → CRUD
    // ════════════════════════════════════════════════════════════════

    @FXML private void openDepartamento()    { abrirCrud("DEPARTAMENTO",     "🏢  Departamentos"); }
    @FXML private void openPosicion()        { abrirCrud("POSICION",         "👷  Posiciones"); }
    @FXML private void openArea()            { abrirCrud("AREA",             "🗺  Áreas"); }
    @FXML private void openTipoEquipo()      { abrirCrud("TIPO_EQUIPO",      "⚙  Tipos de Equipo"); }
    @FXML private void openCriticidad()      { abrirCrud("CRITICIDAD",       "⚠  Criticidad"); }
    @FXML private void openFalla()           { abrirCrud("FALLA",            "🔧  Códigos de Falla"); }
    @FXML private void openTipoOT()          { abrirCrud("TIPO_OT",          "📋  Tipos de OT"); }
    @FXML private void openEstadoOT()        { abrirCrud("ESTADO_OT",        "🔄  Estados de OT"); }
    @FXML private void openPrioridad()       { abrirCrud("PRIORIDAD",        "🚦  Prioridades"); }
    @FXML private void openEstadoEquipo()    { abrirCrud("ESTADO_EQUIPO",    "🖥  Estados de Equipo"); }
    @FXML private void openTipoInstruccion() { abrirCrud("TIPO_INSTRUCCION", "📄  Tipos de Instrucción"); }
    @FXML private void openMiscelaneo()      { abrirCrud("MISCELANEO",       "📦  Misceláneos"); }
    @FXML private void openProveedores()     { abrirCrud("__PROVEEDORES__",  "🏭  Proveedores"); }

    private void abrirCrud(String tipo, String titulo) {
        tipoActual = tipo;
        lblSubcatalogo.setText(titulo);

        // Mostrar panel CRUD, ocultar hub
        scrollHub.setVisible(false);
        scrollHub.setManaged(false);
        panelCrud.setVisible(true);
        panelCrud.setManaged(true);

        cargarTabla();
    }

    @FXML
    private void volverAlHub() {
        panelCrud.setVisible(false);
        panelCrud.setManaged(false);
        scrollHub.setVisible(true);
        scrollHub.setManaged(true);
        tipoActual = null;
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGA DE DATOS
    // ════════════════════════════════════════════════════════════════

    private void cargarTabla() {
        try {
            if ("__PROVEEDORES__".equals(tipoActual)) {
                // Proveedores tienen su propia tabla — se mapean a Catalogo
                // para reutilizar la vista; campos extra se ignoran aquí.
                tablaCatalogo.setItems(
                        FXCollections.observableArrayList(viewModel.listarProveedoresComoItems()));
            } else {
                tablaCatalogo.setItems(
                        FXCollections.observableArrayList(viewModel.listarPorTipo(tipoActual)));
            }
        } catch (Exception e) {
            mostrarError("Error al cargar datos: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO AGREGAR / EDITAR
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void abrirFormAgregar() {
        registroEditando = null;
        lblFormTitulo.setText("Agregar — " + lblSubcatalogo.getText().replaceAll("^\\S+\\s+", ""));
        limpiarForm();
        mostrarOverlayForm(true);
    }

    private void abrirFormEditar(Catalogo c) {
        registroEditando = c;
        lblFormTitulo.setText("Editar registro");
        txtCodigo.setText(c.getCodigo());
        txtNombre.setText(c.getNombre());
        txtDescripcion.setText(c.getDescripcion() != null ? c.getDescripcion() : "");
        lblError.setText("");
        mostrarOverlayForm(true);
    }

    @FXML
    private void guardarRegistro() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String desc   = txtDescripcion.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            lblError.setText("Código y Nombre son obligatorios.");
            return;
        }

        try {
            if (registroEditando == null) {
                // CREAR
                viewModel.crear(tipoActual, codigo, nombre, desc.isEmpty() ? null : desc);
            } else {
                // ACTUALIZAR
                registroEditando.setCodigo(codigo);
                registroEditando.setNombre(nombre);
                registroEditando.setDescripcion(desc.isEmpty() ? null : desc);
                viewModel.actualizar(registroEditando);
            }
            cerrarForm();
            cargarTabla();
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
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        lblError.setText("");
    }

    private void mostrarOverlayForm(boolean visible) {
        overlayForm.setVisible(visible);
        overlayForm.setManaged(visible);
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ════════════════════════════════════════════════════════════════

    private void pedirConfirmacion(Catalogo c) {
        registroAEliminar = c;
        lblConfirmarMsg.setText(
                "¿Seguro que desea eliminar «" + c.getNombre() + "»?\n" +
                        "Esta acción no se puede deshacer.");
        overlayConfirmar.setVisible(true);
        overlayConfirmar.setManaged(true);
    }

    @FXML
    private void confirmarEliminar() {
        if (registroAEliminar == null) return;
        try {
            viewModel.eliminar(registroAEliminar.getId());
            cerrarConfirmar();
            cargarTabla();
        } catch (Exception e) {
            cerrarConfirmar();
            mostrarAlerta("No se pudo eliminar",
                    "El registro puede estar en uso por otros módulos.\n" + e.getMessage());
        }
    }

    @FXML
    private void cerrarConfirmar() {
        registroAEliminar = null;
        overlayConfirmar.setVisible(false);
        overlayConfirmar.setManaged(false);
    }

    // ════════════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════════════

    private void mostrarError(String msg) {
        mostrarAlerta("Error", msg);
    }

    private void mostrarAlerta(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}