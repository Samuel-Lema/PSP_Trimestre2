package main;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Sala extends Thread {

    // Lista de usuarios conectados actualmente en la Sala
    private static ArrayList<Sala> salas = new ArrayList<>();
    private String nombre;

    // Lista de usuarios conectados actualmente en la Sala
    private ArrayList<Usuario> usuarios = new ArrayList<>();

    // Variables de la Interfaz
    JTextArea txtSala = new JTextArea();
    JList lista = new JList();
    JPanel panel = new JPanel();

    DefaultListModel<String> model = new DefaultListModel<>();

    public Sala(String nombre) {

        this.nombre = nombre;
        salas.add(this);

        txtSala.setBounds(0, 0, 600, 360);
        panel.add(txtSala);
        lista.setBounds(600, 0, 200, 360);
        lista.setBackground(Color.lightGray);
        panel.add(lista);

        panel.setLayout(null);
        Administracion.jTab.add(nombre, panel);

        // Cierra la conexión con el cliente seleccionado
        Administracion.btnDrop.addActionListener((ActionEvent ae) -> {

            if (lista.getSelectedIndex() != -1) {

                GlobalMensaje(null, " ### Se ha echado a " + usuarios.get(lista.getSelectedIndex()).getUserName() + " del Servidor ###", false);
                usuarios.get(lista.getSelectedIndex()).closeConnect();
            }
        });
    }

    synchronized public void addUser(Usuario user) {

        usuarios.add(user);

        model.clear();

        for (Usuario usuario : usuarios) {

            model.addElement(usuario.getUserName());
        }

        lista.setModel(model);
    }

    synchronized public void removeUser(Usuario user) {

        usuarios.remove(user);

        model.clear();

        for (Usuario usuario : usuarios) {

            model.addElement(usuario.getUserName());
        }

        lista.setModel(model);
    }

    synchronized public void añadirMensaje(Usuario user, String msg) {

        txtSala.append(user.getUserName() + ": " + msg + "\n");

        // Recorre todos los usuarios de la sala para enviar los mensajes en la sala a cada uno
        for (Usuario usuario : usuarios) {

            // Envio el mensaje 'Nombre Usuario: Mensaje del Usuario'
            usuario.enviarMensaje(user.getUserName() + ": " + msg);
        }
    }

    synchronized public void GlobalMensaje(Usuario user, String msg, Boolean modificado) {

        txtSala.append(msg + "\n");

        // Recorre todos los usuarios de la sala para enviar los mensajes en la sala a cada uno
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
