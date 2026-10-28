package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

public class WardServiceApp {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7031);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/ward/{id}", DataRetrieval::ward);
        app.get("/departments", DataRetrieval::department);
        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.


    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
