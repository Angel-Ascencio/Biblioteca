package org.utl.bibliotecaproyecto.rest;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import org.utl.model.Libro;
import org.utl.controller.ControllerLibro;
import org.utl.viewmodel.LibroViewModel;

/**
 *
 * @author ascencio
 */
@Path("libro")
public class RESTLibros {

    private ControllerLibro controllerLibro;

    //    Constructor inicializa el controller del libros
    public RESTLibros() {
        controllerLibro = new ControllerLibro();
    }

    //Todos
    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        String out = "";
        try {
            ControllerLibro objCc = new ControllerLibro();
            List<Libro> listaLibros = objCc.obtenerLibros();

            Gson objGson = new Gson();
            out = objGson.toJson(listaLibros);
            if (listaLibros == null || listaLibros.isEmpty()) {
                out = "{\"message\":\"No se encontraron libros\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"Se produjo un error en la ejecución\"}";
            e.printStackTrace();
        }
        return Response.ok(out).build();
    }

    //Insertar
    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(@FormParam("l") String libroJson) {
        Gson objGson = new Gson();
        Libro libro;
        try {
            libro = objGson.fromJson(libroJson, Libro.class);
            int idLibroGenerado = controllerLibro.insertarLibro(libro);
            if (idLibroGenerado > 0) {
                String out = String.format("{\"result\":\"Libro insertado exitosamente\", \"id\":%d}", idLibroGenerado);
                return Response.ok(out).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"No se pudo insertar el libro.\"}")
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = String.format("{\"error\":\"Error al insertar el libro: %s\"}", e.getMessage().replace("\"", "\\\""));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage)
                    .build();
        }
    }

    //Buscar
    @Path("buscar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(@QueryParam("valor") String valor) {
        List<Libro> listaLibros = controllerLibro.buscarLibros(valor);
        Gson objGson = new Gson();
        String out = objGson.toJson(listaLibros);
        return Response.ok(out).build();
    }

    // Actualizar
    @Path("update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(String libroJson) {
        Gson objGson = new Gson();
        Libro libro;
        try {
            libro = objGson.fromJson(libroJson, Libro.class);
        } catch (JsonSyntaxException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Error al analizar el JSON\"}")
                    .build();
        }
        String base64Pdf = libro.getArchivo_pdf();
        InputStream pdfInputStream = null;
        if (base64Pdf != null && !base64Pdf.isEmpty()) {
            try {
                byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
                pdfInputStream = new ByteArrayInputStream(pdfBytes);
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Error al decodificar el PDF\"}")
                        .build();
            }
        }
        try {
            controllerLibro.actualizarLibro(libro, pdfInputStream);
            if (pdfInputStream != null) {
                pdfInputStream.close();
            }
            String out = "{\"result\":\"Libro actualizado con éxito\"}";
            return Response.ok(out).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al actualizar el libro\"}")
                    .build();
        }
    }

    //Desactivar
    @Path("delete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("idLibros") @DefaultValue("0") String idLibros) throws Exception {
        Libro libro = new Libro();
        libro.setId_libro(Integer.parseInt(idLibros));
        ControllerLibro objCc = new ControllerLibro();
        objCc.desactivarLibro(libro);
        String out = "{\"result\":\"Libro desactivado con éxito\"}";
        return Response.ok(out).build();
    }

    //Activar
    @Path("activar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response activar(@QueryParam("idLibro") @DefaultValue("0") String idLibro) throws Exception {
        Libro libro = new Libro();
        libro.setId_libro(Integer.parseInt(idLibro));
        controllerLibro.activarLibro(libro);
        String out = "{\"result\":\"Libro activado con éxito\"}";
        return Response.ok(out).build();
    }

    //Ver activos o inactivos
    @Path("seleccionar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response seleccionar(@QueryParam("val") @DefaultValue("") String val) {
        String out = "";
        List<Libro> listaLibros = controllerLibro.seleccionarLibrosPorEstatus(val);
        Gson objGson = new Gson();
        out = objGson.toJson(listaLibros);
        return Response.ok(out).build();
    }

    //consume el controller del metodo MVVM para los libros de la base de datos propia
    @Path("getAllPublic")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPublic() {
        String out = "";
        try {
            ControllerLibro objCc = new ControllerLibro();
            List<LibroViewModel> listaLibros = objCc.getAllPublic();

            Gson objGson = new Gson();
            out = objGson.toJson(listaLibros);
            if (listaLibros == null || listaLibros.isEmpty()) {
                out = "{\"message\":\"No se encontraron libros\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"Se produjo un error en la ejecución\"}";
            e.printStackTrace();
        }
        return Response.ok(out).build();
    }
    //consume el controller del metodo MVVM para los libros de las bases de datos 
    @Path("getAllPublicosTodos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPublicos() {
        String out = "";
        try {
            ControllerLibro objCc = new ControllerLibro();
            List<LibroViewModel> listaLibros = objCc.getAllPublicoTodos();

            Gson objGson = new Gson();
            out = objGson.toJson(listaLibros);
            if (listaLibros == null || listaLibros.isEmpty()) {
                out = "{\"message\":\"No se encontraron libros\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"Se produjo un error en la ejecución\"}";
            e.printStackTrace();
        }
        return Response.ok(out).build();
    }

}
