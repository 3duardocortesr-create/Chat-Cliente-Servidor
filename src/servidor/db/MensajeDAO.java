package servidor.db;

import modelos.Mensaje;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    // CREATE: Guarda un nuevo mensaje de chat
    public boolean insertarMensaje(Mensaje mensaje) {
        String sql = "INSERT INTO Mensajes (id_usuario, contenido, fecha_hora) VALUES (?, ?, ?)";
        Connection conexion = ConexionDB.getInstancia().getConexion();

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, mensaje.getIdUsuario());
            pstmt.setString(2, mensaje.getContenido());
            pstmt.setString(3, mensaje.getFechaHora());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar mensaje: " + e.getMessage());
            return false;
        }
    }

    // READ: Obtiene el historial de chat
    public List<Mensaje> obtenerHistorial() {
        List<Mensaje> historial = new ArrayList<>();
        String sql = "SELECT * FROM Mensajes ORDER BY fecha_hora ASC";
        Connection conexion = ConexionDB.getInstancia().getConexion();

        try (PreparedStatement pstmt = conexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Mensaje m = new Mensaje(
                    rs.getInt("id_mensaje"),
                    rs.getInt("id_usuario"),
                    rs.getString("contenido"),
                    rs.getString("fecha_hora")
                );
                historial.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar historial: " + e.getMessage());
        }
        return historial;
    }
}