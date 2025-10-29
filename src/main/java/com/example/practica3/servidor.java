package com.example.practica3;

import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;

public class servidor{
	
	public static void main(String[] args) {
		//Para leer os imputs do usuario
		InputStreamReader is = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(is);
        try {
            //Función que rexistra o porto se non o estaba xa
            startRegistry(1099);

            //Crea o obxecto remoto
            implementacionServidor exportedObj = new implementacionServidor();
            String registryURL = "rmi://localhost:1099/aerochat";
            Naming.rebind(registryURL, exportedObj);
            System.out.println("O servidor rexistrouse exitosamente. Contido actual do rexistro:");
            listRegistry(registryURL);
            System.out.println("Servidor listo.");
             System.out.println("-----------------------------------------------------------------------------");
        }catch(Exception e) {
            System.out.print("Erro: " + e.getMessage());
        }
	}
	
	private static void startRegistry(int RMIPortNum)
	throws RemoteException{
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            //A seguinte chamada lanza unha excepcion se o rexistro non existe ainda
            registry.list( );
        }
        catch (RemoteException e) {
            //Este codigo executase so se non existe rexistro
            System.out.println("Non se detectou un rexistro RMI no porto " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println( "Creouse o rexistro RMI no porto " + RMIPortNum);
        }
    }
	
	//Lista o contido do rexistro indicado
	private static void listRegistry(String registryURL)
    throws RemoteException, MalformedURLException{
		System.out.println("O rexistro " + registryURL + " conten: ");
		String [ ] names = Naming.list(registryURL);
		for (int i=0; i < names.length; i++)
			System.out.println(names[i]);
	}
}