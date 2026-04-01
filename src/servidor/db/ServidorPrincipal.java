package servidor.db;

import modelos.PaqueteDatos;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class
ServidorPrincipal {
    private final int PUERTO = 9090;
    // Lista para guardar los canales de salida hacia todos los clientes
    private List<ObjectOutputStream> clientesConectados = new ArrayList<>();

    public void iniciarServidor() {
        // Prepara la Base de Datos antes de aceptar conexiones
        ConexionDB.getInstancia();
        InicializadorDB.crearTablas();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO + ". Esperando clientes...");

            // Bucle infinito para aceptar conexiones concurrentes
            while (true) {
                // El hilo principal se pausa aquí hasta que un cliente se conecta
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado desde: " + socketCliente.getInetAddress());

                // Delegamos la atención de este cliente a un nuevo Hilo
                ManejadorCliente manejador = new ManejadorCliente(socketCliente, this);
                Thread hiloCliente = new Thread(manejador);
                hiloCliente.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    // Usamos synchronized para evitar que dos hilos modifiquen la lista al mismo tiempo
    public synchronized void registrarCliente(ObjectOutputStream out) {
        clientesConectados.add(out);
    }

    public synchronized void removerCliente(ObjectOutputStream out) {
        clientesConectados.remove(out);
    }

    // Metodo para retransmitir un paquete a todos los clientes conectados
    public synchronized void emitirATodos(PaqueteDatos paquete) {
        for (ObjectOutputStream out : clientesConectados) {
            try {
                out.writeObject(paquete);
                out.flush(); // Obliga a que los datos viajen por la red inmediatamente
            } catch (IOException e) {
                System.err.println("Error al retransmitir paquete a un cliente.");
            }
        }
    }

    public static void main(String[] args) {
        new ServidorPrincipal().iniciarServidor();
    }
}