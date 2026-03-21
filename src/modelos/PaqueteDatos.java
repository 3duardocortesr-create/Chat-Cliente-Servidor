package modelos;
import java.io.Serializable;

public class PaqueteDatos implements Serializable{
    private static long serialVersionUID = 1L;

    //Constantes para definir qué tipo de acción se está realizando
    public static String ACCION_CONECTAR = "CONECTAR";
    public static String ACCION_CHAT = "CHAT";
    public static String ACCION_ESCRIBIENDO = "ESCRIBIENDO";
    public static String ACCION_CRUD_CREAR_USUARIO = "CRUD_CREAR_USUARIO";
    public static String ACCION_DESCONECTAR = "DESCONECTAR";

    private String accion;          //Qué queremos que haga el servidor/cliente
    private Usuario usuario;        //Quién envía la petición o los datos del usuario afectado
    private Mensaje mensaje;        //El mensaje
    private Object datosExtra;      //Un comodín por si necesitamos enviar una lista

    public PaqueteDatos(){
    }

    public PaqueteDatos(String accion) {
        this.accion = accion;
    }

    //Getters y Setters
    public String getAccion(){ return accion;}
    public void setAccion(){ this.accion = accion;}

    public Usuario getUsuario(){ return usuario;}
    public void setUsuario(Usuario usuario){ this.usuario = usuario;}

    public Mensaje getMensaje(){ return mensaje;}
    public void setUsuario(){ this.mensaje = mensaje;}

    public Object getDatosExtra(){ return datosExtra;}
    public void setDatosExtra(Object datosExtra){ this.datosExtra = datosExtra;}


}
