package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import com.opencsv.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class IngestionServiceApp {

    public static void main(String[] args){
        Javalin app = Javalin.create().start(7030);
        app.get("/health", ctx -> ctx.result("OK"));
        System.out.println(readCsv());
        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }

    private static List<Map<String, String>> readCsv(){
        InputStream inputData = IngestionServiceApp.class.getClassLoader().getResourceAsStream("wards-outdated.csv");
        if(inputData == null) {
            System.out.println("File not found");
            return null;
        }
        try(CSVReader readData = new CSVReader(new InputStreamReader(inputData))){
            List<String[]> data = readData.readAll();
            List<Map<String, String>> json = new ArrayList<>();
            String[] keys = data.get(0);
            data.remove(0);
            for(String[] dart: data){
                Map<String, String> map = new HashMap<>();
                for(int x = 0; dart.length > x;x++){
                    String cleaned = dart[x].toLowerCase().trim();
                    String value = cleaned.isEmpty()? null : cleaned;
                   map.put(keys[x].toLowerCase().trim(), value);
                }
                String availableBeds = map.get("beds_available");
                if(availableBeds.matches("[a-zA-Z]+") || availableBeds.equals("n/a") ) map.put("beds_available", null);
                json.add(map);
            }
            return json;
        } catch (Exception e) {
            System.out.println("Could not print csv");
            return null;
        }
    }
}
