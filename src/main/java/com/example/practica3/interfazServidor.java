package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazServidor extends Remote {

    /**
     * Función para registrar un cliente como conectado por el servidor
     * @param nome Nombre del cliente recién conectado
     * @param clienteNuevo Interfaz del cliente recién conectado
     */
	public void registrarCliente(String nome, interfazCliente clienteNuevo)
		throws Exception;

    /**
     * Función para desconectar a un cliente del servidor
     * @param nombre Nombre del cliente a desconectar
     * @param amigos Lista con los nombres de los amigos del cliente
     */
	public void borrarCliente(String nombre,  ArrayList<String> amigos)
		throws java.rmi.RemoteException;

    /**
     * Función que devuelve el nombre de todos los clientes conectados
     */
	public ArrayList<String> obtenerClientesActuales()
		throws java.rmi.RemoteException;

    /**
     * Función que devuelve el nombre de todos los clientes registrados, independientemente de si están online o no
     */
    public ArrayList<String> obtenerUsuariosExistentes()
            throws RemoteException;

    /**
     * Función para enviarle una solicitud de amistad a otro cliente
     * @param solicitario Nombre del cliente que quiere solicitar la amistad
     * @param solicitado Nombre del cliente al que le se le quiere pedir amistad
     */
	public boolean enviarAmistad(String solicitario, String solicitado)
		throws java.rmi.RemoteException;

    /**
     * Función para eliminar una solicitud de amistad del archivo de solicitudes
     * @param nombre Nombre del cliente que había solicitado la amistad
     * @param amigo Nombre del cliente al que le se le había pedido amistad
     */
    public void borrarSolicitud(String nombre, String amigo)
            throws RemoteException;

    public ArrayList<String> tieneSolicitudes(String nome)
            throws RemoteException;

    public void avisarDeSolicitud(String nome, String amigo)
            throws RemoteException;

    public ArrayList<String> listarAmigos(String nome)
            throws java.rmi.RemoteException;

    public void rescribirAmigos(String nome, String amigo, int modo)
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

    public void intentarConexion(interfazCliente origen, String destino) throws Exception;

    public void rechazarConexion(interfazCliente origen) throws Exception;

    public void aceptarConexion(interfazCliente origen, interfazCliente destino) throws Exception;


}
