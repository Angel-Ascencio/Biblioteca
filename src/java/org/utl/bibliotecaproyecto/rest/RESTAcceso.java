package org.utl.bibliotecaproyecto.rest;

import com.google.gson.Gson;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.utl.controller.ControllerLogin;
import org.utl.model.Usuario;

/**
 *
 * @author ascencio
 */
@Path("acceso")
public class RESTAcceso {

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("u") @DefaultValue("") String u,
            @FormParam("c") @DefaultValue("") String c) {

        ControllerLogin ca = new ControllerLogin();
        Usuario us = ca.logIn(u, c);

        if (!"Error".equals(us.getRol())) {
            org.utl.model.SessionManager.iniciarSesion(us);
        }

        return Response.ok(new Gson().toJson(us)).build();
    }

    @Path("sesion")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarSesion() {
        boolean activa = org.utl.model.SessionManager.sesionActiva();
        String out = "{\"loggedIn\":" + activa + "}";
        return Response.ok(out).build();
    }

    @Path("logout")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout() {
        org.utl.model.SessionManager.cerrarSesion();
        return Response.ok("{\"logout\":true}").build();
    }

}
