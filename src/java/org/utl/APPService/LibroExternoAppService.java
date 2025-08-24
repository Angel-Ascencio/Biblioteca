package org.utl.APPService;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import org.utl.viewmodel.LibroViewModel;

/**
 *
 * @author ascencio
 */
public class LibroExternoAppService {

    private final List<String> urls;

    public LibroExternoAppService() {

        urls = new ArrayList<>();
        //Aqui agrega las rutas de las apis para consumir otros datos
        urls.add("http://Direccion IP:Puerto/direccion de la api en el carpeteo");

    }

    public List<LibroViewModel> getAll() {
        List<LibroViewModel> librosExternos = new ArrayList<>();
        Gson gson = new Gson();
        HttpClient client = HttpClient.newHttpClient();

        for (String url : urls) {
            try {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {

                    List<LibroViewModel> libros = gson.fromJson(response.body(), new TypeToken<List<LibroViewModel>>() {
                    }.getType());
                    librosExternos.addAll(libros);
                } else {
                    System.err.println("Error al consultar el API externo en " + url + ": Código de respuesta " + response.statusCode());
                }
            } catch (Exception e) {
                System.err.println("Error al consultar el API externo en " + url + ": " + e.getMessage());
            }
        }

        return librosExternos;
    }
}
