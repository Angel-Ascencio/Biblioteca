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
    public Response login(@FormParam("u") @DefaultValue("") String u, @FormParam("c") @DefaultValue("") String c) {
        Usuario us = null;
        Gson gson = new Gson();

        ControllerLogin ca = new ControllerLogin();
        us = ca.logIn(u, c);
        String out = gson.toJson(us);

        return Response.ok(out).build();
    }
}
