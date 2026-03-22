package servidor.db;

import modelos.PaqueteDatos;

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
            // TIP CRÍTICO: En Java Sockets, siempre debes inicializar el ObjectOutputStream
            // primero y hacer un flush() antes de inicializar el ObjectInputStream.
            // Si no lo haces, ambos lados se quedarán bloqueados esperando.
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
                        // TODO: Aquí agregaremos la lógica para guardar el mensaje en SQLite
                        System.out.println("Mensaje a procesar...");

                        // Retransmitimos el mensaje a todos para que aparezca en sus pantallas
                        servidor.emitirATodos(paqueteRecibido);
                        break;

                    case PaqueteDatos.ACCION_ESCRIBIENDO:
                        // Si alguien está escribiendo, simplemente le avisamos a los demás
                        servidor.emitirATodos(paqueteRecibido);
                        break;

                    case PaqueteDatos.ACCION_CRUD_CREAR_USUARIO:
                        // TODO: Lógica para insertar en SQLite
                        System.out.println("Petición de crear usuario recibida.");
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