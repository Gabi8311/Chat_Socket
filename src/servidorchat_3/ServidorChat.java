package servidorchat_3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Servidor para el chat.
 *
 * @author Gabriel Moreno
 */
public class ServidorChat {

    public static void main(String[] args) {

        ArrayList<String> usuarios_dentro = new ArrayList<>();
        ArrayList<String> grupo = new ArrayList<>();

        System.out.println("-------------Soy Servidor Chat------------------");

        // Carga el archivo de configuracion de log4J
        int puerto = 1234;
        int maximoConexiones = 10; // Maximo de conexiones simultaneas
        ServerSocket servidor = null;
        Socket socket = null;
        MensajeChat mensajes = new MensajeChat();

        try {
            // Se crea el serverSocket
            servidor = new ServerSocket(puerto, maximoConexiones);
            // Bucle infinito para esperar conexiones
            while (true) {
                System.out.println("Servidor a la espera de conexiones.");

                socket = servidor.accept();
                System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");
                ConexionCliente cc = new ConexionCliente(socket, mensajes, usuarios_dentro, grupo);///Aqui carga el chat y le manda la información al Servidor

                cc.start();

            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        } finally {
            try {
                socket.close();
                servidor.close();
            } catch (IOException ex) {
                System.out.println("Error al cerrar el servidor: " + ex.getMessage());
            }
        }
    }
}
