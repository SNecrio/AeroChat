package com.example.practica3;

import java.rmi.*;
import java.util.ArrayList;

public interface interfazServidor extends Remote {

    /**
     * Función para dar acceso a un cliente al servidor
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

    /**
     * Función que devuelve una lista de nombres de usuarios que le han solicitado amistad a un cliente
     * @param nome Nombre del cliente al que le han solicitado amistad
     */
    public ArrayList<String> tieneSolicitudes(String nome)
            throws RemoteException;

    /**
     * Función para avisar a un cliente de que le han solicitado amistad
     * @param nome Nombre del cliente al que le han solicitado amistad
     * @param amigo Nombre del cliente que ha pedido amistad
     */
    public void avisarDeSolicitud(String nome, String amigo)
            throws RemoteException;

    /**
     * Función que devuelve la lista de amigos de un cliente
     * @param nome Nombre del cliente del que consultar la lista de amigos
     */
    public ArrayList<String> listarAmigos(String nome)
            throws java.rmi.RemoteException;

    /**
     * Función para modificar la lista de amigos de un cliente
     * @param nome Nombre del cliente del que queremos modificar las amistades
     * @param amigo Nombre del usuario que será añadido o eliminado de la lista de amigos
     * @param modo Valor que nos indica si la modificación será de inserción (0) o de borrado (1)
     */
    public void rescribirAmigos(String nome, String amigo, int modo)
            throws java.rmi.RemoteException;

    /**
     * Función para registrar un nuevo usuario en el servidor
     * @param nome Nombre del cliente que desea registrarse
     * @param contrasinal Contraseña que el cliente quiere asociar a su usuario
     */
	public boolean novoUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;

    /**
     * Función para acceder a un usuario ya registrado
     * @param nome Nombre del cliente que desea acceder
     * @param contrasinal Contraseña del usuario
     */
	public boolean accederUsuario(String nome, String contrasinal)
		throws java.rmi.RemoteException;

    /**
     * Función para modificar la contraseña de un usuario
     * @param nome Nombre del cliente del que se quiere cambiar la contraseña
     * @param contrasinal Contraseña actual del usuario
     * @param novo Contraseña nueva del usuario
     */
    public boolean cambiarContrasinal(String nome, String contrasinal, String novo)
            throws java.rmi.RemoteException;

    /*
    public interfazCliente getCliente(String nome)
            throws Exception;*/


    /**
     * Función que inicia el intento de comunicación
     */
    public void intentarConexion(interfazCliente origen, String destino) throws Exception;

    public void rechazarConexion(interfazCliente origen) throws Exception;

    public void aceptarConexion(interfazCliente origen, interfazCliente destino) throws Exception;


}
