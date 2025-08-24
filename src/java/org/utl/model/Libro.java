package org.utl.model;

/**
 *
 * @author ascencio
 */
public class Libro {

    private int id_libro;
    private String nombre_libro;
    private String autor;
    private String genero;
    private int estatus;
    private String archivo_pdf;
    private String universidad;

    public Libro() {
    }

    public Libro(int id_libro, String nombre_libro, String autor, String genero, int estatus, String archivo_pdf, String universidad) {
        this.id_libro = id_libro;
        this.nombre_libro = nombre_libro;
        this.autor = autor;
        this.genero = genero;
        this.estatus = estatus;
        this.archivo_pdf = archivo_pdf;
        this.universidad = universidad;
    }

    public int getId_libro() {
        return id_libro;
    }

    public void setId_libro(int id_libro) {
        this.id_libro = id_libro;
    }

    public String getNombre_libro() {
        return nombre_libro;
    }

    public void setNombre_libro(String nombre_libro) {
        this.nombre_libro = nombre_libro;
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

    public String getArchivo_pdf() {
        return archivo_pdf;
    }

    public void setArchivo_pdf(String archivo_pdf) {
        this.archivo_pdf = archivo_pdf;
    }

    public String getuniversidad() {
        return universidad;
    }

    public void setuniversidad(String universidad) {
        this.universidad = universidad;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Libro{");
        sb.append("id_libro=").append(id_libro);
        sb.append(", nombre_libro=").append(nombre_libro);
        sb.append(", autor=").append(autor);
        sb.append(", genero=").append(genero);
        sb.append(", estatus=").append(estatus);
        sb.append(", archivo_pdf=").append(archivo_pdf);
        sb.append(", universidad=").append(universidad);
        sb.append('}');
        return sb.toString();
    }

}
