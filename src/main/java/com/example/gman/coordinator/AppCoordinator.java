package com.example.gman.coordinator;

import com.example.gman.application.service.*;
import com.example.gman.application.session.SessionManager;
import com.example.gman.infrastructure.database.DatabaseHelper;
import com.example.gman.infrastructure.repository.EmpleadoRepositoryImpl;
import com.example.gman.infrastructure.repository.EquipoRepositoryImpl;
import com.example.gman.infrastructure.repository.OrdenTrabajoRepositoryImpl;
import com.example.gman.infrastructure.repository.UsuarioRepositoryImpl;
import com.example.gman.presentation.controller.*;
import com.example.gman.presentation.viewmodel.EmpleadosViewModel;
import com.example.gman.presentation.viewmodel.EquiposViewModel;
import com.example.gman.presentation.viewmodel.OrdenTrabajoViewModel;
import com.example.gman.presentation.viewmodel.UsuariosViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AppCoordinator {

    private final Stage primaryStage;
    private final SessionManager sessionManager;
    private final OrdenTrabajoService ordenTrabajoService;

    // ═══════════════════════════════════════════════════════════════
    //  SERVICIOS COMPARTIDOS — instanciados UNA SOLA VEZ
    // ═══════════════════════════════════════════════════════════════
    private final AuthService     authService;
    private final EmpleadoService empleadoService;
    private final EquipoService   equipoService;
    private final CatalogoService catalogoService; // ← ACTIVADO

    // Futuros:
    // private final OrdenTrabajoService ordenTrabajoService;
    // private final InventarioService   inventarioService;

    private MainMenuController mainMenuController;

    // ════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════
    public AppCoordinator(Stage stage, SessionManager sessionManager) {
        this.primaryStage  = stage;
        this.sessionManager = sessionManager;

        // ── Repositorios ─────────────────────────────────────────────
        UsuarioRepositoryImpl  usuarioRepo  = new UsuarioRepositoryImpl();
        EmpleadoRepositoryImpl empleadoRepo = new EmpleadoRepositoryImpl();
        DatabaseHelper         db           = new DatabaseHelper();
        EquipoRepositoryImpl   equipoRepo   = new EquipoRepositoryImpl(db);

        // ── Servicios ────────────────────────────────────────────────
        this.authService     = new AuthService(usuarioRepo);
        this.empleadoService = new EmpleadoService(empleadoRepo);
        this.equipoService   = new EquipoService(equipoRepo, null);
        this.catalogoService = new CatalogoService(); // ← sin repositorio externo, usa BD directa
        OrdenTrabajoRepositoryImpl otRepo = new OrdenTrabajoRepositoryImpl();
        this.ordenTrabajoService = new OrdenTrabajoService(
                otRepo, empleadoService, equipoService, catalogoService);
        // Futuros:
        // this.ordenTrabajoService = new OrdenTrabajoService(
        //         otRepo, empleadoService, equipoService, catalogoService);
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS
    // ════════════════════════════════════════════════════════════════
    public SessionManager getSessionManager()     { return sessionManager;    }
    public AuthService    getAuthService()        { return authService;       }
    public EmpleadoService getEmpleadoService()   { return empleadoService;   }
    public EquipoService  getEquipoService()      { return equipoService;     }
    public CatalogoService getCatalogoService()   { return catalogoService;   } // ← ACTIVADO
    public OrdenTrabajoService getOrdenTrabajoService() { return ordenTrabajoService; }

    // ════════════════════════════════════════════════════════════════
    //  INICIO
    // ════════════════════════════════════════════════════════════════
    public void startApp() {
        if (sessionManager.isLoggedIn()) showMainMenu();
        else showLogin();
    }

    private void applyCSS(Scene scene) {
        try {
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error cargando CSS: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  LOGIN
    // ════════════════════════════════════════════════════════════════
    private void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/LoginView.fxml"));
            VBox root = loader.load();

            LoginController controller = loader.getController();
            controller.setCoordinator(this);

            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("GMAN - Login");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLoginSuccess() { showMainMenu(); }

    // ════════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ════════════════════════════════════════════════════════════════
    private void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/MainMenu.fxml"));
            BorderPane root = loader.load();

            mainMenuController = loader.getController();
            mainMenuController.setCoordinator(this);
            mainMenuController.aplicarPermisos(sessionManager);

            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("GMAN - Menú Principal");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  MÓDULOS IMPLEMENTADOS
    // ════════════════════════════════════════════════════════════════

    /** Equipos */
    public void openEquipos() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/EquiposView.fxml"));
            AnchorPane view = loader.load();

            EquiposController controller = loader.getController();
            controller.setViewModel(new EquiposViewModel(equipoService));
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Gestión de Usuarios */
    public void openGestionUsuarios() {
        if (!sessionManager.tienePermiso("GESTION_USUARIOS")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/GestionUsuariosView.fxml"));
            AnchorPane view = loader.load();

            GestionUsuariosController controller = loader.getController();
            controller.setViewModel(new UsuariosViewModel(authService));
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Registro de nuevo usuario */
    public void openRegistro() {
        if (!sessionManager.tienePermiso("GESTION_USUARIOS")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/RegistroView.fxml"));
            AnchorPane view = loader.load();

            RegistroController controller = loader.getController();
            controller.setViewModel(new UsuariosViewModel(authService));
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Empleados */
    public void openEmpleados() {
        if (!sessionManager.tienePermiso("GESTION_EMPLEADOS")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/EmpleadosView.fxml"));
            AnchorPane view = loader.load();

            EmpleadosController controller = loader.getController();
            controller.setViewModel(new EmpleadosViewModel(empleadoService));
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Formulario de empleado */
    public void openEmpleadoForm() {
        if (!sessionManager.tienePermiso("GESTION_EMPLEADOS")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/EmpleadoFormview.fxml"));
            AnchorPane view = loader.load();

            EmpleadoFormController controller = loader.getController();
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Catálogo — HUB de navegación */
    public void openCatalogo() {
        if (!sessionManager.tienePermiso("CATALOGO")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/CatalogosView.fxml"));
            AnchorPane view = loader.load();

            CatalogoController controller = loader.getController();
            controller.setCoordinator(this);
            controller.init(catalogoService); // ← inyección correcta
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Localizaciones — por implementar */
    public void openLocalizaciones() {
        // TODO: cuando implementes LocalizacionesController
//         if (!sessionManager.tienePermiso("LOCALIZACIONES")) return;
//         FXMLLoader loader =LocalizacionesView.fxml
//         LocalizacionesController controller = loader.getController();
//         controller.setCoordinator(this);
//         mainMenuController.setContent(view);
        System.out.println("Localizaciones: módulo pendiente de implementar.");
    }
    public void openOrdenTrabajo() {
        if (!sessionManager.tienePermiso("ORDEN_TRABAJO")) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/gman/OrdenTrabajoView.fxml"));
            AnchorPane view = loader.load();

            OrdenTrabajoController controller = loader.getController();
            controller.setCoordinator(this);
            controller.setViewModel(new OrdenTrabajoViewModel(
                    ordenTrabajoService, empleadoService,
                    equipoService, catalogoService));
            mainMenuController.setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // ════════════════════════════════════════════════════════════════
    //  PLANTILLA — copia esto para cada módulo nuevo
    // ════════════════════════════════════════════════════════════════
    //
    // public void openOrdenTrabajo() {
    //     if (!sessionManager.tienePermiso("ORDEN_TRABAJO")) return;
    //     try {
    //         FXMLLoader loader = new FXMLLoader(
    //                 getClass().getResource("/com/example/gman/OrdenTrabajoView.fxml"));
    //         AnchorPane view = loader.load();
    //         OrdenTrabajoController controller = loader.getController();
    //         OrdenTrabajoViewModel vm = new OrdenTrabajoViewModel(
    //                 ordenTrabajoService, empleadoService, equipoService);
    //         controller.setViewModel(vm);
    //         controller.setCoordinator(this);
    //         mainMenuController.setContent(view);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }
    // }
}