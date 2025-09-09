package org.utl.controller;

import org.utl.model.Usuario;
import org.utl.dao.LoginDAO;

/**
 *
 * @author ascencio
 */
public class ControllerLogin {

    private LoginDAO controllerloginDAO;

    //    Constructor inicializa el DAO de usuarios
    public ControllerLogin() {
        controllerloginDAO = new LoginDAO();
    }

    public Usuario logIn(String nombreUsuario, String contrasenia) {
        Usuario usuario = new Usuario();

        try {
            usuario = controllerloginDAO.logIn(nombreUsuario, contrasenia);
        } catch (Exception e) {
            System.out.println("Error durante el inicio de sesión: " + e.getMessage());
            usuario.setRol("Error");
            usuario.setNombreUsuario("");
            usuario.setIdUsuario(0);
            usuario.setContrasenia("");
        }

        return usuario;
    }

}
