package servidorchat_2;

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

    public ConexionCliente(Socket socket, MensajeChat mensajes, ArrayList<String> usuarios_dentro, ArrayList<String> grupo) {

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

            //Salta la ventana que le dice al usuario que no se puede meter dos usuarios iguales
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
            //Si no añadimos el usuario a ArrayList de Mensaje
            mensajes.getUsuarios_dentro().add(usuario);
            //Vemos los usuarios que hay en el ArrayList de Mensaje
            for (String user : mensajes.getUsuarios_dentro()) {
                System.out.println("Usuario -> " + user);
            }

            while (conectado) {
                try {
                    // Lee un mensaje enviado por el cliente
                    mensajeRecibido = entradaDatos.readUTF();

                    //Hago un Split a todas los mensajes que entran ,si el [0],es "z",añado cada posición al ArrayList
                    String[] spliteado = mensajeRecibido.split("-");
                    if (spliteado[0].equals("z")) {

                        //Voy añadiendo al grupo de mensajes los String spliteados
                        for (int i = 1; i < spliteado.length; i++) {
                            mensajes.getGrupo().add(spliteado[i]);
                        }

                        //Añado al creador del grupo
                        mensajes.getGrupo().add(this.usuario);
                    }

                    //Esto sería que el grupo está creado
                    if (mensajes.getGrupo().size() > 1) {
                        mensajes.setGroup_donne(true);
                    }

                    for (String s : mensajes.getGrupo()) {
                        System.out.println(s);
                    }

                    mensajes.setMensaje(mensajeRecibido);//Llamo al update

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
    public void update(Observable o, Object arg) {////Clave

        try {

            if (mensajes.isGroup_donne()) {
                if (mensajes.getGrupo().contains(this.usuario)) {

                    salidaDatos.writeUTF(arg.toString());
                } else {
                    salidaDatos.writeUTF("");
                }
                // Envia el mensaje al cliente          
                //Esta función llama a toods los usuarios

            } else {

                salidaDatos.writeUTF(arg.toString());
            }

        } catch (IOException ex) {
            System.out.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
}
