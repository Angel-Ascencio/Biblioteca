package org.utl.dao;

import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.utl.bd.ConnectionMysql;
import org.utl.model.Usuario;

/**
 *
 * @author ascencio
 */
public class UsuarioDAO {

    //Ver todo
    public List<Usuario> getAll() throws SQLException, ClassNotFoundException, IOException {
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        String query = "SELECT * FROM vista_usuario;";

        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        List<Usuario> usuarios = new ArrayList<>();

        while (rs.next()) {

            int idUsuario = rs.getInt("idUsuario");
            String nombreUsuario = rs.getString("nombreUsuario");
            String contrasenia = rs.getString("contrasenia");
            int estatus = rs.getInt("estatus");
            String rol = rs.getString("rol");
            String email = rs.getString("email");
            Usuario usuarioObj = new Usuario(idUsuario, nombreUsuario, contrasenia, rol, estatus, email);
            usuarios.add(usuarioObj);

        }
        rs.close();
        pstmt.close();
        conn.close();
        connMysql.close();
        return usuarios;
    }

    //Insertar
    public int insert(Usuario u) throws SQLException {
        int insertedId = -1;
        String query = "{call insertarUsuario(?,?,?,?,?,?)}";
        ConnectionMysql conMysql = new ConnectionMysql();
        Connection conn = null;

        try {
            conn = conMysql.open();
            conn.setAutoCommit(false);

            CallableStatement cstm = conn.prepareCall(query);

            cstm.setString(1, u.getNombreUsuario());
            cstm.setString(2, u.getContrasenia());
            cstm.setInt(3, u.getEstatus());
            cstm.setString(4, u.getRol());
            cstm.setString(5, u.getEmail());

            cstm.registerOutParameter(6, Types.INTEGER);

            cstm.execute();

            insertedId = cstm.getInt(6);

            cstm.close();
            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            // Lanzamos la excepcion para que llegue al cqrs y al rest
            throw new SQLException("Error en la inserción: " + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        return insertedId;
    }

    //Actualizar
    public int update(Usuario u) {
        int updatedId = -1;
        String query = "{call modificarUsuario(?,?,?,?,?,?)}";
        ConnectionMysql conMySQL = new ConnectionMysql();
        Connection conn = null;
        try {
            conn = conMySQL.open();
            conn.setAutoCommit(false);

            CallableStatement cstm = conn.prepareCall(query);
            cstm.setInt(1, u.getIdUsuario());
            cstm.setString(2, u.getNombreUsuario());
            cstm.setString(3, u.getContrasenia());
            cstm.setInt(4, u.getEstatus());
            cstm.setString(5, u.getRol());
            cstm.setString(6, u.getEmail());

            cstm.execute();
            updatedId = u.getIdUsuario();

            cstm.close();
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return updatedId;
    }

    //Buscar
    public List<Usuario> search(String valor) throws SQLException {

        List<Usuario> listaUsuarios = new ArrayList<>();

        String query = "SELECT * FROM vista_usuario WHERE idUsuario LIKE ? OR nombreUsuario LIKE ? OR rol LIKE ? OR email LIKE ?";
        ConnectionMysql connMySQL = new ConnectionMysql();

        Connection conn = connMySQL.open();

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, "%" + valor + "%");
        stmt.setString(2, "%" + valor + "%");
        stmt.setString(3, "%" + valor + "%");
        stmt.setString(4, "%" + valor + "%");

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNombreUsuario(rs.getString("nombreUsuario"));
            u.setContrasenia(rs.getString("contrasenia"));
            u.setEstatus(rs.getInt("estatus"));
            u.setRol(rs.getString("rol"));
            u.setEmail(rs.getString("email"));

            listaUsuarios.add(u);
        }

        rs.close();
        stmt.close();
        conn.close();
        connMySQL.close();

        return listaUsuarios;
    }

    //Desactivar
    public void delete(Usuario u) {
        Connection conn = null;
        try {
            ConnectionMysql objConnMySql = new ConnectionMysql();
            conn = objConnMySql.open();
            conn.setAutoCommit(false);

            String query = "UPDATE usuario SET estatus=0 WHERE idUsuario=" + u.getIdUsuario() + ";";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

            conn.commit();
            conn.setAutoCommit(true);
            stmt.close();
            conn.close();
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
    }

    //Activar
    public void activar(Usuario u) {
        Connection conn = null;
        try {
            ConnectionMysql objConnMySql = new ConnectionMysql();
            conn = objConnMySql.open();
            conn.setAutoCommit(false);

            String query = "UPDATE usuario SET estatus=1 WHERE idUsuario=" + u.getIdUsuario() + ";";
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);

            conn.commit();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Seleccionar
    public List<Usuario> seleccionar(String val) throws SQLException {

        String query = "SELECT * FROM vista_usuario where estatus=" + val;

        ConnectionMysql connMySQL = new ConnectionMysql();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        List<Usuario> lista = new ArrayList<>();

        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("idUsuario"));
            u.setNombreUsuario(rs.getString("nombreUsuario"));
            u.setContrasenia(rs.getString("contrasenia"));
            u.setEstatus(rs.getInt("estatus"));
            u.setRol(rs.getString("rol"));
            u.setEmail(rs.getString("email"));

            lista.add(u);
        }
        rs.close();
        pstmt.close();
        conn.close();
        connMySQL.close();
        return lista;
    }

}
