package org.utl.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.utl.APPService.LibroExternoAppService;
import org.utl.cqrs.LibroCQRS;
import org.utl.model.Libro;
import org.utl.viewmodel.LibroViewModel;
import org.utl.dao.LibroDAO;

/**
 *
 * @author ascencio
 */
public class ControllerLibro {

    private LibroDAO controllerlibrosDAO;
    private LibroCQRS cqrsLibros;

    //    Constructor inicializa el DAO y del cqrs de libros
    public ControllerLibro() {
        controllerlibrosDAO = new LibroDAO();
        cqrsLibros = new LibroCQRS();
    }

    //Todo
    public List<Libro> obtenerLibros() {
        List<Libro> libros = new ArrayList<>();
        try {
            libros = controllerlibrosDAO.getAll();
        } catch (Exception e) {
            System.err.println("Error al obtener libros: " + e.getMessage());
        }
        return libros;
    }

    //Insertar uno nuevo
    public int insertarLibro(Libro libro) throws Exception {
        try {
            return cqrsLibros.agregarLibro(libro);
        } catch (Exception e) {
            System.err.println("Error al insertar el libro: " + e.getMessage());
            throw e;
        }
    }

    //Actualizar libro
    public void actualizarLibro(Libro libro, InputStream pdfInputStream) throws Exception {
        try {
            cqrsLibros.actualizarLibro(libro, pdfInputStream);
            System.out.println("Libro actualizado exitosamente");
        } catch (IllegalArgumentException e) {
            System.out.println("Errores de validación:\n" + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error al actualizar el libro: " + e.getMessage());
        }
    }

// Buscar libro
    public List<Libro> buscarLibros(String valor) {
        List<Libro> libros = new ArrayList<>();
        try {
            libros = controllerlibrosDAO.search(valor);
            System.out.println("Libros obtenidos correctamente.");

            for (Libro libro : libros) {
                System.out.println("ID: " + libro.getId_libro() + ", Nombre: " + libro.getNombre_libro());
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener los libros: " + ex.getMessage());
            ex.printStackTrace();
        }
        return libros;
    }

    //Desactivar
    public void desactivarLibro(Libro libro) {
        try {
            if (libro == null || libro.getId_libro() == 0) {
                throw new IllegalArgumentException("El ID del libro es requerido para desactivarlo.");
            }

            controllerlibrosDAO.delete(libro);
            System.out.println("Libro desactivado exitosamente.");

        } catch (IllegalArgumentException ex) {
            System.err.println("Error al desactivar el libro: " + ex.getMessage());
            ex.printStackTrace();

        }
    }

    //Activar
    public void activarLibro(Libro libro) {
        try {
            if (libro == null || libro.getId_libro() <= 0) {
                throw new IllegalArgumentException("El ID del libro es requerido para activarlo.");
            }
            controllerlibrosDAO.activar(libro);
            System.out.println("Libro activado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.err.println("Error de al activar el libro: " + e.getMessage());
        }
    }

    // Seleccionar
    public List<Libro> seleccionarLibrosPorEstatus(String estatus) {
        List<Libro> libros = new ArrayList<>();
        try {
            libros = controllerlibrosDAO.seleccionar(estatus);
            System.out.println("Libros con estatus " + estatus + " obtenidos correctamente.");

            for (Libro libro : libros) {
                System.out.println("ID: " + libro.getId_libro() + ", Nombre: " + libro.getNombre_libro());
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener los libros con estatus " + estatus + ": " + ex.getMessage());
            ex.printStackTrace();
        }
        return libros;
    }

    // Metodo MVVM (base de datos propia)
    public List<LibroViewModel> getAllPublic() throws SQLException, ClassNotFoundException, IOException {
        List<Libro> libros = controllerlibrosDAO.getAll();
        List<LibroViewModel> lista = new ArrayList<>();

        for (Libro libro : libros) {
            LibroViewModel modeloP = new LibroViewModel(
                    libro.getId_libro(),
                    libro.getNombre_libro(),
                    libro.getAutor(),
                    libro.getGenero(),
                    libro.getEstatus(),
                    libro.getArchivo_pdf(),
                    libro.getuniversidad()
            );
            lista.add(modeloP);
        }

        return lista;
    }

    //nuevo todos (todas las bases de datos)
    public List<LibroViewModel> getAllPublicoTodos() throws SQLException, ClassNotFoundException, IOException {
        List<Libro> libros = controllerlibrosDAO.getAll();
        List<LibroViewModel> librosPublico = new ArrayList<>();
        try {
            libros = controllerlibrosDAO.getAll();
            for (Libro libro : libros) {
                LibroViewModel libroViewModel = new LibroViewModel(
                        libro.getId_libro(),
                        libro.getNombre_libro(),
                        libro.getAutor(),
                        libro.getGenero(),
                        libro.getEstatus(),
                        libro.getArchivo_pdf(),
                        libro.getuniversidad()
                );
                librosPublico.add(libroViewModel);
            }
            System.out.println("Libros obtenidos de manera exitosa.");
            LibroExternoAppService appS = new LibroExternoAppService();
            List<LibroViewModel> librosExternos = appS.getAll();
            librosPublico.addAll(librosExternos);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al obtener los libros: " + e.getMessage());
        }
        return librosPublico;
    }

}
