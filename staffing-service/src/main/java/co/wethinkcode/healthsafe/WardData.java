package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WardData {

    private WardData(){}

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int baseDoctors = 2;


    public static JsonNode getWard(String wardId) {
        String encoded = URLEncoder.encode(wardId, StandardCharsets.UTF_8);
        String url = "http://localhost:7031/ward/" + encoded;
        return getRequest(url);

    }

    private static JsonNode getRequest(String url){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), JsonNode.class);
        }catch (IOException | InterruptedException e){
            return null;
        }
    }
    public static JsonNode getStatus(){
        String url = "http://localhost:7032/alert-level";
        return getRequest(url);
    }

    public static List<Map<String, String>> Doctors(){
        InputStream inputData = StaffingServiceApp.class.getClassLoader().getResourceAsStream("doctors.csv");
        if(inputData == null){
            System.out.println("doctors.json not found");
            return null;
        }
        try{
            CSVReader csvRead = new CSVReader(new InputStreamReader(inputData, StandardCharsets.UTF_8));
            List<String[]> staff = csvRead.readAll();
            return doctorInformation(staff);
        } catch (IOException | CsvException e) {
            System.out.println("Could not read data");
        }
        return null;
    }

    public static void schedule(String wardId){
        JsonNode ward = getWard(wardId);
        if(ward.get("beds_available").asText().equalsIgnoreCase("null")) {
            System.out.println("No beds available at ward: " + ward.get("ward_id").asText());
            return;
        }
        JsonNode alertLevel = getStatus();
        try{
            int level = neededDoctors(alertLevel.asInt());
            List<Map<String, String>> doctors = Doctors();
            assert doctors != null;
            String[] doctorsAvailable = chosenDoctors(level, doctors, ward.get("department").asText());
            schedule appoinment = new schedule(doctorsAvailable,
                    level,
                    ward.get("department").asText(),
                    ward.get("ward_id").asText());
            String schedulePayload = mapper.writeValueAsString(appoinment);
            MqConfig.publishStaffEvents(schedulePayload);
        }catch (NumberFormatException e){
            System.out.println("Invalid alert level: " + alertLevel.get("level").asText());
        } catch (JsonProcessingException e) {
            System.out.println("could not serialize json");
        }

    }


    private static int neededDoctors(int alertLevel){
        return baseDoctors + (alertLevel - 1);
    }

    private static String[] chosenDoctors(int noDoctors, List<Map<String, String>> doctors, String department){
        List<String> drDepartment = new ArrayList<>();
        for(Map<String, String> doctor: doctors){
            if(doctor.get("department").equalsIgnoreCase(department)){
                drDepartment.add(doctor.get("full_name"));
            }
        }
        return drChoice(noDoctors, drDepartment);
    }

    private static List<Map<String, String>> doctorInformation(List<String[]> doctors){
        String[] keys = doctors.get(0);
        doctors.remove(0);
        List<Map<String, String>> allDoctors = new ArrayList<>();
        for(String[] doctor: doctors){
            Map<String, String> doctorInformation = orderInformation(keys, doctor);
            if(doctorInformation == null) return null;
            allDoctors.add(doctorInformation);
        }
        return allDoctors;
    }

    private static Map<String, String> orderInformation(String[] keys, String[] doctor){
        if(keys.length < doctor.length) return null;
        Map<String, String> information = new HashMap<>();
        for(int x = 0; x < doctor.length; x++){
            information.put(keys[x].toLowerCase().trim(), doctor[x].toLowerCase().trim());
        }
        return information;
    }

    private static String[] drChoice(int noDoctor, List<String> doctorsInDepartment){
        String[] doctors = new String[noDoctor];
        for(int x = 0; x < doctors.length; x++){
            int index =  new Random().nextInt(doctorsInDepartment.size());
            doctors[x] = doctorsInDepartment.get(index);
        }
        return doctors;
    }
}
