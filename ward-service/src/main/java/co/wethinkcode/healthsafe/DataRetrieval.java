package co.wethinkcode.healthsafe;
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
        try{HttpClient client = HttpClient.newHttpClient() ;
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body();
        }catch (IOException | InterruptedException e) {
            return null;
        }
    }

    public static void wards(Context ctx){
        Gson json = new Gson();
        JsonObject[] allWards = json.fromJson(DataRetrieval.getData(), JsonObject[].class);
        if(allWards == null) {
            System.out.println("No data");
            return;
        }
        String result = ctx.pathParam("id");
        for(JsonObject ward : allWards){
            System.out.println(ward);
        }
    }
}
