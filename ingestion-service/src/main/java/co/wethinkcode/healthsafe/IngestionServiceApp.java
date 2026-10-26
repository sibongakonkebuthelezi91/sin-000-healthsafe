package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import com.opencsv.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args){
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        System.out.println(readCsv().get(0));
        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }

    private static List<String[]> readCsv(){
        InputStream inputData = IngestionServiceApp.class.getClassLoader().getResourceAsStream("wards-outdated.csv");
        try(CSVReader readData = new CSVReader(new InputStreamReader(inputData))){
            List<String[]> data = readData.readAll();
            for(String[] dart: data){
                for(int x = 0; dart.length > x;x++){
                    dart[x] = dart[x].toLowerCase().trim();
                }
            }
            return data;
        } catch (Exception e) {
            System.out.println("Could not print csv");
            return null;
        }
    }
}
