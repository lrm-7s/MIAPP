package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Inventario;
import com.example.gman.presentation.viewmodel.InventarioViewModel;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.util.List;

public class InventarioController {

    // ── Tabla ────────────────────────────────────────────────────────
    @FXML private Button                        btnNuevo;
    @FXML private TextField                     busquedaField;
    @FXML private TableView<Inventario>         tablaInventario;
    @FXML private TableColumn<Inventario, String>  colCodigo;
    @FXML private TableColumn<Inventario, String>  colDescripcion;
    @FXML private TableColumn<Inventario, Integer> colCantidad;
    @FXML private TableColumn<Inventario, Double>  colPrecio;
    @FXML private TableColumn<Inventario, String>  colProveedor;
    @FXML private TableColumn<Inventario, Void>    colAcciones;
    @FXML private Label                         mensajeLabel;

    // ── Overlay formulario ───────────────────────────────────────────
    @FXML private StackPane overlayPane;
    @FXML private Label     dialogoTitulo;
    @FXML private TextField fCodigo;
    @FXML private TextField fDescripcion;
    @FXML private TextField fCantidad;
    @FXML private TextField fPrecio;
    @FXML private Label     lblError;

    // ── Overlay confirmar eliminación ────────────────────────────────
    @FXML private StackPane overlayConfirmar;
    @FXML private Label     lblConfirmarMsg;

    // ── Overlay consumo por OT ───────────────────────────────────────
    @FXML private StackPane                   overlayConsumo;
    @FXML private Label                       lblConsumoTitulo;
    @FXML private TableView<String[]>         tablaConsumo;
    @FXML private TableColumn<String[], String> colOT;
    @FXML private TableColumn<String[], String> colItemConsumo;
    @FXML private TableColumn<String[], String> colCantUsada;

    // ── Estado ───────────────────────────────────────────────────────
    private InventarioViewModel viewModel;
    private AppCoordinator      coordinator;
    private SessionManager      sessionManager;
    private boolean             modoEdicion = false;
    private Inventario          itemEditando;
    private Inventario          itemAEliminar;

    // ════════════════════════════════════════════════════════════════
    //  INYECCIÓN
    // ════════════════════════════════════════════════════════════════

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void setViewModel(InventarioViewModel viewModel) {
        this.viewModel = viewModel;
        inicializarTabla();
        inicializarTablaConsumo();
        cargarDatos();
        aplicarPermisos();
    }

    // ════════════════════════════════════════════════════════════════
    //  PERMISOS
    // ════════════════════════════════════════════════════════════════

    private void aplicarPermisos() {
        if (sessionManager == null) return;
        boolean puede = sessionManager.tienePermiso("INVENTARIO");
        btnNuevo.setVisible(puede);
        btnNuevo.setManaged(puede);
        colAcciones.setCellFactory(crearCeldaAcciones(puede, puede));
    }

    // ════════════════════════════════════════════════════════════════
    //  TABLA PRINCIPAL
    // ════════════════════════════════════════════════════════════════

    private void inicializarTabla() {
        colCodigo.setCellValueFactory(d ->
                d.getValue().codigoProperty());
        colDescripcion.setCellValueFactory(d ->
                d.getValue().descripcionProperty());
        colCantidad.setCellValueFactory(d ->
                d.getValue().cantidadProperty().asObject());
        colPrecio.setCellValueFactory(d ->
                d.getValue().precioUnitarioProperty().asObject());
        colProveedor.setCellValueFactory(d ->
                d.getValue().proveedorNombreProperty());
        colAcciones.setCellFactory(crearCeldaAcciones(true, true));
        tablaInventario.setItems(viewModel.getItems());
        tablaInventario.setPlaceholder(new Label("Sin registros de inventario"));
    }

    // ════════════════════════════════════════════════════════════════
    //  TABLA CONSUMO POR OT
    // ════════════════════════════════════════════════════════════════

    private void inicializarTablaConsumo() {
        colOT.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colItemConsumo.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colCantUsada.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        tablaConsumo.setPlaceholder(new Label("Sin consumo registrado en OTs"));
    }

    // ════════════════════════════════════════════════════════════════
    //  CARGA
    // ════════════════════════════════════════════════════════════════

