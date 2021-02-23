package servidorchat_1;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Clase principal del cliente del chat
 *
 * @author Gabriel Moreno
 */
public class ClienteChat extends JFrame {

    private JTextArea mensajesChat;
    private Socket socket;
    private JOptionPane jop;
    private int puerto;
    private String host;
    private String usuario;
    private int num;
    private ArrayList<String> nombres_admitidos = new ArrayList<>();
    private MensajeChat usuarios_grupo;
    private DataOutputStream salidaDatos;

    public ArrayList<String> getNombres_admitidos() {
        return nombres_admitidos;
    }

    public void setNombres_admitidos(ArrayList<String> nombres_admitidos) {
        this.nombres_admitidos = nombres_admitidos;
    }

    public ClienteChat() {
        super("Cliente Chat");

        // Elementos de la ventana
        mensajesChat = new JTextArea();
        mensajesChat.setEnabled(false); // El area de mensajes del chat no se debe de poder editar
        mensajesChat.setLineWrap(true); // Las lineas se parten al llegar al ancho del textArea
        mensajesChat.setWrapStyleWord(true); // Las lineas se parten entre palabras (por los espacios blancos)
        JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
        JTextField tfMensaje = new JTextField("");
        JButton btEnviar = new JButton("Enviar");
        JButton btGrupo = new JButton("Crear Grupo");

        // Colocacion de los componentes en la ventana
        Container c = this.getContentPane();
        c.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(20, 20, 20, 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        c.add(scrollMensajesChat, gbc);
        // Restaura valores por defecto
        gbc.gridwidth = 1;
        gbc.weighty = 0;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 20, 20);

        gbc.gridx = 0;
        gbc.gridy = 1;
        c.add(tfMensaje, gbc);
        // Restaura valores por defecto
        gbc.weightx = 0;

        gbc.gridx = 1;
        gbc.gridy = 1;
        c.add(btEnviar, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        c.add(btGrupo, gbc);

        this.setBounds(400, 100, 400, 500);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ventana de configuracion inicial
        VentanaConfiguracion vc = new VentanaConfiguracion(this);
        host = vc.getHost();
        puerto = vc.getPuerto();
        usuario = vc.getUsuario();
        System.out.println("----------Soy ClienteChat-------------");

        System.out.println("Quieres conectarte a " + host + " en el puerto " + puerto + " con el nombre de ususario: " + usuario + ".");

        //Los Usuarios que pueden acceder al chat General
        ArrayList<String> usuarios = new ArrayList<>(Arrays.asList("g", "M", "D", "N"));

        //Informamos al Usuario de que no puede acceder al chat
        if (!usuarios.contains(usuario)) {
            jop.showMessageDialog(null, "Este usuario NO puede acceder al chat",
                    "ERROR_MESSAGE", JOptionPane.WARNING_MESSAGE);
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;

        } else {

            try {

                // Se crea el socket para conectar con el Sevidor del Chat
                socket = new Socket(host, puerto);
            } catch (UnknownHostException ex) {
                System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
            } catch (IOException ex) {
                System.out.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
            }
            // Accion para el boton enviar
            btEnviar.addActionListener(new ConexionServidor(socket, tfMensaje, usuario));
        }
        btGrupo.addActionListener(new ActionListener() {
            //Creo las ventanas para pedir el número y nombres de los Usuarios del Grupo
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    setVisible(false);
                    Object participantes = jop.showInputDialog(null, "Seleccione número de participantes",
                            "Participantes", jop.QUESTION_MESSAGE, null,
                            new Object[]{"Seleccione", "2", "3", "4"}, "Seleccione");

                    num = Integer.parseInt(participantes.toString());
                    for (int i = 0; i < num; i++) {
                        nombres_admitidos.add(jop.showInputDialog("Ingresa el usuario Nº" + (i + 1)));
                    }

                    if (nombres_admitidos.size() > 0) {
                        usuarios_grupo = new MensajeChat();
                        for (int i = 0; i < nombres_admitidos.size(); i++) {
                            usuarios_grupo.getUsuarios_dentro().add(nombres_admitidos.get(i));
                        }
                    }
                    salidaDatos = new DataOutputStream(socket.getOutputStream());
                } catch (IOException ex) {
                    Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * Recibe los mensajes del chat reenviados por el servidor
     */
    public void recibirMensajesServidor() {
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;

        try {
            entradaDatos = new DataInputStream(socket.getInputStream());

        } catch (IOException ex) {
            System.out.println("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException exNull) {
            System.out.println("El socket no se creo correctamente. ");
        }

        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();

                mensajesChat.append(mensaje + System.lineSeparator());
            } catch (IOException ex2) {
                System.out.println("Error al leer del stream de entrada: " + ex2.getMessage());
                conectado = false;
            } catch (NullPointerException ex3) {
                System.out.println("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }

    public void enviarMensajesServidor() {
        // Obtiene el flujo de entrada del socket
        DataInputStream entradaDatos = null;
        String mensaje;
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Error al crear el stream de entrada: " + ex.getMessage());
        } catch (NullPointerException ex) {
            System.out.println("El socket no se creo correctamente. ");
        }

        // Bucle infinito que recibe mensajes del servidor
        boolean conectado = true;
        while (conectado) {
            try {
                mensaje = entradaDatos.readUTF();

                System.out.println("Lo que leemos del mensaje" + mensaje);
                mensajesChat.append(mensaje + System.lineSeparator());
            } catch (IOException ex) {
                System.out.println("Error al leer del stream de entrada: " + ex.getMessage());
                conectado = false;
            } catch (NullPointerException ex) {
                System.out.println("El socket no se creo correctamente. ");
                conectado = false;
            }
        }
    }

    public static void main(String[] args) {
        // Carga el archivo de configuracion de log4J

        ClienteChat c = new ClienteChat();
        c.recibirMensajesServidor();
    }

}
