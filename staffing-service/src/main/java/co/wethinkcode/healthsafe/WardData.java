package co.wethinkcode.healthsafe;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class WardData {
    public static String getWard(String wardId) {
        String encoded = URLEncoder.encode(wardId, StandardCharsets.UTF_8);
        String url = "http://localhost:7031/ward/" + encoded;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
