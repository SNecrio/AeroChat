module com.example.practica3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.rmi;
    requires javafx.graphics;

    opens com.example.practica3 to javafx.fxml;
    exports com.example.practica3;
}