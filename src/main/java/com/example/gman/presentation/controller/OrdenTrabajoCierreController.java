package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.Catalogo;
import com.example.gman.domain.model.Empleado;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class OrdenTrabajoCierreController {

    // ── Cabecera ──────────────────────────────────────────────────────────
    @FXML private TextField          tfNumeroOTCierre;
    @FXML private ComboBox<Catalogo> cbEstado;

    // ── Fechas estimado/real ──────────────────────────────────────────────
    @FXML private DatePicker dpRespuestaEst;
    @FXML private DatePicker dpRespuestaReal;
    @FXML private DatePicker dpInicioEst;
    @FXML private DatePicker dpInicioReal;
    @FXML private DatePicker dpTerminoEst;
    @FXML private DatePicker dpTerminoReal;
    @FXML private DatePicker dpFechaEntrega;

    // CORRECCIÓN: dpInicioMP y dpTerminoMP eliminados del guardado.
    // Si existen en el FXML son campos de solo lectura/informativos,
    // no se mapean a ninguna columna en orden_trabajo.
    // Si no los usas, puedes quitarlos del FXML.
    @FXML private DatePicker dpInicioMP;   // informativo — no se persiste
    @FXML private DatePicker dpTerminoMP;  // informativo — no se persiste

    // ── Informe de cierre ─────────────────────────────────────────────────
    @FXML private TextField          tfDuracion;
    @FXML private ComboBox<Catalogo> cbCodigoFalla;
    @FXML private TextField          tfDescFalla;

    // CORRECCIÓN: aceptadaPorId → empleados (no usuarios)
    // Se usa ComboBox<Empleado> en lugar de TextField libre
    @FXML private ComboBox<Empleado> cbAceptadaPor;

    @FXML private TextArea           taDescCausa;
    @FXML private TextArea           taAccionRealizada;
    @FXML private TextArea           taPrevencion;
    @FXML private Label              mensajeLabel;

    private OrdenTrabajoViewModel viewModel;
    private SessionManager        sessionManager;
    private OrdenTrabajo          ot;

    public void setViewModel(OrdenTrabajoViewModel vm) { this.viewModel = vm; }
    public void setSessionManager(SessionManager sm)   { this.sessionManager = sm; }

    // ════════════════════════════════════════════════════════════════
    //  CARGAR
    // ════════════════════════════════════════════════════════════════

    public void cargar(OrdenTrabajo ot) {
        this.ot = ot;

        // Poblar combos
        cbEstado.setItems(viewModel.getEstadosOt());
        cbEstado.setConverter(catalogoConverter());

        cbCodigoFalla.setItems(viewModel.getCodigosFalla());
        cbCodigoFalla.setConverter(catalogoConverter());

        // CORRECCIÓN: cbAceptadaPor usa empleados
        cbAceptadaPor.setItems(viewModel.getEmpleados());
        cbAceptadaPor.setConverter(empleadoConverter());

        // Listener: al seleccionar falla → auto-fill descripción
        cbCodigoFalla.valueProperty().addListener((obs, old, falla) ->
                tfDescFalla.setText(falla != null
                        ? (falla.getDescripcion() != null ? falla.getDescripcion() : "")
                        : ""));

        // Pre-seleccionar estado CERRADA
        viewModel.getEstadosOt().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase("Cerrada"))
                .findFirst().ifPresent(cbEstado::setValue);

        // Datos existentes de la OT
        tfNumeroOTCierre.setText(ot.getNumeroOt());
        tfNumeroOTCierre.setEditable(false);

        dpRespuestaEst.setValue(parseFecha(ot.getRespuestaEst()));
        dpRespuestaReal.setValue(parseFecha(ot.getRespuestaReal()));
        dpInicioEst.setValue(parseFecha(ot.getInicioEst()));
        dpInicioReal.setValue(parseFecha(ot.getInicioReal()));
        dpTerminoEst.setValue(parseFecha(ot.getTerminoEst()));
        dpTerminoReal.setValue(parseFecha(ot.getTerminoReal()));
        dpFechaEntrega.setValue(parseFecha(ot.getFechaEntrega()));

        // Pre-seleccionar código de falla
        if (ot.getCodigoFallaId() > 0) {
            viewModel.getCodigosFalla().stream()
                    .filter(c -> c.getId() == ot.getCodigoFallaId())
                    .findFirst().ifPresent(cbCodigoFalla::setValue);
        }

        // Pre-seleccionar aceptado por (empleado)
        if (ot.getAceptadaPorId() > 0) {
            viewModel.getEmpleados().stream()
                    .filter(e -> e.getNumeroEmpleado() == ot.getAceptadaPorId())
                    .findFirst().ifPresent(cbAceptadaPor::setValue);
        }

        taDescCausa.setText(ot.getDescCausa()       != null ? ot.getDescCausa()       : "");
        taAccionRealizada.setText(ot.getAccionRealizada() != null ? ot.getAccionRealizada() : "");
        taPrevencion.setText(ot.getPrevencion()      != null ? ot.getPrevencion()      : "");
        tfDuracion.setText(ot.getDuracionDias() > 0 ? String.valueOf(ot.getDuracionDias()) : "");
    }

    // ════════════════════════════════════════════════════════════════
    //  GUARDAR CIERRE
    // ════════════════════════════════════════════════════════════════

    @FXML
    private void guardarCierre() {
        try {
            Catalogo selEstado = cbEstado.getValue();
            if (selEstado == null) { mostrarError("Selecciona el estado de cierre."); return; }
            ot.setEstadoId(selEstado.getId());

            // Fechas est/real
            ot.setRespuestaEst( dateStr(dpRespuestaEst));
            ot.setRespuestaReal(dateStr(dpRespuestaReal));
            ot.setInicioEst(    dateStr(dpInicioEst));
            ot.setInicioReal(   dateStr(dpInicioReal));
            ot.setTerminoEst(   dateStr(dpTerminoEst));
            ot.setTerminoReal(  dateStr(dpTerminoReal));
            ot.setFechaEntrega( dateStr(dpFechaEntrega));

            // Sincronizar campos legacy con los reales
            ot.setFechaRespuesta(dateStr(dpRespuestaReal));
            ot.setFechaInicio(   dateStr(dpInicioReal));
            ot.setFechaTermino(  dateStr(dpTerminoReal));

            // Código de falla (FK)
            Catalogo selFalla = cbCodigoFalla.getValue();
            ot.setCodigoFallaId(selFalla != null ? selFalla.getId() : 0);

            // Cierre texto
            ot.setDescCausa(      taDescCausa.getText().trim());
            ot.setAccionRealizada(taAccionRealizada.getText().trim());
            ot.setPrevencion(     taPrevencion.getText().trim());

            // Duración
            ot.setDuracionDias(tfDuracion.getText().isBlank()
                    ? 0 : Integer.parseInt(tfDuracion.getText().trim()));

            // CORRECCIÓN: Usuario no tiene empleadoId — si no se seleccionó empleado
            // se deja aceptadaPorId en 0 (campo opcional en el ER)
            Empleado selAceptado = cbAceptadaPor.getValue();
            if (selAceptado != null) {
                ot.setAceptadaPorId(selAceptado.getNumeroEmpleado());
            }

            viewModel.cerrar(ot);
            cerrar();

        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número entero.");
        } catch (Exception e) {
            mostrarError("Error al cerrar OT: " + e.getMessage());
        }
    }

    @FXML private void cancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) tfNumeroOTCierre.getScene().getWindow()).close();
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════════════════════

    private String dateStr(DatePicker dp) {
        return dp != null && dp.getValue() != null ? dp.getValue().toString() : "";
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

    private StringConverter<Empleado> empleadoConverter() {
        return new StringConverter<>() {
            @Override public String toString(Empleado e)   { return e == null ? "" : e.getNombre(); }
            @Override public Empleado fromString(String s) { return null; }
        };
    }

    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.setVisible(true);
        mensajeLabel.setManaged(true);
    }
}