package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer extends Thread {

    private ArrayList<Integer> numeros = new ArrayList<>();
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    byte[] mensaje;

    public SocketServer(Socket socket) {

        this.socket = socket;
        
        // Llama al "Run" del Thread para que varios clientes puedan conectarse
        this.start();
    }
    
    @Override public void run() {
        
        try {
            // Acepta lectura y escritura
            System.out.println("Servidor: Conexión recibida");
            is = socket.getInputStream();
            os = socket.getOutputStream();
            
            boolean closeClient = false;
            
            do {
                // Recibe y lee los mensajes
                mensaje = new byte[2000];
                is.read(mensaje);

                System.out.println("Servidor (Operación Recibida por " + socket.getPort() + "): " + new String(mensaje).split("#")[0]);

                if (new String(mensaje).split("#")[0].equals("OFF")) {
                    
                    closeClient = true;
                    socket.close();
                } else {
                
                // Llama a la función splitear que gestiona el orden de la operación para su correcto procesado
                    splitear(new String(mensaje).split("#")[0]);
                }
                
            } while(closeClient == false);
            
        } catch (IOException ex) {}
    }

    // Splitea el mensaje pasado por el cliente para gestionar las prioridades de la operación y los guarda en un array
    synchronized public void splitear(String mensaje) {

        ArrayList<String> array = new ArrayList<>();

        for (String linea : mensaje.split("G")) {

            array.add(linea);
        }

        // Llama a la función 'operar' para comenzar el calculo de operación
        operar(array);
    }

    // Gestiona el calculo de la operación pasada en un Array para facilitar el proceso
    synchronized public void operar(ArrayList<String> operaciones) {

        // Lista de Prioridades
        // ----------------------------------
        // 1º Raiz Cuadrada
        // 2º Potencias
        // 3º Multiplicación y División (Left to Right)
        // 4º Suma y Resta (Left to Right)
        
        boolean existeOperacion = true;

        // 1º Prioridad, las operaciónes de la Raíz cuadrada
        
        do {

            existeOperacion = gestorOperandus("\u221A", operaciones);

        } while (existeOperacion);

        // 2º Prioridad, las operaciónes de la Potencia
        
        do {

            existeOperacion = gestorOperandus("*", operaciones);

        } while (existeOperacion);

        // 3º Prioridad, las operaciónes de la Multiplicación y División
        
        do {

            existeOperacion = gestorOperandus("x/", operaciones);

        } while (existeOperacion);

        // 4º Prioridad, las operaciones de la Suma y Resta
        
        do {

            existeOperacion = gestorOperandus("+-", operaciones);

        } while (existeOperacion);

        System.out.println(""); // Añade una linea vacia en el debug para mejor visualización

        try {
            // Devuelve el resultado al cliente
            System.out.println("Servidor (Operación Enviada): " + operaciones.get(0));
            os.write(operaciones.get(0).getBytes());

        } catch (IOException ex) {}
    }

    // Gestor de Operaciones (Formar una funcion recursiva de si misma)
    synchronized public boolean gestorOperandus(String operador, ArrayList<String> operaciones) {

        int posicion;

        // Guarda si existe la operación que se ha pasado por parametro
        boolean existePosicion = false;

        System.out.println("OPERANDUS (" + operador + ")");
        
        for (String operacion : operaciones) {

            posicion = operaciones.indexOf(operacion);

            if (operador.equals("\u221A") && operacion.equals("\u221A")) { // Raíz cuadrada

                operaciones.set(posicion, castDouble(Math.sqrt(getValue(operaciones, posicion + 1))));
                operaciones.remove(posicion + 1);
                existePosicion = true; break;
            }

            if (operador.equals("*") && operacion.equals("*")) { // Potencia

                operaciones.set(posicion - 1, castDouble(Math.pow(getValue(operaciones, posicion - 1), getValue(operaciones, posicion + 1))));
                operaciones.remove(posicion);
                operaciones.remove(posicion);
                existePosicion = true; break;
            }

            if (operador.equals("x/")) { // Multiplicación y División

                if (operacion.equals("x")) {

                    operaciones.set(posicion - 1, castDouble(getValue(operaciones, posicion - 1) * getValue(operaciones, posicion + 1)));
                    operaciones.remove(posicion);
                    operaciones.remove(posicion);
                    existePosicion = true; break;

                } else if (operacion.equals("/")) {

                    operaciones.set(posicion - 1, castDouble(getValue(operaciones, posicion - 1) / getValue(operaciones, posicion + 1)));
                    operaciones.remove(posicion);
                    operaciones.remove(posicion);
                    existePosicion = true; break;
                }
            }

            if (operador.equals("+-")) { // Suma y Resta

                if (operacion.equals("+")) {

                    operaciones.set(posicion - 1, castDouble(getValue(operaciones, posicion - 1) + getValue(operaciones, posicion + 1)));
                    operaciones.remove(posicion);
                    operaciones.remove(posicion);
                    existePosicion = true; break;

                } else if (operacion.equals("-")) {

                    operaciones.set(posicion - 1, castDouble(getValue(operaciones, posicion - 1) - getValue(operaciones, posicion + 1)));
                    operaciones.remove(posicion);
                    operaciones.remove(posicion);
                    existePosicion = true; break;
                }
            }
        }

        // DEBUG VISUAL POR CONSOLA
        
        System.out.print("EJECUTANDO OPERACIÓN ( " + operador + " ): ");

        for (String operacion : operaciones) {

            System.out.print(operacion + " -- ");
        }

        System.out.println("");

        // Devuelve si el operador que se le ha pasado existía, pasa saber si hay que seguir ejecutando ese mismo operador (Que tiene mayor prioridad que otros)
        return existePosicion;
    }

    // Funciones para Casteo rápido
    public double castString(String numero) {

        return Double.valueOf(numero);
    }

    public String castDouble(double numero) {

        return String.valueOf(numero);
    }

    public Double getValue(ArrayList<String> array, int posicion) {

        return castString(array.get(posicion));
    }
}
