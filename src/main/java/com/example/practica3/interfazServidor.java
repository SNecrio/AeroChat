package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazServidor extends Remote {

	public void registrarCliente(String nome, interfazCliente clienteNuevo)
		throws Exception;
		
	public void borrarCliente(String nombre,  ArrayList<String> amigos)
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
/*
    public void conectarClientes(interfazCliente origen, String destino) throws Exception;
*/
    public boolean cambiarContrasinal(String nome, String contrasinal, String novo)
            throws java.rmi.RemoteException;

   /* public String IPsolicitada(String nome)
            throws RemoteException;*/

    public int portoSolicitado(String nome)
            throws RemoteException;

    public void asignarPorto(String nome, int porto)
            throws RemoteException;

    public void intentarConexion(interfazCliente origen, String destino) throws Exception;

    public void rechazarConexion(interfazCliente destino, interfazCliente origen) throws Exception;
}
