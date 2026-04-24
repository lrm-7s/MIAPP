package com.example.gman.presentation.controller;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class MainMenuController {

    // ─── Contenedor central ──────────────────────────────────────────
    @FXML private AnchorPane contentPane;

    // ─── Botones del menú lateral ────────────────────────────────────
    @FXML private Button btnEquipos;
    @FXML private Button btnManoObra;
    @FXML private Button btnOrdenTrabajo;
    @FXML private Button btnReportes;
    @FXML private Button btnCatalogo;
    @FXML private Button btnPlanMantenimiento;
    @FXML private Button btnHistorico;
    @FXML private Button btnLocalizaciones;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnEmpleados;          // ← corregido (era btnEpleados)

    // ─── Etiquetas de usuario ────────────────────────────────────────
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    // ─── Dependencias ────────────────────────────────────────────────
    private AppCoordinator coordinator;

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // ════════════════════════════════════════════════════════════════
    //  PERMISOS — oculta botones según rol del usuario en sesión
    // ════════════════════════════════════════════════════════════════

    public void aplicarPermisos(SessionManager session) {

        if (lblUsuario != null) lblUsuario.setText(session.getNombreUsuario());
        if (lblRol     != null) lblRol.setText(session.getRolDisplayName());

        aplicarVisibilidad(btnEquipos,            session, "EQUIPOS");
        aplicarVisibilidad(btnManoObra,           session, "MANO_OBRA");
        aplicarVisibilidad(btnOrdenTrabajo,       session, "ORDEN_TRABAJO");
        aplicarVisibilidad(btnReportes,           session, "REPORTES");
        aplicarVisibilidad(btnCatalogo,           session, "CATALOGO");
        aplicarVisibilidad(btnPlanMantenimiento,  session, "PLAN_MANTENIMIENTO");
        aplicarVisibilidad(btnHistorico,          session, "HISTORICO");
        aplicarVisibilidad(btnLocalizaciones,     session, "LOCALIZACIONES");
        aplicarVisibilidad(btnGestionUsuarios,    session, "GESTION_USUARIOS");
        aplicarVisibilidad(btnEmpleados,          session, "GESTION_EMPLEADOS");
    }

    private void aplicarVisibilidad(Button btn, SessionManager session, String modulo) {
        if (btn == null) return;
        boolean permitido = session.tienePermiso(modulo);
        btn.setVisible(permitido);
        btn.setManaged(permitido);
    }

    // ════════════════════════════════════════════════════════════════
    //  CONTENIDO CENTRAL
    // ════════════════════════════════════════════════════════════════

    public void setContent(AnchorPane view) {
        contentPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }

    public void loadModule(String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane module = loader.load();
            setContent(module);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  ACCIONES DEL MENÚ
    // ════════════════════════════════════════════════════════════════

    @FXML private void openEquipos()           { coordinator.openEquipos(); }
    @FXML private void openGestionUsuarios()   { coordinator.openGestionUsuarios(); }
    @FXML private void openEmpleados()         { coordinator.openEmpleados(); }
    @FXML private void openManoObra()          { loadModule("/com/example/gman/ManoObraView.fxml"); }
    @FXML private void openOrdenTrabajo()      { loadModule("/com/example/gman/OrdenTrabajoCierre.fxml"); }
    @FXML private void openReportes()          { loadModule("/com/example/gman/ReportesView.fxml"); }
    @FXML private void openCatalogo()          { loadModule("/com/example/gman/CatalogosView.fxml"); }
    @FXML private void openPlanMantenimiento() { loadModule("/com/example/gman/PlanMantenimientoView.fxml"); }
    @FXML private void openHistorico()         { loadModule("/com/example/gman/HistoricoView.fxml"); }
    @FXML private void openLocalizaciones()    { loadModule("/com/example/gman/LocalizacionesView.fxml"); }
    @FXML private void openInventario() {loadModule("/com/example/gman/InventarioView.fxml");}

    @FXML private void openAbout() {
        System.out.println("GMAN v1.0 - Sistema de Gestión de Mantenimiento");
    }

    @FXML private void logout() {
        coordinator.getSessionManager().clearSession();
        coordinator.startApp();
    }

    @FXML private void exitApp() { System.exit(0); }
}