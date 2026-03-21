package servidor.db;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class InicializadorDB {
    public static void crearTablas(){
        //Obtenemos la conexión usando el Singleton
        Connection conexion = ConexionDB.getInstancia().getConexion();

        //1. Sentencia SQL para la tabla Usuarios
        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS Usuarios ("
                + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT UNIQUE NOT NULL, "
                + "nombre_completo TEXT NOT NULL, "
                + "estado TEXT DEFAULT 'Desconectado'"
                + ");";
        // 2. Sentencia SQL para la tabla Mensajes
        // Relacionada con Usuarios mediante id_usuario
        String sqlMensajes = "CREATE TABLE IF NOT EXISTS Mensajes ("
                + "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_usuario INTEGER NOT NULL, "
                + "contenido TEXT NOT NULL, "
                + "fecha_hora TEXT NOT NULL, "
                + "FOREIGN KEY (id_usuario) REFERENCES Usuarios(id_usuario) ON DELETE CASCADE"
                + ");";
        // Usamos try-with-resources para que el Statement se cierre automáticamente
        try (Statement stmt = conexion.createStatement()) {

            // ACTIVACIÓN CRÍTICA: Habilitar el soporte de llaves foráneas en SQLite
            // en SQLite las llaves foráneas vienen desactivadas por defecto, por eso se activan aquí
            stmt.execute("PRAGMA foreign_keys = ON;");

            // Ejecutamos la creación de las tablas
            stmt.execute(sqlUsuarios);
            stmt.execute(sqlMensajes);

            System.out.println("Base de datos lista: Tablas 'Usuarios' y 'Mensajes' operativas.");

        }catch (SQLException e) {
            System.err.println("Error crítico al inicializar las tablas: " + e.getMessage());
        }
    }
}
