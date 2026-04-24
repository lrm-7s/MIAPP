package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.Equipo;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class OrdenTrabajoFormController {

    @FXML private TextField          tfNumeroOT;
    @FXML private TextField          tfFechaSolicitud;  // ← TextField, NO DatePicker
    @FXML private ComboBox<String>   cbEstado;
    @FXML private ComboBox<String>   cbTipoOT;
    @FXML private ComboBox<String>   cbPrioridad;
    @FXML private DatePicker         dpFechaRequerida;
    @FXML private TextArea           taDescripcion;
    @FXML private ComboBox<String>   cbLocalizacion;
    @FXML private ComboBox<Equipo>   cbEquipo;
    @FXML private TextField          tfEstadoEquipo;
    @FXML private ComboBox<Empleado> cbAsignadoA;
    @FXML private ListView<Empleado> listEmpleados;
    @FXML private TextField          tfRecibidoPor;
    @FXML private TextArea           taNotasTecnico;
    @FXML private Label              mensajeLabel;
    @FXML private Button             btnGuardar;

    // ─── formato para mostrar en UI ──────────────────────────────────
    private static final DateTimeFormatter FMT_UI =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private OrdenTrabajoViewModel viewModel;
    private SessionManager        sessionManager;
    private OrdenTrabajo          otEnEdicion;
    private boolean               modoEdicion = false;
    private String                fechaSolicitudISO; // guarda la fecha para la BD

    public void setViewModel(OrdenTrabajoViewModel vm) { this.viewModel = vm; }
    public void setSessionManager(SessionManager sm)   { this.sessionManager = sm; }

    // ════════════════════════════════════════════════════════════════
    //  CARGAR — null = nueva OT,  objeto = edición
    // ════════════════════════════════════════════════════════════════
    public void cargar(OrdenTrabajo ot) {

        // ── Poblar combos ─────────────────────────────────────────────
        cbEstado.setItems(viewModel.getEstados());
        cbTipoOT.setItems(viewModel.getTiposOt());
        cbPrioridad.setItems(viewModel.getPrioridades());
        cbEquipo.setItems(viewModel.getEquipos());
        cbAsignadoA.setItems(viewModel.getEmpleados());
        listEmpleados.setItems(viewModel.getEmpleados());
        listEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // ── Permisos ──────────────────────────────────────────────────
        boolean soloLectura = sessionManager != null
                && !sessionManager.puedeEditar("ORDEN_TRABAJO");
        setFormularioEditable(!soloLectura);

        if (ot != null) {
            // ── MODO EDICIÓN ──────────────────────────────────────────
            modoEdicion      = true;
            otEnEdicion      = ot;
            fechaSolicitudISO = ot.getFechaSolicitud();

            tfNumeroOT.setText(ot.getNumeroOt());
            tfFechaSolicitud.setText(formatearFechaUI(ot.getFechaSolicitud()));
            tfFechaSolicitud.setEditable(false); // fecha no se modifica
            cbEstado.setValue(ot.getEstado());
            cbTipoOT.setValue(ot.getTipoOt());
            cbPrioridad.setValue(ot.getPrioridad());
            dpFechaRequerida.setValue(parseFecha(ot.getFechaRequerida()));
            taDescripcion.setText(ot.getDescripcion());
            cbLocalizacion.setValue(ot.getLocalizacion());
            tfEstadoEquipo.setText(ot.getEstadoEquipo());
            tfRecibidoPor.setText(ot.getRecibidoPor());
            taNotasTecnico.setText(ot.getNotasTecnico());

        } else {
            // ── MODO NUEVA OT — fecha/hora automática ─────────────────
            modoEdicion      = false;
            otEnEdicion      = null;

            LocalDateTime ahora = LocalDateTime.now();
            fechaSolicitudISO   = ahora.toString(); // ISO para la BD
            tfFechaSolicitud.setText(ahora.format(FMT_UI)); // legible para el usuario
            tfFechaSolicitud.setEditable(false); // solo lectura siempre
            tfFechaSolicitud.setStyle("-fx-background-color: #efefef;");

            cbEstado.setValue("ABIERTA");
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  FORMULARIO EDITABLE / SOLO LECTURA
    // ════════════════════════════════════════════════════════════════
    private void setFormularioEditable(boolean editable) {
        tfNumeroOT.setEditable(editable);
        // tfFechaSolicitud siempre solo lectura (se maneja en cargar)
        cbEstado.setDisable(!editable);
        cbTipoOT.setDisable(!editable);
        cbPrioridad.setDisable(!editable);
        dpFechaRequerida.setDisable(!editable);
        taDescripcion.setEditable(editable);
        cbLocalizacion.setDisable(!editable);
        cbEquipo.setDisable(!editable);
        tfEstadoEquipo.setEditable(editable);
        cbAsignadoA.setDisable(!editable);
        listEmpleados.setDisable(!editable);
        tfRecibidoPor.setEditable(editable);
        taNotasTecnico.setEditable(editable);
        btnGuardar.setVisible(editable);
        btnGuardar.setManaged(editable);
    }

    // ════════════════════════════════════════════════════════════════
    //  GUARDAR
    // ════════════════════════════════════════════════════════════════
    @FXML
    private void guardar() {
        try {
            OrdenTrabajo ot = modoEdicion ? otEnEdicion : new OrdenTrabajo();

            ot.setNumeroOt(       tfNumeroOT.getText().trim());
            ot.setFechaSolicitud( fechaSolicitudISO); // ← usa el ISO guardado
            ot.setEstado(         cbEstado.getValue());
            ot.setTipoOt(         cbTipoOT.getValue());
            ot.setPrioridad(      cbPrioridad.getValue());
            ot.setFechaRequerida( dpFechaRequerida.getValue() != null
                    ? dpFechaRequerida.getValue().toString() : "");
            ot.setDescripcion(    taDescripcion.getText().trim());
            ot.setLocalizacion(   cbLocalizacion.getValue());
            if (cbEquipo.getValue() != null)
                ot.setEquipoId(cbEquipo.getValue().getId());
            ot.setEstadoEquipo(   tfEstadoEquipo.getText().trim());
            ot.setRecibidoPor(    tfRecibidoPor.getText().trim());
            ot.setNotasTecnico(   taNotasTecnico.getText().trim());

            List<Integer> empleadosSeleccionados = listEmpleados
                    .getSelectionModel().getSelectedItems().stream()
                    .map(Empleado::getNumeroEmpleado)
                    .collect(Collectors.toList());

            String creadoPor = sessionManager != null
                    ? sessionManager.getUsername() : "sistema";

            if (modoEdicion) {
                viewModel.actualizar(ot, empleadosSeleccionados);
            } else {
                viewModel.crear(ot, empleadosSeleccionados, creadoPor);
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

    /** Convierte ISO (2026-04-22T13:07:29) → "22/04/2026 13:07:29" para mostrar */
    private String formatearFechaUI(String iso) {
        if (iso == null || iso.isBlank()) return "";
        try {
            LocalDateTime ldt = LocalDateTime.parse(iso);
            return ldt.format(FMT_UI);
        } catch (Exception e) {
            // Si solo tiene fecha sin hora (legacy)
            try {
                LocalDate ld = LocalDate.parse(iso);
                return ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " 00:00:00";
            } catch (Exception ex) {
                return iso;
            }
        }
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try { return LocalDate.parse(fecha); } catch (Exception e) { return null; }
    }

    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.getStyleClass().removeAll("mensaje-exito");
        if (!mensajeLabel.getStyleClass().contains("mensaje-error"))
            mensajeLabel.getStyleClass().add("mensaje-error");
        mensajeLabel.setVisible(true);
    }
}