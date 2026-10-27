package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import com.opencsv.*;

import javax.naming.Context;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class IngestionServiceApp {

    public static void main(String[] args){
        Javalin app = Javalin.create().start(7030);
        app.get("/wards", Data::readCsv);
        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.



    }
}
