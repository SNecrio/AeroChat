package com.example.practica3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
    private AnchorPane panelConectados;
    @FXML
    private VBox vboxConectados;
    @FXML
    private Button conectarBoton;
    @FXML
    private Button abrirUsuariosBoton;
    @FXML
    private Button amigoBoton;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private Label loginWarning;

    private FXMLLoader connectedPopUp;
    private ArrayList<String> conected;
    private interfazCliente cliente;
    private interfazServidor servidor;
    private int userID;
    private interfazCliente actualUser;

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

        loginPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

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
            loginWarning.setText("Error conectandose a servidor");
            throw new RuntimeException(e);
        }
        //try login
        boolean loginSuccess=false;
        try{
            if(servidor.novoUsuario(username,password)) {
                loginSuccess = true;//!
            }else{
                loginSuccess = servidor.accederUsuario(username, password);
            }
        }catch(Exception e){
            loginWarning.setText(e.getMessage());
        }

        if(loginSuccess){
            try{
                cliente = servidor.registrarCliente(username);
                conected = servidor.obtenerClientesActuales();
                cliente.actualizarConectados(conected);
            } catch (Exception e) {
                System.err.println("No se pudo obtener la lista de usuarios actuales");
                throw new RuntimeException(e);
            }

            abrirUsuariosBoton.setDisable(false);
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
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);

        System.out.println("Introduza a IP do servidor RMI: ");
        String hostName = br.readLine();

        String registryURL = "rmi://" + hostName+ ":1099/aerochat";
        servidor = (interfazServidor)Naming.lookup(registryURL);
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

        abrirUsuariosBoton.setDisable(true);
        panelConectados.setDisable(false);
        panelConectados.setOpacity(1);
        panelConectados.toFront();

        //--------------------------------
        vboxConectados.getChildren().clear();

        int id = 0;
        try {
            for (var usuario : conected) {
                if(usuario != cliente.getNombre()){
                    Button button = new Button(usuario);
                    button.setPrefWidth(vboxConectados.getWidth());
                    button.setOnAction(event -> {
                        onUserClick(usuario);
                    });
                    vboxConectados.getChildren().add(button);
                }
            }
        }catch(Exception e){
            System.err.println("Error: " + e);
        }
    }

    @FXML
    protected void onTouchFondoNegro(){
        panelConectados.setDisable(true);
        panelConectados.setOpacity(0);
        conectarBoton.setDisable(true);
        abrirUsuariosBoton.setDisable(false);

        panel.getChildren().remove(fondoNegro);
        fondoNegro = null;
        warningText.setText("");
    }

    @FXML
    protected void onAbrirChat() throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("AerochatChat.fxml"));
        Scene scene = new Scene(loader.load(), 720, 440);

        Stage chat = new Stage();
        chat.setTitle(actualUser.getNombre() + " | Chat");
        chat.setScene(scene);
        chat.show();
        ChatController controller = loader.getController();

        controller.setUsers(cliente, actualUser);
    }

    @FXML
    protected void onUserClick(String nombre){
        try{
            actualUser = servidor.getCliente(nombre);
            conectarBoton.setDisable(false);
        } catch (Exception e) {
            warningText.setText("Error recuperando usuario");
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void conectar() {

    }

    @FXML
    public void anadirAmigo() {

    }
}