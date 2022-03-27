package io.virtualan.cucumblan.message.type;

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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.util.List;

/**
 * This helps to define application specific kafka message user application needs and Easy to
 * customizable
 *
 * @param <T>  the type parameter
 * @param <TT> the type parameter
 * @author Elan Thangmani
 */
public interface MessageType<T, TT> {

    /**
     * define a unique type of the message would be used.
     *
     * @return the type
     */
    String getType();

    /**
     * Gets unique identifier of the message
     *
     * @return the id
     */
    Object getId();

    /**
     * Gets Kafka message key for kafka
     *
     * @return the id
     */
    T getKey();


    /**
     * Gets Kafka message to be used for Kafka
     *
     * @return the message
     */
    TT getMessage();


    /**
     * Mandatory JSON Should be defined and Used for Verification process.
     * this should be either org.json.JSONArray or org.json.JSONObject
     *
     * @return the JsonArray or  message
     */
    Object getMessageAsJson();


    /**
     * Kafka message headers to be added to produce messages
     *
     * @return the kafka headers
     */
    List<Header> getHeaders();


    /**
     * Build message object build logic for the application specific need. Build and produce events
     *
     * @param tt the tt
     * @return the message type
     */
    default MessageType buildProducerMessage(Object tt) throws MessageNotDefinedException{
        return null;
    }

    /**
     * Build message object build logic for the application specific need. Build and produce events
     *
     * @param tt the tt
     * @param contextParameter    context parameter
     * @return the message type
     */
    default MessageType buildProducerMessage(Object tt, java.util.Map<String, Object> contextParameter) throws MessageNotDefinedException {
        return null;
    }

    /**
     * Build message while consuming the message
     *
     * @param record ConsumerRecord object available in the context
     * @param key    the kafka message key
     * @param value  the kafka message object
     * @return the message type used for Pre defined verification steps
     * @throws MessageNotDefinedException the message not defined exception
     */
    default MessageType buildConsumerMessage(ConsumerRecord<T, TT> record, T key, TT value)
            throws MessageNotDefinedException, SkipMessageException{
        return null;
    }

    /**
     * Build message while consuming the message
     *
     * @param record ConsumerRecord object available in the context
     * @param contextParameter    context parameter
     * @return the message type used for Pre defined verification steps
     * @throws MessageNotDefinedException the message not defined exception
     */
    default  MessageType buildConsumerMessage(ConsumerRecord<T, TT> record, java.util.Map<String, Object> contextParameter)
            throws MessageNotDefinedException, SkipMessageException {
        return null;
    }

    /**
     * Build message while consuming the message
     * for your specific needs Refer io.virtualan.cucumblan.message.typeimpl.JSONMessage
     *
     * @param value the any message object
     * @return the message type used for Pre defined verification steps
     * @throws MessageNotDefinedException the message not defined exception
     */
    default MessageType buildConsumerMessage(TT value)
            throws MessageNotDefinedException, SkipMessageException {
        return null;
    }


}
