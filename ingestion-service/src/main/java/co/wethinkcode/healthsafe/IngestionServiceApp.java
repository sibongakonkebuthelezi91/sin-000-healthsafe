package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IngestionServiceApp {

    public static void main(String[] args){
        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        String fileName = "wards-outdates.csv";
        try(BufferedReader br = Files.newBufferedReader(Paths.get(fileName))){
            String line;
            while((line = br.readLine()) != null){

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }
}
