package servidorchat_2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JTextField;

/**
 * Esta clase gestiona el envio de datos entre el cliente y el servidor.
 *
 * @author Gabriel Moreno
 */
public class ConexionServidor implements ActionListener {   //Esto va 4º

    private Socket socket;
    private JTextField tfMensaje;
    private String usuario;
    private DataOutputStream salidaDatos;

    public ConexionServidor(Socket socket, JTextField tfMensaje, String usuario) {
        this.socket = socket;
        this.tfMensaje = tfMensaje;
        this.usuario = usuario;

        System.out.println("--------------Soy ConexionServidor-----------------");

        try {

            salidaDatos = new DataOutputStream(socket.getOutputStream());
            salidaDatos.writeUTF(usuario + ": " + tfMensaje.getText());

        } catch (IOException ex) {
            System.out.println("Error al crear el stream de salida : " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("El socket no se creo correctamente. ");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            salidaDatos.writeUTF(usuario + ": " + tfMensaje.getText());
            tfMensaje.setText("");
        } catch (IOException ex) {
            System.out.println("Error al intentar enviar un mensaje: " + ex.getMessage());
        }
    }
}
