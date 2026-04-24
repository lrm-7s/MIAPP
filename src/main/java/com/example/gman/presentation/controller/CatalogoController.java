package com.example.gman.presentation.controller;

import com.example.gman.application.service.CatalogoService;
import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.presentation.viewmodel.CatalogoViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class CatalogoController {

    // ── Botones de cada card ──────────────────────────────────────────
    @FXML private Button btnDepartamento;
    @FXML private Button btnCodigoFalla;
    @FXML private Button btnTipoEquipo;
    @FXML private Button btnProveedor;
    @FXML private Button btnEquipo;
    @FXML private Button btnLocalizacion;
    @FXML private Button btnEmpleado;
    @FXML private Button btnMiscelaneo;

    private AppCoordinator   coordinator;
    private CatalogoViewModel viewModel;
    private SessionManager   sessionManager; // ← NUEVO

    // ── Inyección ────────────────────────────────────────────────────
    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator    = coordinator;
        this.sessionManager = coordinator.getSessionManager();
    }

    public void init(CatalogoService service) {
        this.viewModel = new CatalogoViewModel(service);
        aplicarPermisos();
    }

    // ── Permisos sobre los botones de las cards ───────────────────────
    private void aplicarPermisos() {
        if (sessionManager == null) return;

        boolean puedeEditar = sessionManager.puedeEditar("CATALOGO");

        // Solo ADMIN y SUPERVISOR ven el botón "Agregar"
        // CONSULTOR ve las cards pero sin botón de acción
        setBtnVisible(btnDepartamento, puedeEditar);
        setBtnVisible(btnCodigoFalla,  puedeEditar);
        setBtnVisible(btnTipoEquipo,   puedeEditar);
        setBtnVisible(btnProveedor,    puedeEditar);
        setBtnVisible(btnMiscelaneo,   puedeEditar);

        // Empleados y Localizaciones: solo ADMIN
        boolean esAdmin = sessionManager.esAdmin();
        setBtnVisible(btnEmpleado,     esAdmin);
        setBtnVisible(btnLocalizacion, puedeEditar);
        setBtnVisible(btnEquipo,       puedeEditar);
    }

    private void setBtnVisible(Button btn, boolean visible) {
        if (btn == null) return;
        btn.setVisible(visible);
        btn.setManaged(visible);
    }

    // ── Acciones de las cards ─────────────────────────────────────────
    @FXML private void nuevoDepartamento() { abrirCatalogo("DEPARTAMENTO"); }
    @FXML private void nuevoCodigoFalla()  { abrirCatalogo("FALLA"); }
    @FXML private void nuevoTipoEquipo()   { abrirCatalogo("TIPO_EQUIPO"); }
    @FXML private void nuevoProveedor()    { abrirCatalogo("PROVEEDOR"); }
    @FXML private void nuevoMiscelaneo()   { abrirCatalogo("MISCELANEO"); }

    @FXML private void nuevoEquipo() {
        coordinator.openEquipos();
    }

    @FXML private void nuevaLocalizacion() {
        coordinator.openLocalizaciones();
    }

    @FXML private void nuevoEmpleado() {
        // Doble verificación — solo ADMIN llega aquí
        if (!sessionManager.esAdmin()) {
            mostrarError("Solo el administrador puede gestionar empleados.");
            return;
        }
        coordinator.openEmpleadoForm();
    }

    // ── Carga filtrada por tipo ───────────────────────────────────────
    private void abrirCatalogo(String tipo) {
        if (!sessionManager.puedeVer("CATALOGO")) {
            mostrarError("No tienes permiso para ver el catálogo.");
            return;
        }
        try {
            viewModel.cargarPorTipo(tipo);
            // TODO: actualizar TableView cuando la agregues al FXML
        } catch (Exception e) {
            mostrarError("Error al cargar " + tipo + ": " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sin permiso");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}