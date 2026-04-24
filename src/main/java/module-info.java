module com.example.gman {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    requires org.xerial.sqlitejdbc;
    requires org.slf4j;


    opens com.example.gman.domain.model to javafx.base;
    opens com.example.gman to javafx.fxml;

    opens com.example.gman.presentation.controller to javafx.fxml;
    opens com.example.gman.presentation.viewmodel to javafx.fxml;

    exports com.example.gman;

}