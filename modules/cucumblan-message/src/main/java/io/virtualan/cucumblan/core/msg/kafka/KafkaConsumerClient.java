package io.virtualan.cucumblan.core.msg.kafka;
/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
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
  private List<String> topic;


  /**
   * Instantiates a new Kafka consumer client.
   *
   * @param resource the resource
   */
  public KafkaConsumerClient(String resource) {
    Properties props = new Properties();
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


  /**
   * Gets event.
   *
   * @param eventName the event name
   * @param id        the id
   * @param resource  the resource
   * @param recheck   the recheck
   * @return the event
   * @throws InterruptedException the interrupted exception
   */
  public static MessageType getEvent(String eventName, String type, String id, String resource,
      int recheck)
      throws InterruptedException, MessageNotDefinedException {
    MessageType expectedJson = (MessageType) MessageContext.getEventContextMap(eventName, id);
    recheck = recheck + 1;
    if (recheck == 5 || expectedJson != null) {
      return expectedJson;
    }
    Thread.sleep(1000);
    KafkaConsumerClient client = new KafkaConsumerClient(resource);
    client.run(eventName, type, id);
    return (MessageType) getEvent(eventName, type, id, resource, recheck);
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
   *
   * @param id the id
   */
  public void run(String eventName, String type, String id) throws MessageNotDefinedException {
    this.topic = loadTopic(eventName);
    consumer.subscribe(this.topic);
    LOGGER.info(" Read Received message: " + topic);
    int noMessageFound = 0;
    ConsumerRecords<Object, Object>
        consumerRecords = null;
    try {
      while (true) {
        boolean isMessageReceived = MessageContext.isEventContextMap(eventName, id);
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
        for (ConsumerRecord<Object, Object> record : consumerRecords) {
          getMessageType(eventName, type, record);
          consumer.commitAsync();
        }
      }
    } finally {
      if (consumer != null) {
        consumer.close();
      }
    }
    LOGGER.info("DONE");

  }


  private boolean getMessageType(String eventName, String type,
      ConsumerRecord<Object, Object> record)
      throws MessageNotDefinedException {
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    MessageType obj = null;
    if (messageType != null) {
      try {
        obj = messageType.buildConsumerMessage(record, record.key(), record.value());
        if (obj != null && obj.getId() != null) {
          MessageContext.setEventContextMap(eventName, obj.getId().toString(), obj);
          return true;
        }  else if(obj != null){
          throw  new MessageNotDefinedException(  "Id is not defined ");
        }
      } catch (MessageNotDefinedException e) {
        LOGGER.warning(record.key() + " is not defined " + e.getMessage());
        throw e;
      }
    } else {
      throw new MessageNotDefinedException(type + " message type is not defined ");
    }
    return false;
  }
}