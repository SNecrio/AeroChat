package com.example.practica3;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalTime;

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
    @FXML
    private Button sendButton;

    private interfazCliente cliente;
    private interfazCliente destino;

    @FXML
    public void initialize(){

        //Inicializamos la ventana y sus fondos
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

        //Asignamos los usuarios y actualizamos la interfaz
        this.cliente = cliente;
        this.destino = destino;
        usuarioLabel.setText(destino.getNombre());

        Stage stage = (Stage) panel.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            try {
                notificarSalida();
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
            //Creamos el mensaje en la interfaz
            Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + cliente.getNombre());
            Label mensaje = new Label(mensajeEnviar);
            chatbox.getChildren().add(usuario);
            chatbox.getChildren().add(mensaje);

            //Se lo mandamos al destino
            cliente.enviarMensaje(destino.getNombre(), mensajeEnviar);
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
        //Al recibir un mensaje, lo creamos en el panel
        Label usuario = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + destino.getNombre());
        Label mensaje = new Label(mensajeRecibido);

        chatbox.getChildren().add(usuario);
        chatbox.getChildren().add(mensaje);
    }

    @FXML
    protected void recibirSalida() throws Exception{
        //Si recibimos que el otro usuario se fue, lo enviamos al panel y desactivamos el texto
        Label aviso = new Label(LocalTime.now().getHour() + ":" + LocalTime.now().getMinute() + "| " + destino.getNombre() + " se ha desconectado");

        aviso.setStyle("-fx-text-fill: #cf0000;"); //Rojo
        chatbox.getChildren().add(aviso);
        messageArea.setDisable(true);
        sendButton.setDisable(true);
    }

    protected void notificarSalida() throws Exception {
        //Notificamos al otro cliente de que nos desconectamos
        destino.recibirSalida(cliente.getNombre());
    }
}