package com.example.practica3;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class ConnectPopUpController {
    @FXML
    private Button amigoButton;

    private String nombre;
    private String IP;
    private boolean esAmigo;

    @FXML
    public void setUsuario(interfazCliente user, boolean esAmigo) {
        this.nombre = user.getNombre();
        this.IP = user.getIP();
        this.esAmigo = esAmigo;

        if(esAmigo){
            amigoButton.setText("Eliminar amigo");
        }else{
            amigoButton.setText("Añadir amigo");
        }
    }

    @FXML
    public void conectar() {

    }

    @FXML
    public void anadirAmigo() {

    }
}