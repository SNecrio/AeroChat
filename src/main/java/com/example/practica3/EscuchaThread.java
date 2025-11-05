package com.example.practica3;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class EscuchaThread extends Thread{

    private ServerSocket serverSocket;
    private AerochatController controller;

    public EscuchaThread(String name, ServerSocket serverSocket, AerochatController controller) throws IOException {
        super(name);
        this.serverSocket = serverSocket;
        this.controller = controller;
    }

    public void run() {
        try {
            // Esperamos conexions
            System.out.println("Esperando conexiones en puerto " + serverSocket.getLocalPort());
            Socket socket = serverSocket.accept();
            // Lemos a mensaxe
            BufferedReader b = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensaxe = b.readLine();

            Platform.runLater(() -> {
                try {
                    if(mensaxe.equalsIgnoreCase("SI")){

                        controller.onAbrirChat(socket);

                    }else{
                        controller.intentoConexionRechazado();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            System.out.println("Esperando conexiones en puerto " + serverSocket.getLocalPort());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
