package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazCliente extends Remote {

    /**
     * Función para notificar a usuarios amigos de la conexión de un cliente
     * @param nombre Nombre del cliente recién conectado
     */
	void notificarLlegada(String nombre)
		throws java.rmi.RemoteException;

    /**
     * Función para notificar a usuarios amigos de la desconexión de un cliente
     * @param nombre Nombre del cliente recién desconectado
     */
	void notificarSalida(String nombre)
		throws java.rmi.RemoteException;

    /**
     * Función para añadir el cliente actual y sus chats
     * @param nombre Nombre del cliente
     * @param cliente Interfaz del cliente
     * @param chat Controlador del chat
     */
    void anadirCliente(String nombre, interfazCliente cliente, ChatController chat)
            throws java.rmi.RemoteException;

    /**
     * Función para añadir notificar a un cliente que le han solicitado amistad
     * @param amigo nombre del cliente que ha enviado la solicitud
     */
    public void notificarAmistad(String amigo)
            throws java.rmi.RemoteException;

    /**
     * Función para actualizar el panel de amigos
     */
    public void recargarAmigos()
            throws java.rmi.RemoteException;;

    /**
     * Función para avisar al cliente de que otro usuario quiere abrir un chat con él
     * @param origen Interfaz del cliente que ha solicitado la conexión
     */
    public void recibirIntentoConexion(interfazCliente origen)
            throws Exception;

    /**
     * Función para rechazar el intento de conexión con otro cliente
     */
    public void recibirRechazoConexion()
            throws Exception;

    /**
     * Función para aceptar la solicitud de chat con otro usuario
     * @param destino Interfaz del cliente que ha aceptado la conexión
     */
    public void recibirAceptacionConexion(interfazCliente destino)
            throws Exception;

    /**
     * Función para desconectarse del chat e informar al otro cliente
     * @param origen Interfaz del cliente que se quiere desconectar
     */
    public void recibirSalida(String origen) throws Exception;

    /**
     * Función para enviarle un mensaje a otro cliente
     * @param destino Interfaz del cliente al que enviarle el mensaje
     * @param mensaje Mensaje a enviar
     */
    public void enviarMensaje(String destino, String mensaje)
            throws java.rmi.RemoteException;

    /**
     * Función para recibir un mensaje de otro cliente
     * @param origen Interfaz del cliente que ha enviado el mensaje
     * @param mensaje Mensaje recibido
     */
    public void recibirMensaje(String origen, String mensaje)
            throws java.rmi.RemoteException;

    public String getNombre()
            throws java.rmi.RemoteException;
}
