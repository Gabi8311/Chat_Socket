package servidorchat_2;

/**
 * Objeto observable del patron observer.
 *
 * @author Gabriel Moreno
 */
import java.util.ArrayList;
import java.util.Observable;

public class MensajeChat extends Observable {

    private String mensaje;
    private ArrayList<String> usuarios_dentro;
    private ArrayList<String> grupo;
    private boolean group_donne = false;

    public MensajeChat() {

        this.group_donne = false;
        usuarios_dentro = new ArrayList<String>();
        grupo = new ArrayList<String>();

    }

    public String getMensaje() {
        return mensaje;
    }

    public ArrayList<String> getUsuarios_dentro() {
        return usuarios_dentro;
    }

    public void setUsuarios_dentro(ArrayList<String> usuarios_dentro) {
        this.usuarios_dentro = usuarios_dentro;
    }

    public ArrayList<String> getGrupo() {
        return grupo;
    }

    public void setGrupo(ArrayList<String> grupo) {
        this.grupo = grupo;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
        // Indica que el mensaje ha cambiado
        this.setChanged();
        // Notifica a los observadores que el mensaje ha cambiado y se lo pasa
        // (Internamente notifyObservers llama al metodo update del observador)
        this.notifyObservers(this.getMensaje());
    }

    public boolean isGroup_donne() {
        return group_donne;
    }

    public void setGroup_donne(boolean group_donne) {
        this.group_donne = group_donne;
    }

}
