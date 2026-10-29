package co.wethinkcode.healthsafe.mq;

import co.wethinkcode.healthsafe.EquipmentFailurePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Shared by every producer/consumer service that talks to the ActiveMQ broker.
 * Duplicated into each participating service's own source tree, since these are
 * independent Maven projects with no shared parent pom.
 *
 * ward-service is a consumer of TOPIC (staffing updates) and a producer on QUEUE
 * (equipment failures detected on its wards).
 */
public final class MqConfig {

    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String TOPIC = "staffing-events-topic";
    public static final String QUEUE = "equipment-failure-queue";
    private static final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
    private static final ObjectMapper jackson = new ObjectMapper();


    private static Connection connection;
    private static Session session;
    private MessageProducer producer;

    private MqConfig() {
    }

    public static void connect(){
        try{
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    public static void subscribeTopic(){
        try{
        Destination destination = session.createTopic(MqConfig.TOPIC);
       MessageConsumer consumer = session.createConsumer(destination);
       consumer.setMessageListener(MqConfig::handleMessage);
        } catch (JMSException e) {
            System.out.println("Could not get message");
        }
    }

    public static void publishEquipmentFailure(EquipmentFailurePayload payload){
        // sending to our broker when a system fails
        try{
            Destination destination = session.createQueue(MqConfig.QUEUE);
            try(MessageProducer produce = session.createProducer(destination)){
            produce.setDeliveryMode(DeliveryMode.PERSISTENT);
            String json = jackson.writeValueAsString(payload);
            TextMessage message = session.createTextMessage(json);
            produce.send(message);
            System.out.println("Message sent");}
        } catch (Exception e) {
            System.out.println("Could not publish payload");
        }
    }

    private static void handleMessage(Message message){
        if(message instanceof TextMessage){
            try{
                String result = ((TextMessage) message).getText();
                if(result == null || result.trim().isBlank()){
                    System.out.println("Message is empty");
                    return;
                }
                JsonNode json = MqConfig.jackson.readValue(result, JsonNode.class);
            } catch (JMSException e) {
                System.out.println("Could not get message");
            } catch (JsonProcessingException e) {
                System.out.println("Could not parse Json");
            }
        }
    }

    public static void close(){

            try{
                if(session != null) session.close();
                if(connection != null){
                connection.close();
                connection = null;
                }
                System.out.println("ActiveMQ Connection gracefully closed.");
            } catch (JMSException e) {
                System.err.println("Could not close ActiveMQ Connection");
            }
    }
}
