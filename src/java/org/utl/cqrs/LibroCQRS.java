package org.utl.cqrs;

import java.io.InputStream;
import org.utl.model.Libro;
import org.utl.dao.LibroDAO;

/**
 *
 * @author ascencio
 */
public class LibroCQRS {

    private LibroDAO controllerLibrosDAO;
//    Constructor inicializa el DAO de libros

    public LibroCQRS() {
        controllerLibrosDAO = new LibroDAO();
    }

//    Agrega un libro nuevo después de validar su nombre y categoría
    public int agregarLibro(Libro libro) throws Exception {
        validarNombreLibro(libro.getNombre_libro());
        validarCategoriaLibro(libro.getGenero());

        return controllerLibrosDAO.insert(libro);
    }
//Actualiza un libro existente y su archivo pdf

    public void actualizarLibro(Libro libro, InputStream pdfInputStream) throws Exception {
        validarNombreLibro(libro.getNombre_libro());
        validarCategoriaLibro(libro.getGenero());

        controllerLibrosDAO.update(libro, pdfInputStream);
    }

    // Validalos datos
    private void validarNombreLibro(String nombre) throws Exception {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new Exception("El nombre del libro es requerido.");
        }
        if (nombre.length() < 5 || nombre.length() > 100) {
            throw new Exception("El nombre del libro debe tener entre 5 y 100 caracteres.");
        }
    }

    private void validarCategoriaLibro(String categoria) throws Exception {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new Exception("El genero del libro es requerida.");
        }
        if (categoria.length() < 5 || categoria.length() > 30) {
            throw new Exception("El genero del libro debe tener entre 5 y 30 caracteres.");
        }
    }
}
