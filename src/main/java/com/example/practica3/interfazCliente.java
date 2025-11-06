package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;
import java.net.*;

public interface interfazCliente extends Remote {

	public void notificarLlegada(String nombre)
		throws java.rmi.RemoteException;
		
	public void notificarSalida(String nombre)
		throws java.rmi.RemoteException;

    public void notificarAmistad(String amigo)
            throws java.rmi.RemoteException;
		
	public void actualizarConectados(ArrayList<String> nombres)
		throws java.rmi.RemoteException;

    public String getNombre()
        throws java.rmi.RemoteException;

    public String getIP()
        throws java.rmi.RemoteException;

    public ArrayList<String> listarAmigos(String nome)
            throws java.rmi.RemoteException;

    public void rescribirAmigos(String nome, String amigo, int modo)
            throws java.rmi.RemoteException;
/*
    public void anadirChat(String nombre, ChatController chat)
            throws java.rmi.RemoteException;



    void confirmarConexion(interfazCliente destino)
            throws RemoteException;
*/

    public void recibirIntentoConexion(interfazCliente origen, int puerto) throws Exception;
    public void recibirRechazoConexion() throws Exception;
}
