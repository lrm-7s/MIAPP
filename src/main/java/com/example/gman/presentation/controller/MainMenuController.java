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

    // ─── Botones del sidebar ─────────────────────────────────────────
    @FXML private Button btnEquipos;
    @FXML private Button btnManoObra;
    @FXML private Button btnOrdenTrabajo;
    @FXML private Button btnReportes;
    @FXML private Button btnCatalogo;
    @FXML private Button btnPlanMantenimiento;
    @FXML private Button btnHistorico;
    @FXML private Button btnLocalizaciones;
    @FXML private Button btnInventario;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnEmpleados;

    // ─── Etiquetas de sesión ─────────────────────────────────────────
    @FXML private Label lblUsuario;
    @FXML private Label lblRol;

    private AppCoordinator coordinator;

    public void setCoordinator(AppCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    // ════════════════════════════════════════════════════════════════
    //  PERMISOS
    // ════════════════════════════════════════════════════════════════

    public void aplicarPermisos(SessionManager session) {
        if (lblUsuario != null) lblUsuario.setText(session.getNombreUsuario());
        if (lblRol     != null) lblRol.setText(session.getRolDisplayName());

        setVisible(btnEquipos,           session, "EQUIPOS");
        setVisible(btnManoObra,          session, "MANO_OBRA");
        setVisible(btnEmpleados,         session, "MANO_OBRA");
        setVisible(btnOrdenTrabajo,      session, "ORDEN_TRABAJO");
        setVisible(btnReportes,          session, "REPORTES");
        setVisible(btnCatalogo,          session, "CATALOGO");
        setVisible(btnPlanMantenimiento, session, "PLAN_MANTENIMIENTO");
        setVisible(btnHistorico,         session, "HISTORICO");
        setVisible(btnLocalizaciones,    session, "LOCALIZACIONES");
        setVisible(btnInventario,        session, "INVENTARIO");
        setVisible(btnGestionUsuarios,   session, "GESTION_USUARIOS");
    }

    private void setVisible(Button btn, SessionManager session, String modulo) {
        if (btn == null) return;
        boolean permitido = session.puedeVer(modulo);
        btn.setVisible(permitido);
        btn.setManaged(permitido);
    }

    // ════════════════════════════════════════════════════════════════
    //  CONTENIDO CENTRAL
    // ════════════════════════════════════════════════════════════════

    public void setContent(AnchorPane view) {
        contentPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view,    0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view,   0.0);
        AnchorPane.setRightAnchor(view,  0.0);
    }

    // ════════════════════════════════════════════════════════════════
    //  ACCIONES — todas delegan al coordinator
    // ════════════════════════════════════════════════════════════════

    @FXML private void openEquipos()           { coordinator.openEquipos();           }
    @FXML private void openEmpleados()         { coordinator.openEmpleados();         }
    @FXML private void openManoObra()          { coordinator.openEmpleados();         }
    @FXML private void openOrdenTrabajo()      { coordinator.openOrdenTrabajo();      }
    @FXML private void openReportes()          { coordinator.openReportes();          }
    @FXML private void openCatalogo()          { coordinator.openCatalogo();          }
    @FXML private void openPlanMantenimiento() { coordinator.openPlanMantenimiento(); }
    @FXML private void openHistorico()         { coordinator.openHistorico();         }
    @FXML private void openLocalizaciones()    { coordinator.openLocalizaciones();    }
    @FXML private void openInventario()        { coordinator.openInventario();        }
    @FXML private void openGestionUsuarios()   { coordinator.openGestionUsuarios();   }

    @FXML private void openAbout() {
        System.out.println("GMAN v1.0 - Sistema de Gestión de Mantenimiento");
    }

    @FXML private void logout() {
        coordinator.getSessionManager().clearSession();
        coordinator.startApp();
    }

    @FXML private void exitApp() { System.exit(0); }
}