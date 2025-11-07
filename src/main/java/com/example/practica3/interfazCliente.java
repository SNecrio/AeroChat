package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazCliente extends Remote {

	void notificarLlegada(String nombre)
		throws java.rmi.RemoteException;

	void notificarSalida(String nombre)
		throws java.rmi.RemoteException;

    void anadirCliente(String nombre, interfazCliente cliente, ChatController chat)
            throws java.rmi.RemoteException;

    public void notificarAmistad(String amigo)
            throws java.rmi.RemoteException;

	public void actualizarConectados(ArrayList<String> nombres)
		throws java.rmi.RemoteException;

    public void recargarAmigos()
            throws java.rmi.RemoteException;;

    public String getNombre()
        throws java.rmi.RemoteException;

    public void enviarMensaje(String destino, String mensaje)
            throws java.rmi.RemoteException;

    public void recibirMensaje(String origen, String mensaje)
            throws java.rmi.RemoteException;

    public void recibirIntentoConexion(interfazCliente origen) throws Exception;
    public void recibirRechazoConexion() throws Exception;
    public void recibirAceptacionConexion(interfazCliente destino) throws Exception;
    public void recibirSalida(String origen) throws Exception;
}
