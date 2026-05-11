package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.*;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenTrabajoFormController {

    // ── Cabecera ──────────────────────────────────────────────────────────
    @FXML private TextField              tfNumeroOT;
    @FXML private TextField              tfFechaSolicitud;
    @FXML private ComboBox<Catalogo>     cbEstado;

    // ── Info general ──────────────────────────────────────────────────────
    @FXML private TextArea               taDescripcion;
    @FXML private ComboBox<Empleado>     cbAsignadoA;
    @FXML private ComboBox<Catalogo>     cbTipoOT;
    @FXML private DatePicker             dpFechaRequerida;
    @FXML private ComboBox<Catalogo>     cbPrioridad;
    @FXML private TextField              tfNumTarea;
    @FXML private TextField              tfCodigoInstruccion;

    // ── Info equipo / localización ────────────────────────────────────────
    @FXML private ComboBox<Localizacion> cbLocalizacion;
    @FXML private TextField              tfDescLocalizacion;
    @FXML private ComboBox<Equipo>       cbEquipo;
    @FXML private TextField              tfDescEquipo;
    @FXML private ComboBox<Catalogo>     cbEstadoEquipo;
    @FXML private DatePicker             dpFechaVencimiento;
    @FXML private TextArea               taNotasTecnico;

    // CORRECCIÓN: cbRecibidoPor es Empleado (recibido_por_id → empleados)
    @FXML private ComboBox<Empleado>     cbRecibidoPor;
    @FXML private TextField              tfOficio;
    @FXML private ListView<Empleado>     listEmpleados;

    // ── Panel info empleado ───────────────────────────────────────────────
    @FXML private ComboBox<Empleado>     cbEmpleadoInfo;
    @FXML private TextField              tfTelefonoOficina;
    @FXML private TextField              tfCelularEmpleado;
    @FXML private TextField              tfCorreoEmpleado;

    // ── Otros ─────────────────────────────────────────────────────────────
    @FXML private Label                  mensajeLabel;
    @FXML private Button                 btnGuardar;

    private static final DateTimeFormatter FMT_UI =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private OrdenTrabajoViewModel viewModel;
    private SessionManager        sessionManager;
    private OrdenTrabajo          otEnEdicion;
    private boolean               modoEdicion = false;
    private String                fechaSolicitudISO;

    public void setViewModel(OrdenTrabajoViewModel vm) { this.viewModel = vm; }
    public void setSessionManager(SessionManager sm)   { this.sessionManager = sm; }

    // ════════════════════════════════════════════════════════════════
    //  CARGAR
    // ════════════════════════════════════════════════════════════════

    public void cargar(OrdenTrabajo ot) {
        poblarCombos();
        configurarListeners();

        boolean soloLectura = sessionManager != null
                && !sessionManager.puedeEditar("ORDEN_TRABAJO");
        setEditable(!soloLectura);

        if (ot != null) {
            modoEdicion       = true;
            otEnEdicion       = ot;
            fechaSolicitudISO = ot.getFechaSolicitud();

            tfNumeroOT.setText(ot.getNumeroOt());
            tfFechaSolicitud.setText(formatearUI(ot.getFechaSolicitud()));
            tfFechaSolicitud.setEditable(false);

            seleccionarPorId(cbEstado,       viewModel.getEstadosOt(),    ot.getEstadoId());
            seleccionarPorId(cbTipoOT,       viewModel.getTiposOt(),      ot.getTipoOtId());
            seleccionarPorId(cbPrioridad,    viewModel.getPrioridades(),  ot.getPrioridadId());
            seleccionarPorId(cbEstadoEquipo, viewModel.getEstadosEquipo(),ot.getEstadoEquipoId());

            cbLocalizacion.getItems().stream()
                    .filter(l -> l.getId() == ot.getLocalizacionId())
                    .findFirst().ifPresent(cbLocalizacion::setValue);

            cbEquipo.getItems().stream()
                    .filter(e -> e.getId() == ot.getEquipoId())
                    .findFirst().ifPresent(cbEquipo::setValue);

            // CORRECCIÓN: recibidoPorId → numero_empleado (no usuarios.id)
            cbRecibidoPor.getItems().stream()
                    .filter(e -> e.getNumeroEmpleado() == ot.getRecibidoPorId())
                    .findFirst().ifPresent(cbRecibidoPor::setValue);

            dpFechaRequerida.setValue(parseFecha(ot.getFechaRequerida()));
            dpFechaVencimiento.setValue(parseFecha(ot.getFechaVencimiento()));
            taDescripcion.setText(ot.getDescripcion());
            taNotasTecnico.setText(ot.getNotasTecnico());
            tfOficio.setText(ot.getOficio());

        } else {
            modoEdicion       = false;
            otEnEdicion       = null;
            LocalDateTime ahora = LocalDateTime.now();
            fechaSolicitudISO = ahora.toString();
            tfFechaSolicitud.setText(ahora.format(FMT_UI));
            tfFechaSolicitud.setEditable(false);
            if (!viewModel.getEstadosOt().isEmpty())
                cbEstado.setValue(viewModel.getEstadosOt().get(0));
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  POBLAR COMBOS
    // ════════════════════════════════════════════════════════════════

    private void poblarCombos() {
        cbEstado.setItems(viewModel.getEstadosOt());
        cbTipoOT.setItems(viewModel.getTiposOt());
        cbPrioridad.setItems(viewModel.getPrioridades());
        cbEstadoEquipo.setItems(viewModel.getEstadosEquipo());
        cbLocalizacion.setItems(viewModel.getLocalizaciones());
        cbEquipo.setItems(viewModel.getEquipos());
        cbAsignadoA.setItems(viewModel.getEmpleados());
        cbRecibidoPor.setItems(viewModel.getEmpleados());
        cbEmpleadoInfo.setItems(viewModel.getEmpleados());
        listEmpleados.setItems(viewModel.getEmpleados());
        listEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        StringConverter<Catalogo> catConv = catalogoConverter();
        cbEstado.setConverter(catConv);
        cbTipoOT.setConverter(catConv);
        cbPrioridad.setConverter(catConv);
        cbEstadoEquipo.setConverter(catConv);
        cbLocalizacion.setConverter(localizacionConverter());

        StringConverter<Empleado> empConv = empleadoConverter();
        cbAsignadoA.setConverter(empConv);
        cbRecibidoPor.setConverter(empConv);
        cbEmpleadoInfo.setConverter(empConv);

        listEmpleados.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Empleado e, boolean empty) {
                super.updateItem(e, empty);
                setText(empty || e == null ? null : e.getNombre());
            }
        });

        cbEquipo.setConverter(new StringConverter<>() {
            @Override public String toString(Equipo e) {
                return e == null ? "" : e.getCodigo() + " — " + e.getNombre();
            }
            @Override public Equipo fromString(String s) { return null; }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  LISTENERS (auto-fill)
    // ════════════════════════════════════════════════════════════════

    private void configurarListeners() {
        cbLocalizacion.valueProperty().addListener((obs, old, loc) ->
                tfDescLocalizacion.setText(loc != null ? loc.getDescripcion() : ""));

        cbEquipo.valueProperty().addListener((obs, old, eq) ->
                tfDescEquipo.setText(eq != null ? eq.getNombre() : ""));

        cbEmpleadoInfo.valueProperty().addListener((obs, old, emp) -> {
            if (emp == null) {
                tfTelefonoOficina.clear();
                tfCelularEmpleado.clear();
                tfCorreoEmpleado.clear();
            } else {
                tfTelefonoOficina.setText("");
                tfCelularEmpleado.setText(emp.getCelular() != null ? emp.getCelular() : "");
                tfCorreoEmpleado.setText(emp.getCorreo()   != null ? emp.getCorreo()   : "");
            }
        });
    }

    // ════════════════════════════════════════════════════════════════
    //  GUARDAR
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void guardar() {
        try {
            OrdenTrabajo ot = modoEdicion ? otEnEdicion : new OrdenTrabajo();

            ot.setNumeroOt(     tfNumeroOT.getText().trim());
            ot.setFechaSolicitud(fechaSolicitudISO);
            ot.setDescripcion(  taDescripcion.getText().trim());
            ot.setNotasTecnico( taNotasTecnico.getText().trim());
            ot.setOficio(       tfOficio.getText().trim());

            Catalogo selEstado = cbEstado.getValue();
            ot.setEstadoId(selEstado != null ? selEstado.getId() : 0);

            Catalogo selTipo = cbTipoOT.getValue();
            ot.setTipoOtId(selTipo != null ? selTipo.getId() : 0);

            Catalogo selPrioridad = cbPrioridad.getValue();
            ot.setPrioridadId(selPrioridad != null ? selPrioridad.getId() : 0);

            Catalogo selEstEq = cbEstadoEquipo.getValue();
            ot.setEstadoEquipoId(selEstEq != null ? selEstEq.getId() : 0);

            Localizacion selLoc = cbLocalizacion.getValue();
            ot.setLocalizacionId(selLoc != null ? selLoc.getId() : 0);

            Equipo selEq = cbEquipo.getValue();
            ot.setEquipoId(selEq != null ? selEq.getId() : 0);

            // CORRECCIÓN: guardar numero_empleado (FK → empleados)
            Empleado selRecibido = cbRecibidoPor.getValue();
            ot.setRecibidoPorId(selRecibido != null ? selRecibido.getNumeroEmpleado() : 0);

            ot.setFechaRequerida(  dpFechaRequerida.getValue()   != null ? dpFechaRequerida.getValue().toString()   : "");
            ot.setFechaVencimiento(dpFechaVencimiento.getValue() != null ? dpFechaVencimiento.getValue().toString() : "");

            List<Integer> empleadosNums = listEmpleados
                    .getSelectionModel().getSelectedItems().stream()
                    .map(Empleado::getNumeroEmpleado)
                    .collect(Collectors.toList());

            // creadoPorId: el repositorio resuelve el id del usuario por su username
            String creadoPorUsername = sessionManager != null && sessionManager.getCurrentUser() != null
                    ? sessionManager.getCurrentUser().getUsername()
                    : null;

            if (modoEdicion) {
                viewModel.actualizar(ot, empleadosNums);
            } else {
                viewModel.crear(ot, empleadosNums, creadoPorUsername);
            }
            cerrar();

        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML private void cancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) tfNumeroOT.getScene().getWindow()).close();
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    private void setEditable(boolean editable) {
        tfNumeroOT.setEditable(editable);
        cbEstado.setDisable(!editable);
        cbTipoOT.setDisable(!editable);
        cbPrioridad.setDisable(!editable);
        cbEstadoEquipo.setDisable(!editable);
        cbLocalizacion.setDisable(!editable);
        cbEquipo.setDisable(!editable);
        cbAsignadoA.setDisable(!editable);
        cbRecibidoPor.setDisable(!editable);
        dpFechaRequerida.setDisable(!editable);
        dpFechaVencimiento.setDisable(!editable);
        taDescripcion.setEditable(editable);
        taNotasTecnico.setEditable(editable);
        tfOficio.setEditable(editable);
        listEmpleados.setDisable(!editable);
        btnGuardar.setVisible(editable);
        btnGuardar.setManaged(editable);
    }

    private void seleccionarPorId(ComboBox<Catalogo> cb,
                                  javafx.collections.ObservableList<Catalogo> items,
                                  int id) {
        items.stream().filter(c -> c.getId() == id).findFirst().ifPresent(cb::setValue);
    }

    private String formatearUI(String iso) {
        if (iso == null || iso.isBlank()) return "";
        try { return LocalDateTime.parse(iso).format(FMT_UI); }
        catch (Exception e) {
            try { return LocalDate.parse(iso).toString() + " 00:00:00"; }
            catch (Exception ex) { return iso; }
        }
    }

    private LocalDate parseFecha(String f) {
        if (f == null || f.isBlank()) return null;
        try { return LocalDate.parse(f); } catch (Exception e) { return null; }
    }

    private StringConverter<Catalogo> catalogoConverter() {
        return new StringConverter<>() {
            @Override public String toString(Catalogo c)   { return c == null ? "" : c.getNombre(); }
            @Override public Catalogo fromString(String s) { return null; }
        };
    }

    private StringConverter<Localizacion> localizacionConverter() {
        return new StringConverter<>() {
            @Override public String toString(Localizacion l) {
                return l == null ? "" : l.getNumeroLocalizacion() + " — " + l.getDescripcion();
            }
            @Override public Localizacion fromString(String s) { return null; }
        };
    }

    private StringConverter<Empleado> empleadoConverter() {
        return new StringConverter<>() {
            @Override public String toString(Empleado e)   { return e == null ? "" : e.getNombre(); }
            @Override public Empleado fromString(String s) { return null; }
        };
    }

    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        if (!mensajeLabel.getStyleClass().contains("mensaje-error"))
            mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
        mensajeLabel.setManaged(true);
    }
}