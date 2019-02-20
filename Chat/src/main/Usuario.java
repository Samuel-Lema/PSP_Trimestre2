package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Usuario extends Thread {

    private Socket socket;
    private Sala sala;
    private String userName = "Unknown";
    private InputStream is;
    private OutputStream os;
    byte[] mensaje;

    public Usuario(Socket socket) {
        this.socket = socket;

        // Llama al "Run" del Thread para iniciar la comunicación con el cliente
        this.start();
    }

    @Override public void run() {
        
        // Acepta lectura y escritura
        try {

            is = socket.getInputStream();
            os = socket.getOutputStream();
        } catch (IOException ex) {}

        // Espera por el 1º mensaje del cliente, lo reconocera como el NickName del Usuario
        userName = recibirMensaje();
        
        changeSala(null);
        
        // Envia un mensaje Global del Servidor en el que muestra datos del nuevo Usuario conectado
        sala.GlobalMensaje(this, "Nuevo usuario conectado ( " + userName + " / " + socket.getInetAddress().getHostAddress() + " / " + socket.getPort() + " )", true);

        // Envia un mensaje Global del Servidor en el que muestra los Usuarios conectados actualmente
        sala.GlobalMensaje(this, "Actualmente hay ( " + sala.getUsuarios().size() + " ) Usuarios conectados", false);

        // Entra en un bucle de comunicación con el cliente hasta que se cierre la conexión
        boolean closeClient = false;
        
        do {
            String mensaje = recibirMensaje();
                    
            if (socket.isClosed() == true) {
                
                 break;
            }
            
            // Gestiona si recibe un /bye el cual debera cerrar la comunicación con el cliente
            if (mensaje.equals("/bye")) {

                // La variable closeClient cambia a 'TRUE' para salir del bucle de comunicación con este cliente
                closeClient = closeConnect();

            } else if(mensaje.contains("/sala")){
            
                changeSala(mensaje.split("/sala ")[1]);
                
            } else {

                // Si el mensaje recibido no es /bye muestra el mensaje en la Sala
                sala.añadirMensaje(this, mensaje);
            }

        } while (closeClient == false);
    }

    // Permite unirse a una Sala
    
    public void changeSala(String nombreS){
        
        // Busca o crea una Sala y se autoañade a ella
        boolean salaEncontrada = false;
        
        String nombreSala;
        
        if (nombreS == null) {
            
            nombreSala = recibirMensaje();
        } else {
            
            nombreSala = nombreS;
        }
        
        
        for (Sala x: Sala.getSalas()) {
            
            if (x.getNombre().equals(nombreSala)) {
                
                salaEncontrada = true;
                sala = x;
                sala.addUser(this);
                break;
            }
        }
        
        if (salaEncontrada == false) {
            
            sala = new Sala(nombreSala);
            sala.addUser(this);
        }
        
        // Gestiona que no se conecten más de 10 usuarios simultaneamente
        if (sala.getUsuarios().size() > 10) {

            System.out.println("El usuario ( " + userName + " ) esta a la espera en la sala ( " + sala.getNombre() + " ).");
            enviarMensaje("La Sala a la que te intentas unir esta llena, intentalo de nuevo más tarde.");
            
            // Lo elimina de la Sala
            sala.removeUser(this);
            sala = null;
        
            try {

                sleep(10000);
            } catch (InterruptedException ex) {}

            // Llama al "Run" del Thread recursivamente hasta que un usuario se desconecte y este pueda conectarse
            run();
        }
    }
    
    // Cierro la conexión con el cliente
    public boolean closeConnect() {
        
        // Envia un mensaje Global del Servidor en el que muestra el Usuario que se ha desconectado
        sala.GlobalMensaje(this, this.userName + " ha dejado el Sala", false);
            
        // Si al desconectarse no hay más usuarios en la Sala, comunica que no hay nadie conectado
        if (sala.getUsuarios().isEmpty()) {
            
            sala.GlobalMensaje(this, "Ningún cliente conectado", false);
        }
            
        // Lo elimina de la Sala
        sala.removeUser(this);
        sala = null;
        
        // Cierra el Socket del Cliente
        try {
            socket.close();
            this.finalize();
            
        } catch (Exception ex) {} catch (Throwable ex) {}
        
        return true;
    }

    // Recibe y devuelve los mensajes leidos
    public String recibirMensaje() {

        mensaje = new byte[2000];

        try {
            is.read(mensaje);
            
        } catch (IOException ex) {}

        return new String(mensaje).split("#")[0];
    }

    // Envia el mensaje al Usuario 
    public void enviarMensaje(String msg) {

        try {
            os.flush();

            // Envia el mensaje al Cliente
            os.write(msg.getBytes());

        } catch (IOException ex) {}
    }

    // Get's y Set's
    public Socket getSocket() {
        return socket;
    }

    public String getUserName() {
        return userName;
    }
}
