package io.virtualan.cucumblan.core.msg.kafka;

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.TopicConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * The type Kafka consumer client.
 */
public class KafkaConsumerClient {

  private final static Logger LOGGER = Logger.getLogger(KafkaConsumerClient.class.getName());

  private final KafkaConsumer consumer;
  private final String eventName;
  private List<String> topic;


  /**
   * Instantiates a new Kafka consumer client.
   *
   * @param eventName the event name
   * @param resource  the resource
   */
  public KafkaConsumerClient(String eventName, String resource) {
    Properties props = new Properties();
    this.eventName = eventName;
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("consumer-" + resource + ".properties");
      props.load(stream);
    } catch (IOException e) {
      LOGGER.warning("consumer-" + resource + ".properties is not loaded");
      System.exit(1);
    }
    consumer = new KafkaConsumer(props);
  }


  public static MessageType getEvent(String eventName, String id, String resource, int recheck)
      throws InterruptedException {
    MessageType expectedJson = (MessageType) MessageContext.getEventContextMap(eventName, id);
    recheck = recheck + 1;
    if (recheck == 5 || expectedJson != null) {
      return expectedJson;
    }
    Thread.sleep(1000);
    KafkaConsumerClient client = new KafkaConsumerClient(eventName, resource);
    client.run(eventName, id);
    return (MessageType) getEvent(eventName, id, resource, recheck);
  }


  private List<String> loadTopic(String eventName) {
    String topics = TopicConfiguration.getProperty(eventName);
    if (topics == null) {
      LOGGER.warning(eventName + " - Topic is not configured.");
      System.exit(1);
    }
    return Arrays.asList(topics.split(";"));
  }

  /**
   * Run.
   */
  public void run(String currentEventName, String id) {
    this.topic = loadTopic(eventName);
    consumer.subscribe(this.topic);
    LOGGER.info(" Read Received message: " + topic);
    int noMessageFound = 0;
    ConsumerRecords<Object, Object>
        consumerRecords = null;
    try {
      while (true) {

        boolean isMessageReceived = MessageContext.isEventContextMap(currentEventName, id);
        if (isMessageReceived) {
          break;
        }
        consumerRecords = consumer.poll(Duration.of(1000, ChronoUnit.MILLIS));

        if (consumerRecords.count() == 0) {
          noMessageFound++;
          if (noMessageFound > ApplicationConfiguration.getMessageCount() || isMessageReceived)
          // If no message found count is reached to threshold exit loop.
          {
            break;
          } else {
            continue;
          }
        }
        consumerRecords.forEach(record -> {
          getMessageType(record);
          consumer.commitAsync();
        });
      }
    }finally {
      if(consumer != null)
      consumer.close();
    }
    LOGGER.info("DONE");

  }

  private boolean getMessageType(ConsumerRecord<Object, Object> record) {
    for (MessageTypeFactory messageType : MessageContext.getMessageTypeFactories()) {
      try {
        MessageType obj = messageType.buildMessage(record, record.key(), record.value());
        MessageContext.setEventContextMap(eventName, String.valueOf(obj.getId()), obj);
        return true;
      } catch (MessageNotDefinedException e) {
        LOGGER.warning(record.key() + " is not defined " + e.getMessage());
      }
    }
    return false;
  }
}