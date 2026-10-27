package co.wethinkcode.healthsafe.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Shared by every producer/consumer service that talks to the "staffing-events-topic"
 * ActiveMQ topic. Duplicated into each participating service's own source tree,
 * since these are independent Maven projects with no shared parent pom.
 */
public final class MqConfig {

    public static final String BROKER_URL = "tcp://localhost:61616";
    public static final String TOPIC = "staffing-events-topic";
    public static final ConnectionFactory CONNECTION_FACTORY = new ActiveMQConnectionFactory(BROKER_URL);
    public static Session session;
    private static ObjectMapper mapper = new ObjectMapper();

    private MqConfig() {
    }

    public static void connect(){
        Connection connect;
        try {
            connect = CONNECTION_FACTORY.createConnection();
            connect.start();
            session = connect.createSession(Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            System.out.println("Refused to connect");;
        }
    }

    public static void publishStaffEvents(String eventPayload){
        try{
            Destination destination = session.createTopic(MqConfig.TOPIC);
            try(MessageProducer producer = session.createProducer(destination)){
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(eventPayload);
            producer.send(message);
            }
        } catch (JMSException e) {
            System.out.println("Refused to publish");
        }
    }
}
