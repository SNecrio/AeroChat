package com.example.practica3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.util.ArrayList;

public class ChatController {

    @FXML
    private AnchorPane panel;
    @FXML
    private VBox chatbox;
    @FXML
    private ScrollPane scroll;
    @FXML
    private TextArea messageArea;
    @FXML
    private Label usuarioLabel;

    private ArrayList<interfazCliente> conected;
    private interfazCliente cliente;
    private interfazCliente destino;
    private interfazServidor servidor;

    @FXML
    public void initialize() throws Exception{

        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                enviarMensaje();
            }
        });

        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        Image backgroundImage = new Image(getClass().getResource("aeroBackground.jpg").toExternalForm());
        BackgroundSize backgroundSize = new BackgroundSize(
                1000, 1000, true, true, false, true);
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        panel.setBackground(null);
        panel.setBackground(new Background(background));


        Image backgroundImageChat = new Image(getClass().getResource("loginBackground.jpeg").toExternalForm());
        BackgroundSize backgroundSizeChat = new BackgroundSize(
                1000, 1000, true, true, false, true);
        BackgroundImage backgroundChat = new BackgroundImage(
                backgroundImageChat,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSizeChat);
        chatbox.setBackground(null);
        chatbox.setBackground(new Background(backgroundChat));

        chatbox.toFront();
    }

    public void setUsers(interfazCliente cliente, interfazCliente destino) throws Exception{
        this.cliente = cliente;
        this.destino = destino;
        usuarioLabel.setText(destino.getNombre());
    }

    @FXML
    protected void enviarMensaje(){

        if(messageArea.getText().isBlank()){
            return;
        }
        try{
            Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + cliente.getNombre());
            Label mensaje = new Label(messageArea.getText());
            chatbox.getChildren().add(usuario);
            chatbox.getChildren().add(mensaje);
        } catch (RemoteException e) {
            System.err.println("Error en el mandado de mensaje");
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> scroll.setVvalue(1.0));

        messageArea.setText("");
    }

    @FXML
    protected void recibirMensaje() throws Exception{

        Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + destino.getNombre());
        Label mensaje = new Label(messageArea.getText());

        chatbox.getChildren().add(usuario);
        chatbox.getChildren().add(mensaje);
    }

}