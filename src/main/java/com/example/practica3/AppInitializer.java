package com.example.practica3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppInitializer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //Creamos al ventana de inicio
        FXMLLoader fxmlLoader = new FXMLLoader(AppInitializer.class.getResource("AerochatStart.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 440);
        stage.setTitle("AeroChat");
        stage.setScene(scene);
        stage.show();
    }
}
