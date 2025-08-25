
package org.utl.model;

import org.utl.model.Usuario;

/**
 *
 * @author ascencio
 */
public class SessionManager {
    private static Usuario usuarioActivo;

    public static void iniciarSesion(Usuario u) {
        usuarioActivo = u;
    }

    public static void cerrarSesion() {
        usuarioActivo = null;
    }

    public static boolean sesionActiva() {
        return usuarioActivo != null;
    }

    public static Usuario getUsuarioActivo() {
        return usuarioActivo;
    }
}