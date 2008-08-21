package com.yoursway.autoupdater.installer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Installer {
    
    private static final String READY = "READY";
    
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(32123);
            
            while (true) {
                Socket socket = server.accept();
                OutputStream stream = socket.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(stream);
                
                writer.write(READY);
                writer.flush();
                writer.close();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
