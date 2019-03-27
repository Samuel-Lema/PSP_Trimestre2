package main;

import java.awt.HeadlessException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class Main {

    private static ServerSocket serverSocket;
    private static InetSocketAddress addr;
    private static Socket newSocket;
    
    public static void main(String[] args) {
        
        try {
            // Genera el ServerSocket Maestro
            serverSocket = new ServerSocket();

            // Asigna ip y puerto
            addr = new InetSocketAddress("10.0.9.5", Integer.valueOf(JOptionPane.showInputDialog("Introduce el número de Puerto")));
            serverSocket.bind(addr);
            
            System.out.println("Servidor: Creando ServerSocket en (" + addr.getHostName() + ") con puerto (" + addr.getPort() + ")");

            do {
                newSocket = serverSocket.accept();
                System.out.println("Servidor: Aceptando conexion nueva en (" + newSocket.getPort() + ") ");
                new SocketServer(newSocket);
                
            } while(true);
            
        } catch (HeadlessException | IOException | NumberFormatException ex) {} 
    }
}
