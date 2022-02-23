package io.virtualan.cucumblan.core.msg.kafka;
/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
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

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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
            InputStream stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("producer-" + resource + ".properties");
            if (stream != null) {
                props.load(stream);
            } else {
                log.warn("producer-" + resource + ".properties is not found");
                System.exit(1);
            }
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
    public static <T, TT> void sendMessage(String resource, String topic, T key, TT msg,
                                           Integer partition, List<Header> headers) {
        ProducerRecord<T, TT> record = null;
        if (key != null && partition != null) {
            record = new ProducerRecord<T, TT>(topic, partition, key, msg, headers);
        } else if (key != null) {
            record = new ProducerRecord<T, TT>(topic, null, key, msg, headers);
        } else {
            record = new ProducerRecord<T, TT>(topic, null, null, msg, headers);

        }
        Producer<T, TT> producer = null;
        try {
            producer = createProducer(resource);

            RecordMetadata metadata = producer.send((ProducerRecord<T, TT>) record).get();
            log.info(metadata.topic() + " message posted successfully ");
        } catch (Exception e) {
            log.error("Error in sending record " + e.getMessage());
        } finally {
            if (producer != null) {
                producer.flush();
                producer.close();
            }
        }
    }

}
