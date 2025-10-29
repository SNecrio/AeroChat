package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazCliente extends Remote {

	public void notificarLlegada(String nombre)
		throws java.rmi.RemoteException;
		
	public void notificarSalida(String nombre)
		throws java.rmi.RemoteException;
		
	public void actualizarConectados(ArrayList<String> nombres)
		throws java.rmi.RemoteException;

    public String getNombre();

    public String getIP();
}
