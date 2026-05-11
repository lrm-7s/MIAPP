package com.example.gman;

import com.example.gman.application.session.SessionManager;
import com.example.gman.config.AppConfig;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.infrastructure.database.DatabaseHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {

            // ✅ PRIMERO configuración global
            AppConfig.init();

            // ✅ Luego base de datos
            DatabaseHelper.initDatabase();

            // ✅ Session
            SessionManager sessionManager = new SessionManager();

            // ✅ Coordinador principal
            AppCoordinator coordinator =
                    new AppCoordinator(primaryStage, sessionManager);

            coordinator.startApp();

        } catch (Exception e) {

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Inicio");
            alert.setHeaderText("No se pudo iniciar GMAN");
            alert.setContentText(e.getMessage());

            alert.showAndWait();

            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}