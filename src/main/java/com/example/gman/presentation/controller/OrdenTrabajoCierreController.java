package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.domain.model.OrdenTrabajo;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class OrdenTrabajoCierreController {

    @FXML private ComboBox<String> cbEstado;
    @FXML private DatePicker  dpFechaRespuesta;
    @FXML private DatePicker  dpInicio;
    @FXML private DatePicker  dpTermino;
    @FXML private DatePicker  dpInicioMP;
    @FXML private DatePicker  dpTerminoMP;
    @FXML private DatePicker  dpFechaEntrega;
    @FXML private TextField   tfCodigoFalla;
    @FXML private TextArea    taDescCausa;
    @FXML private TextArea    taAccionRealizada;
    @FXML private TextArea    taPrevencion;
    @FXML private TextField   tfDuracion;
    @FXML private TextField   tfAceptadaPor;
    @FXML private Label       mensajeLabel;

    private OrdenTrabajoViewModel viewModel;
    private SessionManager        sessionManager;
    private OrdenTrabajo          ot;

    public void setViewModel(OrdenTrabajoViewModel vm)   { this.viewModel = vm; }
    public void setSessionManager(SessionManager sm)     { this.sessionManager = sm; }

    public void cargar(OrdenTrabajo ot) {
        this.ot = ot;
        cbEstado.setItems(viewModel.getEstados());
        cbEstado.setValue("CERRADA");

        // Pre-cargar fechas si ya existen
        dpFechaRespuesta.setValue(parseFecha(ot.getFechaRespuesta()));
        dpInicio.setValue(parseFecha(ot.getFechaInicio()));
        dpTermino.setValue(parseFecha(ot.getFechaTermino()));
        dpFechaEntrega.setValue(parseFecha(ot.getFechaEntrega()));
        tfCodigoFalla.setText(ot.getCodigoFalla());
        taDescCausa.setText(ot.getDescCausa());
        taAccionRealizada.setText(ot.getAccionRealizada());
        taPrevencion.setText(ot.getPrevencion());
        tfDuracion.setText(ot.getDuracionDias() > 0
                ? String.valueOf(ot.getDuracionDias()) : "");
        tfAceptadaPor.setText(ot.getAceptadaPor());
    }

    @FXML
    private void guardarCierre() {
        try {
            ot.setEstado(         cbEstado.getValue());
            ot.setFechaRespuesta( dpFechaRespuesta.getValue() != null
                    ? dpFechaRespuesta.getValue().toString() : "");
            ot.setFechaInicio(    dpInicio.getValue() != null
                    ? dpInicio.getValue().toString() : "");
            ot.setFechaTermino(   dpTermino.getValue() != null
                    ? dpTermino.getValue().toString() : "");
            ot.setFechaEntrega(   dpFechaEntrega.getValue() != null
                    ? dpFechaEntrega.getValue().toString() : "");
            ot.setCodigoFalla(    tfCodigoFalla.getText().trim());
            ot.setDescCausa(      taDescCausa.getText().trim());
            ot.setAccionRealizada(taAccionRealizada.getText().trim());
            ot.setPrevencion(     taPrevencion.getText().trim());
            ot.setDuracionDias(   tfDuracion.getText().isBlank()
                    ? 0 : Integer.parseInt(tfDuracion.getText().trim()));
            ot.setAceptadaPor(    tfAceptadaPor.getText().trim());

            viewModel.cerrar(ot);
            cerrar();
        } catch (NumberFormatException e) {
            mostrarError("La duración debe ser un número entero.");
        } catch (Exception e) {
            mostrarError("Error al cerrar OT: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() { cerrar(); }

    private void cerrar() {
        ((Stage) tfCodigoFalla.getScene().getWindow()).close();
    }

    private LocalDate parseFecha(String fecha) {
        try { return LocalDate.parse(fecha); } catch (Exception e) { return null; }
    }

    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.setVisible(true);
    }
}