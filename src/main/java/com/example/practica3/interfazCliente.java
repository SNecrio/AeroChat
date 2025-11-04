package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;
import java.net.*;

public interface interfazCliente extends Remote {

	public void notificarLlegada(String nombre)
		throws java.rmi.RemoteException;
		
	public void notificarSalida(String nombre)
		throws java.rmi.RemoteException;
		
	public void actualizarConectados(ArrayList<String> nombres)
		throws java.rmi.RemoteException;

    public String getNombre()
        throws java.rmi.RemoteException;

    public String getIP()
        throws java.rmi.RemoteException;

/*
    public void anadirChat(String nombre, ChatController chat)
            throws java.rmi.RemoteException;

    public void recibirConexion(interfazCliente origen)
        throws Exception;

    void confirmarConexion(interfazCliente destino)
            throws RemoteException;
*/
}
