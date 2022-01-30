package io.virtualan.cucumblan.core.msg;

import io.cucumber.java.Scenario;
import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.exception.SkipMessageException;
import io.virtualan.cucumblan.message.exception.UnableToProcessException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.mapson.Mapson;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The type Mq client.
 */
public class MQClient {

    private static Logger logger = Logger.getLogger(MQClient.class.getName());

    /**
     * Post message boolean.
     *
     * @param scenario the scenario
     * @param resource the resource
     * @param sendQ    the send q
     * @param message  the message
     * @return the boolean
     * @throws UnableToProcessException the unable to process exception
     */
    public static boolean postMessage(Scenario scenario, String resource, String sendQ,
                                      String message, String type)
            throws UnableToProcessException {
        try {
            if ("AMQ".equalsIgnoreCase(type)) {
                scenario.attach(message, "text/plain", "message");
                ConnectionFactory connectionFactory =
                        JMSMessageContext.loadConnectionFactory(resource);
                JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
                jmsTemplate.setDefaultDestination(new ActiveMQQueue(sendQ));
                jmsTemplate.convertAndSend(message);
            } else {
                logger.warning(type + " is not supported");
                throw new UnableToProcessException(type + " is not supported");
            }
        } catch (Exception e) {
            logger.warning("Unable to post message for resource " + resource);
            throw new UnableToProcessException(
                    "Unable to post message for resource " + resource + " >> " + e.getMessage());
        }
        return true;
    }

    /**
     * Read message string.
     *
     * @param scenario  the scenario
     * @param resource  the resource
     * @param receiveQ  the receive q
     * @param messageId the message id
     * @return the string
     * @throws IOException  the io exception
     * @throws JMSException the jms exception
     */
    public static String readMessage(Scenario scenario, String resource, String receiveQ,
                                     String messageId, String type)
            throws UnableToProcessException, IOException, JMSException {
        if ("AMQ".equalsIgnoreCase(type)) {
            ConnectionFactory connectionFactory =
                    JMSMessageContext.loadConnectionFactory(resource);
            JmsTemplate jmsTemplateReceive = new JmsTemplate(connectionFactory);
            jmsTemplateReceive.setDefaultDestination(new ActiveMQQueue(receiveQ));
            Message object = jmsTemplateReceive.receiveSelected(new ActiveMQQueue(receiveQ), messageId);
            return object.getBody(String.class);
        } else {
            throw new UnableToProcessException(type + "is not supported");
        }
    }


    /**
     * Read message string.
     *
     * @param scenario the scenario
     * @param resource the resource
     * @param receiveQ the receive q
     * @return the string
     * @throws IOException  the io exception
     * @throws JMSException the jms exception
     */
    public static String readMessage(Scenario scenario, String resource, String receiveQ)
            throws IOException, JMSException {
        ConnectionFactory connectionFactory =
                JMSMessageContext.loadConnectionFactory(resource);
        JmsTemplate jmsTemplateReceive = new JmsTemplate(connectionFactory);
        jmsTemplateReceive.setDefaultDestination(new ActiveMQQueue(receiveQ));
        Object object = jmsTemplateReceive.receiveAndConvert();
        return (String) object;
    }

    public static String findMessage(Scenario scenario, String resource,
                                     String eventNameInput, String mapson, String type)
            throws IOException, JMSException, MessageNotDefinedException, SkipMessageException {
        int countCheck = ApplicationConfiguration.getMessageCount();
        MessageType messageType = MessageContext.getMessageTypes().get(type);
        for (int i = 0; i < countCheck; i++) {
            String expectedJson = null;
            if (messageType != null) {
                String readContent = MQClient.readMessage(scenario, resource, eventNameInput);
                MessageType messageTypeValue = messageType.buildConsumerMessage(readContent);
                expectedJson = (String) messageTypeValue.getMessageAsJson();
            } else {
                expectedJson = MQClient.readMessage(scenario, resource, eventNameInput);
            }
            if (expectedJson != null) {
                Map<String, String> stringMap = Mapson.buildMAPsonFromJson(expectedJson);
                String key = mapson.split("(?<!\\\\)=")[0];
                String value = mapson.split("(?<!\\\\)=")[1];
                if (stringMap.containsKey(key) && value.equals(stringMap.get(key))) {
                    return expectedJson;
                }
            }
        }
        return null;
    }
}

