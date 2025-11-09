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
import java.rmi.*;

import java.rmi.Naming;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AerochatController {

    @FXML
    private AnchorPane panel;
    @FXML
    private ImageView fondoNegro;
    @FXML
    private AnchorPane panelConectados;
    @FXML
    private AnchorPane panelContrasena;
    @FXML
    private AnchorPane panelConexionOrigen;
    @FXML
    private AnchorPane panelConexionDestino;
    @FXML
    private AnchorPane panelSolicitudAmistad;
    @FXML
    private VBox vboxConectados;
    @FXML
    private VBox vboxAmigos;
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
    private Label warningText;
    @FXML
    private Label loginWarning;
    @FXML
    private Label contrasenaWarning;
    @FXML
    private Label connectingUserOrigen;
    @FXML
    private Label connectingUserDestino;
    @FXML
    private Label conectandoLabel;
    @FXML
    private Label huecoNombreSolicitante;
    @FXML
    private Button rechazarConexionOrigenBoton;
    @FXML
    private Button rechazarConexionDestinoBoton;
    @FXML
    private Button aceptarConexionDestinoBoton;
    @FXML
    private Button btnAceptarAmistad;
    @FXML
    private Button btnRechazarAmistad;

    private ArrayList<String> conected;

    private interfazCliente cliente;
    private interfazServidor servidor;
    private String selectedUser;

    private ArrayList<Button> botonesPrincipal;

    private ChatController chatController;

    private Queue<String> colaSolicitudes = new LinkedList<>();
    private String solicitanteAmistadActual = null;

    private boolean tratandoConexion = false;

    //Función para inicializar el interfaz gráfico
    @FXML
    public void initialize(){

        Image backgroundImageLogin;
        BackgroundImage backgroundLogin;
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

        panelConexionOrigen.setBackground(null);
        panelConexionOrigen.setBackground(new Background(backgroundLogin));

        panelConexionDestino.setBackground(null);
        panelConexionDestino.setBackground(new Background(backgroundLogin));

        panelSolicitudAmistad.setBackground(null);
        panelSolicitudAmistad.setBackground(new Background(backgroundLogin));

        botonesPrincipal = new ArrayList<>();
        botonesPrincipal.add(abrirUsuariosBoton);
        botonesPrincipal.add(contrasenaBoton);
        botonesPrincipal.add(anadirAmigoBoton);
        botonesPrincipal.add(recargaAmigos);
        botonesPrincipal.add(conectarAmigoBoton);
        botonesPrincipal.add(eliminarAmigoBoton);

    }

    //Función para iniciar sesión
    @FXML
    private void login(){
        //Se toman los campos proporcionados por el usuario
        String username = usernameText.getText();
        String password = passwordText.getText();
        String ip = ipText.getText();

        //Diferentes casuísitcas no aceptadas
        if(username.isBlank()){
            loginWarning.setText("Introduzca un usuario valido");
            return;
        }
        if(username.contains("|")){
            loginWarning.setText("Los usuarios no pueden contener '|'");
            return;
        }
        if(username.contains(":")){
            loginWarning.setText("Los usuarios no pueden contener ':'");
            return;
        }
        if(password.isBlank()){
            loginWarning.setText("Introduzca una contraseña valida");
            return;
        }
        if(ip.isBlank()){
            ip = "localhost";
        }

        try{
            //Creamos el servidor RMI
            Conectar(ip);
        } catch (Exception e) {
            loginWarning.setText("Error conectandose a servidor");
            throw new RuntimeException(e);
        }

        boolean loginSuccess=false;
        try{
            //Si el cliente no estaba registrado en el archivo de usuarios, se crea un usuario nuevo
            if(servidor.novoUsuario(username,password)) {
                loginSuccess = true;
            }else{
                loginSuccess = servidor.accederUsuario(username, password);
            }
        }catch(Exception e){
            loginWarning.setText(e.getMessage());
        }

        if(loginSuccess){
            try{
                //Creamos el cliente
                cliente = new implementacionCliente(username,this);

                //Registramos al cliente en el servidor
                servidor.registrarCliente(username, cliente);
                conected = servidor.obtenerClientesActuales();

                //Definimos la acción en caso de cerrarse la ventana del programa
                Stage stage = (Stage) panel.getScene().getWindow();
                stage.setOnCloseRequest(event -> {
                    try {
                        //El cliente es borrado del registro de clientes conectados del servidor
                        ArrayList<String> amigos = servidor.listarAmigos(cliente.getNombre());
                        servidor.borrarCliente(cliente.getNombre(), amigos);
                        Thread.sleep(500);
                        System.exit(0);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception e) {
                warningText.setText("Error accediendo al usuario");
                System.out.println("Error: " + e);
            }

            //Activar botones y cerrar panel
            friendText.setDisable(false);
            for(var boton : botonesPrincipal)
                boton.setDisable(false);
            loginPane.setDisable(true);
            loginPane.setOpacity(0);
            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;
            try {
                //Se muestran los amigos del cliente por pantalla y sus solicitudes de amistad si las tiene
                ponerAmigos(servidor.listarAmigos(username));
                ArrayList<String> xente = servidor.tieneSolicitudes(cliente.getNombre());
                if(!xente.isEmpty()){
                    for(String s : xente){
                        recibirSolicitud(s);
                    }
                }

            }catch (Exception e){
                System.out.println("Erro: " + e);
            }
        }else{
            loginWarning.setText("Contraseña no coincidente con ese usuario");
        }
    }

    //Función para cambiar la contraseña de un usuario
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

        boolean success;
        try{
            //El servidor es el encargado de cambiar la contraseña
            success = servidor.cambiarContrasinal(cliente.getNombre(), oldPassword, newPassword);
        } catch (Exception e) {
            contrasenaWarning.setText("Error cambiando contraseña");
            throw new RuntimeException(e);
        }

        if(!success){
            contrasenaWarning.setText("Tu contraseña no es la introducida");
        }else{
            notiPrincipal.appendText("Contraseña cambiada exitosamente\n");

            friendText.setDisable(false);
            for(var boton : botonesPrincipal)
                boton.setDisable(false);
            panelContrasena.setDisable(true);
            panelContrasena.setOpacity(0);
            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;
        }
    }

    //Función que crea un interfazServidor RMI
    private void Conectar(String hostName) throws Exception {
        String registryURL = "rmi://" + hostName+ ":1099/aerochat";
        servidor = (interfazServidor)Naming.lookup(registryURL);
    }

    //Función que muestra el panel con la gente conectada
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

    //Función que muestra los nombres de los usuarios conectados
    @FXML
    protected void botonesConectados(){
        vboxConectados.getChildren().clear();
        try {
            conected = servidor.obtenerClientesActuales();
            for (var usuario : conected) {
                if(!usuario.equals(cliente.getNombre())){
                    Button button = new Button(usuario);
                    button.setPrefWidth(panelConectados.getWidth());
                    vboxConectados.getChildren().add(button);
                }
            }
        }catch(Exception e){
            System.err.println("Error: " + e);
        }
    }

    //Función que muestra un fondo negro
    private void crearFondoNegro(int panelID){

        if(fondoNegro != null){
            panel.getChildren().remove(fondoNegro);
            fondoNegro = null;
        }

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

            case 0:
                panelConectados.setDisable(true);
                panelConectados.setOpacity(0);
                break;
            case 1:
                panelContrasena.setDisable(true);
                panelContrasena.setOpacity(0);
                break;
            case 2:
                panelConexionOrigen.setDisable(true);
                panelConexionOrigen.setOpacity(0);
                break;
            case 3:
                rechazarConexionDestinoBoton.setDisable(false);
                aceptarConexionDestinoBoton.setDisable(false);
                panelConexionDestino.setDisable(true);
                panelConexionDestino.setOpacity(0);
                break;
            case 4:
                panelSolicitudAmistad.setDisable(true);
                panelSolicitudAmistad.setOpacity(0);
                break;
            default:
                return;
        }
        conectarAmigoBoton.setDisable(true);

        friendText.setDisable(false);
        for(var boton : botonesPrincipal)
            boton.setDisable(false);

        panel.getChildren().remove(fondoNegro);
        fondoNegro = null;
        warningText.setText("");
    }

    //Función que muestra la pantalla de cambio de contraseña
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

    //Función que reconoce el usuario seleccionado por el cliente actual
    @FXML
    protected void onUserClick(String nombre){
        try{
            selectedUser = nombre;
            conectarAmigoBoton.setDisable(false);
        } catch (Exception e) {
            warningText.setText("Error recuperando usuario");
            throw new RuntimeException(e);
        }
    }

    //Función para comenzar la conexión entre dos clientes
    @FXML
    protected void intentarConexion(){

        try{
            panelConectados.setDisable(true);
            panelConectados.setOpacity(0.0);
            crearFondoNegro(-1);

            panelConexionOrigen.setDisable(false);
            panelConexionOrigen.setOpacity(1.0);
            connectingUserOrigen.setText(selectedUser);
            panelConexionOrigen.toFront();

            tratandoConexion = true;
            servidor.intentarConexion(cliente, selectedUser);
            notiPrincipal.appendText("Intentando conectar con " + selectedUser + "\n");

        } catch (Exception e) {
            warningText.setText("Error conectando usuario");
            throw new RuntimeException(e);
        }
    }

    //Función que muestra la ventana de intento de conexión
    @FXML
    protected void recibirConexion(interfazCliente origen){

        try{
            panelConectados.setDisable(true);
            panelConectados.setOpacity(0.0);
            panelContrasena.setDisable(true);
            panelContrasena.setOpacity(0.0);

            crearFondoNegro(-1);

            panelConexionDestino.setDisable(false);
            panelConexionDestino.setOpacity(1.0);
            panelConexionDestino.toFront();

            conectandoLabel.setText("Este usuario esta intentando conectar contigo:");
            connectingUserDestino.setText(origen.getNombre());

            rechazarConexionDestinoBoton.setOnAction(event -> {rechazarConexionDestino(origen);});
            aceptarConexionDestinoBoton.setOnAction(event -> { aceptarConexionDestino(origen);});

        } catch (Exception e) {
            warningText.setText("Un usuario ha intentado conectarse pero fallo");
            throw new RuntimeException(e);
        }
    }

    //Función para que un cliente pueda aceptar la conexión con otro
    public void aceptarConexionDestino(interfazCliente origen){

        onTouchFondoNegro(3);
        try{
            servidor.aceptarConexion(origen, cliente);
            abrirChat(origen);
        } catch (Exception e) {
            warningText.setText("Fallo al conectarse");
            throw new RuntimeException(e);
        }
    }

    public void intentoConexionAceptado(interfazCliente destino) throws Exception {
        if(!tratandoConexion){
            throw new Exception();
        }
        onTouchFondoNegro(2);
        abrirChat(destino);
    }

    public void abrirChat(interfazCliente destino) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AerochatChat.fxml"));
        Scene scene = new Scene(loader.load(), 720, 440);

        Stage chat = new Stage();
        chat.setTitle(destino.getNombre() + " | Chat");
        chat.setScene(scene);
        chat.show();
        chatController = loader.getController();
        chatController.setUsers(cliente,destino);

        cliente.anadirCliente(destino.getNombre(), destino, chatController);
    }

    //Cancelar conexion
    public void cancelarConexionOrigen(){
        tratandoConexion = false;
        onTouchFondoNegro(2);
    }
    public void rechazarConexionDestino(interfazCliente origen){

        try{
            servidor.rechazarConexion(origen);
        } catch (Exception e) {
            warningText.setText("No se pudo mandar el rechazo");
            throw new RuntimeException(e);
        }

        onTouchFondoNegro(3);
    }
    public void intentoConexionRechazado(){

        tratandoConexion = false;
        rechazarConexionOrigenBoton.setDisable(true);
        onTouchFondoNegro(2);
        notiPrincipal.appendText("Conexion rechazada por el destino\n");
    }

    //Función que muestra los amigos de un usuario en el panel de amigos
    private void ponerAmigos(ArrayList<String> amigos){

        try{
            vboxAmigos.getChildren().clear();
            for(String amigo : amigos){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendUser.fxml"));
                AnchorPane panelAmigo = loader.load();
                FriendController controlador = loader.getController();

                controlador.conectadoBoton.setOnAction(event -> {
                    onUserClick(amigo);
                });

                boolean conectado = false;
                conected=servidor.obtenerClientesActuales();
                if(conected.contains(amigo)){
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

    //Función para solicitar amistad
    @FXML
    public void anadirAmigo() {

        String nombre = friendText.getText();

        if(nombre.isBlank() || nombre.contains("|") || nombre.contains(":")){
            warningText.setText("Introduzca un nombre valido");
            return;
        }

        friendText.setText("");

        try {
            if (servidor.listarAmigos(cliente.getNombre()).contains(nombre)) {
                notiPrincipal.appendText("El usuario " + nombre + " ya se encuentra en tu lista de amigos\n");
            } else if (!servidor.obtenerUsuariosExistentes().contains(nombre)) {
                notiPrincipal.appendText("El usuario " + nombre + " no existe\n");
            }else if(cliente.getNombre().equals(nombre)){
                notiPrincipal.appendText("¿Estás intentando ser tu propio amigo?\n");
            }else{
                if(servidor.enviarAmistad(cliente.getNombre(), nombre)){
                    notiPrincipal.appendText("Solicitud enviada con éxito a " + nombre + "\n");
                    //Se le avisa al cliente notificado de que alguien le ha solicitado amistad (si está conectado)
                    conected=servidor.obtenerClientesActuales();
                    if(conected.contains(nombre)) servidor.avisarDeSolicitud(nombre, cliente.getNombre());
                }
                else notiPrincipal.appendText("Ya le has enviado una solicitud de amistad al usuario " + nombre + "\n");
            }
        } catch (RemoteException e) {
            System.err.println("Error enviando amistad");
        }
    }

    //Función para avisar a un cliente que le han enviado una solicitud de amistad
    @FXML
    public void recibirSolicitud(String posibleAmigo) {
            //Se añade el nombre del solicitante a una cola (por si se tienen varias solicitudes)
            colaSolicitudes.add(posibleAmigo);
            if (solicitanteAmistadActual == null) {
            mostrarSiguienteSolicitud();
            }
    }

    //Función para mostrar al siguiente solicitante en la cola (si lo hay)
    private void mostrarSiguienteSolicitud() {
        solicitanteAmistadActual = colaSolicitudes.poll();

        if (solicitanteAmistadActual == null) {
            panelSolicitudAmistad.setDisable(true);
            panelSolicitudAmistad.setOpacity(0.0);
            panelSolicitudAmistad.toBack();
            return;
        }

        panelSolicitudAmistad.setDisable(false);
        panelSolicitudAmistad.setOpacity(1.0);
        crearFondoNegro(-1);
        panelSolicitudAmistad.toFront();

        huecoNombreSolicitante.setText(solicitanteAmistadActual);
        btnAceptarAmistad.setDisable(false);
        btnRechazarAmistad.setDisable(false);
    }

    //Función para añadir un usuario como amigo
    @FXML
    public void aceptarAmigo() {

        onTouchFondoNegro(4);
        try {
            //Se llama a la función de rescritura con el modo de añadir amigos
            servidor.rescribirAmigos(cliente.getNombre(), solicitanteAmistadActual, 0);
            //Se recarga el panel de amigos
            recargaAmigos();
            notiPrincipal.appendText("Solicitud de amistad aceptada\n");
            try {
                //Se borra la solicitud ya tramitada del archivo
                servidor.borrarSolicitud(cliente.getNombre(), solicitanteAmistadActual);
            }catch (RemoteException e){
                System.out.println("Error: " + e);
            }
            solicitanteAmistadActual = null;
            //Se muestra la siguiente solicitud (si la hay)
            mostrarSiguienteSolicitud();
        } catch (RemoteException e) {
            notiPrincipal.appendText("Hubo un error aceptando la amistad\n");
            System.out.println("Error: " + e);
        }
    }

    //Función para rechazar una petición de amistad
    @FXML
    public void rechazarAmigo() {
        notiPrincipal.appendText("Solicitud de amistad rechazada\n");
        try {
            //Se borra la solicitud tramitada del archivo
            servidor.borrarSolicitud(cliente.getNombre(), solicitanteAmistadActual);
        }catch (RemoteException e){
            System.out.println("Error: " + e);
        }
        onTouchFondoNegro(4);
        solicitanteAmistadActual = null;
        mostrarSiguienteSolicitud();
    }

    //Función para recargar el panel de amigos del interfaz gráfico
    @FXML
    public void recargaAmigos() {
        try {
            ponerAmigos(servidor.listarAmigos(cliente.getNombre()));
        } catch (RemoteException e) {
            System.out.println("Error: " + e);
        }
    }

    //Función para eliminar un amigo
    @FXML
    public void borrarAmigo() {
        try {
            String amigo = selectedUser;
            if(!servidor.listarAmigos(cliente.getNombre()).contains(amigo)){
                notiPrincipal.appendText("El usuario " + amigo + " no se encuentra en tu lista de amigos.\n");
            } else {
                servidor.rescribirAmigos(cliente.getNombre(), amigo, 1);
                ponerAmigos(servidor.listarAmigos(cliente.getNombre()));
                notiPrincipal.appendText("El usuario " + amigo + " ya no se encuentra en tu lista de amigos\n");
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e);
        }
    }

    public void setWarningText(String warning) {
        this.warningText.setText(warning);
    }
}