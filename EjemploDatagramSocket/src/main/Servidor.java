package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Servidor {

    public static void main(String[] args) {
        
        try {
            System.out.println("Creando socket datagram");
            DatagramSocket socketUDP = new DatagramSocket(5555);

            System.out.println("Recibiendo mensaje");
            byte[] recibir = new byte[25];
            DatagramPacket peticion = new DatagramPacket(recibir, 25);
            socketUDP.receive(peticion);
            double numero1=Double.parseDouble(new String(recibir));
            System.out.println("Número recibido: " + numero1);
            
            System.out.println("Recibiendo mensaje");
            recibir = new byte[25];
            peticion = new DatagramPacket(recibir, 25);
            socketUDP.receive(peticion);
            double numero2=Double.parseDouble(new String(recibir));
            System.out.println("Número recibido: " + numero2);

            String mensaje = ""+(numero1+numero2);
            InetAddress addr2 = InetAddress.getByName("localhost");
            DatagramPacket respuesta = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length, addr2, 5556);
 
            System.out.println("Enviando mensaje");
            socketUDP.send(respuesta);
            System.out.println("Mensaje enviado");

            System.out.println("Cerrando el socket datagrama");

            socketUDP.close();

            System.out.println("Terminado");

        } catch (IOException e) {}
    }
}