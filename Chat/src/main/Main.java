package main;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Main {
    
    // Variables del Servidor (Sala de Chat)
    private static ServerSocket serverSocket;
    private static InetSocketAddress addr;
    
    // Variables del Cliente (Usuario)
    private static Socket socketUsuario;
    
    public static void main(String[] args) {
        
        try {
            // Genera la Sala de Chat
            serverSocket = new ServerSocket();

            // Asigna ip y puerto
            addr = new InetSocketAddress("localhost", Integer.valueOf(JOptionPane.showInputDialog("Introduce el número de Puerto")));
            serverSocket.bind(addr);
            
            System.out.println("--> Creando ServerSocket en (" + addr.getHostName() + ") con puerto (" + addr.getPort() + ")");
            
            // Genero Interfaz
            new Administracion();
            
            // Escucho nuevas peticiones de conexión y genero un Objeto usuario por cada conexión
            do {
                socketUsuario = serverSocket.accept();
                new Usuario(socketUsuario);
                
            } while(true);

        } catch (HeadlessException | IOException | NumberFormatException ex) {} 
    }
}