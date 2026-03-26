package servidor.db;

import modelos.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // CREATE: Inserta un nuevo usuario
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO Usuarios (username, nombre_completo, estado) VALUES (?, ?, ?)";

        // Obtiene la conexión del Singleton
        Connection conexion = ConexionDB.getInstancia().getConexion();

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            // Sustituimos los '?' por los valores del objeto
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getEstado());

            // Ejecuta la actualización y devuelve el número de filas afectadas
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // READ: Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuarios";
        Connection conexion = ConexionDB.getInstancia().getConexion();

        try (PreparedStatement pstmt = conexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getString("nombre_completo"),
                    rs.getString("estado")
                );
                listaUsuarios.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }
        return listaUsuarios;
    }

    // UPDATE: Modifica el estado o nombre
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE Usuarios SET nombre_completo = ?, estado = ? WHERE id_usuario = ?";
        Connection conexion = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNombreCompleto());
            pstmt.setString(2, usuario.getEstado());
            pstmt.setInt(3, usuario.getIdUsuario());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Elimina un usuario
    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM Usuarios WHERE id_usuario = ?";
        Connection conexion = ConexionDB.getInstancia().getConexion();
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}