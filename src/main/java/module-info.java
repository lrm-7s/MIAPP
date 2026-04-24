module com.example.gman {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gman to javafx.fxml;
    exports com.example.gman;
}