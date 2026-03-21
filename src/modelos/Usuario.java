package modelos;
import java.io.Serializable;

public class Usuario implements Serializable{
    //serialVersionUID asegura que el cliente y el servidor usen la misma version de la clase
    private static long serialVersionUID = 1L;
    private int idUsuario;
    private String username;
    private String nombreCompleto;
    private String estado;

    //Constructor vacío (para cuando creemos el objeto y luego lo llenemos)
    public Usuario(){
    }

    //Constructor con parámetros
    public Usuario(int idUsuario, String username, String nombreCompleto, String estado){
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdUsuario(){ return idUsuario;}
    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}

    public String getUsername(){ return username;}
    public void setUsername(String username){this.username = username;}

    public String getNombreCompleto(){ return nombreCompleto;}
    public void setNombreCompleto(){this.nombreCompleto = nombreCompleto;}

    public String getEstado(){ return estado;}
    public void setEstado(){this.estado = estado;}

    //El toString para cuando se requiera mostrar el usuario en una lista en la GUI
    @Override
    public String toString() {
        return username + " (" + estado + ")";
    }
}
