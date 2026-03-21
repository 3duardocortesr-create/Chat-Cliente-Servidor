package servidor.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Variable estática privada que almacenará única instancia de la clase
    private static ConexionDB instancia;
    // Objeto Connection de JDBC
    private Connection conexion;
    // Ruta de la base de datos SQLite
    private String URL = "jdbc:sqlite:chat_app.db";
    // Constructor privado: Evita que otras clases hagan "new ConexionDB()"
    private ConexionDB(){
        try{
            //Establecer conexión
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexión a SQLite establecida con éxito");
        } catch(SQLException e){
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
    // Metodo estático público para obtener la instancia (el núcleo de Singleton)
    public static ConexionDB getInstancia(){
        // Si la instancia no existe, la crea. Si existe, devuelve la que hay.
        if (instancia == null){
            instancia = new ConexionDB();
        }
        return instancia;
    }

    // Metodo para que otras clases (como los DAOs) puedan usar la conexion
    public Connection getConexion() {
        return conexion;
    }

    // Metodo de limpieza para cuando apague el servidor
    public void cerrarConexion(){
        try{
            if(conexion != null && !conexion.isClosed()){
                conexion.close();
                System.out.println("Conexión a la base de datos cerrada de forma segura");
            }
        } catch(SQLException e){
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
