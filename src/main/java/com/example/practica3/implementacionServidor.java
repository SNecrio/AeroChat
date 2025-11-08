package com.example.practica3;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.security.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class implementacionServidor extends UnicastRemoteObject
    implements interfazServidor {
	private HashMap<String, interfazCliente> clientes;   //Incluye a los clientes que están conectados

    private static String arquivoUsuarios = "usuarios.txt";
    private static String arquivoAmigos = "amigos.txt";
    private static String arquivoSolicitudes = "solicitudes.txt";
  
	public implementacionServidor() throws RemoteException {
      super( );
	  clientes = new HashMap<>();
	}

    public void registrarCliente(String nome, interfazCliente clienteNuevo) throws Exception{
		//Metemos al nuevo cliente en el HashMap de clientes conectados
        clientes.put(nome, clienteNuevo);

		//Notificamos a sus amigos da la nueva conexión
		for(Map.Entry<String,interfazCliente> entrada : clientes.entrySet()){
			String outro = entrada.getKey();
			interfazCliente interOutro = entrada.getValue();
			if(!outro.equals(nome)){
                ArrayList<String> amigo = listarAmigos(nome);
                if(amigo!=null && amigo.contains(outro)){
                    try {
                        interOutro.notificarLlegada(nome);
                    } catch (Exception e) {
                        System.out.println("Error rexistrando cliente: " + e);
                    }
                }
			}	
		}
	}
	
	public void borrarCliente(String nome, ArrayList<String> amigos) throws RemoteException{
		//Notificamos a los amigos de la desconexión
		for(interfazCliente interfaz : clientes.values()){
            if(amigos != null && amigos.contains(interfaz.getNombre())) {
                try {
                    interfaz.notificarSalida(nome);
                } catch (Exception e) {
                    System.out.println("Error eliminando cliente: " + e);
                }
            }
		}
        clientes.remove(nome);
	}
	
	public ArrayList<String> obtenerClientesActuales() throws RemoteException{
		return new ArrayList<>(clientes.keySet());
	}

    public ArrayList<String> obtenerUsuariosExistentes() throws RemoteException{
        String cadea;
        ArrayList<String> usuarios = new ArrayList<>();

        // Leemos el archivo de registro de los clientes para obtener sus nombres
        try(FileReader f = new FileReader(arquivoUsuarios)){
            BufferedReader b = new BufferedReader(f);
            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\|");
                usuarios.add(partes[0]);
            }
        }catch(Exception e){
            System.out.println("Error obteniendo los usuarios: " + e);
        }
        return usuarios;
    }
	
	public boolean enviarAmistad(String solicitante, String solicitado) throws RemoteException{
        if(noSolicitado(solicitante, solicitado)){

            // Añadimos una solicitud de amistad con los nombres correspondientes al archivo de solicitudes
            try (FileWriter f = new FileWriter(arquivoSolicitudes, true);
                 BufferedWriter w = new BufferedWriter(f)) {
                w.write(solicitante + "|" + solicitado);
                w.newLine();
                return true;
            } catch(Exception e){
                System.out.println("Error en envío de amistad: " + e);
            }
        }
        return false;
	}

    public void borrarSolicitud(String nombre, String amigo) throws RemoteException{
        String cadea;
        ArrayList<String> arqEnteiro = new ArrayList<>();

        // Rescribimos el archivo de solicitudes omitiendo la ya tramitada
        try(FileReader f = new FileReader(arquivoSolicitudes)){
            BufferedReader b = new BufferedReader(f);
            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\|");
                if(!(partes[0].equals(amigo) && partes[1].equals(nombre))){
                    arqEnteiro.add(cadea);
                }
            }
        }catch(Exception e){
            System.out.println("Error leyendo solicitudes: " + e);
        }

        try (FileWriter f = new FileWriter(arquivoSolicitudes, false);
             BufferedWriter w = new BufferedWriter(f)) {
            for(String s : arqEnteiro) {
                w.write(s);
                w.newLine();
            }
        } catch(Exception e){
            System.out.println("Error actualizando solicitudes: " + e);
        }
    }

    private boolean noSolicitado(String solicitante, String solicitado) throws RemoteException{
        String cadea;
        try(FileReader f = new FileReader(arquivoSolicitudes)){
            BufferedReader b = new BufferedReader(f);
            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\|");
                if(partes[0].equals(solicitante) && partes[1].equals(solicitado)) return false;
            }
        }catch(Exception e){
            System.out.println("Error leyendo solicitudes: " + e);
        }
        return true;
    }

    public ArrayList<String> tieneSolicitudes(String nome) throws RemoteException{
        String cadea;
        ArrayList<String> solicitudes = new ArrayList<>();
        try(FileReader f = new FileReader(arquivoSolicitudes)){
            BufferedReader b = new BufferedReader(f);
            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\|");
                if(partes[1].equals(nome)) solicitudes.add(partes[0]);
            }
        }catch(Exception e){
            System.out.println("Error leyendo solicitudes: " + e);
        }
        return solicitudes;
    }

    public void avisarDeSolicitud(String nome, String amigo) throws RemoteException{
        interfazCliente cliente = clientes.get(nome);
        cliente.notificarAmistad(amigo);
    }

    public ArrayList<String> listarAmigos(String nome) throws java.rmi.RemoteException{
        ArrayList<String> amigos = new ArrayList<>();
        try(FileReader f = new FileReader(arquivoAmigos)){
            BufferedReader b = new BufferedReader(f);
            String cadea;

            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\:");

                if(partes[0].equals(nome)){
                    String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    break;
                }
            }
        }catch(Exception e){
            System.out.println("Erro atopando amigos: " + e);
        }
        return amigos;
    }

    //Si modo 0 es para meter nuevo amigo, si modo 1 es para eliminar ese amigo de la lista
    public void rescribirAmigos(String nome, String amigo, int modo) throws java.rmi.RemoteException{
        try {
            ArrayList<String> amigos = new ArrayList<>();
            ArrayList<String> arqEntero = new ArrayList<>();
            FileReader r = new FileReader(arquivoAmigos);
            BufferedReader b = new BufferedReader(r);
            String cadea;
            boolean nomeEstaba=false, amigoEstaba=false;

            while((cadea = b.readLine())!=null) {
                String[] partes = cadea.split("\\:");
                if (partes[0].equals(nome)) {
                    String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    if (modo == 0 && !amigos.contains(amigo)) amigos.add(amigo);
                    else if (modo == 1) amigos.remove(amigo);

                    if(!amigos.isEmpty()) {
                        String linea = nome + ":" + String.join("|", amigos);
                        arqEntero.add(linea);
                        amigos.clear();
                    }
                    nomeEstaba = true;
                } else if(partes[0].equals(amigo)){
                    String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    if (modo == 0 && !amigos.contains(nome)) amigos.add(nome);
                    else if (modo == 1) amigos.remove(nome);

                    if(!amigos.isEmpty()) {
                        String linea = amigo + ":" + String.join("|", amigos);
                        arqEntero.add(linea);
                        amigos.clear();
                    }
                    amigoEstaba = true;
                } else {
                    arqEntero.add(cadea);
                }
            }
            if(!nomeEstaba){
                String linea = nome + ":" + amigo;
                arqEntero.add(linea);
            }
            if(!amigoEstaba){
                String linea = amigo + ":" + nome;
                arqEntero.add(linea);
            }

            try(FileWriter f = new FileWriter(arquivoAmigos, false);
                BufferedWriter w = new BufferedWriter(f);){
                for(String a : arqEntero){
                    w.write(a);
                    w.newLine();
                }
            }catch(Exception e){
                System.out.println("Erro rescribindo amigos: " + e);
            }

        }catch(Exception e){
            System.out.println("Erro rescribindo amigos: " + e);
        }
        interfazCliente cliente = clientes.get(amigo);
        if(cliente!=null) cliente.recargarAmigos();
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

	private byte[] hashear(String contrasinal, byte[] salt) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		return md.digest(contrasinal.getBytes(StandardCharsets.UTF_8));
	}

    public void intentarConexion(interfazCliente origen, String destino) throws Exception {
        interfazCliente clienteDestino = getCliente(destino);
        clienteDestino.recibirIntentoConexion(origen);
    }

    public void aceptarConexion(interfazCliente origen, interfazCliente destino) throws Exception{
        origen.recibirAceptacionConexion(destino);
    }

    public void rechazarConexion(interfazCliente origen) throws Exception{
        origen.recibirRechazoConexion();
    }
}
