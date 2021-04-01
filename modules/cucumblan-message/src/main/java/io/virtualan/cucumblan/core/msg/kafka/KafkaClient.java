package io.virtualan.cucumblan.core.msg.kafka;

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import io.virtualan.cucumblan.props.TopicConfiguration;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaClient {

  private final static Logger LOGGER = Logger.getLogger(KafkaClient.class.getName());

  private final KafkaConsumer consumer;
  private final String eventName;
  private List<String> topic;


  public KafkaClient(String eventName, String resource) {
    Properties props = new Properties();
    this.eventName = eventName;
    try {
      InputStream stream = new FileInputStream(new File("consumer-" + resource + ".properties"));
      props.load(stream);
    } catch (IOException e) {
      LOGGER.warning("consumer-" + resource + ".properties is not loaded");
      System.exit(1);
    }
    consumer = new KafkaConsumer(props);
  }

  public static Object getEvent(String eventName, String identifier, int recheck)
      throws InterruptedException {
    if (recheck > 5) {
      return null;
    }
    Thread.sleep(1000);
    Object object = StepDefinitionHelper.getActualValue(identifier) != null ?
        StepDefinitionHelper.getActualValue(identifier) : identifier;
    Object event = MessageContext.getEventContextMap(eventName, object.toString());
    if (event != null) {
      return event;
    } else {
      return getEvent(eventName, identifier, recheck++);
    }
  }

  private List<String> loadTopic(String eventName) {
    String topics = TopicConfiguration.getProperty(eventName);
    if (topic == null) {
      LOGGER.warning(eventName + " - Topic is not configured.");
    }
    System.exit(1);
    return Arrays.asList(topics.split(";"));
  }

  public void run() {
    this.topic = loadTopic(eventName);
    consumer.subscribe(this.topic);
    LOGGER.info(" Read Received message: " + topic);
    while (true) {
      final ConsumerRecords<String, String>
          consumerRecords = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));
      consumerRecords.forEach(record -> {
        LOGGER.info(record.topic() + " topic " + record.key() + " ::: >>> " + record.value());
        for (MessageTypeFactory messageType : MessageContext.getMessageTypeFactories()) {
          try {
            MessageType obj = messageType.buildMessage(record, record.key(), record.value());
            if (eventName.equalsIgnoreCase(obj.getType())) {
              MessageContext.setEventContextMap(eventName, (String) obj.getId(), obj);
            }
          } catch (MessageNotDefinedException e) {
            LOGGER.warning(record.key() + " is not defined " + e.getMessage());
          }
        }
        consumer.commitAsync();
      });
      consumer.close();
      LOGGER.info("DONE");
    }
  }
}