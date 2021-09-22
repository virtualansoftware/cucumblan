package io.virtualan.cucumblan.core.msg;

import io.cucumber.java.Scenario;
import io.virtualan.cucumblan.message.exception.UnableToProcessException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;

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
  public static boolean postMessage(Scenario scenario, String resource, String sendQ, String message)
      throws UnableToProcessException {
    try {
      scenario.attach(message, "application/json", "message");
      ConnectionFactory connectionFactory =
          JMSMessageContext.loadConnectionFactory(resource);
      JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
      jmsTemplate.setDefaultDestination(new ActiveMQQueue(sendQ));
      jmsTemplate.convertAndSend(message);
    }catch (Exception e){
      logger.warning("Unable to post message for resource " + resource );
      throw  new UnableToProcessException("Unable to post message for resource " + resource +" >> "+ e.getMessage());
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
  public static String  readMessage(Scenario scenario, String resource, String receiveQ, String messageId)
      throws IOException, JMSException {
    ConnectionFactory connectionFactory =
        JMSMessageContext.loadConnectionFactory(resource);
    JmsTemplate jmsTemplateReceive = new JmsTemplate(connectionFactory);
    jmsTemplateReceive.setDefaultDestination(new ActiveMQQueue(receiveQ));
    Message object =  jmsTemplateReceive.receiveSelected(new ActiveMQQueue(receiveQ), messageId);
    return  object.getBody(String.class);
  }
}
