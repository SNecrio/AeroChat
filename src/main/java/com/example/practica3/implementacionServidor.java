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

        // Leemos el archivo de solicitudes, omitiendo la solicitud deseada
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

        // Rescribimos el archivo de salida
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

    //Función para comprobar si un cliente ya le ha enviado una solicitud a otro
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

        //Leemos el archivo y guardamos los nombres de los amigos del cliente dado
        try(FileReader f = new FileReader(arquivoAmigos)){
            BufferedReader b = new BufferedReader(f);
            String cadea;
            //Separamos la cadena leida en partes
            while((cadea = b.readLine())!=null){
                String[] partes = cadea.split("\\:");
                //Comprobamos que el nombre leido coincida con el proporcionado
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

    public void rescribirAmigos(String nome, String amigo, int modo) throws java.rmi.RemoteException{
        try {
            ArrayList<String> amigos = new ArrayList<>();
            ArrayList<String> arqEntero = new ArrayList<>();

            //Leemos el archivo de amigos
            FileReader r = new FileReader(arquivoAmigos);
            BufferedReader b = new BufferedReader(r);
            String cadea;
            boolean nomeEstaba=false, amigoEstaba=false;

            while((cadea = b.readLine())!=null) {
                String[] partes = cadea.split("\\:");

                //Comprobamos si el nombre encontrado coindice con el del usuario a modificar
                if (partes[0].equals(nome)) {
                    String[] partes2 = partes[1].split("\\|");
                    amigos.addAll(Arrays.asList(partes2));
                    if (modo == 0 && !amigos.contains(amigo)) amigos.add(amigo);  //Añadimos el amigo a la lista
                    else if (modo == 1) amigos.remove(amigo); //Quitamos al amigo de la lista
                    //Si el usuario aún tiene otros amigos, lo mantenemos en el archivo. Si no, no lo rescribimos.
                    if(!amigos.isEmpty()) {
                        String linea = nome + ":" + String.join("|", amigos);
                        arqEntero.add(linea);
                        amigos.clear();
                    }
                    nomeEstaba = true; //Indicamos que el usuario tenía una entrada en el archivo
                } else if(partes[0].equals(amigo)){
                    //Repetimos el proceso pero para el otro usuario, ya que cada uno tiene una entrada en el archivo
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
            //Si el usuario o amigo no estaba en el archivo, lo añadimos
            if(!nomeEstaba){
                String linea = nome + ":" + amigo;
                arqEntero.add(linea);
            }
            if(!amigoEstaba){
                String linea = amigo + ":" + nome;
                arqEntero.add(linea);
            }
            //Rescribimos el archivo ya con los cambios aplicados
            try(FileWriter f = new FileWriter(arquivoAmigos, false);
                BufferedWriter w = new BufferedWriter(f);){
                for(String a : arqEntero){
                    w.write(a);
                    w.newLine();
                }
            }catch(Exception e){
                System.out.println("Erroe rescribiendo amigos: " + e);
            }

        }catch(Exception e){
            System.out.println("Error rescribiendo amigos: " + e);
        }
        interfazCliente cliente = clientes.get(amigo);
        //Actualizamos la interfaz gráfica del amigo que hemos añadido/borrado
        if(cliente!=null) cliente.recargarAmigos();
    }
	
	public boolean novoUsuario(String nome, String contrasinal){
		try{
            //Si el usuario existe no creamos uno nuevo
			if(usuarioExiste(nome)){
                return false;
			} else {
                //Hasheamos la contraseña para no escribirla en texto plano
                //Generamos el salt
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                //Calculamos el hash
                byte[] hash = hashear(contrasinal, salt);

                //Escribimos el nuevo usuario en el archivo
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
        //Leemos el archivo donde se encuentran los usuarios
		try(FileReader f = new FileReader(arquivoUsuarios)){
			BufferedReader b = new BufferedReader(f);

			while((cadea = b.readLine())!=null){
				String[] partes = cadea.split("\\|");
                //Comprobamos que el nombre proporcionado esté en el archivo
				if(partes[0].equals(nome)){
					byte[] salt = Base64.getDecoder().decode(partes[1]);
					byte[] hashGardado = Base64.getDecoder().decode(partes[2]);
					byte[] hashActual = hashear(contrasinal, salt);
					//Comprobamos si la contraseña dada es la misma que la almacenada
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
                    //Comprobamos que la contraseña proporcionada coincida con la almacenada
                    if(Arrays.equals(hashGardado, hashActual)){
                        coincide = true;
                        //Encriptamos la nueva contraseña
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
            //Rescribimos el archivo con la contraseña modificada
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

    //Función hash para encriptar las contraseñas
    private byte[] hashear(String contrasinal, byte[] salt) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt);
        return md.digest(contrasinal.getBytes(StandardCharsets.UTF_8));
    }

    //Función para comprobar si el nombre de un usuario dado ya está registrado en el archivo
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

    public void intentarConexion(interfazCliente origen, String destino) throws Exception {
        interfazCliente clienteDestino = clientes.get(destino);
        clienteDestino.recibirIntentoConexion(origen);
    }

    public void aceptarConexion(interfazCliente origen, interfazCliente destino) throws Exception{
        origen.recibirAceptacionConexion(destino);
    }

    public void rechazarConexion(interfazCliente origen) throws Exception{
        origen.recibirRechazoConexion();
    }
}
