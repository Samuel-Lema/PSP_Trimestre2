package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Cliente {
    
    public static void main (String[] args){

        try{
            System.out.println("Creando socket datagram");
            DatagramSocket socketUDP=new DatagramSocket(5556);

            InetAddress addr2=InetAddress.getByName("localhost");
            
            String mensaje="5";
            DatagramPacket datagrama=new DatagramPacket(mensaje.getBytes(),mensaje.getBytes().length,addr2,5555);
            socketUDP.send(datagrama);
            System.out.println("numero enviado");
            
            mensaje="7";
            datagrama=new DatagramPacket(mensaje.getBytes(),mensaje.getBytes().length,addr2,5555);
            socketUDP.send(datagrama);
            System.out.println("numero enviado");
           
            System.out.println("Recibiendo número");
            byte[] recibir=new byte[25];
            DatagramPacket respuesta=new DatagramPacket(recibir,25);
            socketUDP.receive(respuesta);
            System.out.println("Número recibido: "+new String(recibir));
            
            System.out.println("Cerrando el socket datagrama");

            socketUDP.close();

            System.out.println("Terminado");
            
        }catch(IOException e){}
    }
}