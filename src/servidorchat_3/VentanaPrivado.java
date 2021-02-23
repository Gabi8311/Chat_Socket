package servidorchat_3;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

/**
 * Una sencilla ventana para configurar el chat
 *
 * @author Gabriel Moreno
 */
public class VentanaPrivado extends JDialog {

    private JTextField tfUsuarioPrivado;
    private JTextField tfMensajePrivado;
    private DataOutputStream salidaDatos;
    private Socket socket;

    /**
     * Constructor de la ventana de configuracion inicial
     *
     * @param padre Ventana padre
     */
    public VentanaPrivado() {

        JLabel lbUsuarioPrivado = new JLabel("Usuario Privado:");
        tfUsuarioPrivado = new JTextField();

        JLabel lbMensajePrivado = new JLabel("Mensaje Privado:");
        tfMensajePrivado = new JTextField();

        JButton btAceptar = new JButton("Aceptar");
        btAceptar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Se crea el socket para conectar con el Sevidor del Chat
                    socket = new Socket("localhost", 1234);

                    String usuario_intro = tfUsuarioPrivado.getText();
                    String mensaje_intro = tfMensajePrivado.getText();

                    salidaDatos = new DataOutputStream(socket.getOutputStream());

                    salidaDatos.writeUTF("Tienes_un_privado=" + usuario_intro + ": =");///Aqui se lleva el String con el nombre

                    salidaDatos.writeUTF(mensaje_intro);///Aqui se lleva el String con el mensaje

                    setVisible(false);

                } catch (UnknownHostException ex) {
                    System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
                } catch (IOException ex) {
                    System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
                }
            }
        });

        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(20, 20, 0, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        c.add(lbUsuarioPrivado, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(lbMensajePrivado, gbc);

        gbc.ipadx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 1;
        gbc.gridy = 0;
        c.add(tfUsuarioPrivado, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        c.add(tfMensajePrivado, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        c.add(btAceptar, gbc);

        this.pack(); // Le da a la ventana el minimo tamaño posible
        this.setLocation(500, 200); // Posicion de la ventana
        this.setResizable(false); // Evita que se pueda estirar la ventana
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Deshabilita el boton de cierre de la ventana 
        this.setVisible(true);

    }

    public String getUsuarioPrivado() {
        return this.tfUsuarioPrivado.getText();
    }

//    public void cerrar_ventana() {
//        System.exit(0);
//    }
}
