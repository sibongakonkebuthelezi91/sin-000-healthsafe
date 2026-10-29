package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonReader {
    private static ObjectMapper mapper = new ObjectMapper();

    public static List<EquipmentFailurePayload> readJson(){
        InputStream inputData = JsonReader.class.getClassLoader().getResourceAsStream("equipment_failure_payload.json");
        if(inputData == null){
            System.out.println("No file named " + "equipment_failure_payload.json" + " found");
            return List.of();
        }
        try {
            List<EquipmentFailurePayload> data = mapper.readValue(inputData, new TypeReference<>() {
            });
            System.out.println(data);
            return data;
        } catch (IOException e) {
            System.out.println("Error reading file " + "equipment_failure_payload.json");
            return List.of();
        }
    }

    public static void  publishEquipment(List<EquipmentFailurePayload> equipments){
        for(EquipmentFailurePayload equipment: equipments){
            MqConfig.publishEquipmentFailure(equipment);
        }
    }

    public static void publishMessage(){
        publishEquipment(readJson());

    }
}
