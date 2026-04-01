package Cliente;

import modelos.PaqueteDatos;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import modelos.Usuario;

public class ClienteConexion implements Runnable {
    private String ipServidor = "127.0.0.1";
    private int puerto = 9090;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private VentanaCliente ventana; // Referencia a la GUI para poder actualizarla

    public ClienteConexion(VentanaCliente ventana) {
        this.ventana = ventana;
    }

    // Metodo para que la GUI pueda enviar paquetes al servidor
    public void enviarPaquete(PaqueteDatos paquete) {
        try {
            if (out != null) {
                out.writeObject(paquete);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error al enviar paquete: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ipServidor, puerto);
            // Igual que en el servidor: primero el OutputStream y flush
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("Conectado al servidor con éxito.");

            // Bucle infinito para escuchar lo que manda el servidor
            while (true) {
                PaqueteDatos paqueteRecibido = (PaqueteDatos) in.readObject();

                // Usamos SwingUtilities porque Swing no es "Thread-Safe".
                // y esto asegura que la GUI se actualice de forma segura.
                SwingUtilities.invokeLater(() -> {
                    procesarPaqueteEntrante(paqueteRecibido);
                });
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Desconectado del servidor.");
        }
    }

    private void procesarPaqueteEntrante(PaqueteDatos paquete) {
        switch (paquete.getAccion()) {
            case "CRUD_LISTA_USUARIOS":
                // Extraemos la lista que viene en el paquete
                List<Usuario> usuarios = (List<Usuario>) paquete.getDatosExtra();
                // IMPORTANTE: Usamos SwingUtilities para actualizar la interfaz de forma segura
                SwingUtilities.invokeLater(() -> {
                ventana.actualizarListaUsuarios(usuarios);
                });
                break;
            case PaqueteDatos.ACCION_CHAT:
                // Le decimos a la ventana que dibuje el nuevo mensaje
                ventana.mostrarNuevoMensaje(paquete.getMensaje());
                break;
            case PaqueteDatos.ACCION_ESCRIBIENDO:
                // Le decimos a la ventana que active la leyenda "escribiendo..."
                ventana.mostrarEscribiendo(paquete.getUsuario().getUsername());
                String nombre = paquete.getUsuario().getUsername();
                SwingUtilities.invokeLater(() -> ventana.mostrarEscribiendo(nombre));
                break;
        }
    }
}
