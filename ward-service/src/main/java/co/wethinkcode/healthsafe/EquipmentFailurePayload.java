package co.wethinkcode.healthsafe;

import java.time.Instant;

public record EquipmentFailurePayload(
        String wardId,
        String equipment,
        String severity,
        String time

) {
    public EquipmentFailurePayload(String wardId, String equipment, String severity){
        this(wardId, equipment, severity, Instant.now().toString());
    }
}
