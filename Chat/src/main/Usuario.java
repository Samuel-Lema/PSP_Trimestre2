package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    DataInputStream tamañoByte;
    DataOutputStream escribirByte;
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

        // Entra en un bucle de comunicación con el cliente hasta que se cierre la conexión
        boolean closeClient = false;
        
        do {
            // Llamo a la funcion 'recibirMensaje()' que me devuelve un mensaje enviado por este cliente y lo guardo
            String mensaje = recibirMensaje();
                    
            // Si se ha cerrado la conexión que salga del bucle de comunicación
            if (socket.isClosed() == true) {
                
                 break;
            }
            
            if (mensaje.equals("/bye")) { // Si recibe un /bye el cual debera cerrar la comunicación con el cliente

                closeClient = closeConnect();

            } else if(mensaje.contains("/sala")){ // Si recibe un /sala 'Nombre Sala' cambiara a la Sala con ese nuevo nombre
            
                changeSala(mensaje.split("/sala ")[1]);
                 
            } else if(mensaje.contains("/info")){ // Si recibe un /info se le pasa al cliente info de todas las salas
            
                String nombresSalas = "SALAS";
                
                for (Sala s: Sala.getSalas()) {
                    
                    nombresSalas += " | " + s.getNombre();
                }
                
                enviarMensaje(nombresSalas);
                
            } else { // Si el mensaje recibido ningun comando definido muestra el mensaje en la Sala
                
                sala.añadirMensaje(this, mensaje);
            }

        } while (closeClient == false);
    }
    
    public void changeSala(String nombreS){
        
        // Busca o crea una Sala y se autoañade a ella
        boolean salaEncontrada = false;
        
        String nombreSala;
        
        if (nombreS == null) { // Si no me pasa un nombre de sala predefinido se lo pregunto
            
            nombreSala = recibirMensaje();
        } else {  // Si me pasa un nombre de sala predefinido lo gestiono y lo elimino de la sala anterior en caso de que tuviese una asignada
            
            nombreSala = nombreS;
            sala.userOption(this, 1);
        }
        
        for (Sala x: Sala.getSalas()) { // Recorre las salas para comprobar si existe o no la sala que me envia
            
            if (x.getNombre().equals(nombreSala)) { // Si la encuentra la sala, se la asigno
                
                salaEncontrada = true;
                sala = x;
                sala.userOption(this, 0);
                break;
            }
        }
        
        if (salaEncontrada == false) { // Si no encuentra la sala, la genero
            
            sala = new Sala(nombreSala);
            sala.userOption(this, 0);
        }
        
        // Gestiona que no se conecten más de 10 usuarios simultaneamente
        if (sala.getUsuarios().size() > 10) {

            System.out.println("El usuario ( " + userName + " ) esta a la espera en la sala ( " + sala.getNombre() + " ).");
            enviarMensaje("La Sala a la que te intentas unir esta llena, intentalo de nuevo más tarde.");
            
            // Lo elimina de la Sala
            sala.userOption(this, 1);
            sala = null;
        
            try {

                sleep(1000);
            } catch (InterruptedException ex) {}

            // Llama al "Run" del Thread recursivamente hasta que un usuario se desconecte y este pueda conectarse
            run();
        }
        
        // Envia un mensaje Global del Servidor en el que muestra datos del nuevo Usuario conectado
        // Le paso (El usuario mismo || el mensaje || si quiero que se envie modificado a este usuario en especifico)
        sala.GlobalMensaje(this, "Nuevo usuario conectado ( " + userName + " / " + socket.getInetAddress().getHostAddress() + " / " + socket.getPort() + " )", true);

        // Envia un mensaje Global del Servidor en el que muestra los Usuarios conectados actualmente
        // Le paso (El usuario mismo || el mensaje || si quiero que se envie modificado a este usuario en especifico)
        sala.GlobalMensaje(this, "Actualmente hay ( " + sala.getUsuarios().size() + " ) Usuarios conectados a ( " + sala.getNombre() + " )", false);
        
        // Le envio los nombres de usuarios de la sala al cliente para que el los gestione a su parecer
        String nombresUsuario = "USUARIOS";
                
        for (Usuario nombre: sala.getUsuarios()) {
                    
            nombresUsuario += " | " + nombre.getUserName();
        }
                
        enviarMensaje(nombresUsuario);
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
        sala.userOption(this, 1);
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

        tamañoByte = new DataInputStream(is);
        
        try {
            mensaje = new byte[tamañoByte.readInt()];
            is.read(mensaje);
            
        } catch (IOException ex) {}

        return new String(mensaje).split("#")[0];
    }

    // Envia el mensaje al Usuario 
    public void enviarMensaje(String msg) {

        escribirByte = new DataOutputStream(os);
         
        try {
            // Envia el mensaje al Cliente
            escribirByte.writeInt(msg.getBytes().length);
            escribirByte.write(msg.getBytes());

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