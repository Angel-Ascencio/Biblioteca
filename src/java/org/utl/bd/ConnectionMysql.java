package org.utl.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ascencio
 */
public class ConnectionMysql {

    Connection conn;

    /**
     * Metodo para abrir una conexión a BD-MySQL
     *
     * @return Devuelve un objeto de tipo Connection
     */
    public Connection open() {
        //datos para conectar con la base de datos

        //Ingresa tu usuario y contraseña de base de datos
        String user = "usuario";
        String password = "contraseña";
        // Ingresa tu host, puesrto y el nombre de la base de datos lo demas dejalo igual
        String url = "jdbc:mysql://<HOST>:<PUERTO>/<NOMBRE_BASE_DATOS>?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para cerrar una conexión a BD-MySQL existente
     */
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
