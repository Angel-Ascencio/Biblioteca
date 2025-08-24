package org.utl.viewmodel;

/**
 *
 * @author ascencio
 */
public class LibroViewModel {

    private int id;
    private String nombre;
    private String autor;
    private String genero;
    private int estatus;
    private String archivo;
    private String universidad;

    public LibroViewModel() {
    }

    public LibroViewModel(int id_libro, String nombre_libro, String autor, String genero, int estatus, String archivo_pdf, String universidad) {
        this.id = id_libro;
        this.nombre = nombre_libro;
        this.autor = autor;
        this.genero = genero;
        this.estatus = estatus;
        this.archivo = archivo_pdf;
        this.universidad = universidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id_libro) {
        this.id = id_libro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre_libro) {
        this.nombre = nombre_libro;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo_pdf) {
        this.archivo = archivo_pdf;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LibroViewModel{");
        sb.append("id=").append(id);
        sb.append(", nombre=").append(nombre);
        sb.append(", autor=").append(autor);
        sb.append(", genero=").append(genero);
        sb.append(", estatus=").append(estatus);
        sb.append(", archivo=").append(archivo);
        sb.append(", universidad=").append(universidad);
        sb.append('}');
        return sb.toString();
    }

}
