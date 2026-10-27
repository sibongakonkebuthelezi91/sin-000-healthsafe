package co.wethinkcode.healthsafe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class DataRetrieval {

    public static String getData(){
        String url = "http://localhost:7030/wards";
        HttpClient client = HttpClient.newHttpClient() ;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
              try{HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
        }catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public static void wards(Context ctx){
        ObjectMapper json = new ObjectMapper();
        String data = getData();
        try {
            JsonNode[] allWards = json.readValue(data, JsonNode[].class);
            if (allWards == null) {
                ctx.status(400).result("No data");
                return;
            }
            String result = ctx.pathParam("id");
            for (JsonNode ward : allWards) {
                if (result.equalsIgnoreCase(ward.get("ward_id").asText())) {
                    ctx.status(200).json(ward);
                    return;
                }
            }
            ctx.status(400).result("No ward found");
        } catch (JsonProcessingException e) {
            ctx.status(400).result(String.valueOf(e));
        }
    }
}
