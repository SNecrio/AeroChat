package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazServidor extends Remote {

	public interfazCliente registrarCliente(String nombre)
		throws Exception;
		
	public void borrarCliente(String nombre)
		throws java.rmi.RemoteException;
		
	public ArrayList<String> obtenerClientesActuales()
		throws java.rmi.RemoteException;
		
	public void enviarAmistad()
		throws java.rmi.RemoteException;
		
	public boolean novoUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;
	
	public boolean accederUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;

    public interfazCliente getCliente(String nome) throws Exception;

    public boolean cambiarContrasinal(String nome, String contrasinal, String novo)
            throws java.rmi.RemoteException;;
}
