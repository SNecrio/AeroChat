package com.example.practica3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.*;

import java.net.InetAddress;
import java.rmi.*;
import java.io.*;

import java.rmi.Naming;
import java.util.ArrayList;

public class AerochatController {

    @FXML
    private Label warningText;
    @FXML
    private AnchorPane panel;
    @FXML
    private ImageView fondoNegro;
    @FXML
    private ScrollPane panelConectados;
    @FXML
    private VBox vboxConectados;
    @FXML
    private VBox conPop;
    @FXML
    private Button amigoButton;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private Label loginWarning;

    private FXMLLoader connectedPopUp;
    private ArrayList<interfazCliente> conected;
    private interfazCliente cliente;
    private interfazServidor servidor;
    private int userID;

    @FXML
    public void initialize() throws Exception{
        connectedPopUp = new FXMLLoader(getClass().getResource("FriendUser.fxml"));

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

        fondoNegro = new ImageView();
        fondoNegro.setImage(new Image(getClass().getResource("fondoNegro.jpg").toExternalForm()));
        fondoNegro.setFitWidth(10000);
        fondoNegro.setFitHeight(10000);
        fondoNegro.setOpacity(0.35d);
        fondoNegro.setDisable(false);
        panel.getChildren().add(fondoNegro);

        Image backgroundImageLogin = new Image(getClass().getResource("loginBackground.jpeg").toExternalForm());
        BackgroundImage backgroundLogin = new BackgroundImage(
                backgroundImageLogin,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        loginPane.setBackground(null);
        loginPane.setBackground(new Background(backgroundLogin));
        loginPane.toFront();

    }

    @FXML
    private void login(){

        String username = usernameText.getText();
        String password = passwordText.getText();

        if(username.isBlank()){
            loginWarning.setText("Introduzca un usuario valido");
            return;
        }
        if(password.isBlank()){
            loginWarning.setText("Introduzca una contraseña valida");
            return;
        }

        try{
            Conectar(username);
        } catch (Exception e) {
            System.err.println("Error conectandose a servidor");
            throw new RuntimeException(e);
        }
        //try login
        boolean loginSuccess=false;
        try{
            if(servidor.novoUsuario(username,password)) {
                servidor.registrarCliente(username, cliente);
                loginSuccess = true;//!
            }
        }catch(Exception e){
            System.err.println("Error: " + e);
        }

        if(loginSuccess){
            loginPane.setDisable(true);
            loginPane.setOpacity(0);

            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;
            return;
        }else{
            loginWarning.setText("Contraseña no coincidente con ese usuario");
            return;
        }
    }

    private void Conectar(String username) throws Exception {
        String registryURL = "rmi://localhost:1099/aerochat";
        //ConectedList conectedMetadata = (ConectedList)Naming.lookup(registryURL + "/conected");
        //conected = conectedMetadata.getConected();
        servidor = (interfazServidor)Naming.lookup(registryURL);

        conected = servidor.obtenerClientesActuales();
        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println("Tu nombre de host: " + localHost.getHostName());
        System.out.println("Tu IP local: " + localHost.getHostAddress());

        cliente = new implementacionCliente(username, localHost.getHostAddress());
    }

    @FXML
    protected void onGenteConectada() {

        fondoNegro = new ImageView();
        fondoNegro.setImage(new Image(getClass().getResource("fondoNegro.jpg").toExternalForm()));
        fondoNegro.setFitWidth(10000);
        fondoNegro.setFitHeight(10000);
        fondoNegro.setOpacity(0.35d);
        fondoNegro.setDisable(false);
        fondoNegro.setOnMouseClicked(event -> {onTouchFondoNegro();});

        panel.getChildren().add(fondoNegro);
        panelConectados.setDisable(false);
        panelConectados.setOpacity(1);
        panelConectados.toFront();

        //--------------------------------
        vboxConectados.getChildren().clear();

        int id = 0;
        try {
            for (var usuario : conected) {
                //Button button = new Button((usuario.getName()));
                Button button = new Button(usuario.getNombre());
                button.setPrefWidth(vboxConectados.getWidth());
                button.setOnAction(event -> {
                    onUserClick(id);
                });
                vboxConectados.getChildren().add(button);
            }
        }catch(Exception e){
            System.err.println("Error: " + e);
        }
    }

    @FXML
    protected void onTouchFondoNegro(){
        panelConectados.setDisable(true);
        panelConectados.setOpacity(0);
        conPop.setDisable(true);
        conPop.setOpacity(0);

        panel.getChildren().remove(fondoNegro);
        fondoNegro = null;
        warningText.setText("");
    }

    @FXML
    protected void onUserClick(int id){
        conPop.setDisable(false);
        conPop.setOpacity(1);
        conPop.toFront();

        //User actualUser = conected.get(id);

        /*
        if(actualUser is amigo){
            popUpController.setAmigo(true);
        }*/
        boolean esAmigo = true;//!

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