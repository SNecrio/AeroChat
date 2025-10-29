package com.example.practica3;

import java.rmi.server.*;
import java.rmi.*;
import java.util.ArrayList;

public class implementacionCliente extends UnicastRemoteObject implements interfazCliente {

    private String name;
    private String IP;

    public implementacionCliente(String eName, String eIP) throws RemoteException {
        super();
        this.name = eName;
        this.IP = eIP;
    }

    public String getNombre(){
        return name;
    }

    public String getIP(){
        return IP;
    }

    public void notificarLlegada(String nombre) {
        System.out.println("\n" + nombre + " conectouse.");
    }

    public void notificarSalida(String nombre) {
        System.out.println("\n" + nombre + " desconectouse.");
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
