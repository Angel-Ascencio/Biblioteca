package org.utl.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.utl.model.Usuario;
import org.utl.dao.UsuarioDAO;
import org.utl.cqrs.UsuarioCQRS;

/**
 *
 * @author ascencio
 */
public class ControllerUsuario {

    private UsuarioDAO controllerusuariosDAO;
    private UsuarioCQRS cqrsUsuarios;

    //    Constructor inicializa el DAO y del cqrs de usuarios
    public ControllerUsuario() {
        controllerusuariosDAO = new UsuarioDAO();
        cqrsUsuarios = new UsuarioCQRS();
    }

    //Todo
    public List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            usuarios = controllerusuariosDAO.getAll();
        } catch (Exception e) {
            System.err.println("Error al obtener los usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    //Insertar uno nuevo
    public boolean insertarUsuario(Usuario usuario) throws Exception {
        try {
            int id = cqrsUsuarios.agregarUsuario(usuario);
            System.out.println("Usuario insertado exitosamente");
            return true;
        } catch (Exception e) {
            System.err.println("Errores de validación: " + e.getMessage());
            throw e; // Lanzamos la excepción para que llegue al REST
        }
    }

    //Actualizar 
    public boolean actualizarUsuario(Usuario usuario) throws Exception {
        try {
            cqrsUsuarios.actualizarUsuario(usuario);
            System.out.println("Usuario actualizado exitosamente");
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Errores de validación:\n" + e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            System.err.println("Error al actualizar el usuario: " + e.getMessage());
            throw e;
        }
    }

    // Buscar
    public List<Usuario> buscarUsuario(String valor) {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            usuarios = controllerusuariosDAO.search(valor);
            System.out.println("Usuarios obtenidos correctamente.");

            for (Usuario usuario : usuarios) {
                System.out.println("ID: " + usuario.getIdUsuario() + ", usuario: " + usuario.getNombreUsuario());
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener los usuarios: " + ex.getMessage());
            ex.printStackTrace();
        }
        return usuarios;
    }

    //Desactivar
    public void desactivarUsuario(Usuario usuario) {
        try {
            if (usuario == null || usuario.getIdUsuario() == 0) {
                throw new IllegalArgumentException("El ID del usuario es requerido para desactivarlo.");
            }

            controllerusuariosDAO.delete(usuario);
            System.out.println("Usuario desactivado exitosamente.");

        } catch (IllegalArgumentException ex) {
            System.err.println("Error al desactivar el Usuario: " + ex.getMessage());
            ex.printStackTrace();

        }
    }

    //Activar
    public void activarUsuario(Usuario usuario) {
        try {
            if (usuario == null || usuario.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("El ID del usuario es requerido para activarlo.");
            }
            controllerusuariosDAO.activar(usuario);
            System.out.println("Usuario activado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error de al activar el usuario: " + e.getMessage());
        }
    }

    // Seleccionar
    public List<Usuario> seleccionarUsuarioPorEstatus(String estatus) {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            usuarios = controllerusuariosDAO.seleccionar(estatus);
            System.out.println("Libros con estatus " + estatus + " obtenidos correctamente.");

            for (Usuario usuario : usuarios) {
                System.out.println("ID: " + usuario.getIdUsuario() + ", Nombre: " + usuario.getNombreUsuario());
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener los libros con estatus " + estatus + ": " + ex.getMessage());
            ex.printStackTrace();
        }
        return usuarios;
    }
}
