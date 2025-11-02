package com.example.practica3;

import javafx.application.Platform;

import java.rmi.server.*;
import java.rmi.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

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
        controller.notiPrincipal.appendText(nombre + " se ha conectado.\n");
    }

    public void notificarSalida(String nombre) {
        controller.notiPrincipal.appendText(nombre + " se ha desconectado.\n");
    }

    public void actualizarConectados(ArrayList<String> nombres) {
        System.out.println("\n Clientes conectados actualmente: ");
        for (String n : nombres){
			System.out.println(" · " + n);
		}
    }
/*
    public void recibirMensaje(String emisor, String mensaje) {
        System.out.println(emisor + ": " + mensaje);
    }
	*/
}
