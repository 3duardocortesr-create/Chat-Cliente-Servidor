package servidor.db;

import modelos.Mensaje;
import modelos.PaqueteDatos;
import modelos.Usuario;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private ServidorPrincipal servidor;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // El constructor recibe el socket del cliente y una referencia al servidor principal
    public ManejadorCliente(Socket socket, ServidorPrincipal servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            // En Java Sockets, siempre se debe inicializar el ObjectOutputStream
            // primero y hacer un flush() antes de inicializar el ObjectInputStream.
            // Si no, ambos lados se quedarán bloqueados esperando.
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // Avisamos al servidor que guarde este canal de salida
            servidor.registrarCliente(out);

            // Bucle infinito para escuchar peticiones de este cliente
            while (true) {
                // readObject() bloquea el hilo hasta que recibe un paquete
                PaqueteDatos paqueteRecibido = (PaqueteDatos) in.readObject();

                // Decidimos qué hacer basándonos en la acción del paquete
                switch (paqueteRecibido.getAccion()) {
                    case PaqueteDatos.ACCION_CHAT:
                        MensajeDAO daoMensaje = new MensajeDAO();
                        Mensaje nuevoMensaje = paqueteRecibido.getMensaje();
                        boolean mensajeGuardado = daoMensaje.insertarMensaje(nuevoMensaje);
                        if (mensajeGuardado){
                            System.out.println("Mensaje guardado correctamente en la BD.");
                        } else {
                            System.err.println("No se pudo guardar el mensaje en la BD.");
                        }
                        // Retransmitimos el mensaje a todos para que aparezca en sus pantallas
                        servidor.emitirATodos(paqueteRecibido);
                        break;

                    case PaqueteDatos.ACCION_ESCRIBIENDO:
                        // Si alguien está escribiendo, simplemente le avisamos a los demás
                        servidor.emitirATodos(paqueteRecibido);
                        break;

                    case PaqueteDatos.ACCION_CRUD_CREAR_USUARIO:
                        UsuarioDAO daoUsuario = new UsuarioDAO();
                        //Se extrae el usuario que viene dentro del paquete
                        Usuario nuevoUsuario = paqueteRecibido.getUsuario();
                        boolean exito = daoUsuario.insertarUsuario(nuevoUsuario);
                        if (exito) {
                            System.out.println("Usuario " + nuevoUsuario.getUsername() + "registrado en BD. ");
                        } else {
                            System.out.println("Fallo el registro de Usuario");
                        }
                        break;

                    // Aquí irán los demás casos del CRUD (Eliminar, Modificar, Consultar)
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Un cliente se ha desconectado de forma inesperada.");
        } finally {
            // Pase lo que pase, si el cliente se va, limpiamos la memoria y cerramos el socket
            servidor.removerCliente(out);
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }
}