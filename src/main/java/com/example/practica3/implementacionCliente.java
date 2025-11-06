package com.example.practica3;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.rmi.server.*;
import java.rmi.*;
import java.util.*;
import java.net.*;

public class implementacionCliente extends UnicastRemoteObject implements interfazCliente {

    private String name;
    private String IP;
    private AerochatController controller;
    private Dictionary<String, ChatController> chatsAbiertos;

    public implementacionCliente(String eName, String eIP, AerochatController controller) throws RemoteException {
        super();
        this.name = eName;
        this.IP = eIP;
        this.controller = controller;
        chatsAbiertos = new Hashtable<>();
    }

    public String getNombre(){
        return name;
    }

    public String getIP(){
        return IP;
    }

    public void anadirChat(String nombre, ChatController chat){ chatsAbiertos.put(nombre, chat); }

    public void notificarLlegada(String nombre) {
        Platform.runLater(() -> {
            controller.notiPrincipal.appendText(nombre + " se ha conectado.\n");
            controller.recargaAmigos();
        });
    }

    public void recibirIntentoConexion(interfazCliente origen, int puerto){
        controller.recibirConexion(origen, puerto);
    }

    public void recibirRechazoConexion() throws Exception{
        controller.intentoConexionRechazado();
    }

    public void notificarSalida(String nombre) {
        Platform.runLater(() -> {
            controller.notiPrincipal.appendText(nombre + " se ha desconectado.\n");
            controller.recargaAmigos();
        });
    }

    public void notificarAmistad(String amigo) {
        Platform.runLater(() -> {
            controller.recibirSolicitud(amigo);
        });
    }

    public void actualizarConectados(ArrayList<String> nombres) {
        System.out.println("\n Clientes conectados actualmente: ");
        for (String n : nombres){
			System.out.println(" · " + n);
		}
    }


/*
    public void confirmarConexion(interfazCliente destino) throws RemoteException {
        controller.abrirChatConfirmado(destino);
    }

    public void recibirMensaje(String emisor, String mensaje) {
        System.out.println(emisor + ": " + mensaje);
    }
	*/
}
