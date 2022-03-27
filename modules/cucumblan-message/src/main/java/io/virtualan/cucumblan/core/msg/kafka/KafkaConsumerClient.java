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

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.exception.SkipMessageException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.TopicConfiguration;
import io.virtualan.cucumblan.props.util.EventRequest;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

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
    public KafkaConsumerClient(String eventName, String resource) {
        Properties props = new Properties();
        try {
            this.topic = loadTopic(eventName);
            InputStream stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("consumer-" + resource + ".properties");
            if (stream != null) {
                props.load(stream);
                if (!props.containsKey("group.id")) {
                    props.put("group.id", eventName + "_" + resource + "_" + UUID.randomUUID());
                }
            } else {
                LOGGER.warning("consumer-" + resource + ".properties is not found");
                System.exit(1);
            }
        } catch (IOException e) {
            LOGGER.warning("consumer-" + resource + ".properties is not loaded");
            System.exit(1);
        }
        consumer = new KafkaConsumer(props);
    }


    /**
     * Gets event.
     *
     * @param eventRequest@return the event
     * @throws InterruptedException the interrupted exception
     */
    public static MessageType getEvent(
            EventRequest eventRequest)
            throws InterruptedException, MessageNotDefinedException {
        MessageType expectedJson = (MessageType) MessageContext.getEventContextMap(
                eventRequest.getEventName(),
                eventRequest.getId());
        eventRequest.setRecheck(eventRequest.getRecheck() + 1);
        if (eventRequest.getRecheck() == 5 || expectedJson != null) {
            if (eventRequest.getClient() != null) {
                eventRequest.getClient().closeConsumer();
            }
            return expectedJson;
        }
        Thread.sleep(1000);
        if (eventRequest.getClient() == null) {
            eventRequest.setClient(new KafkaConsumerClient(eventRequest.getEventName(), eventRequest.getResource()));
        }
        eventRequest.getClient()
                .run(eventRequest.getEventName(), eventRequest.getType(), eventRequest.getId());

        return (MessageType) getEvent(eventRequest);
    }


    private List<String> loadTopic(String eventName) {
        String topics = StepDefinitionHelper.getActualValue(TopicConfiguration.getProperty(eventName));
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
        consumer.subscribe(this.topic);
        LOGGER.info(" Read Received message: " + topic);
        int noMessageFound = 0;
        ConsumerRecords<Object, Object>
                consumerRecords = null;
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
        LOGGER.info("DONE");
    }

    public void closeConsumer() {
        if (consumer != null) {
            consumer.close();
        }
    }

    private boolean getMessageType(String eventName, String type,
                                   ConsumerRecord<Object, Object> record)
            throws MessageNotDefinedException {
        MessageType messageType = MessageContext.getMessageTypes().get(type);
        MessageType obj = null;
        if (messageType != null) {
            try {
                obj = messageType.buildConsumerMessage(record, record.key(), record.value());
                if (obj == null) {
                    java.util.HashMap contextParam = new java.util.HashMap();
                    contextParam.put("EVENT_NAME", eventName);
                    contextParam.put("TYPE", type);
                    obj = messageType.buildConsumerMessage(record, contextParam);
                }
                if (obj != null && obj.getId() != null) {
                    MessageContext.setEventContextMap(eventName, obj.getId().toString(), obj);
                    return true;
                } else if (obj != null) {
                    throw new MessageNotDefinedException("Id is not defined ");
                }
            } catch (SkipMessageException e) {
                LOGGER.warning(record.key() + " is skipped " + e.getMessage());
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