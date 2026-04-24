package com.example.gman.presentation.controller;

import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.domain.model.Empleado;
import com.example.gman.presentation.viewmodel.EmpleadosViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EmpleadoFormController {

    // ─── Campos del formulario ───────────────────────────────────────
    @FXML private Label dialogoTitulo;

    @FXML private TextField fNumero;
    @FXML private TextField fNombre;
    @FXML private TextField fDireccion;
    @FXML private TextField fPosicion;
    @FXML private TextField fCelular;
    @FXML private TextField fDepartamento;
    @FXML private TextField fCorreo;
    @FXML private TextField fSalario;
    @FXML private TextField fExtra1;
    @FXML private TextField fExtra2;
    @FXML private TextField fExtra3;

    @FXML private Label mensajeLabel;

    // ─── Dependencias ────────────────────────────────────────────────
    private EmpleadosViewModel viewModel;
    private Stage stage;
    private AppCoordinator coordinator;
    private boolean modoEdicion = false;
    private Empleado empleadoActual;

    // ─── Inyección ───────────────────────────────────────────────────
    public void setViewModel(EmpleadosViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // ─── Configuración ───────────────────────────────────────────────
    public void setModoEdicion(boolean modo) {
        this.modoEdicion = modo;

        if (modo) {
            dialogoTitulo.setText("Editar Empleado");
            fNumero.setEditable(false);
        } else {
            dialogoTitulo.setText("Nuevo Empleado");
            fNumero.setEditable(false);
            fNumero.setPromptText("(auto)");
        }
    }

    public void setEmpleado(Empleado e) {
        this.empleadoActual = e;

        if (e != null) {
            fNumero.setText(String.valueOf(e.getNumeroEmpleado()));
            fNombre.setText(e.getNombre());
            fDireccion.setText(e.getDireccion());
            fPosicion.setText(e.getPosicion());
            fCelular.setText(e.getCelular());
            fDepartamento.setText(e.getDepartamento());
            fCorreo.setText(e.getCorreo());
            fSalario.setText(String.valueOf(e.getSalarioPorHora()));
            fExtra1.setText(String.valueOf(e.getTiempoExtra1()));
            fExtra2.setText(String.valueOf(e.getTiempoExtra2()));
            fExtra3.setText(String.valueOf(e.getTiempoExtra3()));
        }
    }

    // ─── Acciones ────────────────────────────────────────────────────
    @FXML
    private void guardar() {
        try {
            Empleado e = leerFormulario();

            if (modoEdicion) {
                e.setNumeroEmpleado(Integer.parseInt(fNumero.getText()));
                viewModel.actualizarEmpleado(e);
                mostrarExito("Empleado actualizado.");
            } else {
                viewModel.crearEmpleado(e);
                mostrarExito("Empleado creado.");
            }

            cerrar();

        } catch (Exception ex) {
            ex.printStackTrace(); // 🔥 importante para debug
            mostrarError(ex.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private void cerrar() {
        if (stage != null) stage.close();
    }

    // ─── Helpers ─────────────────────────────────────────────────────
    private Empleado leerFormulario() {
        Empleado e = new Empleado();

        e.setNombre(fNombre.getText().trim());
        e.setDireccion(fDireccion.getText().trim());
        e.setPosicion(fPosicion.getText().trim());
        e.setCelular(fCelular.getText().trim());
        e.setDepartamento(fDepartamento.getText().trim());
        e.setCorreo(fCorreo.getText().trim());

        e.setSalarioPorHora(parseDouble(fSalario));
        e.setTiempoExtra1(parseDouble(fExtra1));
        e.setTiempoExtra2(parseDouble(fExtra2));
        e.setTiempoExtra3(parseDouble(fExtra3));

        return e;
    }

    private double parseDouble(TextField field) {
        String t = field.getText().trim();
        if (t.isEmpty()) return 0;
        return Double.parseDouble(t.replace(",", "."));
    }
    private void mostrarError(String msg) {
        mensajeLabel.setText("⚠ " + msg);
        mensajeLabel.setVisible(true);
    }
    private void mostrarExito(String msg) {
        mensajeLabel.setText("✔ " + msg);
        mensajeLabel.setVisible(true);
    }
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;
    }

}