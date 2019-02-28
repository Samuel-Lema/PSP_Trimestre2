package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Sala extends Thread {

    private static ArrayList<Sala> salas = new ArrayList<>(); // Lista de usuarios conectados actualmente en la Sala
    private String nombre; // Nombre de la Sala

    private ArrayList<Usuario> usuarios = new ArrayList<>(); // Lista de usuarios conectados actualmente en la Sala

    // Variables de la Interfaz
    DefaultListModel<String> model = new DefaultListModel<>();
    JTextArea chatSala = new JTextArea();
    JList listaUsuarios = new JList();
    JPanel panel = new JPanel();
    

    public Sala(String nombre) {

        // Asigno el nombre a la Sala y se autoañade a un Array Global con todas las Salas activas
        this.nombre = nombre;
        salas.add(this);

        // Genero la Sala de Chat y lista de Usuarios de la Sala en la (INTERFAZ)
        chatSala.setBounds(0, 0, 600, 360);
        panel.add(chatSala);
        listaUsuarios.setBounds(600, 0, 200, 360);
        listaUsuarios.setBackground(Color.lightGray);
        panel.add(listaUsuarios);

        panel.setLayout(null);
        Administracion.jTab.add(nombre, panel);

        // Cierra la conexión con el cliente seleccionado en la lista de Usuarios de la Sala
        Administracion.btnDrop.addActionListener((ActionEvent ae) -> {

            if (listaUsuarios.getSelectedIndex() != -1) {

                // Muestro a todos los usuarios que se ha echado a X usuario
                GlobalMensaje(null, " ### Se ha echado a " + usuarios.get(listaUsuarios.getSelectedIndex()).getUserName() + " del Servidor ###", false);
                // Cierro la conexión con ese usuario
                usuarios.get(listaUsuarios.getSelectedIndex()).closeConnect();
            }
        });
    }
    
    synchronized public void userOption(Usuario user, int opciones) {

        switch(opciones){
            case 0: usuarios.add(user); break; // Añado al usuario a la sala
            case 1: usuarios.remove(user); break; // Elimino al usuario de la sala
            default: break;
        }
        
        // Refresco la lista de usuario de la Sala
        model.clear();

        for (Usuario usuario : usuarios) {

            model.addElement(usuario.getUserName());
        }

        listaUsuarios.setModel(model);
    }

    synchronized public void añadirMensaje(Usuario user, String msg) {

        // Muestra el mensaje enviado por un usuario en la sala de chat
        chatSala.append(user.getUserName() + ": " + msg + "\n");

        // Envia el mensaje recibido a todos los usuarios de la sala
        for (Usuario usuario : usuarios) {

            // Envio el mensaje 'Nombre User: Mensaje del User'
            usuario.enviarMensaje(user.getUserName() + ": " + msg);
        }
    }

    synchronized public void GlobalMensaje(Usuario user, String msg, Boolean modificado) {

        // Muestra el mensaje gestionado por el servidor en la sala de chat
        chatSala.append(msg + "\n");

        // Envia el mensaje a todos los usuarios de la sala
        for (Usuario usuario : usuarios) {

            if (modificado && usuario.equals(user)) {

                // Envio el mensaje 'Conectado a la Sala de Chat' para avisar a un Usuario especifico
                usuario.enviarMensaje("Conectado a la Sala de Chat");

            } else {

                // Envio el mensaje 'Servidor: Mensaje del Servidor' para avisar a usuarios de avisos genericos
                usuario.enviarMensaje("Servidor: " + msg);
            }
        }
    }

    // Get's
    public ArrayList<Usuario> getUsuarios() {

        return usuarios;
    }

    public static ArrayList<Sala> getSalas() {

        return salas;
    }

    public String getNombre() {

        return nombre;
    }
}