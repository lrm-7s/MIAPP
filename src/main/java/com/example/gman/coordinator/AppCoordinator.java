package com.example.gman.coordinator;

import com.example.gman.application.service.*;
import com.example.gman.application.session.SessionManager;
import com.example.gman.config.AppConfig;
import com.example.gman.infrastructure.network.ApiClient;
import com.example.gman.infrastructure.repository.*;
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

    private final Stage          primaryStage;
    private final SessionManager sessionManager;

    // ════════════════════════════════════════════════════════════════
    //  SERVICIOS — instanciados UNA SOLA VEZ
    // ════════════════════════════════════════════════════════════════
    private final AuthService            authService;
    private final EmpleadoService        empleadoService;
    private final EquipoService          equipoService;
    private final CatalogoService        catalogoService;
    private final LocalizacionesService  localizacionesService;
    private final OrdenTrabajoService    ordenTrabajoService;

    private MainMenuController mainMenuController;

    // ════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ════════════════════════════════════════════════════════════════
    public AppCoordinator(Stage stage, SessionManager sessionManager) {
        this.primaryStage   = stage;
        this.sessionManager = sessionManager;

        // ── Repositorios ─────────────────────────────────────────────
        // CORRECCIÓN: Los repositorios que generamos usan DatabaseHelper
        // con métodos estáticos — no reciben DatabaseHelper por constructor.
        // Solo EquipoRepositoryImpl mantiene inyección si así lo tienes.
        UsuarioRepositoryImpl        usuarioRepo  = new UsuarioRepositoryImpl();
        EmpleadoRepositoryImpl       empleadoRepo = new EmpleadoRepositoryImpl();
        EquipoRepositoryImpl         equipoRepo   = new EquipoRepositoryImpl();
        LocalizacionesRepositoryImpl locRepo      = new LocalizacionesRepositoryImpl();
        OrdenTrabajoRepositoryImpl   otRepo       = new OrdenTrabajoRepositoryImpl();
        ApiClient apiClient        = new ApiClient(AppConfig.API_BASE_URL);
        RemoteEquipoRepository remoteEquipoRepo = new RemoteEquipoRepository(apiClient);


        // ── Servicios ────────────────────────────────────────────────
        this.catalogoService       = new CatalogoService();
        this.authService           = new AuthService(usuarioRepo);
        this.empleadoService       = new EmpleadoService(empleadoRepo);
        this.equipoService         = new EquipoService(equipoRepo,remoteEquipoRepo);
        this.localizacionesService = new LocalizacionesService(locRepo);
        this.ordenTrabajoService   = new OrdenTrabajoService(
                otRepo, empleadoService, equipoService, catalogoService);
    }

    // ════════════════════════════════════════════════════════════════
    //  GETTERS
    // ════════════════════════════════════════════════════════════════
    public SessionManager        getSessionManager()        { return sessionManager;        }
    public AuthService           getAuthService()           { return authService;           }
    public EmpleadoService       getEmpleadoService()       { return empleadoService;       }
    public EquipoService         getEquipoService()         { return equipoService;         }
    public CatalogoService       getCatalogoService()       { return catalogoService;       }
    public LocalizacionesService getLocalizacionesService() { return localizacionesService; }
    public OrdenTrabajoService   getOrdenTrabajoService()   { return ordenTrabajoService;   }

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
            FXMLLoader loader = fxml("LoginView.fxml");
            VBox root = loader.load();
            LoginController controller = loader.getController();
            controller.setCoordinator(this);
            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("GMAN - Login");
            primaryStage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void onLoginSuccess() { showMainMenu(); }

    // ════════════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ════════════════════════════════════════════════════════════════
    private void showMainMenu() {
        try {
            FXMLLoader loader = fxml("MainMenu.fxml");
            BorderPane root = loader.load();
            mainMenuController = loader.getController();
            mainMenuController.setCoordinator(this);
            mainMenuController.aplicarPermisos(sessionManager);
            Scene scene = new Scene(root);
            applyCSS(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("GMAN - Menú Principal");
            primaryStage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════════
    //  MÓDULOS
    // ════════════════════════════════════════════════════════════════

    /** Equipos */
    public void openEquipos() {
        if (!sessionManager.puedeVer("EQUIPOS")) return;
        try {
            FXMLLoader loader = fxml("EquiposView.fxml");
            AnchorPane view = loader.load();
            EquiposController controller = loader.getController();
            controller.setCoordinator(this);
            controller.setViewModel(new EquiposViewModel(
                    equipoService, catalogoService, localizacionesService));
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Gestión de Usuarios */
    public void openGestionUsuarios() {
        if (!sessionManager.puedeVer("GESTION_USUARIOS")) return;
        try {
            FXMLLoader loader = fxml("GestionUsuariosView.fxml");
            AnchorPane view = loader.load();
            GestionUsuariosController controller = loader.getController();
            controller.setViewModel(new UsuariosViewModel(authService));
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Registro de nuevo usuario */
    public void openRegistro() {
        if (!sessionManager.puedeVer("GESTION_USUARIOS")) return;
        try {
            FXMLLoader loader = fxml("RegistroView.fxml");
            AnchorPane view = loader.load();
            RegistroController controller = loader.getController();
            controller.setViewModel(new UsuariosViewModel(authService));
            controller.setCoordinator(this);
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Mano de Obra / Empleados
     * CORRECCIÓN: EmpleadosViewModel usa constructor vacío,
     * los servicios se instancian internamente */
    public void openEmpleados() {
        if (!sessionManager.puedeVer("MANO_OBRA")) return;
        try {
            FXMLLoader loader = fxml("EmpleadosView.fxml");
            AnchorPane view = loader.load();
            EmpleadosController controller = loader.getController();
            // setCoordinator inyecta el sessionManager
            controller.setCoordinator(this);
            // setViewModel dispara inicialización de tabla y combos
            controller.setViewModel(new EmpleadosViewModel());
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Catálogos
     * CORRECCIÓN: CatalogoController no tiene setCoordinator ni init(service)
     * — usa @FXML initialize() con su propio ViewModel interno.
     * Solo se pasa el session para aplicar permisos. */
    public void openCatalogo() {
        if (!sessionManager.puedeVer("CATALOGO")) return;
        try {
            FXMLLoader loader = fxml("CatalogosView.fxml");
            AnchorPane view = loader.load();
            CatalogoController controller = loader.getController();
            controller.setSession(sessionManager);
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Localizaciones
     * CORRECCIÓN: LocalizacionesController no tiene setCoordinator,
     * usa setSession() y setViewModel() con constructor vacío */
    public void openLocalizaciones() {
        if (!sessionManager.puedeVer("LOCALIZACIONES")) return;
        try {
            FXMLLoader loader = fxml("LocalizacionesView.fxml");
            AnchorPane view = loader.load();
            LocalizacionesController controller = loader.getController();
            controller.setSession(sessionManager);
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Orden de Trabajo */
    public void openOrdenTrabajo() {
        if (!sessionManager.puedeVer("ORDEN_TRABAJO")) return;
        try {
            FXMLLoader loader = fxml("OrdenTrabajoView.fxml");
            AnchorPane view = loader.load();
            OrdenTrabajoController controller = loader.getController();
            controller.setCoordinator(this);
            controller.setViewModel(new OrdenTrabajoViewModel(
                    ordenTrabajoService, empleadoService,
                    equipoService, catalogoService, localizacionesService));
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Inventario */
    public void openInventario() {
        if (!sessionManager.puedeVer("INVENTARIO")) return;
        try {
            FXMLLoader loader = fxml("InventarioView.fxml");
            AnchorPane view = loader.load();
            // InventarioController aún no implementado
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Plan de Mantenimiento */
    public void openPlanMantenimiento() {
        if (!sessionManager.puedeVer("PLAN_MANTENIMIENTO")) return;
        try {
            FXMLLoader loader = fxml("PlanMantenimientoView.fxml");
            AnchorPane view = loader.load();
            // PlanMantenimientoController aún no implementado
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Histórico */
    public void openHistorico() {
        if (!sessionManager.puedeVer("HISTORICO")) return;
        try {
            FXMLLoader loader = fxml("HistoricoView.fxml");
            AnchorPane view = loader.load();
            // HistoricoController aún no implementado
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** Reportes */
    public void openReportes() {
        if (!sessionManager.puedeVer("REPORTES")) return;
        try {
            FXMLLoader loader = fxml("ReportesView.fxml");
            AnchorPane view = loader.load();
            // ReportesController aún no implementado
            mainMenuController.setContent(view);
        } catch (IOException e) { e.printStackTrace(); }
    }

    // ════════════════════════════════════════════════════════════════
    //  HELPER
    // ════════════════════════════════════════════════════════════════
    private FXMLLoader fxml(String nombre) {
        var url = Thread.currentThread()
                .getContextClassLoader()
                .getResource("com/example/gman/" + nombre); // sin / al inicio
        if (url == null)
            throw new RuntimeException("FXML no encontrado: " + nombre);
        return new FXMLLoader(url);
    }
}