package com.example.practica3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class EscuchaThread extends Thread{

    private ServerSocket serverSocket;
    private BufferedReader entrada;
    private ChatController controller;

    public EscuchaThread(String name, ServerSocket serverSocket, ChatController controller) throws IOException {
        super(name);
        this.serverSocket = serverSocket;
        this.controller = controller;
    }

    public void run() {
        while (true) {
            try {
                // Esperamos conexions
                System.out.println("Esperando conexiones en puerto " + serverSocket.getLocalPort());
                Socket socket = serverSocket.accept();
                // Lemos a mensaxe
                BufferedReader b = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String mensaxe = b.readLine();

                if(mensaxe.startsWith("SOL")){
                    System.out.println("RECIBIUSE : " + mensaxe);
                }
                if (mensaxe != null) {
                    System.out.println("Mensaxe: " + mensaxe);

                    javafx.application.Platform.runLater(() -> {
                        //controller.mostrarMensaxe(mensaxe);
                    });
                }
                // Cerramos a conexion despois de ler a mensaxe
                socket.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                break;
            }
        }
    }

}
