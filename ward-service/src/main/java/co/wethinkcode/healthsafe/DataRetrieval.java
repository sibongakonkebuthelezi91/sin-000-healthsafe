package co.wethinkcode.healthsafe;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class DataRetrieval {
    public static ObjectMapper json = new ObjectMapper();

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

    public static void ward(Context ctx){
        String data = getData();
//        System.out.println(data);
        try {
            JsonNode[] allWards = json.readValue(data, JsonNode[].class);
            if (allWards == null) {
                ctx.status(404).json("No data");
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


    public static void department(Context ctx){
        String data = getData();

        if(data == null) {
            ctx.status(404).json("No data");
            return;
        }
        try{
            JsonNode[] allDepartments = json.readValue(data, JsonNode[].class);
            List<String> departments = new ArrayList<>();
            for(JsonNode department: allDepartments){
                if(!departments.contains(department.get("department").asText())) departments.add(department.get("department").asText());
            }
            ctx.status(200).json(departments);
        } catch (JsonProcessingException e) {
            ctx.status(400).result("Bad request");
        }
    }
}
