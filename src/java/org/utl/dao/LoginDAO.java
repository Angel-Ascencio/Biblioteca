package org.utl.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.utl.bd.ConnectionMysql;
import org.utl.model.Usuario;

/**
 *
 * @author ascencio
 */
public class LoginDAO {

    public Usuario logIn(String nombreUsuario, String contrasenia) {
        Usuario u = new Usuario();
        Connection conn = null;
        try {

            String query = "SELECT * FROM vista_usuario_activo WHERE BINARY nombreUsuario=? AND BINARY contrasenia=?";

            ConnectionMysql connMySQL = new ConnectionMysql();
            conn = connMySQL.open();
            conn.setAutoCommit(false);
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nombreUsuario);
            pstmt.setString(2, contrasenia);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                u.setContrasenia(rs.getString("contrasenia"));
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombreUsuario(rs.getString("nombreUsuario"));
                u.setRol(rs.getString("rol"));
            } else {
                u.setContrasenia("");
                u.setIdUsuario(0);
                u.setNombreUsuario("");
                u.setRol("Error");
            }
            rs.close();
            pstmt.close();

            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
            connMySQL.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return u;
    }

}
