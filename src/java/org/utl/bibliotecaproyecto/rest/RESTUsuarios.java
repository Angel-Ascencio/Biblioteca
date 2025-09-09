package org.utl.bibliotecaproyecto.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.utl.controller.ControllerUsuario;
import org.utl.model.Usuario;
import org.utl.model.SessionManager; // IMPORTANTE: importar tu SessionManager

/**
 *
 * @author ascencio
 */
@Path("usuario")
public class RESTUsuarios {

    private ControllerUsuario controllerusuario;

    public RESTUsuarios() {
        controllerusuario = new ControllerUsuario();
    }

    private Response verificarSesion() {
        if (!SessionManager.sesionActiva()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"error\":\"Acceso no autorizado\"}")
                           .build();
        }
        return null;
    }

    //Todos
    @Path("getAll")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        String out = "";
        try {
            List<Usuario> listaLibros = controllerusuario.obtenerUsuarios();
            Gson objGson = new Gson();
            out = objGson.toJson(listaLibros);
            if (listaLibros == null || listaLibros.isEmpty()) {
                out = "{\"message\":\"No se encontraron usuarios\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"Se produjo un error en la ejecución\"}";
            e.printStackTrace();
        }
        return Response.ok(out).build();
    }

    //Nuevo
    @Path("insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response insert(@FormParam("u") @DefaultValue("") String usuarioJson) {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        Gson gson = new Gson();
        Usuario usuario = gson.fromJson(usuarioJson, Usuario.class);

        String out;
        try {
            boolean insertado = controllerusuario.insertarUsuario(usuario);
            if (insertado) {
                out = "{\"result\":\"Usuario insertado exitosamente\"}";
            } else {
                out = "{\"error\":\"No se pudo insertar el usuario\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"" + e.getMessage() + "\"}";
        }

        return Response.ok(out).build();
    }

    //Buscar
    @Path("buscar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(@QueryParam("valor") String valor) {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        List<Usuario> listaUsuarios = controllerusuario.buscarUsuario(valor);
        Gson objGson = new Gson();
        String out = objGson.toJson(listaUsuarios);
        return Response.ok(out).build();
    }

    //Actualizar
    @Path("update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@FormParam("u") @DefaultValue("") String usuario) {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        Gson objGson = new Gson();
        Usuario u = objGson.fromJson(usuario, Usuario.class);

        String out;
        try {
            boolean actualizado = controllerusuario.actualizarUsuario(u);
            if (actualizado) {
                out = "{\"result\":\"Usuario actualizado con éxito\"}";
            } else {
                out = "{\"error\":\"No se encontró el usuario a actualizar\"}";
            }
        } catch (Exception e) {
            out = "{\"error\":\"Error al actualizar el usuario: " + e.getMessage() + "\"}";
        }

        return Response.ok(out).build();
    }

    //Desactivar
    @Path("delete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@QueryParam("idUsuarios") @DefaultValue("0") String idUsuarios) throws Exception {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(Integer.parseInt(idUsuarios));
        controllerusuario.desactivarUsuario(usuario);
        String out = "{\"result\":\"Usuario desactivado con éxito\"}";
        return Response.ok(out).build();
    }

    //Activar
    @Path("activar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response activar(@QueryParam("idUsuario") @DefaultValue("0") String idUsuario) throws Exception {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(Integer.parseInt(idUsuario));
        controllerusuario.activarUsuario(usuario);
        String out = "{\"result\":\"Usuario activado con éxito\"}";
        return Response.ok(out).build();
    }

    //Activos o inactivos
    @Path("seleccionar")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response seleccionar(@QueryParam("val") @DefaultValue("") String val) {
        Response sesion = verificarSesion();
        if (sesion != null) return sesion;

        List<Usuario> listaLibros = controllerusuario.seleccionarUsuarioPorEstatus(val);
        Gson objGson = new Gson();
        String out = objGson.toJson(listaLibros);
        return Response.ok(out).build();
    }
}
