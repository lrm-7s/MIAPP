package com.example.gman;

import com.example.gman.application.session.SessionManager;
import com.example.gman.coordinator.AppCoordinator;
import com.example.gman.config.AppConfig;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Inicialización global
        AppConfig.init();
        SessionManager sessionManager = new SessionManager();

        // Crear coordinador
        AppCoordinator coordinator = new AppCoordinator(primaryStage, sessionManager);
        coordinator.startApp();
    }

    public static void main(String[] args) {
        launch(args);
    }
}