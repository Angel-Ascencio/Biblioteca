package org.utl.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.utl.bd.ConnectionMysql;
import org.utl.model.Libro;

/**
 *
 * @author ascencio
 */
public class LibroDAO {

    //Ver todo
    public List<Libro> getAll() throws SQLException, ClassNotFoundException, IOException {
        ConnectionMysql connMysql = new ConnectionMysql();
        Connection conn = connMysql.open();

        String query = "SELECT * FROM vista_libros";
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        List<Libro> libros = new ArrayList<>();
        while (rs.next()) {
            int id_libro = rs.getInt("id_libro");
            String nombre_libro = rs.getString("nombre_libro");
            String autor = rs.getString("autor");
            String genero = rs.getString("genero");
            int estatus = rs.getInt("estatus");
            String archivo_pdf = rs.getString("archivo_pdf");
            String universidad = rs.getString("universidad");

            Libro libroObj = new Libro(id_libro, nombre_libro, autor, genero, estatus, archivo_pdf, universidad);
            libros.add(libroObj);
        }
        rs.close();
        pstmt.close();
        conn.close();
        connMysql.close();
        return libros;
    }

    //Insertar
    public int insert(Libro l) throws SQLException {
        String query = "{call insertarLibro(?,?,?,?,?,?)}";
        ConnectionMysql conMysql = new ConnectionMysql();
        Connection conn = null;
        int insertedId = -1;

        try {
            conn = conMysql.open();
            conn.setAutoCommit(false);

            CallableStatement cstm = conn.prepareCall(query);
            cstm.setString(1, l.getNombre_libro());
            cstm.setString(2, l.getAutor());
            cstm.setString(3, l.getGenero());
            cstm.setInt(4, l.getEstatus());
            cstm.setString(5, l.getArchivo_pdf());

            cstm.registerOutParameter(6, Types.INTEGER);
            cstm.execute();

            insertedId = cstm.getInt(6);

            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Error al insertar el libro: " + ex.getMessage(), ex);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
        return insertedId;
    }

//Actualizar
    public void update(Libro l, InputStream pdfInputStream) throws IOException {
        String query = "{call modificarLibro(?,?,?,?,?,?)}";
        ConnectionMysql conMySQL = new ConnectionMysql();
        Connection conn = null;

        try {
            conn = conMySQL.open();
            conn.setAutoCommit(false);

            CallableStatement cstm = conn.prepareCall(query);
            cstm.setInt(1, l.getId_libro());
            cstm.setString(2, l.getNombre_libro());
            cstm.setString(3, l.getAutor());
            cstm.setString(4, l.getGenero());
            cstm.setInt(5, l.getEstatus());

            // Convertir Pdf a Base64
            if (pdfInputStream != null) {
                byte[] pdfBytes = pdfInputStream.readAllBytes();
                String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
                cstm.setString(6, base64Pdf);  // pDF en base64
            } else {
                cstm.setNull(6, Types.VARCHAR);  // nulo si no hay Pdf
            }

            cstm.execute();
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//Buscar
    public List<Libro> search(String valor) throws SQLException {

        List<Libro> listaLibros = new ArrayList<>();

        String query = "SELECT * FROM vista_libros WHERE id_libro LIKE ? OR nombre_libro LIKE ? OR autor LIKE ? OR genero LIKE ?";
        ConnectionMysql connMySQL = new ConnectionMysql();

        Connection conn = connMySQL.open();

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, "%" + valor + "%");
        stmt.setString(2, "%" + valor + "%");
        stmt.setString(3, "%" + valor + "%");
        stmt.setString(4, "%" + valor + "%");

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            Libro l = new Libro();
            l.setId_libro(rs.getInt("id_libro"));
            l.setNombre_libro(rs.getString("nombre_libro"));
            l.setAutor(rs.getString("autor"));
            l.setGenero(rs.getString("genero"));
            l.setEstatus(rs.getInt("estatus"));
            l.setArchivo_pdf(rs.getString("archivo_pdf"));
            l.setuniversidad(rs.getString("universidad"));

            listaLibros.add(l);
        }

        rs.close();
        stmt.close();
        conn.close();
        connMySQL.close();

        return listaLibros;
    }

    //Desactivar
    public void delete(Libro l) {
        Connection conn = null;
        try {
            ConnectionMysql objConnMySql = new ConnectionMysql();
            conn = objConnMySql.open();
            conn.setAutoCommit(false);

            String query = "UPDATE libros SET estatus=0 WHERE id_libro=" + l.getId_libro() + ";";
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
    public void activar(Libro l) {
        Connection conn = null;
        try {
            ConnectionMysql objConnMySql = new ConnectionMysql();
            conn = objConnMySql.open();
            conn.setAutoCommit(false);

            String query = "UPDATE libros SET estatus=1 WHERE id_libro=" + l.getId_libro() + ";";
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
    public List<Libro> seleccionar(String val) throws SQLException {

        String query = "SELECT * FROM vista_libros where estatus=" + val;

        ConnectionMysql connMySQL = new ConnectionMysql();
        Connection conn = connMySQL.open();
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet rs = pstmt.executeQuery();

        List<Libro> lista = new ArrayList<>();

        while (rs.next()) {
            Libro l = new Libro();
            l.setId_libro(rs.getInt("Id_libro"));
            l.setNombre_libro(rs.getString("Nombre_libro"));
            l.setAutor(rs.getString("autor"));
            l.setGenero(rs.getString("genero"));
            l.setEstatus(rs.getInt("estatus"));
            l.setArchivo_pdf(rs.getString("Archivo_pdf"));

            lista.add(l);
        }
        rs.close();
        pstmt.close();
        conn.close();
        connMySQL.close();
        return lista;
    }

}
