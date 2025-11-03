package com.example.practica3;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    private String destino;
    private interfazServidor servidor;

    private BufferedReader entrada;
    private PrintWriter salida;
    private Socket socket;

    private ChatThread hilo;

    @FXML
    public void initialize() throws Exception{

        messageArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                //enviarMensaje();
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

    public void setUsers(Socket socket, String destino) throws Exception{
        this.destino = destino;
        usuarioLabel.setText(destino);

        try{
            this.socket = socket;
            salida = new PrintWriter(socket.getOutputStream(), true);
            hilo = new ChatThread("Receptor",socket,this);
            hilo.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) panel.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            try {
                salida.println("USR DESCONECTADO");
                System.exit(0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @FXML
    protected void enviarMensaje(){

        String mensajeEnviar = messageArea.getText();
        if(mensajeEnviar.isBlank()){
            return;
        }
        try{
            //Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + cliente.getNombre());
            Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| ");
            Label mensaje = new Label(mensajeEnviar);
            chatbox.getChildren().add(usuario);
            chatbox.getChildren().add(mensaje);

            salida.println(mensajeEnviar);

        } catch (Exception e) {
            System.err.println("Error en el mandado de mensaje");
            throw new RuntimeException(e);
        }

        chatbox.layout();
        scroll.setVvalue(scroll.getVmax());

        messageArea.setText("");
    }

    @FXML
    protected void recibirMensaje(String mensajeRecibido) throws Exception{

        if(mensajeRecibido.equalsIgnoreCase("USR DESCONECTADO")){
            //Label mensaje = new Label(destino.getNombre() + " se ha desconectado");
            Label mensaje = new Label("Usuario desconectado");
            mensaje.setStyle("-fx-text-fill: #cf0000;"); //Rojo
            chatbox.getChildren().add(mensaje);
            return;
        }

        //Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + destino.getNombre());
        Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| ");
        Label mensaje = new Label(mensajeRecibido);

        chatbox.getChildren().add(usuario);
        chatbox.getChildren().add(mensaje);
    }
}