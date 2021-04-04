package io.virtualan.cucumblan.message.type;

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
import java.util.List;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.json.JSONObject;

/**
 * The interface Message type.
 *
 * @param <T>  the type parameter
 * @param <TT> the type parameter
 * @author Elan Thangmani
 */
public interface MessageType<T, TT> {

    /**
     * Gets type.
     *
     * @return the type
     */
    String getType();

    /**
     * Gets id.
     *
     * @return the id
     */
    T getId();


    /**
     * Gets message.
     *
     * @return the message
     */
    TT getMessage();


    /**
     * Gets message.
     *
     * @return the message
     */
    Object getMessageAsJson();


    /**
     * Gets message.
     *
     * @return the message
     */
    List<Header> getHeaders();


    /**
     * Build message type.
     *
     * @param tt the tt
     * @return the message type
     */
    MessageType build(Object tt) throws MessageNotDefinedException;

}
