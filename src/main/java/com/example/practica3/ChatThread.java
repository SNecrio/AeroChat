package com.example.practica3;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatThread extends Thread{

    private Socket socket;
    private BufferedReader entrada;
    private ChatController controller;

    public ChatThread(String name, Socket socket, ChatController controller) throws IOException {
        super(name);
        this.socket = socket;
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.controller = controller;
    }

    public void run(){

       while(true){
            try {
                String respuesta = entrada.readLine();

                Platform.runLater(() -> {
                    try {
                        controller.recibirMensaje(respuesta);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
