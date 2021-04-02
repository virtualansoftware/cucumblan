package io.virtualan.cucumblan.core.msg.kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * The type Kafka producer client.
 */
@Slf4j
public class KafkaProducerClient {


  /**
   * Create producer producer.
   *
   * @param <T>      the type parameter
   * @param <TT>     the type parameter
   * @param resource the resource
   * @return the producer
   */
  public static <T, TT> Producer<T, TT> createProducer(String resource) {
    Properties props = new Properties();
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("producer-" + resource + ".properties");
      props.load(stream);
    } catch (IOException e) {
      log.warn("producer-" + resource + ".properties is not loaded");
      System.exit(1);
    }
    return new KafkaProducer<>(props);
  }

  /**
   * Send message.
   *
   * @param <T>       the type parameter
   * @param <TT>      the type parameter
   * @param resource  the resource
   * @param topic     the topic
   * @param key       the key
   * @param msg       the msg
   * @param partition the partition
   */
  public static <T, TT> void sendMessage(String resource, String topic, T key, TT msg, Integer partition) {
    ProducerRecord<T, TT> record = null;
    if (key != null && partition != null) {
      record = new ProducerRecord<T, TT>(topic, partition, key, msg);
    } else if (key != null) {
      record = new ProducerRecord<T, TT>(topic, key, msg);
    } else {
      record = new ProducerRecord<T, TT>(topic, msg);
    }
    try {
      RecordMetadata metadata = createProducer(resource).send(
          (ProducerRecord<Object, Object>) record).get();
      log.info(metadata.topic() + " message posted successfully ");
    } catch (Exception e) {
      log.error("Error in sending record " + e.getMessage());
    }
  }

}
