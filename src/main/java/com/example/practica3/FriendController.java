package com.example.practica3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.util.ArrayList;

public class FriendController {

    @FXML
    private Button nombreBoton;
    @FXML
    private Label conectadoLabel;

    private interfazCliente amigo;

    public void setUser(String amigo, boolean conectado) throws Exception{
        //nombreBoton.setLabel(amigo);
        if(conectado){
            conectadoLabel.setText("৹ Conectado");
            conectadoLabel.setStyle("-fx-text-fill: #03d000;"); //Verde
        }else{
            conectadoLabel.setText("৹ Desconectado");
            conectadoLabel.setStyle("-fx-text-fill: #cf0000;"); //Rojo
        }
    }
}