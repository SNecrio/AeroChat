package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazServidor extends Remote {

	public void registrarCliente(String nombre, interfazCliente interfaz)
		throws java.rmi.RemoteException;
		
	public void borrarCliente(String nombre)
		throws java.rmi.RemoteException;
		
	public ArrayList<interfazCliente> obtenerClientesActuales()
		throws java.rmi.RemoteException;
		
	public void enviarAmistad()
		throws java.rmi.RemoteException;
		
	public boolean novoUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;
	
	public boolean accederUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;

}