    private void cargarDatos() {
        try {
            viewModel.cargar();
            ocultarMensaje();
        } catch (Exception e) {
            mostrarError("Error al cargar inventario: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  FILTRO
    // ════════════════════════════════════════════════════════════════

    @FXML private void filtrar() {
        tablaInventario.setItems(viewModel.filtrar(busquedaField.getText()));
    }

    @FXML private void limpiarBusqueda() {
        busquedaField.clear();
        tablaInventario.setItems(viewModel.getItems());
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO
    // ════════════════════════════════════════════════════════════════

    @FXML private void abrirNuevo() {
        modoEdicion  = false;
        itemEditando = null;
        dialogoTitulo.setText("Nuevo Ítem");
        limpiarFormulario();
        overlayPane.setVisible(true);
    }

    private void abrirEdicion(Inventario item) {
        modoEdicion  = true;
        itemEditando = item;
        dialogoTitulo.setText("Editar Ítem");
        poblarFormulario(item);
        overlayPane.setVisible(true);
    }

    @FXML private void guardar() {
        try {
            Inventario item = leerFormulario();
            if (modoEdicion) {
                item.setId(itemEditando.getId());
                viewModel.actualizar(item);
                mostrarExito("Ítem actualizado correctamente.");
            } else {
                viewModel.crear(item);
                mostrarExito("Ítem creado correctamente.");
            }
            cerrarDialogo();
        } catch (IllegalArgumentException ex) {
            lblError.setText(ex.getMessage());
        } catch (Exception ex) {
            lblError.setText("Error: " + ex.getMessage());
        }
    }

    @FXML private void cerrarDialogo() {
        overlayPane.setVisible(false);
        limpiarFormulario();
    }

    // ════════════════════════════════════════════════════════════════
    //  ELIMINACIÓN
    // ════════════════════════════════════════════════════════════════

    private void pedirConfirmacion(Inventario item) {
        itemAEliminar = item;
        lblConfirmarMsg.setText(
                "¿Seguro que desea eliminar «" + item.getDescripcion() + "»?\n"
                        + "Esta acción no se puede deshacer.");
        overlayConfirmar.setVisible(true);
    }

    @FXML private void confirmarEliminar() {
        if (itemAEliminar == null) return;
        try {
            viewModel.eliminar(itemAEliminar.getId());
            cerrarConfirmar();
            mostrarExito("Ítem eliminado correctamente.");
        } catch (Exception ex) {
            cerrarConfirmar();
            mostrarError("Error al eliminar: " + ex.getMessage());
        }
    }

    @FXML private void cerrarConfirmar() {
        itemAEliminar = null;
        overlayConfirmar.setVisible(false);
    }

    // ════════════════════════════════════════════════════════════════
    //  CONSUMO POR OT
    // ════════════════════════════════════════════════════════════════

    private void abrirConsumo(Inventario item) {
        lblConsumoTitulo.setText("Consumo en OTs — " + item.getDescripcion());
        List<String[]> consumo = viewModel.consumoPorOT(item.getId());
        tablaConsumo.getItems().setAll(consumo);
        overlayConsumo.setVisible(true);
    }

    @FXML private void cerrarConsumo() {
        overlayConsumo.setVisible(false);
        tablaConsumo.getItems().clear();
    }

    // ════════════════════════════════════════════════════════════════
    //  CELDA DE ACCIONES
    // ════════════════════════════════════════════════════════════════

    private Callback<TableColumn<Inventario, Void>, TableCell<Inventario, Void>>
    crearCeldaAcciones(boolean puedeEditar, boolean puedeEliminar) {
        return col -> new TableCell<>() {
            private final Button btnEditar   = new Button("✏ Editar");
            private final Button btnEliminar = new Button("🗑 Eliminar");
            private final Button btnConsumo  = new Button("📋 OTs");
            private final HBox   box         = new HBox(6);

            {
                btnEditar.getStyleClass().add("btn-tabla-editar");
                btnEliminar.getStyleClass().add("btn-tabla-eliminar");
                btnConsumo.getStyleClass().add("btn-tabla-editar");

                btnEditar.setOnAction(e ->
                        abrirEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e ->
                        pedirConfirmacion(getTableView().getItems().get(getIndex())));
                btnConsumo.setOnAction(e ->
                        abrirConsumo(getTableView().getItems().get(getIndex())));

                box.getChildren().add(btnConsumo);
                if (puedeEditar)   box.getChildren().add(btnEditar);
                if (puedeEliminar) box.getChildren().add(btnEliminar);
                box.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        };
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS FORMULARIO
    // ════════════════════════════════════════════════════════════════

    private void limpiarFormulario() {
        fCodigo.clear();
        fDescripcion.clear();
        fCantidad.clear();
        fPrecio.clear();
        lblError.setText("");
    }

    private void poblarFormulario(Inventario item) {
        fCodigo.setText(item.getCodigo());
        fDescripcion.setText(item.getDescripcion());
        fCantidad.setText(String.valueOf(item.getCantidad()));
        fPrecio.setText(item.getPrecioUnitario() > 0
                ? String.valueOf(item.getPrecioUnitario()) : "");
        lblError.setText("");
    }

    private Inventario leerFormulario() {
        Inventario item = new Inventario();

        String codigo = fCodigo.getText().trim();
        if (codigo.isEmpty())
            throw new IllegalArgumentException("El código es obligatorio.");

        String desc = fDescripcion.getText().trim();
        if (desc.isEmpty())
            throw new IllegalArgumentException("La descripción es obligatoria.");

        item.setCodigo(codigo);
        item.setDescripcion(desc);
        item.setCantidad(parseInt(fCantidad, "Cantidad"));
        item.setPrecioUnitario(parseDouble(fPrecio, "Precio unitario"));
        return item;
    }

    private int parseInt(TextField field, String nombre) {
        String t = field.getText().trim();
        if (t.isEmpty()) return 0;
        try { return Integer.parseInt(t); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("«" + nombre + "» debe ser un número entero.");
        }
    }

    private double parseDouble(TextField field, String nombre) {
        String t = field.getText().trim();
        if (t.isEmpty()) return 0.0;
        try { return Double.parseDouble(t.replace(",", ".")); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("«" + nombre + "» debe ser un número válido.");
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS UI
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

    private void ocultarMensaje() { mensajeLabel.setVisible(false); }
}