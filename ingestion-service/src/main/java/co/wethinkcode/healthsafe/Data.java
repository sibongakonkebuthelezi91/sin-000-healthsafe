package co.wethinkcode.healthsafe;

import com.opencsv.CSVReader;
import io.javalin.http.Context;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Data {

    public Data(){}

    public static void readCsv(Context ctx){
        InputStream inputData = IngestionServiceApp.class.getClassLoader().getResourceAsStream("wards-outdated.csv");
        if(inputData == null) {
            System.out.println("File not found");
            return;
        }
        try(CSVReader readData = new CSVReader(new InputStreamReader(inputData))){
            List<String[]> data = readData.readAll();
            ctx.json(dataCleaner(data));
        } catch (Exception e) {
            ctx.status(400).json("Could not retrieve data");
            return;
        }
    }

    private static List<Map<String, String>> dataCleaner(List<String[]> data){
        List<Map<String, String>> json = new ArrayList<>();
        List<String> visited = new ArrayList<>();
        String[] keys = data.get(0);
        data.remove(0);
        for(String[] dart: data){
            Map<String, String> map = mapping(keys, dart);
            if(visited.contains(map.get("ward_id"))){
                continue;
            }
            json.add(map);
            visited.add(map.get("ward_id"));
        }

        return json;
    }

    private static Map<String, String> mapping(String[] keys, String[] values){
        Map<String, String> map = new HashMap<>();
        for(int x = 0; values.length > x;x++){
            String cleaned = values[x].toLowerCase().trim();
            String value = cleaned.isEmpty()? null : cleaned;
            map.put(keys[x].toLowerCase().trim(), value);
        }
        String availableBeds = map.get("beds_available");
        if(availableBeds.matches("[a-zA-Z]+") || availableBeds.equals("n/a") ) map.put("beds_available", null);
        return map;
    }
}
