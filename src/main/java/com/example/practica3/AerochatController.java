package com.example.practica3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

import java.rmi.Naming;
import java.rmi.server.RemoteServer;
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
    private AnchorPane panelContrasena;
    @FXML
    private VBox vboxConectados;
    @FXML
    private VBox vboxAmigos;
    @FXML
    private Button conectarBoton;
    @FXML
    private Button abrirUsuariosBoton;
    @FXML
    private Button contrasenaBoton;
    @FXML
    private Button anadirAmigoBoton;
    @FXML
    private Button recargaAmigos;
    @FXML
    private Button conectarAmigoBoton;
    @FXML
    private Button eliminarAmigoBoton;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField usernameText;
    @FXML
    private TextField passwordText;
    @FXML
    private TextField ipText;
    @FXML
    private TextField oldPasswordText;
    @FXML
    private TextField newPasswordText;
    @FXML
    private TextField friendText;
    @FXML
    public TextArea notiPrincipal;
    @FXML
    private Label loginWarning;
    @FXML
    private Label contrasenaWarning;

    private ArrayList<String> conected;
    private ArrayList<interfazCliente> friendList;

    private interfazCliente cliente;
    private interfazServidor servidor;
    private interfazCliente actualUser;

    private Image backgroundImageLogin;
    private BackgroundImage backgroundLogin;

    private ArrayList<Button> botonesPrincipal;

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

        backgroundImageLogin = new Image(getClass().getResource("loginBackground.jpeg").toExternalForm());
        backgroundLogin = new BackgroundImage(
                backgroundImageLogin,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        loginPane.setBackground(null);
        loginPane.setBackground(new Background(backgroundLogin));
        loginPane.toFront();

        panelContrasena.setBackground(null);
        panelContrasena.setBackground(new Background(backgroundLogin));

        botonesPrincipal = new ArrayList<>();
        botonesPrincipal.add(abrirUsuariosBoton);
        botonesPrincipal.add(contrasenaBoton);
        botonesPrincipal.add(anadirAmigoBoton);
        botonesPrincipal.add(recargaAmigos);
        botonesPrincipal.add(conectarAmigoBoton);
        botonesPrincipal.add(eliminarAmigoBoton);

    }

    @FXML
    private void login(){

        String username = usernameText.getText();
        String password = passwordText.getText();
        String ip = ipText.getText();

        if(username.isBlank()){
            loginWarning.setText("Introduzca un usuario valido");
            return;
        }
        if(username.contains("|")){
            loginWarning.setText("Los usuarios no pueden contener '|'");
            return;
        }

        if(password.isBlank()){
            loginWarning.setText("Introduzca una contraseña valida");
            return;
        }
        if(ip.isBlank()){
            loginWarning.setText("Introduzca una IP valida");
            return;
        }

        try{
            Conectar(username, ip);
        } catch (Exception e) {
            loginWarning.setText("Error conectandose a servidor");
            throw new RuntimeException(e);
        }

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

        //Se conecto al servidor bien
        if(loginSuccess){
            try{
                InetAddress localHost = InetAddress.getLocalHost();
                String IP = localHost.getHostAddress();
                cliente = new implementacionCliente(username,IP,this);

                servidor.registrarCliente(username, cliente);
                conected = servidor.obtenerClientesActuales();
                cliente.actualizarConectados(conected);

                Stage stage = (Stage) panel.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    try {
                        servidor.borrarCliente(cliente.getNombre());
                        System.exit(0);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });

                try{
                    //Obtener lista de amigos
                } catch (Exception e) {
                    warningText.setText("No se pudo obtener la lista de amigos");
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                warningText.setText("No se pudo obtener la lista de usuarios actuales");
                throw new RuntimeException(e);
            }

            //Activar botones y cerrar panel
            friendText.setDisable(false);
            for(var boton : botonesPrincipal)
                boton.setDisable(false);
            loginPane.setDisable(true);
            loginPane.setOpacity(0);
            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;

            ponerAmigos();
        }else{
            loginWarning.setText("Contraseña no coincidente con ese usuario");
        }
    }

    @FXML
    private void changePassword(){

        String oldPassword = oldPasswordText.getText();
        String newPassword = newPasswordText.getText();

        if(oldPassword.isBlank()){
            contrasenaWarning.setText("Introduzca contraseñas validas");
            return;
        }
        if(newPassword.isBlank()){
            contrasenaWarning.setText("Introduzca contraseñas validas");
            return;
        }

        boolean success = false;
        try{
            success = servidor.cambiarContrasinal(cliente.getNombre(), oldPassword, newPassword);
        } catch (Exception e) {
            contrasenaWarning.setText("Error cambiando contraseña");
            throw new RuntimeException(e);
        }

        if(!success){
            contrasenaWarning.setText("Tu contraseña no es la introducida");
        }else{
            warningText.setText("Contraseña cambiada exitosamente");

            friendText.setDisable(false);
            for(var boton : botonesPrincipal)
                boton.setDisable(false);

            panelContrasena.setDisable(true);
            panelContrasena.setOpacity(0);

            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;
        }
    }

    private void Conectar(String username, String hostName) throws Exception {
        String registryURL = "rmi://" + hostName+ ":1099/aerochat";
        servidor = (interfazServidor)Naming.lookup(registryURL);
    }

    @FXML
    protected void onGenteConectada() {
        crearFondoNegro(0);

        friendText.setDisable(true);
        for(var boton : botonesPrincipal)
            boton.setDisable(true);

        panelConectados.setDisable(false);
        panelConectados.setOpacity(1);
        panelConectados.toFront();

        botonesConectados();
    }

    @FXML
    protected void botonesConectados(){
        vboxConectados.getChildren().clear();

        int id = 0;
        try {
            conected = servidor.obtenerClientesActuales();
            for (var usuario : conected) {
                if(usuario.equals(cliente.getNombre())){
                    Button button = new Button(usuario);
                    button.setPrefWidth(panelConectados.getWidth());
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

    private void crearFondoNegro(int panelID){
        fondoNegro = new ImageView();
        fondoNegro.setImage(new Image(getClass().getResource("fondoNegro.jpg").toExternalForm()));
        fondoNegro.setFitWidth(10000);
        fondoNegro.setFitHeight(10000);
        fondoNegro.setOpacity(0.35d);
        fondoNegro.setDisable(false);
        fondoNegro.setOnMouseClicked(event -> {onTouchFondoNegro(panelID);});

        panel.getChildren().add(fondoNegro);
    }
    @FXML
    protected void onTouchFondoNegro(int panelID){

        switch (panelID){
            default:
            case 0:
                panelConectados.setDisable(true);
                panelConectados.setOpacity(0);
                break;
            case 1:
                panelContrasena.setDisable(true);
                panelContrasena.setOpacity(0);
                break;
        }
        conectarBoton.setDisable(true);

        friendText.setDisable(false);
        for(var boton : botonesPrincipal)
            boton.setDisable(false);

        panel.getChildren().remove(fondoNegro);
        fondoNegro = null;
        warningText.setText("");
    }

    @FXML
    protected void onCambiarContrasena() {
        crearFondoNegro(1);

        friendText.setDisable(true);
        for(var boton : botonesPrincipal)
            boton.setDisable(true);

        panelContrasena.setDisable(false);
        panelContrasena.setOpacity(1);
        panelContrasena.toFront();
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

        cliente.anadirChat(actualUser.getNombre(),controller);
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
    private void ponerAmigos(){

        try{
            for(var amigo : friendList){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendUser.fxml"));
                FriendController controlador = loader.getController();
                AnchorPane panelAmigo = loader.load();

                boolean conectado = false;
                if(conected.contains(amigo.getNombre())){
                    conectado = true;
                }
                controlador.setUser(amigo,conectado);
                vboxAmigos.getChildren().add(panelAmigo);
            }
        } catch (Exception e) {
            warningText.setText("Error mostrando amigos");
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void anadirAmigo() {

        String nombre = friendText.getText();

        if(nombre.isBlank() || nombre.contains("|")){
            warningText.setText("Introduzca un nombre valido");
            return;
        }

        friendText.setText("");

        //enviarAmistad
        //warningText.setText("Solicitud enviada");

    }
}