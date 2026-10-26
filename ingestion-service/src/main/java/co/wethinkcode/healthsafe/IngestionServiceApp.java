package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import com.opencsv.*;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.*;
import java.util.Arrays;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args){
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        String fileName = "wards-outdated.csv";
        InputStream inputData = IngestionServiceApp.class.getClassLoader().getResourceAsStream(fileName);
        try(CSVReader readData = new CSVReader(new InputStreamReader(inputData))){
            List<String[]> data = readData.readAll();

            for(String[] dart: data){
                for(int x = 0; dart.length > x;x++){
                    dart[x] = dart[x].toLowerCase().trim();
                }
                System.out.println(Arrays.toString(dart));
            }
        } catch (Exception e) {
            System.out.println("Could not print csv");
        }
        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }
}
