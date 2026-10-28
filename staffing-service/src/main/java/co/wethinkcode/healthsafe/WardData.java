package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class WardData {

    private WardData(){}

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

    private static List<String[]> Doctors(){
        InputStream inputData = StaffingServiceApp.class.getClassLoader().getResourceAsStream("doctors.json");
        if(inputData == null){
            System.out.println("doctors.json not found");
            return null;
        }
        try{
            CSVReader csvRead = new CSVReader(new InputStreamReader(inputData, StandardCharsets.UTF_8));
            List<String[]> data = csvRead.readAll();
            return data;
        } catch (IOException | CsvException e) {
            System.out.println("Could not read data");
        }
        return null;
    }
}
