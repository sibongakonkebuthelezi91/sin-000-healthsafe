package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WardData {
    private static ObjectMapper mapper = new ObjectMapper();
    public static JsonNode getWard(String wardId) {
        String encoded = URLEncoder.encode(wardId, StandardCharsets.UTF_8);
        String url = "http://localhost:7031/ward/" + encoded;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           return mapper.readValue(response.body(), JsonNode.class);
        } catch (IOException | InterruptedException e) {
            return null;
        }

    }

    public static JsonNode getStatus(){
        String url = "http://localhost:7032/alert-level";
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           return mapper.readValue(response.body(), JsonNode.class);
        }catch (IOException | InterruptedException e){
            return null;
        }
    }
}
