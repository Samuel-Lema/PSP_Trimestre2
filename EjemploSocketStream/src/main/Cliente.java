package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Cliente {

	public static void main(String[] args){
		try{
			System.out.println("Creando socket cliente");
			Socket clienteSocket=new Socket();
			System.out.println("Estableciendo la conexión");
			
			InetSocketAddress addr=new InetSocketAddress("localhost",6666);
			clienteSocket.connect(addr);

			InputStream is = clienteSocket.getInputStream();
			OutputStream os= clienteSocket.getOutputStream();

			System.out.println("Enviando mensaje");
                        
                        int[] numeros={1,2,3,4,1};
                        
                        for(int i=0;i<5;i++){
                            os.write(numeros[i]);
                            System.out.println("Enviado "+numeros[i]);
                        }
                        
                        System.out.println("Números enviados");

                        
			byte[] recibido=new byte[50];
			is.read(recibido);
			System.out.println("Suma recibida= "+recibido[0]);
                        
			clienteSocket.close();

			System.out.println("Terminado");

		}catch (IOException e) {
                    System.out.println("Error de conexión");;
		}
	}
    
}
