package servidorchat_1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Esta clase gestiona el envio de datos entre el servidor y el cliente al que
 * atiende.
 *
 * @author Gabriel Moreno
 */
public class ConexionCliente extends Thread implements Observer {

    private Socket socket;
    private MensajeChat mensajes;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private String usuario;
    private JOptionPane jop;

    public ConexionCliente(Socket socket, MensajeChat mensajes, ArrayList<String> usuarios_dentro) {

        this.socket = socket;
        this.mensajes = mensajes;

        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
            salidaDatos = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            System.out.println("Error al crear los stream de entrada y salida : " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("--------------Soy ConexionCliente---------------");

        String mensajeRecibido;
        boolean conectado = true;

        try {

            // Lee un mensaje enviado por el cliente
            this.usuario = entradaDatos.readUTF(); //Recibe la información del cliente
            
            // Se apunta a la lista de observadores de mensajes
            mensajes.addObserver(this);

        } catch (IOException ex1) {
            System.out.println("Error al leer el Usuario --> " + ex1.getMessage());
        }

        if (mensajes.getUsuarios_dentro().contains(usuario)) {
            System.out.println("El Usuario ya está dentro y conectado");
            try {
                entradaDatos.close();
                salidaDatos.close();
            } catch (IOException ex2) {
                System.out.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
            }
            jop.showMessageDialog(null, "No puede haber usuarios repetidos",
                    "ERROR_MESSAGE", JOptionPane.WARNING_MESSAGE);
            mensajes.deleteObserver(this);
            
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
                
        } else {
            //Añadimos al Usuario a Usuarios_dento de mensajes
            mensajes.getUsuarios_dentro().add(usuario);

            for (String user : mensajes.getUsuarios_dentro()) {
                System.out.println("Usuario -> " + user);
            }

            while (conectado) {
                try {
                    // Lee un mensaje enviado por el cliente
                    mensajeRecibido = entradaDatos.readUTF();
                    
                    // Pone el mensaje recibido en mensajes para que se notifique 
                    // a sus observadores que hay un nuevo mensaje.
                    mensajes.setMensaje(mensajeRecibido);

                } catch (IOException ex) {
                    System.out.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
                    conectado = false;
                    
                    // Si se ha producido un error al recibir datos del cliente se cierra la conexion con el.
                    try {
                        entradaDatos.close();
                        salidaDatos.close();
                    } catch (IOException ex2) {
                        System.out.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg
    ) {
        try {
            // Envia el mensaje al cliente
            salidaDatos.writeUTF(arg.toString());
        } catch (IOException ex) {
            System.out.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
 
}
