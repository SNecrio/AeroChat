package com.example.practica3;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;


public class FriendController {

    @FXML
    public Button conectadoBoton;
    @FXML
    public Label conectadoLabel;
    private interfazCliente amigo;

    public void setUser(String amigo, boolean conectado) throws Exception{
        conectadoBoton.setText(amigo);
        if(conectado){
            conectadoLabel.setText("৹ Conectado");
            conectadoLabel.setStyle("-fx-text-fill: #03d000;"); //Verde
        }else{
            conectadoLabel.setText("৹ Desconectado");
            conectadoLabel.setStyle("-fx-text-fill: #cf0000;"); //Rojo
        }
    }
}