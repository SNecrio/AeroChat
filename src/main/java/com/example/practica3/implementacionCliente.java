package com.example.practica3;

import javafx.application.Platform;

import java.rmi.server.*;
import java.rmi.*;
import java.util.*;

public class implementacionCliente extends UnicastRemoteObject implements interfazCliente {

    private String name;
    private AerochatController controller;
    private Dictionary<String, interfazCliente> clientesAbiertos;
    private Dictionary<String, ChatController> chatsAbiertos;

    public implementacionCliente(String eName, AerochatController controller) throws RemoteException {
        super();
        this.name = eName;
        this.controller = controller;
        clientesAbiertos = new Hashtable<>();
        chatsAbiertos = new Hashtable<>();
    }

    public void notificarLlegada(String nombre) {
        Platform.runLater(() -> {
            controller.notiPrincipal.appendText(nombre + " se ha conectado.\n");
            controller.recargaAmigos();
        });
    }

    public void notificarSalida(String nombre) {
        Platform.runLater(() -> {
            controller.notiPrincipal.appendText(nombre + " se ha desconectado.\n");
            controller.recargaAmigos();
        });
    }

    public void anadirCliente(String nombre, interfazCliente cliente, ChatController chat){
        clientesAbiertos.put(nombre, cliente);
        chatsAbiertos.put(nombre,chat);
    }

    public void notificarAmistad(String amigo) {
        Platform.runLater(() -> {
            controller.recibirSolicitud(amigo);
        });
    }

    public void recargarAmigos(){
        Platform.runLater(() -> {
            controller.recargaAmigos();
        });
    }

    public void recibirIntentoConexion(interfazCliente origen){
        Platform.runLater(() -> {
            controller.recibirConexion(origen);
        });
    }

    public void recibirRechazoConexion() throws Exception{
        Platform.runLater(() -> {
            controller.intentoConexionRechazado();
        });
    }

    public void recibirAceptacionConexion(interfazCliente destino) throws Exception{
        Platform.runLater(() -> {
            try {
                controller.intentoConexionAceptado(destino);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void recibirSalida(String origen){
        ChatController chat = chatsAbiertos.get(origen);

        Platform.runLater(() -> {
            try{
                chat.recibirSalida();
            } catch (Exception e) {
                controller.setWarningText("Error recibiendo salida");
                throw new RuntimeException(e);
            }
        });
    }

    public void enviarMensaje(String destino, String mensaje){
        interfazCliente clienteDestino = clientesAbiertos.get(destino);

        Platform.runLater(() -> {
            try{
                clienteDestino.recibirMensaje(name,mensaje);
            } catch (Exception e) {
                controller.setWarningText("Error mandando mensaje");
                throw new RuntimeException(e);
            }
        });
    }

    public void recibirMensaje(String origen, String mensaje){
        ChatController chat = chatsAbiertos.get(origen);

        Platform.runLater(() -> {
            try{
                chat.recibirMensaje(mensaje);
            } catch (Exception e) {
                controller.setWarningText("Error recibiendo mensaje");
                throw new RuntimeException(e);
            }
        });
    }

    public String getNombre(){
        return name;
    }
}
