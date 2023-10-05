module com.example.launcher_aquarium {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.launcher_aquarium to javafx.fxml;
    exports com.example.launcher_aquarium;
}