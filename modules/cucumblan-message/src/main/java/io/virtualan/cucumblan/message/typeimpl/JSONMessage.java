package io.virtualan.cucumblan.message.typeimpl;

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
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.mapson.Mapson;
import io.virtualan.mapson.exception.BadInputDataException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Json message.
 */
public class JSONMessage implements MessageType<Integer, String> {

    private String type = "JSON";
    private Integer id;
    private String body;

    /**
     * Instantiates a new Json message.
     */
    public JSONMessage() {
    }

    /**
     * Instantiates a new Json message.
     *
     * @param id   the id
     * @param body the body
     */
    public JSONMessage(Integer id, String body) {
        this.body = body;
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public List<Header> getHeaders() {
        return null;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public Integer getKey() {
        return id;
    }

    @Override
    public String getMessage() {
        return body;
    }

    @Override
    public JSONObject getMessageAsJson() {
        return new JSONObject(body);
    }

    @Override
    public MessageType buildProducerMessage(Object messages) throws MessageNotDefinedException {
        if (messages instanceof List) {
            String message = ((List<String>) messages).stream().collect(Collectors.joining());
            JSONObject body = new JSONObject(message);
            return new JSONMessage(body.getInt("id"), message);
        } else {
            String message = null;
            try {
                message = Mapson.buildMAPsonAsJson((Map<String, String>) messages);
                JSONObject body = new JSONObject(message);
                return new JSONMessage(body.getInt("id"), message);
            } catch (BadInputDataException e) {
                throw new MessageNotDefinedException(e.getMessage());
            }
        }
    }


    @Override
    public MessageType buildConsumerMessage(ConsumerRecord<Integer, String> record, Integer key,
                                            String body)
            throws MessageNotDefinedException {
        Integer id = new JSONObject(body).getInt("id");
        return new JSONMessage(id, body);
    }


    @Override
    public String toString() {
        return "JSONMessage{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", body=" + body +
                '}';
    }
}
