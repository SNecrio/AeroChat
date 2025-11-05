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
        controller.notiPrincipal.appendText(nombre + " se ha conectado.\n");
        controller.recargaAmigos();
    }

    public void recibirIntentoConexion(interfazCliente origen, int puerto){
        controller.recibirConexion(origen, puerto);
    }

    public void recibirRechazoConexion() throws Exception{
        controller.intentoConexionRechazado();
    }

    public void notificarSalida(String nombre) {
        controller.notiPrincipal.appendText(nombre + " se ha desconectado.\n");
        controller.recargaAmigos();
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

    //Si modo 0 es para meter nuevo amigo, si modo 1 es para eliminar ese amigo de la lista
    public void rescribirAmigos(String nome, String amigo, int modo) throws java.rmi.RemoteException{
        try {
            ArrayList<String> amigos = new ArrayList<>();
            ArrayList<String> arqEntero = new ArrayList<>();
            FileReader r = new FileReader(arquivoAmigos);
            BufferedReader b = new BufferedReader(r);
            String cadea;

            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\:");
                if(partes[0].equals(nome)){
                    String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    if(modo==0 && !amigos.contains(amigo)) amigos.add(amigo);
                    else if(modo==1) amigos.remove(amigo);
                    String linea = nome + ":" + String.join("|", amigos);
                    arqEntero.add(linea);
                } else {
                    arqEntero.add(cadea);
                }
            }

            FileWriter f = new FileWriter(arquivoAmigos, false);
            BufferedWriter w = new BufferedWriter(f);
            for(String a : arqEntero){
                w.write(a);
                w.newLine();
            }

        }catch(Exception e){
            System.out.println("Erro rescribindo amigos: " + e);
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
