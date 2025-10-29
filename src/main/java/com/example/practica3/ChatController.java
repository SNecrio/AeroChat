package com.example.practica3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.ArrayList;

public class ChatController {

    @FXML
    private AnchorPane panel;
    @FXML
    private Button backButton;
    @FXML
    private VBox chatbox;
    @FXML
    private TextArea messageArea;
    @FXML
    private Label usuarioLabel;

    private ArrayList<interfazCliente> conected;
    private interfazCliente usuario;
    private interfazCliente destino;
    private interfazServidor servidor;

    @FXML
    public void initialize() throws Exception{
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

        Image backgroundImageLogin = new Image(getClass().getResource("loginBackground.jpeg").toExternalForm());
        BackgroundImage backgroundLogin = new BackgroundImage(
                backgroundImageLogin,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        chatbox.setBackground(null);
        chatbox.setBackground(new Background(backgroundLogin));
        chatbox.toFront();
    }

    public void setUsers(interfazCliente usuario) throws Exception{
        this.usuario = usuario;
        usuarioLabel.setText(usuario.getNombre());
    }
}