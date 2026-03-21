package modelos;
import java.io.Serializable;

public class Mensaje implements Serializable{
    public static long serialVersionUID = 1L;

    private int idMensaje;
    private int idUsuario;
    private String contenido;
    private String fechaHora;

    public Mensaje(){
    }

    public Mensaje(int idMensaje, int idUsuario, String contenido, String fechaHora){
        this.idMensaje = idMensaje;
        this.idUsuario = idUsuario;
        this.contenido = contenido;
        this.fechaHora = fechaHora;
    }
    // Getters y Setters;
    public int getIdMensaje(){ return idMensaje;}
    public void setIdMensaje(){ this.idMensaje = idMensaje;}

    public int getIdUsuario(){ return idUsuario;}
    public void setIdUsuario(){ this.idUsuario = idUsuario;}

    public String getContenido(){ return contenido;}
    public void setContenido(){this.contenido = contenido;}

    public String getFechaHora(){ return fechaHora;}
    public void setFechaHora(){ this.fechaHora = fechaHora;}

}
