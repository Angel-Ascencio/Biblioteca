package org.utl.cqrs;

import org.utl.model.Usuario;
import org.utl.dao.UsuarioDAO;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ascencio
 */
public class UsuarioCQRS {

    private UsuarioDAO controllerUsuarioDAO;

    // Roles permitidos
    private static final List<String> ROLES_VALIDOS = Arrays.asList(
            "Alumno", "Bibliotecario", "Administrador"
    );

    //    Constructor inicializa el dao de usuario
    public UsuarioCQRS() {
        controllerUsuarioDAO = new UsuarioDAO();
    }

    //    Agrega un usuario nuevo después de validar
    public int agregarUsuario(Usuario usuario) throws Exception {
        validarNombreUsuario(usuario.getNombreUsuario());
        validarContrasenia(usuario.getContrasenia());
        validarEmail(usuario.getEmail());
        validarEstatus(usuario.getEstatus());
        validarRol(usuario.getRol());

        return controllerUsuarioDAO.insert(usuario);
    }

    //    Actualiza un usuario nuevo después de validar
    public void actualizarUsuario(Usuario usuario) throws Exception {
        validarNombreUsuario(usuario.getNombreUsuario());
        validarContrasenia(usuario.getContrasenia());
        validarEmail(usuario.getEmail());
        validarEstatus(usuario.getEstatus());
        validarRol(usuario.getRol());

        controllerUsuarioDAO.update(usuario);
    }

    // Validalos datos
    private void validarNombreUsuario(String nombreUsuario) throws Exception {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido.");
        }
        if (nombreUsuario.length() < 3 || nombreUsuario.length() > 20) {
            throw new Exception("El nombre de usuario debe tener entre 3 y 20 caracteres.");
        }
    }

    private void validarContrasenia(String contrasenia) throws Exception {
        if (contrasenia == null || contrasenia.trim().isEmpty()) {
            throw new Exception("La contraseña es requerida.");
        }
        if (contrasenia.length() < 4 || contrasenia.length() > 16) {
            throw new Exception("La contraseña debe tener entre 4 y 16 caracteres.");
        }
    }

    private void validarEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email es requerido.");
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new Exception("El email no tiene un formato válido.");
        }
    }

    private void validarEstatus(int estatus) throws Exception {
        if (estatus != 0 && estatus != 1) {
            throw new Exception("El estatus debe ser 0 (inactivo) o 1 (activo).");
        }
    }

    private void validarRol(String rol) throws Exception {
        if (rol == null || rol.trim().isEmpty()) {
            throw new Exception("El rol es requerido.");
        }
        if (!ROLES_VALIDOS.contains(rol)) {
            throw new Exception("El rol debe ser uno de los permitidos: " + ROLES_VALIDOS);
        }
    }
}
