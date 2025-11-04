package com.example.practica3;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.FileReader;
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
    private static String arquivoAmigos = "amigos.txt";

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
        System.out.println("Llegando chat: " + nombre);
        //controller.notiPrincipal.appendText(nombre + " se ha conectado.\n");
    }

    public void recibirIntentoConexion(interfazCliente origen, int puerto){
        controller.recibirConexion(origen, puerto);
    }

    public void notificarSalida(String nombre) {
        System.out.println("Saliendo chat: " + nombre);
        //controller.notiPrincipal.appendText(nombre + " se ha desconectado.\n");
    }

    public void actualizarConectados(ArrayList<String> nombres) {
        System.out.println("\n Clientes conectados actualmente: ");
        for (String n : nombres){
			System.out.println(" · " + n);
		}
    }

    public ArrayList<String> listarAmigos(String nome) throws java.rmi.RemoteException{
        ArrayList<String> amigos = new ArrayList<>();
        try(FileReader f = new FileReader(arquivoAmigos)){
            BufferedReader b = new BufferedReader(f);
            String cadea;

            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\:");

                if(partes[0].equals(nome)){
                   String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    break;
                }
            }
        }catch(Exception e){
            System.out.println("Erro atopando amigos: " + e);
        }
        return amigos;
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
