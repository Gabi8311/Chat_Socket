package servidorchat_3;

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
public class ConexionCliente extends Thread implements Observer {//Esto va 3º

    private Socket socket;
    private MensajeChat mensajes;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private String usuario;
    private JOptionPane jop;
    private String privado = "";
    private String aux;

    public ConexionCliente(Socket socket, MensajeChat mensajes, ArrayList<String> usuarios_dentro, ArrayList<String> privado) {

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

        // Se apunta a la lista de observadores de mensajes
        mensajes.addObserver(this);

        try {
            // Lee un mensaje enviado por el cliente
            this.usuario = entradaDatos.readUTF(); //Recibe la información del usuario y se la asigna al usuario que llama al run

        } catch (IOException ex1) {
            System.out.println("Error al leer el Usuario --> " + ex1.getMessage());
        }

        if (mensajes.getUsuarios_dentro().contains(usuario)) {

            //salta la ventana que le dice al usuario que no se puede meter dos usuarios iguales
            jop.showMessageDialog(null, "No puede haber usuarios repetidos",
                    "ERROR_MESSAGE", JOptionPane.WARNING_MESSAGE);

            try {
                entradaDatos.close();
                salidaDatos.close();
            } catch (IOException ex2) {
                System.out.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
            }

            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            //Miro que no tenga al principio "Tienes_un_privado" y si no lo tiene,lo add
            String[] spliteado2 = this.usuario.split("=");
            if (!spliteado2[0].equals("Tienes_un_privado")) {

                //Si no añadimos el usuario a ArrayList de Mensaje
                mensajes.getUsuarios_dentro().add(usuario);
            }

            //Vemos los usuarios que hay en el ArrayList de Mensaje
            for (String user : mensajes.getUsuarios_dentro()) {
                System.out.println("Usuario -> " + user);
            }

            while (conectado) {
                try {
                    // Lee un mensaje enviado por el cliente
                    mensajeRecibido = entradaDatos.readUTF();

                    if (spliteado2[0].equals("Tienes_un_privado")) {
                        mensajes.setMensaje(this.usuario + mensajeRecibido);//Llamo al update

                    } else {
                        mensajes.setMensaje(mensajeRecibido);//Llamo al update
                    }

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
    public void update(Observable o, Object arg) {////Clave,esta función llama a toods los usuarios

        try {
            String[] spliteado = arg.toString().split("=");

            //Miro que spliteado[0] sea Tienes_un_privado
            if (spliteado[0].equals("Tienes_un_privado")) {

                privado = spliteado[1];

                //Miro que ell usuario al que se transmite el mensaje sea igual que privado
                if (this.usuario.equals(privado)) {//privado ==usuario
                    salidaDatos.writeUTF(spliteado[2]);
                }
            } else {
                salidaDatos.writeUTF(arg.toString());
            }

            // Envia el mensaje al cliente         
        } catch (IOException ex) {
            System.out.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
}
