package com.example.practica3;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.security.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class implementacionServidor extends UnicastRemoteObject
    implements interfazServidor {
	private HashMap<String, interfazCliente> clientes;
     private HashMap<String, Integer> portosClientes;
     private static String arquivoUsuarios = "usuarios.txt";
    //private static String arquivoSolicitudes = "solicitudes.txt";
  
	public implementacionServidor() throws RemoteException {
      super( );
	  clientes = new HashMap<>();
      //ipsClientes = new HashMap<>();
      portosClientes = new HashMap<>();
      //amigosClientes = new HashMap<>();
	}
   
    public void registrarCliente(String nome, interfazCliente clienteNuevo) throws Exception{
		//Metemos ao novo cliente no hashmap
        clientes.put(nome, clienteNuevo);

		//Notificamos aos amigos da nova conexion
		for(Map.Entry<String,interfazCliente> entrada : clientes.entrySet()){
			String outro = entrada.getKey();
			interfazCliente interOutro = entrada.getValue();
			if(!outro.equals(nome)){
                //if(amigos != null && amigosClientes.get(nome).contains(outro)) {
                ArrayList<String> amig = interOutro.listarAmigos(nome);
                if(amig!=null && amig.contains(outro)){
                    try {
                        interOutro.notificarLlegada(nome);
                    } catch (Exception e) {
                        System.out.println("Erro rexistrando cliente: " + e);
                    }
                }
			}	
		}
	}
	
	public void borrarCliente(String nome, interfazCliente inter) throws RemoteException{
		clientes.remove(nome);
		System.out.println("Cliente " + nome + " desconectado");

		//Notificamos aos amigos da desconexion
		for(interfazCliente interfaz : clientes.values()){
            ArrayList<String> amigos = inter.listarAmigos(nome);
            boolean ba = amigos.contains(interfaz.getNombre());
            int i =4;
            if(amigos != null && amigos.contains(interfaz.getNombre())) {
                try {
                    interfaz.notificarSalida(nome);
                } catch (Exception e) {
                    System.out.println("Erro eliminando cliente: " + e);
                }
            }
		}
	}
	
	public ArrayList<String> obtenerClientesActuales() throws RemoteException{
		return new ArrayList<>(clientes.keySet());
	}
	
	public void enviarAmistad() throws RemoteException{
		
	}
	
	public boolean novoUsuario(String nome, String contrasinal){
		try{
			if(usuarioExiste(nome)){
                return false;
			} else {
                //Hasheamos o contrasinal para que sexa seguro
                //Xeramos o salt
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                //Calculamos o hash
                byte[] hash = hashear(contrasinal, salt);

                //Escribimos o novo usuario no arqiuvo
                FileWriter f = new FileWriter(arquivoUsuarios, true);
                try(BufferedWriter w = new BufferedWriter(f)){
                    w.write(nome + "|"+Base64.getEncoder().encodeToString(salt)+"|"+Base64.getEncoder().encodeToString(hash));
                    w.newLine();
                }

                System.out.println("Usuario " + nome + " rexistrado");
                return true;
            }
		}catch(Exception e){
			System.out.println("Erro en novo usuario: " + e);
		}
		return false;
	}
	
	public boolean accederUsuario(String nome, String contrasinal){
		String cadea; 
		try(FileReader f = new FileReader(arquivoUsuarios)){
			BufferedReader b = new BufferedReader(f);

			while((cadea = b.readLine())!=null){
				String[] partes = cadea.split("\\|");

				if(partes[0].equals(nome)){
					byte[] salt = Base64.getDecoder().decode(partes[1]);
					byte[] hashGardado = Base64.getDecoder().decode(partes[2]);
					byte[] hashActual = hashear(contrasinal, salt);
					//Comprobamos se a contrasinal é a mesma
					return Arrays.equals(hashGardado, hashActual);
				}	
			}
		}catch(Exception e){
			System.out.println("Erro en acceder usuario: " + e);
		}
		return false;
	}

    public boolean cambiarContrasinal(String nome, String contrasinal, String novo){
        String cadea;
        List<String> rescritura = new ArrayList<String>();
        byte[] salt=null, hash=null;
        boolean coincide=false;

        try(FileReader f = new FileReader(arquivoUsuarios)) {
            BufferedReader b = new BufferedReader(f);

            while ((cadea = b.readLine()) != null) {
                String[] partes = cadea.split("\\|");

                if (partes[0].equals(nome)) {
                    salt = Base64.getDecoder().decode(partes[1]);
                    byte[] hashGardado = Base64.getDecoder().decode(partes[2]);
                    byte[] hashActual = hashear(contrasinal, salt);
                    //Comprobamos se o contrasinal é o mesmo
                    if(Arrays.equals(hashGardado, hashActual)){
                        coincide = true;
                        SecureRandom random = new SecureRandom();
                        salt = new byte[16];
                        random.nextBytes(salt);
                        //Calculamos o hash
                        hash = hashear(novo, salt);

                        rescritura.add(nome + "|"+Base64.getEncoder().encodeToString(salt)+"|"+Base64.getEncoder().encodeToString(hash));
                        }
                } else {
                    rescritura.add(cadea);
                }
            }

            if(coincide){
                FileWriter fw = new FileWriter(arquivoUsuarios, false);
                try(BufferedWriter w = new BufferedWriter(fw)){
                    for(String s : rescritura){
                        w.write(s);
                        w.newLine();
                    }
                }
            }

        }catch(Exception e){
            System.out.println("Erro: " + e);
        }

        return coincide;
    }
	
	private boolean usuarioExiste(String nome) throws Exception{
		String cadea; 
		File arq = new File(arquivoUsuarios);
		if(!arq.exists()) return false;
		
		FileReader f = new FileReader(arquivoUsuarios);
		BufferedReader b = new BufferedReader(f);
		while((cadea = b.readLine())!=null){
			if(cadea.startsWith(nome+"|")) return true;
		}
		return false;
	}

    public interfazCliente getCliente(String nome) throws Exception{

        try{
            return clientes.get(nome);
        } catch (Exception e) {
            System.err.println("No se encontro usuario con ese nombre");
            throw new RuntimeException(e);
        }
    }

    /*public String IPsolicitada(String nome) throws RemoteException{
        try {
            return ipsClientes.get(nome);
        } catch (Exception e){
            System.err.println("Error: " + e);
        }
        return null;
    }*/

    public int portoSolicitado(String nome) throws RemoteException {
        try {
            return portosClientes.get(nome);
        } catch (Exception e) {
        System.out.println("Error: " + e);
        }
        return 0;
    }
	
	private byte[] hashear(String contrasinal, byte[] salt) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		return md.digest(contrasinal.getBytes(StandardCharsets.UTF_8));
	}

    public void asignarPorto(String nome, int porto) throws RemoteException{
        portosClientes.put(nome, porto);
    }

    public void intentarConexion(interfazCliente origen, String destino) throws Exception {
        interfazCliente clienteDestino = getCliente(destino);

        clienteDestino.recibirIntentoConexion(origen, portoSolicitado(origen.getNombre()));
    }

    public void rechazarConexion(interfazCliente destino, interfazCliente origen) throws Exception{
        origen.recibirRechazoConexion();
    }

}
