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
	private static String arquivo = "usuarios.txt";
  
	public implementacionServidor() throws RemoteException {
      super( );
	  clientes = new HashMap<>();
	}
   
    public void registrarCliente(String nome, interfazCliente clienteNuevo) throws Exception{
		//Metemos ao novo cliente no hashmap
        clientes.put(nome, clienteNuevo);
		
		//Notificamos aos demais da nova conexion
		for(Map.Entry<String,interfazCliente> entrada : clientes.entrySet()){
			String outro = entrada.getKey();
			interfazCliente interOutro = entrada.getValue();
			if(!outro.equals(nome)){
				try{
					interOutro.notificarLlegada(nome);
				} catch (Exception e) {
					System.out.println("Erro: " + e);
				}
			}	
		}
		Set<String> actuais = clientes.keySet();
		ArrayList<String> conectados = new ArrayList<>(actuais);
	}
	
	public void borrarCliente(String nome) throws RemoteException{
		clientes.remove(nome);
		System.out.println("Cliente " + nome + " desconectado");
		
		//Notificamos aos demais da desconexion
		for(interfazCliente interfaz : clientes.values()){
			try{
				interfaz.notificarSalida(nome);
			} catch (Exception e) {
				System.out.println("Erro: " + e);
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
                FileWriter f = new FileWriter(arquivo, true);
                try(BufferedWriter w = new BufferedWriter(f)){
                    w.write(nome + "|"+Base64.getEncoder().encodeToString(salt)+"|"+Base64.getEncoder().encodeToString(hash));
                    w.newLine();
                }

                System.out.println("Usuario " + nome + " rexistrado");
                return true;
            }
		}catch(Exception e){
			System.out.println("Erro: " + e);
		}
		return false;
	}
	
	public boolean accederUsuario(String nome, String contrasinal){
		String cadea; 
		try(FileReader f = new FileReader(arquivo)){
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
			System.out.println("Erro: " + e);
		}
		return false;
	}

    public boolean cambiarContrasinal(String nome, String contrasinal, String novo){
        String cadea;
        List<String> rescritura = new ArrayList<String>();
        byte[] salt=null, hash=null;
        boolean coincide=false;

        try(FileReader f = new FileReader(arquivo)) {
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
                FileWriter fw = new FileWriter(arquivo, false);
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
		File arq = new File(arquivo);
		if(!arq.exists()) return false;
		
		FileReader f = new FileReader(arquivo);
		BufferedReader b = new BufferedReader(f);
		while((cadea = b.readLine())!=null){
			if(cadea.startsWith(nome+"|")) return true;
		}
		return false;
	}

    public interfazCliente getCliente(String nome) throws Exception{
        return clientes.get(nome);
    }
	
	private byte[] hashear(String contrasinal, byte[] salt) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		return md.digest(contrasinal.getBytes(StandardCharsets.UTF_8));
	}
	
}
